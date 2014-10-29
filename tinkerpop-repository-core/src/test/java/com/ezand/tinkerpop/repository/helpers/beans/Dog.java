package com.ezand.tinkerpop.repository.helpers.beans;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.google.common.collect.Maps;
import com.tinkerpop.gremlin.structure.Element;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dog implements GraphElement<Long> {
    private final Map<String, Object> changes = Maps.newHashMap();
    private Element element;

    Long id;
    String name;
    String bread;

    public Dog(Element element) {
        this.element = element;
    }

    @Override
    public Long $getId() {
        return null;
    }

    @Override
    public Object[] $toKeyValues() {
        return new Object[]{
                "name", name, "bread", bread
        };
    }

    @Override
    public Map<String, Object> $getPropertyChanges() {
        return changes;
    }

    @Override
    public Element $getElement() {
        return this.element;
    }
}
