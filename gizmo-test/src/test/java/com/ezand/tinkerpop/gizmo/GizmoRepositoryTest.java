package com.ezand.tinkerpop.gizmo;

import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getChanges;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.Test;

import com.ezand.tinkerpop.gizmo.helpers.beans.AnimalShelter;
import com.ezand.tinkerpop.gizmo.helpers.repository.AnimalShelterRepository;
import com.tinkerpop.gremlin.structure.Graph;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class GizmoRepositoryTest {
    private AnimalShelterRepository repository;
    private Graph graph;

    @Before
    public void init() {
        this.graph = TinkerGraph.open();
        this.repository = new AnimalShelterRepository(graph);
    }

    @Test
    public void foo() {
        AnimalShelter animalShelter = repository.save(new AnimalShelter("My Shelter", "Street 1, City", 1));

        assertThat(animalShelter, notNullValue());
        assertThat(getChanges(animalShelter).size(), equalTo(0));

        animalShelter.setName("My Shelter");
        assertThat(getChanges(animalShelter).size(), equalTo(0));
        assertThat(graph.v(getId(animalShelter, Long.class)).property("name").value(), equalTo("My Shelter"));

        animalShelter.setName("My Awesome Shelter");
        assertThat(getChanges(animalShelter).size(), equalTo(1));
        AnimalShelter updatedAnimalShelter = repository.update(animalShelter);
        assertThat(graph.v(getId(updatedAnimalShelter, Long.class)).property("name").value(), equalTo("My Awesome Shelter"));
    }
}