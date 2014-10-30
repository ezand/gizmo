package com.ezand.tinkerpop.gizmo.utils;

import static java.lang.String.format;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.ezand.tinkerpop.gizmo.structure.GizmoElement;

public class Exceptions {

    public static <T> RuntimeException instantiationException(Class<T> clazz) {
        return new RuntimeException(format("An error occurred while trying to create instance of class '%s'", clazz));
    }

    public static RuntimeException instantiationException(String beanClass) {
        return new RuntimeException(format("An error occurred while trying to create instance of class '%s'", beanClass));
    }

    public static RuntimeException methodInvocationException(Method method, Object instance, Object... arguments) {
        return new RuntimeException(format("An error occurred while executing method %s on %s with arguments %s", method.getName(), instance.getClass().getName(), Arrays.toString(arguments)));
    }

    public static <B extends GizmoElement> RuntimeException beanDescriptorException(Class<B> beanClass) {
        return new RuntimeException(format("An error occurred while getting bean descriptors for class %s", beanClass));
    }

    public static RuntimeException classLoadingException(String className) {
        return new RuntimeException(format("Unable to load class %s", className));
    }

    public static <T> RuntimeException elementConstructorNotFoundException(Class<T> clazz) {
        return new RuntimeException(format("Element constructor not found for class %s", clazz.getName()));
    }

    public static <T> RuntimeException beanNotManageableException(Class<T> clazz) {
        return new RuntimeException(format("Bean with class %s does not implement GraphElement", clazz.getName()));
    }

    public static <T> RuntimeException beanNotManagedException() {
        return new RuntimeException("The specified bean has is not managed yet");
    }
}
