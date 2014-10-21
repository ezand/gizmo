package com.ezand.tinkerpop.repository.helpers.repository;

import com.ezand.tinkerpop.repository.GraphRepository;
import com.ezand.tinkerpop.repository.helpers.beans.AnimalShelter;
import com.tinkerpop.gremlin.structure.Graph;

public class AnimalShelterRepository implements GraphRepository<AnimalShelter, Long> {
    private final Graph graph;

    public AnimalShelterRepository(Graph graph) {
        this.graph = graph;
    }

    @Override
    public Graph getGraph() {
        return graph;
    }

    @Override
    public String getLabel() {
        return AnimalShelter.class.getName();
    }
}
