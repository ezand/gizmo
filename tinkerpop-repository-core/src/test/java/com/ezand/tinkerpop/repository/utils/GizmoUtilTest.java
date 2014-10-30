package com.ezand.tinkerpop.repository.utils;

import static com.ezand.tinkerpop.repository.utils.GizmoUtil.isManaged;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import com.ezand.tinkerpop.repository.helpers.beans.Animal;
import com.ezand.tinkerpop.repository.helpers.beans.AnimalShelter;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class GizmoUtilTest {
    @Test
    public void should_be_managed() throws Exception {
        TinkerGraph graph = TinkerGraph.open();
        Vertex vertex = graph.addVertex("name", "My Shelter");

        Animal animal = new Animal(vertex, null, "My Shelter", new AnimalShelter());
        assertThat(isManaged(animal), equalTo(true));
    }

    @Test
    public void should_not_be_managed_when_element_is_not_set() throws Exception {
        assertThat(isManaged(new Animal()), equalTo(false));
    }

    @Test
    public void should_not_be_managed_when_element_doesnt_implemnt_GraphElement() throws Exception {
        assertThat(isManaged("Foo"), equalTo(false));
    }
}
