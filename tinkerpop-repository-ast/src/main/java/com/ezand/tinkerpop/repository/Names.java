/*
 * Created on Dec 1, 2010
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright @2010 the original author or authors.
 */
package com.ezand.tinkerpop.repository;

/**
 * Inspired by <a href="https://github.com/alexruiz/dw-lombok">Alex Ruiz' dw-lombok</a>
 */
public final class Names {
    /**
     * Splits the name of the class using "\." as the regular expression. For example, {@code java.lang.String} will be
     * split into { "java", "lang", "String" }.
     *
     * @param type the given class.
     * @return the name of the type split using "\." as the regular expression.
     */
    public static String[] splitNameOf(Class<?> type) {
        return type.getName().split("\\.");
    }

    public static final String CONSTRUCTOR_NAME = "<init>";

    // Prefixes
    public static final String PREFIX_METHOD = "$";
    public static final String PREFIX_FIELD = "$";

    // Field names
    public static final String FIELD_NAME_ID = PREFIX_FIELD + "id";
    public static final String FIELD_NAME_PROPERTY_CHANGES = PREFIX_FIELD + "propertyChanges";
    public static final String FIELD_NAME_ELEMENT = PREFIX_FIELD + "element";

    // Method names
    public static final String METHOD_NAME_TO_KEY_VALUES = PREFIX_METHOD + "toKeyValues";
    public static final String METHOD_NAME_GET_ID = PREFIX_METHOD + "getId";
    public static final String METHOD_NAME_GET_PROPERTY_CHANGES = PREFIX_METHOD + "getPropertyChanges";
    public static final String METHOD_NAME_GET_ELEMENT = PREFIX_METHOD + "getElement";

    // Annotation parameter names
    public static final String ANNOTATION_PARAMETER_NAME_ID_CLASS = "idClass";

    // Variables names
    public static final String VARIABLE_NAME_ARGUMENTS = "arguments";

    // Parameter names
    public static final String PARAMETER_NAME_ELEMENT = "element";
}
