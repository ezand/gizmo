package com.ezand.tinkerpop.gizmo.utils;

import static java.lang.String.format;

import java.lang.reflect.Method;

import com.ezand.tinkerpop.gizmo.structure.GizmoElement;
import com.tinkerpop.gremlin.process.T;

public class Exceptions {

    public static <B> RuntimeException instantiationException(Class<B> clazz) {
        return instantiationException(clazz.getName());
    }

    public static RuntimeException instantiationException(String beanClass) {
        return new RuntimeException(format("An error occurred while trying to create instance of class '%s'", beanClass));
    }

    public static <B> RuntimeException elementConstructorNotFoundException(Class<B> clazz) {
        return new RuntimeException(format("Element constructor not found for class %s", clazz.getName()));
    }

    public static <B> RuntimeException beanNotManageableException(Class<B> clazz) {
        return new RuntimeException(format("Bean with class %s does not implement %s", clazz.getName(), GizmoElement.class.getSimpleName()));
    }

    public static RuntimeException beanNotManagedException() {
        return new RuntimeException("The specified bean has is not managed yet");
    }

    public static RuntimeException invalidArgumentCountException(int argumentCount) {
        return new RuntimeException(format("Argument count must be an even number, actual count is %s", argumentCount));
    }

    public static RuntimeException invalidArgumentKeyException(Class<?> clazz) {
        return new RuntimeException(format("Argument key must be an instance of %s or %s, actual type is %s", String.class.getName(), T.class.getName(), clazz.toString()));
    }

    public static RuntimeException beanInspectionException(Class<?> clazz) {
        return new RuntimeException(format("An error occurred while getting bean description for class %s", clazz.getName()));
    }

    public static RuntimeException methodInvocationException(Class<?> clazz, Method method, Object... arguments) {
        return new RuntimeException(format("Could not execute method %s on class %s with arguments %s", method.getName(), clazz.getName(), arguments));
    }

    public static RuntimeException classLoadingException(String className) {
        return new RuntimeException(format("Unable to load class %s", className));
    }
}
