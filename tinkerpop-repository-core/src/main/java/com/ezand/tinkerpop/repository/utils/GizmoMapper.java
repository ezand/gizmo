package com.ezand.tinkerpop.repository.utils;

import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.createInstance;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.findElementConstructor;

import java.lang.reflect.Constructor;

import com.ezand.tinkerpop.repository.structure.GizmoElement;
import com.tinkerpop.gremlin.structure.Element;

public class GizmoMapper {
    public static <B> B map(Element element, Class<B> beanClass) {
        Constructor<B> constructor = findElementConstructor(beanClass);
        return createInstance(constructor, element);
    }

    public static <B> Object[] map(B bean) {
        return ((GizmoElement) bean).$toKeyValues();
    }
}
