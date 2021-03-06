package com.ezand.tinkerpop.gizmo;

import static com.ezand.tinkerpop.gizmo.Names.ANNOTATION_PARAMETER_NAME_FETCH_MODE;
import static com.ezand.tinkerpop.gizmo.Names.CONSTRUCTOR_NAME;
import static com.ezand.tinkerpop.gizmo.structure.FetchMode.LAZY;
import static com.ezand.tinkerpop.gizmo.utils.ReflectionUtils.getDefaultValue;
import static com.google.common.collect.Lists.newArrayList;
import static com.sun.tools.javac.code.Symbol.TypeSymbol;
import static com.sun.tools.javac.tree.JCTree.JCAnnotation;
import static com.sun.tools.javac.tree.JCTree.JCClassDecl;
import static com.sun.tools.javac.tree.JCTree.JCExpression;
import static com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import static com.sun.tools.javac.tree.JCTree.JCNewClass;
import static com.sun.tools.javac.tree.JCTree.JCTypeApply;
import static com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import static com.sun.tools.javac.util.List.nil;
import static com.tinkerpop.gremlin.util.StreamFactory.stream;
import static java.util.stream.Collectors.toSet;
import static lombok.core.AST.Kind.FIELD;
import static lombok.core.AST.Kind.METHOD;
import static lombok.javac.Javac.CTC_BOT;
import static lombok.javac.Javac.isPrimitive;
import static lombok.javac.handlers.JavacHandlerUtil.MemberExistsResult.NOT_EXISTS;
import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.JavacHandlerUtil.hasAnnotation;
import static lombok.javac.handlers.JavacHandlerUtil.toGetterName;
import static lombok.javac.handlers.JavacHandlerUtil.toSetterName;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.type.DeclaredType;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

import com.ezand.tinkerpop.gizmo.annotations.Id;
import com.ezand.tinkerpop.gizmo.annotations.Relationship;
import com.ezand.tinkerpop.gizmo.structure.FetchMode;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.util.List;

public class ASTUtil {
    public static boolean getterExists(JavacNode enclosing, JavacNode field) {
        return methodExists(toGetterName(field), enclosing, 0);
    }

    public static boolean setterExists(JavacNode enclosing, JavacNode field) {
        return methodExists(toSetterName(field), enclosing, 1);
    }

    public static boolean methodExists(String methodName, JavacNode node, int parameterCount) {
        return !JavacHandlerUtil.methodExists(methodName, node, parameterCount).equals(NOT_EXISTS);
    }

    public static Object getAnnotationParameterValue(JCAnnotation annotation, String parameterName, Object defaultValue) {
        return annotation.attribute.getElementValues().entrySet()
                .stream()
                .filter(e -> e.getKey().name.toString().equals(parameterName))
                .map(e -> e.getValue().getValue())
                .findFirst()
                .orElse(defaultValue);
    }

    public static Set<JavacNode> getFields(JavacNode typeNode) {
        return stream(typeNode.down())
                .filter(n -> n.getKind().equals(FIELD))
                .collect(toSet());
    }

    public static Set<JavacNode> getAccessorFields(JavacNode typeNode) {
        return stream(typeNode.down())
                .filter(n -> n.getKind().equals(FIELD)
                        && getterExists(typeNode, n)
                        && setterExists(typeNode, n))
                .collect(toSet());
    }

    public static Set<JavacNode> getMethods(JavacNode typeNode) {
        return stream(typeNode.down())
                .filter(n -> n.getKind().equals(METHOD))
                .collect(toSet());
    }

    public static Set<JavacNode> getConstructors(JavacNode typeNode) {
        return stream(typeNode.down())
                .filter(n -> n.getKind().equals(METHOD) && n.getName().equals(CONSTRUCTOR_NAME))
                .collect(toSet());
    }

    public static JavacNode findGetter(JavacNode typeNode, JavacNode fieldNode) {
        return getMethods(typeNode)
                .stream()
                .filter(m -> m.getName().equals(toGetterName(fieldNode)))
                .findFirst()
                .orElse(null);
    }

    public static JavacNode findSetter(JavacNode typeNode, JavacNode fieldNode) {
        return getMethods(typeNode)
                .stream()
                .filter(m -> m.getName().equals(toSetterName(fieldNode)))
                .findFirst()
                .orElse(null);
    }

    public static JCNewClass newClass(JavacNode typeNode, Class<?> clazz, List<Class<?>> types, JCExpression... args) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCExpression expression = JavacHandlerUtil.chainDots(typeNode, Names.splitNameOf(clazz));

        if (types != null && types.isEmpty()) {
            expression = maker.TypeApply(expression, nil());
        }

