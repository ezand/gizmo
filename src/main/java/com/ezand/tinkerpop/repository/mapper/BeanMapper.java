package com.ezand.tinkerpop.repository.mapper;

import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.createInstance;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getPropertyDescriptors;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.invokeBeanMethod;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.loadClass;
import static com.tinkerpop.gremlin.structure.Graph.Features;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.XSlf4j;

import com.ezand.tinkerpop.repository.configuration.GlobalConfiguration;
import com.ezand.tinkerpop.repository.configuration.MapperConfiguration;
import com.ezand.tinkerpop.repository.resolver.InstanceResolver;
import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.structure.Element;

@XSlf4j
public class BeanMapper {
    private final MapperConfiguration configuration;
    private final InstanceResolver instanceResolver;
    private final Features graphFeatures;

    public BeanMapper(Features graphFeatures) {
        this.configuration = new GlobalConfiguration();
        this.instanceResolver = createInstance(configuration.getInstanceResolverClass());
        this.graphFeatures = graphFeatures;
    }

    @SuppressWarnings("unchecked")
    public <B extends GraphElement> B mapToBean(Element element) {
        if (element == null) {
            log.warn("Graph element was null, so will return null as well");
            return null;
        }

        Class<B> beanClass = getBeanClass(element);

        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(beanClass);
        Set<String> propertyNames = Arrays.stream(propertyDescriptors).map(FeatureDescriptor::getName).collect(Collectors.toSet());

        Map<String, Optional<?>> properties = Maps.toMap(propertyNames, element::property)
                .entrySet()
                .stream()
                .filter(e -> e != null)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().isPresent() ? Optional.of(e.getValue().value()) : Optional.empty()));
        properties.put("id", Optional.of(element.id()));

        return instanceResolver.resolve(beanClass, properties);
    }

    public <B extends GraphElement> Object[] mapToKeyValues(B bean, String label) {
        Set<String> ignoredProperties = Sets.newHashSet("class");
        List<Object> arguments = Lists.newArrayList();
        if (graphFeatures.vertex().supportsCustomIds()) {
            arguments.add(T.id);
            arguments.add(bean.getId());
        } else {
            ignoredProperties.add("id");
        }

        Arrays.stream(getPropertyDescriptors(bean.getClass()))
                .filter(pd -> !ignoredProperties.contains(pd.getName()))
                .forEach(pd -> {
                    Object value = invokeBeanMethod(bean, pd.getReadMethod());
                    if (value != null) {
                        arguments.add(pd.getName());
                        arguments.add(value);
                    }
                });

        arguments.add(T.label);
        arguments.add(label);

        return arguments.toArray();
    }

    @SuppressWarnings("unchecked")
    public <B extends GraphElement> Class<B> getBeanClass(Element element) {
        return (Class<B>) loadClass(element.label());
    }
}
