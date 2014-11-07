package com.ezand.tinkerpop.gizmo.helpers.repository;

import com.ezand.tinkerpop.gizmo.GizmoRepository;
import com.ezand.tinkerpop.gizmo.helpers.beans.Dog;
import com.tinkerpop.gremlin.structure.Graph;

public class DogRepository extends GizmoRepository<Dog, Long> {
    public DogRepository(Graph graph) {
        super(graph);
    }
}