        return maker.NewClass(null, types == null ? nil() : toExpressions(typeNode, types.toArray(new Class[types.length()])), expression, args != null ? List.from(args) : nil(), null);
    }

    public static JCVariableDecl newVariable(JavacNode typeNode, String variableName, JCExpression initialValue, List<Class<?>> types) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCExpression type = JavacHandlerUtil.chainDots(typeNode, Names.splitNameOf(Set.class));
        return maker.VarDef(maker.Modifiers(0), typeNode.toName(variableName), types == null ? type : typed(typeNode, type, types.toArray(new Class[types.length()])), initialValue);
    }

    public static JCTypeApply typed(JavacNode typeNode, JCExpression target, Class<?>... classes) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        return maker.TypeApply(target, toExpressions(typeNode, classes));
    }

    private static List<JCExpression> toExpressions(JavacNode typeNode, Class<?>... classes) {
        return List.from(Arrays.stream(classes)
                .map(c -> JavacHandlerUtil.chainDots(typeNode, Names.splitNameOf(c)))
                .collect(Collectors.toList()));
    }

    public static JCExpression getDefaultASTValue(JavacNode fieldNode) {
        JavacTreeMaker maker = fieldNode.getTreeMaker();
        JCVariableDecl fieldDeclaration = (JCVariableDecl) fieldNode.get();
        String fieldType = fieldDeclaration.vartype.type.toString();

        Object defaultValue = getDefaultValue(fieldType, isPrimitive(fieldDeclaration.vartype));
        if (defaultValue == null) {
            return maker.Literal(CTC_BOT, null);
        } else {
            return maker.Literal(defaultValue);
        }
    }

    public static JCFieldAccess getThisField(JavacNode typeNode, String fieldName) {
        return typeNode.getTreeMaker().Select(chainDotsString(typeNode, "this"), typeNode.toName(fieldName));
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> void removeAnnotations(JavacNode annotationNode, Class<A>... annotationClasses) {
        JCClassDecl type = (JCClassDecl) annotationNode.up().get();

        java.util.List<JCAnnotation> annotations = newArrayList();
        Arrays.stream(annotationClasses)
                .forEach(c -> annotations.addAll(filterOut(type.mods.annotations, c)));

        type.mods.annotations = List.from(annotations);
    }

    public static java.util.List<JCAnnotation> filterOut(List<JCAnnotation> annotations, Class<? extends Annotation> vertexClass) {
        return annotations
                .stream()
                .filter(a -> !a.type.toString().equals(vertexClass.getName()))
                .collect(Collectors.toList());
    }

    public static JavacNode findIdField(JavacNode typeNode) {
        return getFields(typeNode)
                .stream()
                .filter(n -> hasAnnotation(Id.class, n))
                .findFirst()
                .orElse(null);
    }

    public static boolean isIdField(JavacNode fieldNode, JavacNode idFieldNode) {
        return idFieldNode != null && fieldNode.getName().equals(idFieldNode.getName());
    }

    public static <A extends Annotation> JCAnnotation getAnnotation(List<JCAnnotation> annotations, Class<A> annotation) {
        return annotations.stream()
                .filter(a -> a.annotationType.type.toString().equals(annotation.getTypeName()))
                .findFirst()
                .orElse(null);
    }

    public static boolean isCollection(JavacTypes typesUtil, JavacElements elementUtils, TypeSymbol type) {
        return isAssignable(typesUtil, elementUtils, type, "java.util.Collection");
    }

    public static boolean isList(JavacTypes typesUtil, JavacElements elementUtils, TypeSymbol type) {
        return isAssignable(typesUtil, elementUtils, type, "java.util.List");
    }

    public static boolean isSet(JavacTypes typesUtil, JavacElements elementUtils, TypeSymbol type) {
        return isAssignable(typesUtil, elementUtils, type, "java.util.Set");
    }

    public static Set<JavacNode> getRelationshipFields(JavacNode typeNode) {
        return stream(typeNode.down())
                .filter(f -> hasAnnotation(Relationship.class, f))
                .collect(Collectors.toSet());
    }

    public static Set<JavacNode> getRelationshipFields(JavacNode typeNode, FetchMode fetchMode) {
        return getRelationshipFields(typeNode)
                .stream()
                .filter(f -> {
                    JCAnnotation annotation = getAnnotation(((JCVariableDecl) f.get()).mods.annotations, Relationship.class);
                    return FetchMode.valueOf(getAnnotationParameterValue(annotation, ANNOTATION_PARAMETER_NAME_FETCH_MODE, LAZY).toString()).equals(fetchMode);
                })
                .collect(Collectors.toSet());
    }

    private static boolean isAssignable(JavacTypes typesUtil, JavacElements elementUtils, TypeSymbol type, String collectionInterface) {
        DeclaredType collectionType = typesUtil.getDeclaredType(
                elementUtils.getTypeElement(collectionInterface),
                typesUtil.getWildcardType(null, null));
        return typesUtil.isAssignable(type.asType(), collectionType);
    }
}
