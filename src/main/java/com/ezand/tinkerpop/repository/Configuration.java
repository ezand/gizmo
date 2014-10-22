package com.ezand.tinkerpop.repository;

import java.util.Properties;

import com.ezand.tinkerpop.repository.resolver.ImmutableInstanceResolver;

public class Configuration {
    public static final String PROPERTY_INSTANCE_RESOLVER = "instanceResolver";

    public static final String DEFAULT_INSTANCE_RESOLVER = ImmutableInstanceResolver.class.getName();

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(Configuration.class.getResourceAsStream("/config.properties"));
        } catch (Exception e) {
            properties.setProperty(PROPERTY_INSTANCE_RESOLVER, DEFAULT_INSTANCE_RESOLVER);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
