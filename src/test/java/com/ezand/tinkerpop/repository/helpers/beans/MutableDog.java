package com.ezand.tinkerpop.repository.helpers.beans;

import lombok.Data;

import com.ezand.tinkerpop.repository.structure.GraphElement;

@Data
public class MutableDog implements GraphElement<Long> {
    Long id;
    String name;
    String bread;
}
