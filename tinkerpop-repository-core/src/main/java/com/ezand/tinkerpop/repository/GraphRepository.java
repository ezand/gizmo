package com.ezand.tinkerpop.repository;

import static com.ezand.tinkerpop.repository.utils.GraphUtil.fromElement;
import static com.ezand.tinkerpop.repository.utils.GraphUtil.getKeyValues;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.google.common.reflect.TypeToken;
import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.process.graph.GraphTraversal;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Graph;
import com.tinkerpop.gremlin.structure.Vertex;

public abstract class GraphRepository<B extends GraphElement<B, ID>, ID> implements CRUDRespository<B, ID> {
    protected final Graph graph;
    protected final Class<? super B> beanClass;

    protected GraphRepository(Graph graph) {
        this.graph = graph;
        this.beanClass = new TypeToken<B>(getClass()) {
        }.getRawType();
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
        return (B) fromElement(getGraph().v(id), beanClass);
    }

    @Override
    public Set<B> find(String key, Object value) {
        return mapTraversal(getClassTypeTraversal().has(key, value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(ID... ids) {
        Arrays.stream(ids).forEach(i -> getGraph().v(i).remove());
    }

    @Override
    public void delete(B bean) {
        getGraph().v(bean.$getId()).remove();
    }

    @SuppressWarnings("unchecked")
    @Override
    public B save(B bean) {
        if (isExistingBean(bean)) {
            return update(bean);
        }

        Vertex vertex = getGraph().addVertex(getKeyValues(bean));
        return (B) fromElement(vertex, beanClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<B> save(B... beans) {
        Set<B> saved = new HashSet<>();
        Arrays.stream(beans)
                .forEach(b -> saved.add(save(b)));
        return saved;
    }

    @Override
    public B update(B bean) {
        if (bean.$getId() == null) {
            return save(bean);
        }

        // TODO loop through properties and update vertex
        return null;
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
    public long count(String key, String property) {
        return getClassTypeTraversal().has(key, property).count().next();
    }

    protected GraphTraversal<Vertex, Element> getClassTypeTraversal() {
        return getGraph().V().has(com.tinkerpop.gremlin.process.T.label, getLabel());
    }

    @SuppressWarnings("unchecked")
    protected Set<B> mapTraversal(Traversal<Vertex, Element> traversal) {
        return traversal
                .toSet()
                .stream()
                .filter(e -> e instanceof Vertex)
                .map(e -> (B) fromElement(e, beanClass))
                .collect(Collectors.toSet());
    }

    protected boolean isExistingBean(B bean) {
        return bean.$getId() != null && graph.v(bean.$getId()).count().next() > 0;
    }
}
