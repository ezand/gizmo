package com.ezand.tinkerpop.repository.resolver;

import static com.ezand.tinkerpop.repository.Exceptions.argumentCountMistmatchException;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.createInstance;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getArguments;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructor;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructorProperties;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.Map;

import com.ezand.tinkerpop.repository.structure.GraphElement;

public class ImmutableInstanceResolver implements InstanceResolver {
    @Override
    public <B extends GraphElement> B resolve(Class<B> beanClass, Map<String, Object> properties) {
        Constructor<B> constructor = getConstructor(beanClass, ConstructorProperties.class);
        ConstructorProperties constructorProperties = getConstructorProperties(constructor);
        Object[] constructorArguments = getArguments(properties, constructorProperties);
        if (constructorArguments.length != constructor.getParameterCount()) {
            throw argumentCountMistmatchException(constructorArguments.length, constructor.getParameterCount());
        }
        return createInstance(constructor, constructorArguments);
    }
}
