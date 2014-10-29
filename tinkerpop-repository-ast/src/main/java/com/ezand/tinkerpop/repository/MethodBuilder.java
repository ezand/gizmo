package com.ezand.tinkerpop.repository;

import static com.ezand.tinkerpop.repository.ASTUtil.typed;
import static com.sun.tools.javac.tree.JCTree.JCBlock;
import static com.sun.tools.javac.tree.JCTree.JCExpression;
import static com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import static com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import static com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import static com.sun.tools.javac.util.List.nil;
import static lombok.javac.Javac.CTC_VOID;
import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.JavacHandlerUtil.injectMethod;
import static lombok.javac.handlers.JavacHandlerUtil.recursiveSetGeneratedBy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.javac.Javac;
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
public class MethodBuilder {
    private int modifiers;
    private String name;
    private List<JCTypeParameter> typeParameters;
    private List<JCVariableDecl> parameters;
    private String returnType;
    private List<Class<?>> returnTypeTypeArgs;
    private boolean returnTypeArray;
    private List<JCExpression> thrown;
    private JCExpression defaultValue;
    private JCBlock body;

    public void injectInto(JavacNode typeNode) {
        injectMethod(typeNode, buildWith(typeNode));
    }

    public JCMethodDecl buildWith(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        JCExpression returnTypeExpression;
        if (returnType.equals(Void.class.getName())) {
            returnTypeExpression = maker.Type(Javac.createVoidType(maker, CTC_VOID));
        } else {
            if (returnTypeArray) {
                returnTypeExpression = maker.TypeArray(chainDotsString(typeNode, returnType));
            } else {
                returnTypeExpression = chainDotsString(typeNode, returnType);
            }
        }

        if (returnTypeTypeArgs != null && !returnTypeTypeArgs.isEmpty()) {
            returnTypeExpression = typed(typeNode, returnTypeExpression, returnTypeTypeArgs.toArray(new Class[returnTypeTypeArgs.size()]));
        }

        return recursiveSetGeneratedBy(
                maker.MethodDef(maker.Modifiers(modifiers),
                        typeNode.toName(name),
                        returnTypeExpression,
                        typeParameters != null ? typeParameters : nil(),
                        parameters != null ? parameters : nil(),
                        thrown != null ? thrown : nil(),
                        body,
                        defaultValue),
                typeNode.get(), typeNode.getContext());
    }

    public static MethodBuilder newMethod() {
        return new MethodBuilder();
    }
}
