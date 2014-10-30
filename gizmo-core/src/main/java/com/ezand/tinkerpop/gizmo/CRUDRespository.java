package com.ezand.tinkerpop.gizmo;

import java.util.Set;

public interface CRUDRespository<B, ID> {

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

    @SuppressWarnings({"unchecked"})
    Set<B> update(B... beans);

    long count();

    long count(String key);

    long count(String key, Object property);
}
