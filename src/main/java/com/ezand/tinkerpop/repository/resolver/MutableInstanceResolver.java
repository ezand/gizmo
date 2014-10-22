package com.ezand.tinkerpop.repository.resolver;

import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.createInstance;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getPropertyDescriptors;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.invokeBeanMethod;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Map;

import com.ezand.tinkerpop.repository.structure.GraphElement;

public class MutableInstanceResolver implements InstanceResolver {
    @Override
    public <B extends GraphElement> B resolve(Class<B> beanClass, Map<String, Object> properties) {
        B instance = createInstance(beanClass);

        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(beanClass);
        Arrays.stream(propertyDescriptors)
                .forEach(pd -> {
                    Object value = properties.get(pd.getName());
                    if (value != null) {
                        invokeBeanMethod(instance, pd.getWriteMethod(), value);
                    }
                });

        return instance;
    }
}
