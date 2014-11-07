package com.ezand.tinkerpop.gizmo.helpers.beans;

import static com.ezand.tinkerpop.gizmo.structure.Direction.IN;

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
    private Long id;
    private String name;
    private String address;
    private int inhabitantCount;

    @Relationship(label = "inhabits", direction = IN)
    private Set<Dog> inhabitants;

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
        final StringBuilder sb = new StringBuilder("AnimalShelter{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", inhabitantCount=").append(inhabitantCount);
        sb.append(", inhabitants=").append(inhabitants);
        sb.append('}');
        return sb.toString();
    }
}
