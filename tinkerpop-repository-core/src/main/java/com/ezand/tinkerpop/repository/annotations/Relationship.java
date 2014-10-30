package com.ezand.tinkerpop.repository.annotations;

import static com.ezand.tinkerpop.repository.structure.Cascade.ALL;
import static com.ezand.tinkerpop.repository.structure.FetchMode.EAGER;
import static com.tinkerpop.gremlin.structure.Direction.OUT;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ezand.tinkerpop.repository.structure.Cascade;
import com.ezand.tinkerpop.repository.structure.FetchMode;
import com.tinkerpop.gremlin.structure.Direction;

@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface Relationship {
    String label();

    Direction direction() default OUT;

    FetchMode fetchMode() default EAGER;

    Cascade cascade() default ALL;
}
