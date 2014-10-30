package com.ezand.tinkerpop.gizmo.utils;

import static com.ezand.tinkerpop.gizmo.utils.ReflectionUtils.createInstance;

import java.lang.reflect.Constructor;

import com.ezand.tinkerpop.gizmo.structure.GizmoElement;
import com.tinkerpop.gremlin.structure.Element;

public class GizmoMapper {
    public static <B> B map(Element element, Class<B> beanClass) {
        Constructor<B> constructor = ReflectionUtils.findElementConstructor(beanClass);
        return ReflectionUtils.createInstance(constructor, element);
    }

    public static <B> Object[] map(B bean) {
        return ((GizmoElement) bean).$toKeyValues();
    }
}
