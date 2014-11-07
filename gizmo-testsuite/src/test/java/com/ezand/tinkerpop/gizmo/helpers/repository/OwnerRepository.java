package com.ezand.tinkerpop.gizmo.helpers.repository;

import com.ezand.tinkerpop.gizmo.GizmoRepository;
import com.ezand.tinkerpop.gizmo.helpers.beans.Owner;
import com.tinkerpop.gremlin.structure.Graph;

public class OwnerRepository extends GizmoRepository<Owner, Long> {
    public OwnerRepository(Graph graph) {
        super(graph);
    }
}
