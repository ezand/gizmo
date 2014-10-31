package com.ezand.tinkerpop.gizmo.utils;

import static com.ezand.tinkerpop.gizmo.utils.ReflectionUtils.createInstance;
import static com.ezand.tinkerpop.gizmo.utils.ReflectionUtils.findElementConstructor;
import static com.ezand.tinkerpop.gizmo.utils.ReflectionUtils.getDefaultValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Constructor;

import org.junit.Test;

import com.ezand.tinkerpop.gizmo.helpers.beans.AnimalShelter;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class ReflectionUtilsTest {
    @Test
    public void should_get_element_constructor() throws Exception {
        Constructor<AnimalShelter> constructor = findElementConstructor(AnimalShelter.class);
        assertThat(constructor, notNullValue());
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_when_element_constructor_is_not_found() throws Exception {
        findElementConstructor(String.class);
    }

    @Test
    public void should_create_new_instance() throws Exception {
        TinkerGraph graph = TinkerGraph.open();
        Vertex vertex = graph.addVertex("name", "My shelter");

        Constructor<AnimalShelter> constructor = findElementConstructor(AnimalShelter.class);
        AnimalShelter animalShelter = createInstance(constructor, vertex);

        assertThat(animalShelter, notNullValue());
        assertThat(animalShelter.getName(), equalTo("My shelter"));
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_when_constructor_argument_is_missing() throws Exception {
        Constructor<AnimalShelter> constructor = findElementConstructor(AnimalShelter.class);
        createInstance(constructor);
    }

    @Test
    public void should_get_default_value_for_type() throws Exception {
        // Non-primitives
        assertThat(getDefaultValue(null, false), nullValue());
        assertThat(getDefaultValue(AnimalShelter.class.getName(), false), nullValue());
        assertThat(getDefaultValue(String.class.getName(), false), nullValue());
        assertThat(getDefaultValue(Boolean.class.getName(), false), nullValue());
        assertThat(getDefaultValue(Integer.class.getName(), false), nullValue());
        assertThat(getDefaultValue(Long.class.getName(), false), nullValue());
        assertThat(getDefaultValue(Short.class.getName(), false), nullValue());
        assertThat(getDefaultValue(Byte.class.getName(), false), nullValue());
        assertThat(getDefaultValue(Character.class.getName(), false), nullValue());
        assertThat(getDefaultValue(Float.class.getName(), false), nullValue());
        assertThat(getDefaultValue(Double.class.getName(), false), nullValue());

        // Primitives
        assertThat(getDefaultValue(boolean.class.getName(), true), equalTo(false));
        assertThat(getDefaultValue(int.class.getName(), true), equalTo(-1));
        assertThat(getDefaultValue(long.class.getName(), true), equalTo(-1L));
        assertThat(getDefaultValue(short.class.getName(), true), equalTo((short) -1));
        assertThat(getDefaultValue(byte.class.getName(), true), equalTo((byte) -1));
        assertThat(getDefaultValue(char.class.getName(), true), equalTo('\0'));
        assertThat(getDefaultValue(float.class.getName(), true), equalTo(-1.0F));
        assertThat(getDefaultValue(double.class.getName(), true), equalTo(-1.0D));
    }
}
