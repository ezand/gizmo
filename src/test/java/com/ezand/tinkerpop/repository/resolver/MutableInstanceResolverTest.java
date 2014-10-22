package com.ezand.tinkerpop.repository.resolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import com.ezand.tinkerpop.repository.helpers.beans.AnimalShelter;
import com.ezand.tinkerpop.repository.helpers.beans.MutableDog;

public class MutableInstanceResolverTest {
    public static final String AWESOME_DOG = "Awesome Dog";
    public static final String FLAT_COATED_RETRIEVER = "Flat Coated Retriever";

    private final InstanceResolver resolver = new MutableInstanceResolver();

    @Test
    public void should_resolve_instance() throws Exception {
        Map<String, Optional<?>> properties = new HashMap<String, Optional<?>>() {{
            put("id", Optional.of(Long.MAX_VALUE));
            put("name", Optional.of(AWESOME_DOG));
            put("animals", Optional.empty());
            put("bread", Optional.of(FLAT_COATED_RETRIEVER));
        }};
        MutableDog dog = resolver.resolve(MutableDog.class, properties);

        assertThat(dog, notNullValue());
        assertThat(dog.getId(), notNullValue());
        assertThat(dog.getId(), equalTo(Long.MAX_VALUE));
        assertThat(dog.getName(), equalTo(AWESOME_DOG));
        assertThat(dog.getBread(), equalTo(FLAT_COATED_RETRIEVER));
    }

    @Test(expected = RuntimeException.class)
    public void should_not_resolve_immutable_bean() throws Exception {
        Map<String, Optional<?>> properties = new HashMap<String, Optional<?>>() {{
            put("id", Optional.of(Long.MAX_VALUE));
            put("name", Optional.of("The shelter shack"));
            put("animals", Optional.empty());
        }};
        resolver.resolve(AnimalShelter.class, properties);
    }
}
