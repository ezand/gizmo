package com.ezand.tinkerpop.gizmo.helpers.repository;

import com.ezand.tinkerpop.gizmo.GizmoRepository;
import com.tinkerpop.gremlin.structure.Graph;

public class NonManageableRepository extends GizmoRepository<String, String> {
    public NonManageableRepository(Graph graph) {
        super(graph);
    }
}
