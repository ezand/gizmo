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

public class ImmutableInstanceResolverTest {
    public static final String MY_AWESOME_SHELTER = "My awesome shelter";

    private final InstanceResolver resolver = new ImmutableInstanceResolver();

    @Test
    public void should_resolve_instance() throws Exception {
        Map<String, Optional<?>> properties = new HashMap<String, Optional<?>>() {{
            put("id", Optional.of(Long.MAX_VALUE));
            put("name", Optional.of(MY_AWESOME_SHELTER));
        }};
        AnimalShelter animalShelter = resolver.resolve(AnimalShelter.class, properties);

        assertThat(animalShelter, notNullValue());
        assertThat(animalShelter.getId(), notNullValue());
        assertThat(animalShelter.getId(), equalTo(Long.MAX_VALUE));
        assertThat(animalShelter.getName(), equalTo(MY_AWESOME_SHELTER));
    }

    @Test(expected = RuntimeException.class)
    public void should_not_resolve_mutable_bean() throws Exception {
        Map<String, Optional<?>> properties = new HashMap<String, Optional<?>>() {{
            put("id", Optional.of(Long.MAX_VALUE));
            put("name", Optional.of("Doggy"));
            put("bread", Optional.of("Black Shepard"));
        }};
        resolver.resolve(MutableDog.class, properties);
    }
}
