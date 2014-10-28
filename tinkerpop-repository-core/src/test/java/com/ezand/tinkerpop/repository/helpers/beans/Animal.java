package com.ezand.tinkerpop.repository.helpers.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.ezand.tinkerpop.repository.structure.Relationship;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Property;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal implements GraphElement<Animal, Long> {
    Long id;
    String name;

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
    public void $applyElement(Element element) {
        Property name = element.property("name");
        this.name = name.isPresent() ? (String) name.value() : null;
        this.id = (Long) element.id();
    }
}
