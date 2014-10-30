package com.ezand.tinkerpop.repository.utils;

import static com.ezand.tinkerpop.repository.utils.Exceptions.beanNotManageableException;

import java.util.Map;

import com.ezand.tinkerpop.repository.structure.GizmoElement;
import com.tinkerpop.gremlin.structure.Element;

public class GizmoUtil {
    @SuppressWarnings("unchecked")
    public static <B> Map<String, Object> getChanges(B bean) {
        return ((GizmoElement) bean).$getPropertyChanges();
    }

    @SuppressWarnings({"unchecked", "UnusedParameters"})
    public static <B, ID> ID getId(B bean, Class<ID> idClass) {
        return (ID) ((GizmoElement) bean).$getId();
    }

    public static <B> Element getElement(B bean) {
        return ((GizmoElement) bean).$getElement();
    }

    public static <B> boolean isManageable(B bean) {
        return bean instanceof GizmoElement;
    }

    public static <B> boolean isManaged(B bean) {
        return isManageable(bean) && ((GizmoElement) bean).$getElement() != null;
    }

    public static <B> void assertManageableBean(B bean) {
        if (!isManageable(bean)) {
            throw beanNotManageableException(bean.getClass());
        }
    }
}
