package com.ezand.tinkerpop.repository;

import static java.lang.String.format;

import java.lang.reflect.Method;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.tinkerpop.gremlin.structure.Element;

public class Exceptions {
    public static RuntimeException invalidOrMissingJavaClassInformation(Element element) {
        return new RuntimeException(format("Unable to resolve java class information from graph element label '%s'", element.label()));
    }

    public static <B extends GraphElement> RuntimeException methodInvocationException(B bean, Method method) {
        return new RuntimeException(format("Could not execute method '%s' on bean '%s'", method.getName(), bean.getClass().getName()));
    }

    public static <B extends GraphElement> RuntimeException beanInfoException(B bean) {
        return new RuntimeException(format("An error occurred while getting bean info for bean '%s'", bean.getClass().getName()));
    }

    public static <B extends GraphElement> RuntimeException instantiationException(Class<B> beanClass) {
        return new RuntimeException(format("An error occurred while trying to instantiate bean of class '%s'", beanClass));
    }

    public static <B extends GraphElement> RuntimeException instantiationException(String beanClass) {
        return new RuntimeException(format("An error occurred while trying to instantiate bean of class '%s'", beanClass));
    }

    public static <B extends GraphElement> RuntimeException argumentCountMistmatchException(int actual, int expected) {
        return new RuntimeException(format("Provided augment count %s does not match expected %s", actual, expected));
    }
}
