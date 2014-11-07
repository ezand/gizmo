package com.ezand.tinkerpop.gizmo.structure;

import java.lang.reflect.Method;

import lombok.Value;

@Value
public class RelationshipAccessors {
    private FetchMode fetchMode;
    private Cascade cascade;
    private Method read;
    private Method write;
}
