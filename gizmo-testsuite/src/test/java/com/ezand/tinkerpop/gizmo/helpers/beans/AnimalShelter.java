package com.ezand.tinkerpop.gizmo.helpers.beans;

import static com.tinkerpop.gremlin.structure.Direction.IN;

import java.util.Set;

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
public class AnimalShelter {
    @Id
    Long id;
    String name;
    String address;
    int inhabitantCount;

    @Relationship(label = "inhabits", direction = IN)
    Set<Dog> inhabitants;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
