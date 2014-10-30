package com.ezand.tinkerpop.gizmo.utils;

import static java.lang.String.format;

import com.ezand.tinkerpop.gizmo.structure.GizmoElement;

public class Exceptions {

    public static <T> RuntimeException instantiationException(Class<T> clazz) {
        return instantiationException(clazz.getName());
    }

    public static RuntimeException instantiationException(String beanClass) {
        return new RuntimeException(format("An error occurred while trying to create instance of class '%s'", beanClass));
    }

    public static <T> RuntimeException elementConstructorNotFoundException(Class<T> clazz) {
        return new RuntimeException(format("Element constructor not found for class %s", clazz.getName()));
    }

    public static <T> RuntimeException beanNotManageableException(Class<T> clazz) {
        return new RuntimeException(format("Bean with class %s does not implement %s", clazz.getName(), GizmoElement.class.getSimpleName()));
    }

    public static <T> RuntimeException beanNotManagedException() {
        return new RuntimeException("The specified bean has is not managed yet");
    }
}
