package com.ezand.tinkerpop.repository.mapper;

import static com.ezand.tinkerpop.repository.Configuration.PROPERTY_INSTANCE_RESOLVER;
import static com.ezand.tinkerpop.repository.Exceptions.beanInfoException;
import static com.ezand.tinkerpop.repository.Exceptions.invalidOrMissingJavaClassInformation;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.createInstance;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.invokeBeanMethod;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.XSlf4j;

import com.ezand.tinkerpop.repository.Configuration;
import com.ezand.tinkerpop.repository.resolver.InstanceResolver;
import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tinkerpop.gremlin.structure.Element;

@XSlf4j
public class BeanMapper {
    private final InstanceResolver instanceResolver;

    public BeanMapper() {
        instanceResolver = createInstance(Configuration.getProperty(PROPERTY_INSTANCE_RESOLVER), InstanceResolver.class);
    }

    @SuppressWarnings("unchecked")
    public <B extends GraphElement> B mapToBean(Element element) {
        if (element == null) {
            log.warn("Graph element was null, so will return null as well");
            return null;
        }

        Class<B> beanClass = getBeanClass(element);

        Map<String, Object> properties = Maps.asMap(element.keys(), element::property)
                .entrySet()
                .stream()
                .filter(e -> e.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().value()));
        properties.put("id", element.id());

        return instanceResolver.resolve(beanClass, properties);
    }

    public <B extends GraphElement> Object[] mapToKeyValues(B bean, String label) {
        try {
            Set<String> ignoredProperties = Sets.newHashSet("class", "id");

            // TODO check if id assignment is allowed by graph
            // TODO map to key value array
            // TODO add java class as property
            List<Object> arguments = Lists.newArrayList();
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            Arrays.stream(beanInfo.getPropertyDescriptors())
                    .filter(pd -> !ignoredProperties.contains(pd.getName()))
                    .forEach(pd -> {
                        arguments.add(pd.getName());
                        arguments.add(invokeBeanMethod(bean, pd.getReadMethod()));
                    });

            return arguments.toArray();
        } catch (IntrospectionException e) {
            throw beanInfoException(bean);
        }
    }

    @SuppressWarnings("unchecked")
    public <B extends GraphElement> Class<B> getBeanClass(Element element) {
        try {
            return (Class<B>) Class.forName(element.label());
        } catch (ClassNotFoundException e) {
            throw invalidOrMissingJavaClassInformation(element);
        }
    }
}
