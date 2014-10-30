package com.ezand.tinkerpop.repository.utils;

import static com.ezand.tinkerpop.repository.utils.Exceptions.beanDescriptorException;
import static com.ezand.tinkerpop.repository.utils.Exceptions.classLoadingException;
import static com.ezand.tinkerpop.repository.utils.Exceptions.elementConstructorNotFoundException;
import static com.ezand.tinkerpop.repository.utils.Exceptions.instantiationException;
import static com.ezand.tinkerpop.repository.utils.Exceptions.methodInvocationException;
import static com.google.common.collect.Maps.newIdentityHashMap;

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

import com.ezand.tinkerpop.repository.structure.GizmoElement;
import com.google.common.collect.Lists;
import com.tinkerpop.gremlin.structure.Element;

public class ReflectionUtils {
    private static final Map<Class<?>, Constructor<?>> ELEMENT_CONSTRUCTOR_CACHE = newIdentityHashMap();

    @SuppressWarnings("unchecked")
    public static <B> Constructor<B> findElementConstructor(Class<B> beanClass) {
        try {
            Constructor<?> cachedConstructor = ELEMENT_CONSTRUCTOR_CACHE.get(beanClass);
            if (cachedConstructor == null) {
                Constructor<B> constructor = beanClass.getDeclaredConstructor(Element.class);
                ELEMENT_CONSTRUCTOR_CACHE.put(beanClass, constructor);
                return constructor;
            } else {
                return (Constructor<B>) cachedConstructor;
            }
        } catch (NoSuchMethodException e) {
            throw elementConstructorNotFoundException(beanClass);
        }
    }

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
    public static <A extends Annotation, B extends GizmoElement> Constructor<B> getConstructor(Class<B> beanClass, Class<A> annotationClass) {
        return (Constructor<B>) Arrays.stream(beanClass.getDeclaredConstructors())
                .filter(c -> c.getDeclaredAnnotation(annotationClass) != null)
                .distinct()
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public static <B extends GizmoElement> ConstructorProperties getConstructorProperties(Constructor<B> constructor) {
        return constructor.getDeclaredAnnotation(ConstructorProperties.class);
    }

    public static <B extends GizmoElement> Object invokeBeanMethod(B bean, Method method, Object... arguments) {
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

    public static <B extends GizmoElement> PropertyDescriptor[] getPropertyDescriptors(Class<B> beanClass) {
        try {
            List<PropertyDescriptor> propertyDescriptors = Arrays.stream(Introspector.getBeanInfo(beanClass).getPropertyDescriptors())
                    .filter(pd -> !pd.getName().equals("class"))
                    .collect(Collectors.toList());
            return propertyDescriptors.toArray(new PropertyDescriptor[propertyDescriptors.size()]);
        } catch (IntrospectionException e) {
            throw beanDescriptorException(beanClass);
        }
    }

    public static <B extends GizmoElement> Set<String> getPropertyKeys(Class<B> beanClass) {
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

    public static Object getDefaultValue(String type, boolean isPrimitive) {
        if (type == null) return null;
        if (!isPrimitive) return null;
        if (type.equals(boolean.class.getName())) return false;
        if (type.equals(int.class.getName())) return -1;
        if (type.equals(long.class.getName())) return -0L;
        if (type.equals(short.class.getName())) return (short) 0;
        if (type.equals(byte.class.getName())) return (byte) 0;
        if (type.equals(char.class.getName())) return '\0';
        if (type.equals(float.class.getName())) return -1.0F;
        if (type.equals(double.class.getName())) return -1.0D;
        return null;
    }
}
