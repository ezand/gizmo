package com.ezand.tinkerpop.repository.helpers.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Property;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dog implements GraphElement<Dog, Long> {
    Long id;
    String name;
    String bread;

    @Override
    public Long $getId() {
        return null;
    }

    @Override
    public Object[] $toKeyValues() {
        return new Object[] {
                "name", name, "bread", bread
        };
    }

    @Override
    public void $applyElement(Element element) {
        Property name = element.property("name");
        Property bread = element.property("bread");
        this.name = name.isPresent() ? (String) name.value() : null;
        this.bread = bread.isPresent() ? (String) bread.value() : null;
        this.id = (Long) element.id();
    }
}
