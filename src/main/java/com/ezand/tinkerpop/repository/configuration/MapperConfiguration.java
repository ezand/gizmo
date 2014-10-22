package com.ezand.tinkerpop.repository.configuration;

import com.ezand.tinkerpop.repository.resolver.ImmutableInstanceResolver;
import com.ezand.tinkerpop.repository.resolver.InstanceResolver;

public interface MapperConfiguration {
    <R extends InstanceResolver> Class<R> getInstanceResolverClass();

    // Default values
    @SuppressWarnings("unchecked")
    default <R extends InstanceResolver> Class<R> defaultInstanceResolverClass() {
        return (Class<R>) ImmutableInstanceResolver.class;
    }

    // Configuration keys
    default String instanceResolverKey() {
        return "instanceResolver";
    }
}
