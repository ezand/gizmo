package com.ezand.tinkerpop.gizmo.utils;

import static com.ezand.tinkerpop.gizmo.utils.GizmoMapper.map;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getId;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.removeEmptyArguments;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import com.ezand.tinkerpop.gizmo.helpers.beans.AnimalShelter;
import com.google.common.collect.Sets;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class GizmoMapperTest {
    public static final String SHELTER_NAME = "My shelter";

    @Test
    public void should_map_element_to_bean() throws Exception {
        Vertex vertex = TinkerGraph.open().addVertex("name", SHELTER_NAME);
        AnimalShelter animalShelter = map(vertex, AnimalShelter.class);

        assertThat(animalShelter, notNullValue());
        assertThat(animalShelter.getName(), equalTo(SHELTER_NAME));
        assertThat(getId(animalShelter, Long.class), equalTo(vertex.id()));
    }

    @Test
    public void should_map_bean_to_key_values() throws Exception {
        AnimalShelter animalShelter = new AnimalShelter(null, SHELTER_NAME, "Street 1, City", 1, Sets.newHashSet(), null);
        Object[] keyValues = removeEmptyArguments(map(animalShelter, AnimalShelter.class.getName()));

        assertThat(keyValues, notNullValue());
        assertThat(keyValues.length, greaterThan(0));

        Vertex vertex = TinkerGraph.open().addVertex(keyValues);
        assertThat(vertex.property("name").value(), equalTo(SHELTER_NAME));
        assertThat(vertex.label(), equalTo(AnimalShelter.class.getName()));
    }
}
