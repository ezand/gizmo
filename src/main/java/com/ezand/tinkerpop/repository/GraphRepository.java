package com.ezand.tinkerpop.repository;

import static com.ezand.tinkerpop.repository.mapper.BeanMapper.mapToBean;
import static com.ezand.tinkerpop.repository.mapper.BeanMapper.mapToVertex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.process.graph.GraphTraversal;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Graph;
import com.tinkerpop.gremlin.structure.Vertex;

public interface GraphRepository<B extends GraphElement<ID>, ID> extends CRUDRespository<B, ID> {

    Graph getGraph();

    String getLabel();

    @Override
    default Set<B> find() {
        return mapTraversal(getClassTypeTraversal());
    }

    @Override
    default B find(ID id) {
        return mapToBean(getGraph().v(id));
    }

    @Override
    default Set<B> find(String key, Object value) {
        return mapTraversal(getClassTypeTraversal().has(key, value));
    }

    @SuppressWarnings("unchecked")
    @Override
    default void delete(ID... ids) {
        Arrays.stream(ids).forEach(i -> getGraph().v(i).remove());
    }

    @Override
    default void delete(B bean) {
        getGraph().v(bean.getId()).remove();
    }

    @Override
    default B save(B bean) {
        if (bean.getId() != null) {
            return update(bean);
        }

        return mapToBean(getGraph().addVertex(mapToVertex(bean, getLabel())));
    }

    @SuppressWarnings("unchecked")
    @Override
    default Set<B> save(B... beans) {
        Set<B> saved = new HashSet<>();
        Arrays.stream(beans)
                .forEach(b -> saved.add(save(b)));
        return saved;
    }

    @Override
    default B update(B bean) {
        if (bean.getId() == null) {
            return save(bean);
        }

        // TODO loop through properties and update vertex
        return null;
    }

    @Override
    default long count() {
        return getClassTypeTraversal().count().next();
    }

    @Override
    default long count(String key) {
        return getClassTypeTraversal().has(key).count().next();
    }

    @Override
    default long count(String key, String property) {
        return getClassTypeTraversal().has(key, property).count().next();
    }

    default GraphTraversal<Vertex, Element> getClassTypeTraversal() {
        return getGraph().V().has(com.tinkerpop.gremlin.process.T.label, getLabel());
    }

    @SuppressWarnings("unchecked")
    default Set<B> mapTraversal(Traversal<Vertex, Element> traversal) {
        return traversal
                .toSet()
                .stream()
                .filter(e -> e instanceof Vertex)
                .map(e -> (B) mapToBean(e))
                .collect(Collectors.toSet());
    }
}
