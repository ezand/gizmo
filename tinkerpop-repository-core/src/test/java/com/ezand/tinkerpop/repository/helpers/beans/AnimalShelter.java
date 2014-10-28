package com.ezand.tinkerpop.repository.helpers.beans;

import static com.tinkerpop.gremlin.structure.Direction.IN;

import java.util.Set;

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
public class AnimalShelter implements GraphElement<AnimalShelter, Long> {
    Long id;
    String name;

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
    public void $applyElement(Element element) {
        Property name = element.property("name");
        this.name = name.isPresent() ? (String) name.value() : null;
        this.id = (Long) element.id();
    }
}
