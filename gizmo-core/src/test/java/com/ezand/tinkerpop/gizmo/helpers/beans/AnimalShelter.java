package com.ezand.tinkerpop.gizmo.helpers.beans;

import static com.tinkerpop.gremlin.structure.Direction.IN;

import java.util.Map;
import java.util.Set;

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
public class AnimalShelter implements GizmoElement<Long> {
    private final Map<String, Object> changes = Maps.newHashMap();
    private Element element;

    Long id;
    String name;

    public AnimalShelter(Element element) {
        this.id = (Long) element.id();
        this.element = element;
        Property nameProperty = element.property("name");
        this.name = nameProperty.isPresent() ? (String) nameProperty.value() : null;
    }

    @Relationship(label = "inhabits", direction = IN)
    Set<Animal> animals;

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

    public void setName(String name) {
        if (!name.equals(this.name)) {
            changes.put("name", name);
        }
        this.name = name;
    }
}
