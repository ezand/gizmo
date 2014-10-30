package com.ezand.tinkerpop.gizmo.helpers.repository;

import com.ezand.tinkerpop.gizmo.GizmoRepository;
import com.ezand.tinkerpop.gizmo.helpers.beans.AnimalShelter;
import com.tinkerpop.gremlin.structure.Graph;

public class AnimalShelterRepository extends GizmoRepository<AnimalShelter, Long> {
    public AnimalShelterRepository(Graph graph) {
        super(graph);
    }
}
