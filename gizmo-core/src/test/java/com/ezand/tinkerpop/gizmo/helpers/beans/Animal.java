package com.ezand.tinkerpop.gizmo.helpers.beans;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ezand.tinkerpop.gizmo.structure.GizmoElement;
import com.ezand.tinkerpop.gizmo.annotations.Relationship;
import com.google.common.collect.Maps;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Property;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal implements GizmoElement<Long> {
    private final Map<String, Object> changes = Maps.newHashMap();
    private Element element;

    Long id;
    String name;

    public Animal(Element element) {
        this.element = element;
        this.id = (Long) element.id();
        Property nameProperty = element.property("name");
        this.name = nameProperty.isPresent() ? (String) nameProperty.value() : null;
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
