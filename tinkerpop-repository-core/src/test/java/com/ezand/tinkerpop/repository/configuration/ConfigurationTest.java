package com.ezand.tinkerpop.repository.configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import com.ezand.tinkerpop.repository.resolver.ImmutableInstanceResolver;
import com.ezand.tinkerpop.repository.resolver.InstanceResolver;
import com.ezand.tinkerpop.repository.resolver.MutableInstanceResolver;

@SuppressWarnings("unchecked")
public class ConfigurationTest {

    @Test
    public void should_get_default_when_missing_config_file() throws Exception {
        GlobalConfiguration configuration = new GlobalConfiguration("/non_existing.properties");
        assertInstanceResolver(configuration, configuration.defaultInstanceResolverClass());
    }

    @Test
    public void should_load_mutable_instance_resolver_from_config_file() throws Exception {
        GlobalConfiguration configuration = new GlobalConfiguration("/config_mutable.properties");
        assertInstanceResolver(configuration, MutableInstanceResolver.class);
    }

    @Test
    public void should_load_immutable_instance_resolver_from_config_file() throws Exception {
        GlobalConfiguration configuration = new GlobalConfiguration("/config_immutable.properties");
        assertInstanceResolver(configuration, ImmutableInstanceResolver.class);
    }

    private <R extends InstanceResolver> void assertInstanceResolver(GlobalConfiguration configuration, Class<R> instanceResolverClass) {
        Class<R> className = configuration.getInstanceResolverClass();
        assertThat(className, notNullValue());
        assertThat(className, equalTo(instanceResolverClass));
    }
}
