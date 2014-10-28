package com.ezand.tinkerpop.repository.configuration;

import static com.ezand.tinkerpop.repository.utils.ReflectionUtils.loadClass;

import java.util.HashMap;

import lombok.extern.slf4j.XSlf4j;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.ezand.tinkerpop.repository.resolver.InstanceResolver;

// TODO find a good pattern to handle global configuration
@XSlf4j
public class GlobalConfiguration implements MapperConfiguration {
    public static final String DEFAULT_CONFIG_FILE = "/config.properties";

    private Configuration configuration;

    public GlobalConfiguration() {
        this(DEFAULT_CONFIG_FILE);
    }

    public GlobalConfiguration(String configurationFile) {
        try {
            configuration = new PropertiesConfiguration(GlobalConfiguration.class.getResource(configurationFile));
        } catch (Exception e) {
            log.warn("Could not load configuration file, returning default configuration");
            configuration = new MapConfiguration(new HashMap<String, Object>() {{
                put(instanceResolverKey(), defaultInstanceResolverClass().getName());
            }});
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends InstanceResolver> Class<R> getInstanceResolverClass() {
        return (Class<R>) loadClass(configuration.getString(instanceResolverKey()));
    }
}
