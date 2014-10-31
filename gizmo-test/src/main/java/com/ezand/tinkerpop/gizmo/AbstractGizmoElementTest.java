package com.ezand.tinkerpop.gizmo;

import static com.ezand.tinkerpop.gizmo.Names.FIELD_NAME_ELEMENT;
import static com.ezand.tinkerpop.gizmo.Names.FIELD_NAME_ID;
import static com.ezand.tinkerpop.gizmo.Names.FIELD_NAME_PROPERTY_CHANGES;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_GET_ELEMENT;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_GET_ID;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_GET_PROPERTY_CHANGES;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_TO_KEY_VALUES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;

import org.junit.Test;

import com.ezand.tinkerpop.gizmo.structure.GizmoElement;
import com.tinkerpop.gremlin.structure.Element;

public abstract class AbstractGizmoElementTest<E> {
    public abstract Class<E> getClazz();

    @Test
    public void should_implement_interface() throws Exception {
        boolean hasInterface = Arrays.stream(getClazz().getInterfaces())
                .filter(i -> i.getName().equals(GizmoElement.class.getName()))
                .count() == 1L;

        assertThat(hasInterface, equalTo(true));
    }

    @Test
    public void should_have_generated_constructor() throws Exception {
        assertThat(getClazz().getDeclaredConstructor(Element.class), notNullValue());
    }

    @Test
    public void should_have_generated_fields() throws Exception {
        assertThat(getClazz().getDeclaredField(FIELD_NAME_ELEMENT), notNullValue());
        assertThat(getClazz().getDeclaredField(FIELD_NAME_ID), notNullValue());
        assertThat(getClazz().getDeclaredField(FIELD_NAME_PROPERTY_CHANGES), notNullValue());
    }

    @Test
    public void should_have_generated_methods() throws Exception {
        assertThat(getClazz().getDeclaredMethod(METHOD_NAME_GET_ELEMENT), notNullValue());
        assertThat(getClazz().getDeclaredMethod(METHOD_NAME_GET_ID), notNullValue());
        assertThat(getClazz().getDeclaredMethod(METHOD_NAME_GET_PROPERTY_CHANGES), notNullValue());
        assertThat(getClazz().getDeclaredMethod(METHOD_NAME_TO_KEY_VALUES), notNullValue());
    }
}
