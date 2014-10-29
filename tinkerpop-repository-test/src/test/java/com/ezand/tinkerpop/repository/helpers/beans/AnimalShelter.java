package com.ezand.tinkerpop.repository.helpers.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ezand.tinkerpop.repository.annotations.Vertex;
import com.ezand.tinkerpop.repository.structure.GraphElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Vertex(idClass = Long.class)
public class AnimalShelter {
    private String name;
    private String address;
    private int inhabitantCount;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnimalShelter{");
        sb.append("id='").append(((GraphElement) this).$getId()).append('\'');
        sb.append(", name='").append(address).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", inhabitantCount='").append(inhabitantCount).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
