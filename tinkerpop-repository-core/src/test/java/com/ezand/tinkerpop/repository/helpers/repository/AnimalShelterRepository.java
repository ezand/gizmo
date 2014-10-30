package com.ezand.tinkerpop.repository.helpers.repository;

import com.ezand.tinkerpop.repository.GizmoRepository;
import com.ezand.tinkerpop.repository.helpers.beans.AnimalShelter;
import com.tinkerpop.gremlin.structure.Graph;

public class AnimalShelterRepository extends GizmoRepository<AnimalShelter, Long> {
    public AnimalShelterRepository(Graph graph) {
        super(graph);
    }
}
