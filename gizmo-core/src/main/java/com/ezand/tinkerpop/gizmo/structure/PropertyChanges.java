package com.ezand.tinkerpop.gizmo.structure;

import java.util.Map;
import java.util.Objects;

import lombok.Getter;

import com.google.common.collect.Maps;

public class PropertyChanges {
    @Getter
    private final Map<String, Object> changes = Maps.newIdentityHashMap();

    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            this.changes.put(propertyName, newValue);
        }
    }

    public void clear() {
        this.changes.clear();
    }
}
