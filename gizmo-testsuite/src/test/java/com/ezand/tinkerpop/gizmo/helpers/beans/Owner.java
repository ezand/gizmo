package com.ezand.tinkerpop.gizmo.helpers.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ezand.tinkerpop.gizmo.annotations.Id;
import com.ezand.tinkerpop.gizmo.annotations.Vertex;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Vertex(idClass = Long.class)
public class Owner {
    @Id
    private Long id;
    private String firstName;
    private String lastName;

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
        final StringBuilder sb = new StringBuilder("Owner{");
        sb.append("id=").append(id);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
