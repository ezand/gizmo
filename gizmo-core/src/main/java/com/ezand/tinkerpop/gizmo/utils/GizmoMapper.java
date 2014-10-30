package com.ezand.tinkerpop.gizmo.utils;

import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.getManageable;
import static com.ezand.tinkerpop.gizmo.utils.GizmoUtil.prependLabelArguments;
import static com.ezand.tinkerpop.gizmo.utils.ReflectionUtils.createInstance;
import static com.ezand.tinkerpop.gizmo.utils.ReflectionUtils.findElementConstructor;

import java.lang.reflect.Constructor;

import com.tinkerpop.gremlin.structure.Element;

public class GizmoMapper {
    public static <B> B map(Element element, Class<B> beanClass) {
        Constructor<B> constructor = findElementConstructor(beanClass);
        return createInstance(constructor, element);
    }

    public static <B> Object[] map(B bean, String label) {
        return prependLabelArguments(getManageable(bean).$toKeyValues(), label);
    }
}
