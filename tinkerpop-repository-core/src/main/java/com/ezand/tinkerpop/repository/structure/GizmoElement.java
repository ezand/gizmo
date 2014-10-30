package com.ezand.tinkerpop.repository.structure;

import java.util.Map;

import com.tinkerpop.gremlin.structure.Element;

public interface GizmoElement<ID> {
    ID $getId();

    Object[] $toKeyValues();

    Map<String, Object> $getPropertyChanges();

    Element $getElement();
}
