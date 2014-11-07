package com.ezand.tinkerpop.gizmo;

import static com.ezand.tinkerpop.gizmo.utils.Exceptions.beanNotManagedException;
import static com.ezand.tinkerpop.gizmo.utils.GizmoMapper.map;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.assertManageableBean;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getChanges;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getElement;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.isManaged;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.removeEmptyArguments;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.validateArguments;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.TypeToken;
import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.process.graph.GraphTraversal;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Graph;
import com.tinkerpop.gremlin.structure.Property;
import com.tinkerpop.gremlin.structure.Vertex;

public abstract class GizmoRepository<B, ID> implements CRUDRespository<B, ID> {
    protected final Graph graph;
    protected final Class<? super B> beanClass;
//    protected final Map<String, RelationshipAccessors> accessors;

    protected GizmoRepository(Graph graph) {
        this.beanClass = new TypeToken<B>(getClass()) {
        }.getRawType();
        this.graph = graph;
//        this.accessors = getRelationshipMethods(beanClass);
    }

    public Graph getGraph() {
        return this.graph;
    }

    protected String getLabel() {
        return this.beanClass.getName();
    }

    @Override
    public Set<B> find() {
        return mapTraversal(getClassTypeTraversal());
    }

    @SuppressWarnings("unchecked")
    @Override
    public B find(ID id) {
        try {
            return (B) map(getGraph().v(id), beanClass);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public Set<B> find(String key, Object value) {
        return mapTraversal(getClassTypeTraversal().has(key, value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(ID... ids) {
        stream(ids).forEach(i -> getGraph().v(i).remove());
    }

    @Override
    public void delete(B bean) {
        assertManageableBean(bean);

        if (!isManaged(bean)) {
            throw beanNotManagedException();
        }

        getElement(bean).remove();
    }

    @SuppressWarnings("unchecked")
    @Override
    public B save(B bean) {
        assertManageableBean(bean);

        if (isManaged(bean)) {
            return update(bean);
        }

        Object[] arguments = map(bean, getLabel());
        Object[] filteredArguments = removeEmptyArguments(arguments);

        validateArguments(filteredArguments);

        Vertex vertex = getGraph().addVertex(filteredArguments);
        return (B) map(vertex, beanClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<B> save(B... beans) {
        Set<B> saved = newHashSet();
        stream(beans).forEach(b -> saved.add(save(b)));
        return saved;
    }

    @Override
    public B update(B bean) {
        assertManageableBean(bean);

        if (!isManaged(bean)) {
            return save(bean);
        }

        Element element = getElement(bean);
        getChanges(bean)
                .entrySet()
                .forEach(e -> {
                    Property<Object> property = element.property(e.getKey());
                    if (property.isPresent()) {
                        property.remove();
                    }

                    if (e.getValue() != null) {
                        element.property(e.getKey(), e.getValue());
                    }
                });

        return bean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<B> update(B... beans) {
        Set<B> updated = newHashSet();
        stream(beans).forEach(b -> updated.add(update(b)));
        return updated;
    }

    @Override
    public long count() {
        return getClassTypeTraversal().count().next();
    }

    @Override
    public long count(String key) {
        return getClassTypeTraversal().has(key).count().next();
    }

    @Override
    public long count(String key, Object property) {
        return getClassTypeTraversal().has(key, property).count().next();
    }

    protected GraphTraversal<Vertex, Element> getClassTypeTraversal() {
        return getGraph().V().has(T.label, getLabel());
    }

    @SuppressWarnings("unchecked")
    protected Set<B> mapTraversal(Traversal<Vertex, Element> traversal) {
        return traversal
                .toSet()
                .stream()
                .filter(e -> e instanceof Vertex)
                .map(e -> (B) map(e, beanClass))
                .collect(Collectors.toSet());
    }
}
