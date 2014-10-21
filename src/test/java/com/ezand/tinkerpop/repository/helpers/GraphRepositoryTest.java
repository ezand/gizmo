package com.ezand.tinkerpop.repository.helpers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.Test;

import com.ezand.tinkerpop.repository.helpers.beans.AnimalShelter;
import com.ezand.tinkerpop.repository.helpers.repository.AnimalShelterRepository;
import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.structure.Graph;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class GraphRepositoryTest {
    private Graph graph;
    private AnimalShelterRepository repository;

    @Before
    public void init() {
        graph = TinkerGraph.open();
        repository = new AnimalShelterRepository(graph);
    }

    @Test
    public void should_find_by_id() {
        String animalShelterName = "My awsome shelter";
        Vertex vertex = graph.addVertex(T.label, AnimalShelter.class.getName(), "name", animalShelterName);
        AnimalShelter animalShelter = repository.find((long) vertex.id());

        assertThat(animalShelter, notNullValue());
        assertThat(animalShelter.getId(), notNullValue());
        assertThat(animalShelter.getName(), equalTo(animalShelterName));
    }
}
