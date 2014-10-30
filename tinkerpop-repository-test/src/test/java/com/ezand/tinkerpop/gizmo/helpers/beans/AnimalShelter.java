package com.ezand.tinkerpop.gizmo.helpers.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.ezand.tinkerpop.gizmo.annotations.Vertex;
import com.ezand.tinkerpop.gizmo.structure.GizmoElement;

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
        sb.append("id='").append(((GizmoElement) this).$getId()).append('\'');
        sb.append(", name='").append(address).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", inhabitantCount='").append(inhabitantCount).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
