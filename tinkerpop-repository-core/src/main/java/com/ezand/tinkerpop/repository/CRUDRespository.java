package com.ezand.tinkerpop.repository;

import java.util.Set;

import com.ezand.tinkerpop.repository.structure.GraphElement;

public interface CRUDRespository<B extends GraphElement<B, ID>, ID> {

    Set<B> find();

    B find(ID id);

    Set<B> find(String key, Object value);

    @SuppressWarnings({"unchecked"})
    void delete(ID... ids);

    void delete(B bean);

    B save(B bean);

    @SuppressWarnings("unchecked")
    Set<B> save(B... beans);

    B update(B bean);

    long count();

    long count(String key);

    long count(String key, String property);
}
