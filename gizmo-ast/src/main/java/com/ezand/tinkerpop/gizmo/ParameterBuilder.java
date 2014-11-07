package com.ezand.tinkerpop.gizmo;

import static com.sun.tools.javac.code.Flags.FINAL;
import static com.sun.tools.javac.code.Flags.PARAMETER;
import static com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

@Accessors(fluent = true)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParameterBuilder {
    private String name;
    private String type;
    private boolean isFinal;

    public JCVariableDecl buildWith(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        long flags = PARAMETER;
        if (isFinal) {
            flags |= FINAL;
        }

        return maker.VarDef(maker.Modifiers(flags), typeNode.toName(name), type != null ? chainDotsString(typeNode, type) : null, null);
    }

    public static ParameterBuilder newParameter() {
        return new ParameterBuilder();
    }
}
