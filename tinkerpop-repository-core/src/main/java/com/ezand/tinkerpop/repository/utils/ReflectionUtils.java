package com.ezand.tinkerpop.repository.utils;

import static com.ezand.tinkerpop.repository.utils.Exceptions.beanDescriptorException;
import static com.ezand.tinkerpop.repository.utils.Exceptions.classLoadingException;
import static com.ezand.tinkerpop.repository.utils.Exceptions.instantiationException;
import static com.ezand.tinkerpop.repository.utils.Exceptions.methodInvocationException;

import java.beans.ConstructorProperties;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.google.common.collect.Lists;

public class ReflectionUtils {
    public static Object[] getConstructorArguments(Map<String, Optional<?>> properties, ConstructorProperties constructorProperties) {
        List<Object> arguments = Lists.newArrayList();
        Arrays.stream(constructorProperties.value())
                .forEach(key -> {
                    Optional<?> optional = properties.get(key);
                    arguments.add(optional != null ? optional.orElse(null) : null);
                });
        return arguments.toArray();
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation, B extends GraphElement> Constructor<B> getConstructor(Class<B> beanClass, Class<A> annotationClass) {
        return (Constructor<B>) Arrays.stream(beanClass.getDeclaredConstructors())
                .filter(c -> c.getDeclaredAnnotation(annotationClass) != null)
                .distinct()
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public static <B extends GraphElement> ConstructorProperties getConstructorProperties(Constructor<B> constructor) {
        return constructor.getDeclaredAnnotation(ConstructorProperties.class);
    }

    public static <B extends GraphElement> Object invokeBeanMethod(B bean, Method method, Object... arguments) {
        try {
            return method.invoke(bean, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw methodInvocationException(method, bean, arguments);
        }
    }

    public static Object createInstance(String className) {
        try {
            return loadClass(className).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw instantiationException(className);
        }
    }

    public static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw instantiationException(clazz);
        }
    }

    public static <T> T createInstance(Constructor<T> constructor, Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw instantiationException(constructor.getDeclaringClass().getName());
        }
    }

    public static <B extends GraphElement> PropertyDescriptor[] getPropertyDescriptors(Class<B> beanClass) {
        try {
            List<PropertyDescriptor> propertyDescriptors = Arrays.stream(Introspector.getBeanInfo(beanClass).getPropertyDescriptors())
                    .filter(pd -> !pd.getName().equals("class"))
                    .collect(Collectors.toList());
            return propertyDescriptors.toArray(new PropertyDescriptor[propertyDescriptors.size()]);
        } catch (IntrospectionException e) {
            throw beanDescriptorException(beanClass);
        }
    }

    public static <B extends GraphElement> Set<String> getPropertyKeys(Class<B> beanClass) {
        PropertyDescriptor[] pd = ReflectionUtils.getPropertyDescriptors(beanClass);
        return Arrays.stream(pd)
                .map(FeatureDescriptor::getName)
                .collect(Collectors.toSet());
    }

    public static <B> B newInstance(Class<B> beanClass) {
        try {
            return beanClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw classLoadingException(beanClass.getName());
        }
    }

    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw classLoadingException(className);
        }
    }
}
