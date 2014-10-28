package com.ezand.tinkerpop.repository;

import static com.sun.tools.javac.tree.JCTree.JCAnnotation;
import static com.sun.tools.javac.tree.JCTree.JCExpression;
import static com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import static com.sun.tools.javac.util.List.nil;
import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.JavacHandlerUtil.injectField;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

import com.sun.tools.javac.util.List;

/**
 * Inspired by <a href="https://github.com/alexruiz/dw-lombok">Alex Ruiz' dw-lombok</a>
 */
@Accessors(fluent = true)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldBuilder {
    private String name;
    private String type;
    private JCExpression defaultValue;
    private List<JCAnnotation> annotations;
    private long modifiers;

    public FieldBuilder annotations(JCAnnotation... annotations) {
        this.annotations = List.from(annotations);
        return this;
    }

    public JavacNode injectInto(JavacNode typeNode) {
        return injectField(typeNode, buildWith(typeNode));
    }

    public JCVariableDecl buildWith(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        return maker.VarDef(
                maker.Modifiers(modifiers, annotations != null ? annotations : nil()),
                typeNode.toName(name),
                chainDotsString(typeNode, type),
                defaultValue);
    }

    public static FieldBuilder newField() {
        return new FieldBuilder();
    }
}
