package com.ezand.tinkerpop.repository;

import com.ezand.tinkerpop.repository.annotations.Vertex;
import com.ezand.tinkerpop.repository.structure.GraphElement;

@Vertex(idClass = Long.class)
public class AnimalShelter {
    private String name;
    private String address;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnimalShelter{");
        sb.append("id='").append(((GraphElement) this).$getId()).append('\'');
        sb.append(", name='").append(address).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
