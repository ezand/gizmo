package com.ezand.tinkerpop.repository.utils;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.google.common.collect.Lists;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Property;

public class ReflectionUtils {
    public static Object[] getConstructorArguments(Element element, ConstructorProperties constructorProperties) {
        List<Object> args = Lists.newArrayList(element.id());
        Arrays.stream(constructorProperties.value())
                .forEach(key -> {
                    Property<Object> property = element.property(key);
                    if (property.isPresent()) {
                        args.add(property.value());
                    }
                });
        return args.toArray();
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
}
