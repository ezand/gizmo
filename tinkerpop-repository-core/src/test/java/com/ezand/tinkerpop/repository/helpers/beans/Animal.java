package com.ezand.tinkerpop.repository.helpers.beans;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.ezand.tinkerpop.repository.structure.Relationship;
import com.google.common.collect.Maps;
import com.tinkerpop.gremlin.structure.Element;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal implements GraphElement<Long> {
    private final Map<String, Object> changes = Maps.newHashMap();
    private Element element;

    Long id;
    String name;

    public Animal(Element element) {
        this.element = element;
    }

    @Relationship(label = "inhabits")
    AnimalShelter shelter;

    @Override
    public Long $getId() {
        return id;
    }

    @Override
    public Object[] $toKeyValues() {
        return new Object[]{
                "name", name
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
