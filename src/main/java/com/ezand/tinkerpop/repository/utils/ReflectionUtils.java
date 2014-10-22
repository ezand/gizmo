package com.ezand.tinkerpop.repository.utils;

import static com.ezand.tinkerpop.repository.Exceptions.instantiationException;
import static com.ezand.tinkerpop.repository.Exceptions.methodInvocationException;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.google.common.collect.Lists;

public class ReflectionUtils {
    public static Object[] getArguments(Map<String, Object> properties, ConstructorProperties constructorProperties) {
        List<Object> arguments = Lists.newArrayList();
        Arrays.stream(constructorProperties.value())
                .forEach(key -> {
                    Object value = properties.get(key);
                    if (value != null) {
                        arguments.add(value);
                    }
                });
        return arguments.toArray();
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation, B extends GraphElement> Constructor<B> getConstructor(Class<B> beanClass, Class<A> annotationClass) {
        return (Constructor<B>) Arrays.stream(beanClass.getDeclaredConstructors())
                .filter(c -> c.getDeclaredAnnotation(annotationClass) != null)
                .distinct()
                .findFirst()
                .get();
    }

    public static <B extends GraphElement> ConstructorProperties getConstructorProperties(Constructor<B> constructor) {
        return constructor.getDeclaredAnnotation(ConstructorProperties.class);
    }

    public static <B extends GraphElement> Object invokeBeanMethod(B bean, Method method, Object... arguments) {
        try {
            return method.invoke(bean, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw methodInvocationException(bean, method);
        }
    }

    @SuppressWarnings({"unchecked", "UnusedParameters"})
    public static <T> T createInstance(String className, Class<T> clazz) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            throw instantiationException(className);
        }
    }

    public static <T> T createInstance(Constructor<T> constructor, Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw instantiationException(constructor.getDeclaringClass().getName());
        }
    }
}
