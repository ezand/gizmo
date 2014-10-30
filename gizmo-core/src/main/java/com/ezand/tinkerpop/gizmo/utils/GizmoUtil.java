package com.ezand.tinkerpop.gizmo.utils;

import static com.ezand.tinkerpop.gizmo.utils.Exceptions.beanNotManageableException;
import static java.lang.System.arraycopy;

import java.util.Map;

import com.ezand.tinkerpop.gizmo.structure.GizmoElement;
import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.structure.Element;

public class GizmoUtil {
    @SuppressWarnings("unchecked")
    public static <B> Map<String, Object> getChanges(B bean) {
        return getManageable(bean).$getPropertyChanges();
    }

    @SuppressWarnings({"unchecked", "UnusedParameters"})
    public static <B, ID> ID getId(B bean, Class<ID> idClass) {
        return (ID) getManageable(bean).$getId();
    }

    public static <B> Element getElement(B bean) {
        return getManageable(bean).$getElement();
    }

    public static <B> boolean isManageable(B bean) {
        return bean instanceof GizmoElement;
    }

    public static <B> boolean isManaged(B bean) {
        return isManageable(bean) && getManageable(bean).$getElement() != null;
    }

    public static <B> void assertManageableBean(B bean) {
        if (!isManageable(bean)) {
            throw beanNotManageableException(bean.getClass());
        }
    }

    public static <B> GizmoElement getManageable(B bean) {
        return (GizmoElement) bean;
    }

    public static Object[] prependLabelArguments(Object[] keyValues, String label) {
        Object[] arguments = new Object[keyValues.length + 2];
        arguments[0] = T.label;
        arguments[1] = label;
        arraycopy(keyValues, 0, arguments, 2, keyValues.length);
        return arguments;
    }
}
