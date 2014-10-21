package com.ezand.tinkerpop.repository;

import static java.lang.String.format;

import com.tinkerpop.gremlin.structure.Element;

public class Exceptions {
    public static RuntimeException invalidOrMissingJavaClassInformation(Element element) {
        return new RuntimeException(format("Unable to resolve java class information from graph element label '%s'", element.label()));
    }
}
