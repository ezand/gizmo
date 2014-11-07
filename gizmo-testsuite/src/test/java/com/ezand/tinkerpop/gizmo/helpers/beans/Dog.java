package com.ezand.tinkerpop.gizmo.helpers.beans;

import static com.ezand.tinkerpop.gizmo.structure.FetchMode.EAGER;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ezand.tinkerpop.gizmo.annotations.Id;
import com.ezand.tinkerpop.gizmo.annotations.Relationship;
import com.ezand.tinkerpop.gizmo.annotations.Vertex;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Vertex(idClass = Long.class)
public class Dog {
    @Id
    private Long id;
    private String name;
    private String bread;

    @Relationship(label = "inhabits", fetchMode = EAGER)
    private AnimalShelter animalShelter;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Dog{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", bread='").append(bread).append('\'');
        sb.append(", animalShelter=").append(animalShelter);
        sb.append('}');
        return sb.toString();
    }
}
