package com.ezand.tinkerpop.repository.utils;

import static java.lang.String.format;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.ezand.tinkerpop.repository.structure.GraphElement;

public class Exceptions {

    public static <T> RuntimeException instantiationException(Class<T> clazz) {
        return new RuntimeException(format("An error occurred while trying to create instance of class '%s'", clazz));
    }

    public static RuntimeException instantiationException(String beanClass) {
        return new RuntimeException(format("An error occurred while trying to create instance of class '%s'", beanClass));
    }

    public static RuntimeException argumentCountMismatchException(int actual, int expected) {
        return new RuntimeException(format("Provided augment count %s does not match expected %s", actual, expected));
    }

    public static RuntimeException methodInvocationException(Method method, Object instance, Object... arguments) {
        return new RuntimeException(format("An error occurred while executing method %s on %s with arguments %s", method.getName(), instance.getClass().getName(), Arrays.toString(arguments)));
    }

    public static <B extends GraphElement> RuntimeException beanDescriptorException(Class<B> beanClass) {
        return new RuntimeException(format("An error occurred while getting bean descriptors for class %s", beanClass));
    }

    public static RuntimeException classLoadingException(String className) {
        return new RuntimeException(format("Unable to load class %s", className));
    }
}
