package com.ezand.tinkerpop.repository.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.ezand.tinkerpop.repository.helpers.beans.AnimalShelter;
import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class BeanMapperTest {
    public static final String MY_AWESOME_SHELTER = "My awesome shelter";

    private final TinkerGraph graph = TinkerGraph.open();
    private final BeanMapper mapper = new BeanMapper(graph.features());

    @Before
    public void init() throws Exception {
        graph.clear();
    }

    @Test
    public void should_get_bean_class() throws Exception {
        Vertex vertex = createVertex();
        Class<GraphElement> beanClass = mapper.getBeanClass(vertex);

        assertThat(beanClass, notNullValue());
        assertThat(beanClass, equalTo(AnimalShelter.class));
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_expcetion_when_bean_class_cannot_be_resolved() {
        Vertex vertex = graph.addVertex(T.label, "non_java_class_label", "name", MY_AWESOME_SHELTER);
        mapper.getBeanClass(vertex);
    }

    @Test
    public void should_map_to_bean() throws Exception {
        Vertex vertex = createVertex();
        GraphElement graphElement = mapper.mapToBean(vertex);

        assertThat(graphElement, notNullValue());
        assertThat(graphElement, instanceOf(AnimalShelter.class));

        AnimalShelter animalShelter = (AnimalShelter) graphElement;
        assertThat(animalShelter.getId(), notNullValue());
        assertThat(animalShelter.getName(), equalTo(MY_AWESOME_SHELTER));
    }

    @Test
    public void should_map_to_null_when_null_element_is_provided() throws Exception {
        GraphElement graphElement = mapper.mapToBean(null);
        assertThat(graphElement, nullValue());
    }

    @Test
    public void should_map_to_key_values() throws Exception {
        AnimalShelter animalShelter = new AnimalShelter(null, MY_AWESOME_SHELTER);
        Object[] keyValues = mapper.mapToKeyValues(animalShelter, AnimalShelter.class.getName());

        assertThat(keyValues, notNullValue());
        assertThat(keyValues.length, greaterThan(0));
        assertThat(keyValues.length % 2, equalTo(0));

        assertThat(arrayContains(keyValues, "name"), equalTo(true));
        assertThat(arrayContains(keyValues, T.label), equalTo(true));
        assertThat(arrayContains(keyValues, T.id), equalTo(graph.features().vertex().supportsCustomIds()));
    }

    private boolean arrayContains(Object[] array, Object value) {
        return Arrays.stream(array)
                .filter(o -> o.equals(value))
                .count() > 0;
    }

    private Vertex createVertex() {
        return graph.addVertex(T.label, AnimalShelter.class.getName(), "name", MY_AWESOME_SHELTER);
    }
}
