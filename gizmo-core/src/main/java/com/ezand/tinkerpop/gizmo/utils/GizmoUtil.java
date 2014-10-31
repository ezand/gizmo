package com.ezand.tinkerpop.gizmo.utils;

import static com.ezand.tinkerpop.gizmo.utils.Exceptions.beanNotManageableException;
import static com.ezand.tinkerpop.gizmo.utils.Exceptions.invalidArgumentCountException;
import static com.ezand.tinkerpop.gizmo.utils.Exceptions.invalidArgumentKeyException;
import static java.lang.System.arraycopy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.ezand.tinkerpop.gizmo.structure.GizmoElement;
import com.google.common.collect.Sets;
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

    public static Object[] removeEmptyArguments(Object[] arguments) {
        for (int i = 0; i < arguments.length; i += 2) {
            if (arguments[i + 1] == null) {
                arguments[i] = null;
            }
        }

        List<Object> filteredArguments = Arrays.stream(arguments)
                .filter(a -> a != null)
                .collect(Collectors.toList());

        return filteredArguments.toArray(new Object[filteredArguments.size()]);
    }

    public static void validateArguments(Object[] arguments) {
        if (arguments.length % 2 != 0) {
            throw invalidArgumentCountException(arguments.length);
        }

        for (int i = 0; i < arguments.length; i += 2) {
            if (!(arguments[i] instanceof String) && !(arguments[i] instanceof T)) {
                throw invalidArgumentKeyException(arguments[i].getClass());
            }
        }
    }
}
