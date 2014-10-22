package com.ezand.tinkerpop.repository.utils;

import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.createInstance;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructor;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructorArguments;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructorProperties;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getPropertyDescriptors;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.invokeBeanMethod;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.loadClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import java.beans.ConstructorProperties;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ezand.tinkerpop.repository.helpers.beans.AnimalShelter;
import com.ezand.tinkerpop.repository.helpers.beans.MutableDog;

public class ReflectionUtilsTest {
    public static final String MY_SHELTER = "My shelter";

    @Test
    public void should_get_constructor() throws Exception {
        Constructor<AnimalShelter> constructor = getConstructor(AnimalShelter.class, ConstructorProperties.class);
        assertThat(constructor, notNullValue());
    }

    @Test
    public void should_get_constructor_properties() throws Exception {
        ConstructorProperties constructorProperties = getConstructorProperties(getConstructor(AnimalShelter.class, ConstructorProperties.class));
        assertThat(constructorProperties, notNullValue());
    }

    @Test
    public void should_get_arguments() throws Exception {
        Constructor<AnimalShelter> constructor = getConstructor(AnimalShelter.class, ConstructorProperties.class);
        ConstructorProperties constructorProperties = getConstructorProperties(constructor);
        Object[] arguments = getConstructorArguments(getArgumentMap(), constructorProperties);

        assertThat(arguments, notNullValue());
        assertThat(arguments.length, equalTo(2));
        assertThat(arguments[0], equalTo(Long.MAX_VALUE));
        assertThat(arguments[1], equalTo(MY_SHELTER));
    }

    @Test
    public void should_invoke_bean_method() throws Exception {
        AnimalShelter animalShelter = new AnimalShelter(null, MY_SHELTER);
        Object name = invokeBeanMethod(animalShelter, AnimalShelter.class.getDeclaredMethod("getName"));

        assertThat(name, notNullValue());
        assertThat(name, equalTo(MY_SHELTER));
    }

    @Test
    public void should_create_instance() throws Exception {
        assertThat(createInstance(MutableDog.class.getName()), notNullValue());
        assertThat(createInstance(MutableDog.class), notNullValue());

        Constructor<AnimalShelter> constructor = getConstructor(AnimalShelter.class, ConstructorProperties.class);
        ConstructorProperties constructorProperties = getConstructorProperties(constructor);
        Object[] constructorArguments = getConstructorArguments(getArgumentMap(), constructorProperties);
        assertThat(createInstance(constructor, constructorArguments), notNullValue());
    }

    @Test
    public void should_get_property_descriptors() throws Exception {
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(MutableDog.class);

        assertThat(propertyDescriptors, notNullValue());
        assertThat(propertyDescriptors.length, greaterThan(0));
    }

    @Test
    public void should_load_class() throws Exception {
        Class<?> clazz = loadClass(AnimalShelter.class.getName());

        assertThat(clazz, notNullValue());
        assertThat(clazz.getName(), equalTo(AnimalShelter.class.getName()));
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_when_class_cannot_be_found() throws Exception {
        loadClass("non.existing.ClassName");
    }

    private Map<String, Object> getArgumentMap() {
        return new HashMap<String, Object>() {{
            put("id", Long.MAX_VALUE);
            put("name", MY_SHELTER);
        }};
    }
}
