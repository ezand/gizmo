package com.ezand.tinkerpop.repository.structure;

import com.tinkerpop.gremlin.structure.Element;

public interface GraphElement<T, ID> {
    ID $getId();

    Object[] $toKeyValues();

    void $applyElement(Element element);
}
