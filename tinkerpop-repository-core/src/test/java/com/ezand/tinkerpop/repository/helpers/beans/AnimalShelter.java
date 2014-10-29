package com.ezand.tinkerpop.repository.helpers.beans;

import static com.tinkerpop.gremlin.structure.Direction.IN;

import java.util.Map;
import java.util.Set;

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
public class AnimalShelter implements GraphElement<Long> {
    private final Map<String, Object> changes = Maps.newHashMap();
    private Element element;

    Long id;
    String name;

    public AnimalShelter(Element element) {
        this.element = element;
    }

    @Relationship(label = "inhabits", direction = IN)
    Set<Animal> animals;

    @Override
    public Long $getId() {
        return null;
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
