package com.ezand.tinkerpop.gizmo.utils;

import static com.ezand.tinkerpop.gizmo.utils.Exceptions.classLoadingException;
import static com.ezand.tinkerpop.gizmo.utils.Exceptions.elementConstructorNotFoundException;
import static com.ezand.tinkerpop.gizmo.utils.Exceptions.instantiationException;
import static com.ezand.tinkerpop.gizmo.utils.Exceptions.methodInvocationException;
import static com.google.common.collect.Maps.newIdentityHashMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

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

    public static <B> B createInstance(Constructor<B> constructor, Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw instantiationException(constructor.getDeclaringClass().getName());
        }
    }

    public static <A extends Annotation> A getFieldAnnotation(Class<?> clazz, String fieldName, Class<A> annotationClass) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.getDeclaredAnnotation(annotationClass);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw classLoadingException(className);
        }
    }

    public static Object invokeMethod(Method method, Object target, Object... arguments) {
        try {
            return method.invoke(target, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw methodInvocationException(method.getDeclaringClass(), method, arguments);
        }
    }

    public static Object getDefaultValue(String type, boolean isPrimitive) {
        if (type == null) return null;
        if (!isPrimitive) return null;
        if (type.equals(boolean.class.getName())) return false;
        if (type.equals(int.class.getName())) return -1;
        if (type.equals(long.class.getName())) return -1L;
        if (type.equals(short.class.getName())) return (short) -1;
        if (type.equals(byte.class.getName())) return (byte) -1;
        if (type.equals(char.class.getName())) return '\0';
        if (type.equals(float.class.getName())) return -1.0F;
        if (type.equals(double.class.getName())) return -1.0D;
        return null;
    }
}
