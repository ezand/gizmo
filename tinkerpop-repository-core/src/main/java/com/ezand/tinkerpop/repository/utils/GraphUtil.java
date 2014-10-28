package com.ezand.tinkerpop.repository.utils;

import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.newInstance;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.tinkerpop.gremlin.structure.Element;

public class GraphUtil {
    public static <B> B fromElement(Element element, Class<B> beanClass) {
        B bean = newInstance(beanClass);
        ((GraphElement) bean).$applyElement(element);
        return bean;
    }

    public static <B> Object[] getKeyValues(B bean) {
        return ((GraphElement) bean).$toKeyValues();
    }
}
