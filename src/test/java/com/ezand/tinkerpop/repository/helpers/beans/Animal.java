package com.ezand.tinkerpop.repository.helpers.beans;

import lombok.Value;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.ezand.tinkerpop.repository.structure.Relationship;

@Value
public class Animal implements GraphElement<Long> {
    Long id;
    String name;

    @Relationship(label = "inhabits")
    AnimalShelter shelter;
}
