package com.ezand.tinkerpop.repository.resolver;

import java.util.Map;

import com.ezand.tinkerpop.repository.structure.GraphElement;

public interface InstanceResolver {
    <B extends GraphElement> B resolve(Class<B> beanClass, Map<String, Object> properties);
}
