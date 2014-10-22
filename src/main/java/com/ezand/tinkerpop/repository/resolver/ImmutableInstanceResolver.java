package com.ezand.tinkerpop.repository.resolver;

import static com.ezand.tinkerpop.repository.utils.Exceptions.argumentCountMismatchException;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.createInstance;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructor;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructorArguments;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructorProperties;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;

import com.ezand.tinkerpop.repository.structure.GraphElement;

public class ImmutableInstanceResolver implements InstanceResolver {
    @Override
    public <B extends GraphElement> B resolve(Class<B> beanClass, Map<String, Optional<?>> properties) {
        Constructor<B> constructor = getConstructor(beanClass, ConstructorProperties.class);
        ConstructorProperties constructorProperties = getConstructorProperties(constructor);
        Object[] constructorArguments = getConstructorArguments(properties, constructorProperties);
        if (constructorArguments.length != constructor.getParameterCount()) {
            throw argumentCountMismatchException(constructorArguments.length, constructor.getParameterCount());
        }
        return createInstance(constructor, constructorArguments);
    }
}
