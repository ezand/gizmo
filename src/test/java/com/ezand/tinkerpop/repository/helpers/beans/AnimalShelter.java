package com.ezand.tinkerpop.repository.helpers.beans;

import lombok.Value;

import com.ezand.tinkerpop.repository.structure.GraphElement;

@Value
public class AnimalShelter implements GraphElement<Long> {
    Long id;
    String name;
}
