package com.ezand.tinkerpop.repository.mapper;

import static com.ezand.tinkerpop.repository.Exceptions.invalidOrMissingJavaClassInformation;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructor;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructorArguments;
import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.getConstructorProperties;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.ConstructorProperties;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import lombok.extern.slf4j.XSlf4j;

import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.tinkerpop.gremlin.structure.Element;

@XSlf4j
public class BeanMapper {
    @SuppressWarnings("unchecked")
    public static <B extends GraphElement> B mapToBean(Element element) {
        if (element == null) {
            log.warn("Graph element was null, so will return null as well");
            return null;
        }

        try {
            Class<B> beanClass = getBeanClass(element);
            Constructor<B> constructor = getConstructor(beanClass, ConstructorProperties.class);
            ConstructorProperties constructorProperties = getConstructorProperties(constructor);
            Object[] constructorArguments = getConstructorArguments(element, constructorProperties);

            return constructor.newInstance(constructorArguments);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            log.error("An error occurred while creating bean instance of graph element properties", e);
            throw invalidOrMissingJavaClassInformation(element); // TODO
        }
    }


    public static <B extends GraphElement> Object[] mapToVertex(B bean, String label) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            BeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();

        } catch (IntrospectionException e) {
            e.printStackTrace(); // TODO
        }


        // TODO map to key value array
        // TODO add java class as property
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <B extends GraphElement> Class<B> getBeanClass(Element element) {
        try {
            return (Class<B>) Class.forName(element.label());
        } catch (ClassNotFoundException e) {
            throw invalidOrMissingJavaClassInformation(element);
        }
    }
}
