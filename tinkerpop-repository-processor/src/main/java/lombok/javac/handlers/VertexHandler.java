package lombok.javac.handlers;

import static com.ezand.tinkerpop.repository.ASTUtil.findSetter;
import static com.ezand.tinkerpop.repository.ASTUtil.getAccessorFields;
import static com.ezand.tinkerpop.repository.ASTUtil.getAnnotationParameterValue;
import static com.ezand.tinkerpop.repository.ASTUtil.getDefaultASTValue;
import static com.ezand.tinkerpop.repository.ASTUtil.getFields;
import static com.ezand.tinkerpop.repository.ASTUtil.getThisField;
import static com.ezand.tinkerpop.repository.ASTUtil.getterExists;
import static com.ezand.tinkerpop.repository.FieldBuilder.newField;
import static com.ezand.tinkerpop.repository.MethodBuilder.newMethod;
import static com.ezand.tinkerpop.repository.Names.ANNOTATION_PARAMETER_NAME_ID_CLASS;
import static com.ezand.tinkerpop.repository.Names.CONSTRUCTOR_NAME;
import static com.ezand.tinkerpop.repository.Names.FIELD_NAME_ELEMENT;
import static com.ezand.tinkerpop.repository.Names.FIELD_NAME_ID;
import static com.ezand.tinkerpop.repository.Names.FIELD_NAME_PROPERTY_CHANGES;
import static com.ezand.tinkerpop.repository.Names.METHOD_NAME_GET_ELEMENT;
import static com.ezand.tinkerpop.repository.Names.METHOD_NAME_GET_ID;
import static com.ezand.tinkerpop.repository.Names.METHOD_NAME_GET_PROPERTY_CHANGES;
import static com.ezand.tinkerpop.repository.Names.METHOD_NAME_TO_KEY_VALUES;
import static com.ezand.tinkerpop.repository.Names.PARAMETER_NAME_ELEMENT;
import static com.ezand.tinkerpop.repository.Names.VARIABLE_NAME_ARGUMENTS;
import static com.ezand.tinkerpop.repository.Names.splitNameOf;
import static com.ezand.tinkerpop.repository.ParameterBuilder.newParameter;
import static com.sun.tools.javac.code.Flags.FINAL;
import static com.sun.tools.javac.code.Flags.PRIVATE;
import static com.sun.tools.javac.code.Flags.PUBLIC;
import static com.sun.tools.javac.code.Type.ClassType;
import static com.sun.tools.javac.tree.JCTree.JCAnnotation;
import static com.sun.tools.javac.tree.JCTree.JCAssign;
import static com.sun.tools.javac.tree.JCTree.JCBlock;
import static com.sun.tools.javac.tree.JCTree.JCClassDecl;
import static com.sun.tools.javac.tree.JCTree.JCExpression;
import static com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import static com.sun.tools.javac.tree.JCTree.JCIdent;
import static com.sun.tools.javac.tree.JCTree.JCIf;
import static com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import static com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import static com.sun.tools.javac.tree.JCTree.JCNewClass;
import static com.sun.tools.javac.tree.JCTree.JCReturn;
import static com.sun.tools.javac.tree.JCTree.JCStatement;
import static com.sun.tools.javac.tree.JCTree.JCTypeApply;
import static com.sun.tools.javac.tree.JCTree.JCTypeCast;
import static com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import static com.sun.tools.javac.util.List.nil;
import static java.util.stream.Collectors.toSet;
import static lombok.javac.handlers.JavacHandlerUtil.chainDots;
import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

import org.mangosdk.spi.ProviderFor;

import com.ezand.tinkerpop.repository.annotations.Vertex;
import com.ezand.tinkerpop.repository.structure.GizmoElement;
import com.ezand.tinkerpop.repository.structure.PropertyChanges;
import com.google.common.collect.Lists;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Property;

@ProviderFor(JavacAnnotationHandler.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class VertexHandler extends JavacAnnotationHandler<Vertex> {

    @Override
    public void handle(AnnotationValues<Vertex> annotation, JCAnnotation ast, JavacNode annotationNode) {
        JavacNode typeNode = annotationNode.up();
        JCAnnotation annotationAst = (JCAnnotation) annotationNode.get();

        // Annotation parameter values
        ClassType idClass = (ClassType) getAnnotationParameterValue(annotationAst, ANNOTATION_PARAMETER_NAME_ID_CLASS);

        // Modify the AST
        implementInterfaces(typeNode, idClass);
        createPropertyChangesField(typeNode);
        modifySetters(typeNode);
        createGetPropertyChangesMethod(typeNode);
        createToKeyValuesMethod(typeNode);
        JavacNode idFieldNode = createIdField(typeNode, idClass);
        createElementField(typeNode);
        createGetElementMethod(typeNode);
        createConstructor(typeNode, idFieldNode);
        createGetIdMethod(typeNode, idClass, idFieldNode);

        JCClassDecl type = (JCClassDecl) annotationNode.up().get();
        java.util.List<JCAnnotation> annotations = filterOut(type.mods.annotations, Vertex.class);
        type.mods.annotations = List.from(annotations);

        System.out.println(typeNode);
    }

    private java.util.List<JCAnnotation> filterOut(List<JCAnnotation> annotations, Class<? extends Annotation> vertexClass) {
        return annotations
                .stream()
                .filter(a -> !a.type.toString().equals(vertexClass.getName()))
                .collect(Collectors.toList());
    }

    /////////////////////////////
    // AST manipulating - TYPE //
    /////////////////////////////
    private void implementInterfaces(JavacNode typeNode, ClassType idClass) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCClassDecl classDeclaration = (JCClassDecl) typeNode.get();

        // GraphElement
        JCExpression graphElement = chainDots(typeNode, splitNameOf(GizmoElement.class));

        // GraphElement<idClass>
        JCTypeApply typedGraphElement = maker.TypeApply(graphElement, List.of(
                chainDotsString(typeNode, idClass.toString())
        ));

        Set<JCExpression> interfaces = classDeclaration.getImplementsClause()
                .stream()
                .collect(toSet());
        interfaces.add(typedGraphElement);

        classDeclaration.implementing = List.from(interfaces.toArray(new JCExpression[interfaces.size()]));
    }

    private void createConstructor(JavacNode typeNode, JavacNode idFieldNode) {
        newMethod()
                .name(CONSTRUCTOR_NAME)
                .modifiers(PUBLIC)
                .parameters(List.of(
                        newParameter()
                                .type(Element.class.getName())
                                .name("element")
                                .isFinal(true)
                                .buildWith(typeNode)
                ))
                .body(createConstructorBody(typeNode, idFieldNode))
                .returnType(Void.class.getName())
                .injectInto(typeNode);
    }

    ///////////////////////////////
    // AST manipulating - FIELDS //
    ///////////////////////////////
    private void createPropertyChangesField(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        newField()
                .name(FIELD_NAME_PROPERTY_CHANGES)
                .type(PropertyChanges.class.getName())
                .modifiers(PRIVATE | FINAL)
                .defaultValue(
                        maker.NewClass(null, nil(), chainDots(typeNode, splitNameOf(PropertyChanges.class)), nil(), null)
                )
                .injectInto(typeNode);
    }

    private JavacNode createIdField(JavacNode typeNode, ClassType idClass) {
        return newField()
                .name(FIELD_NAME_ID)
                .modifiers(PRIVATE)
                .type(idClass == null ? Object.class.getName() : idClass.toString())
                .injectInto(typeNode);
    }

    private void createElementField(JavacNode typeNode) {
        newField()
                .name(FIELD_NAME_ELEMENT)
                .modifiers(PRIVATE)
                .type(Element.class.getName())
                .injectInto(typeNode);
    }

    ////////////////////////////////
    // AST manipulating - METHODS //
    ////////////////////////////////
    private void createGetPropertyChangesMethod(JavacNode typeNode) {
        newMethod()
                .modifiers(PUBLIC)
                .name(METHOD_NAME_GET_PROPERTY_CHANGES)
                .returnType(Map.class.getName()) // TODO add type info
                .returnTypeTypeArgs(List.of(String.class, Object.class))
                .body(createGetPropertyChangesBody(typeNode))
                .injectInto(typeNode);
    }

    private void createToKeyValuesMethod(JavacNode typeNode) {
        newMethod()
                .modifiers(PUBLIC)
                .name(METHOD_NAME_TO_KEY_VALUES)
                .returnType(Object.class.getName())
                .returnTypeArray(true)
                .body(createToKeyValuesBody(typeNode))
                .injectInto(typeNode);
    }

    private void createGetIdMethod(JavacNode typeNode, ClassType idClass, JavacNode fieldNode) {
        newMethod()
                .modifiers(PUBLIC | FINAL)
                .name(METHOD_NAME_GET_ID)
                .returnType(idClass == null ? Object.class.getName() : idClass.toString())
                .body(createGetIdBody(typeNode, fieldNode))
                .injectInto(typeNode);
    }

    private void createGetElementMethod(JavacNode typeNode) {
        newMethod()
                .modifiers(PUBLIC)
                .name(METHOD_NAME_GET_ELEMENT)
                .returnType(Element.class.getName())
                .body(createGetElementBody(typeNode))
                .injectInto(typeNode);
    }

    /////////////////////////////////////////////
    // AST manipulating - METHOD MODIFICATIONS //
    /////////////////////////////////////////////
    private void modifySetters(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        getFields(typeNode)
                .stream()
                .forEach(f -> {
                    JavacNode setterMethod = findSetter(typeNode, f);
                    if (setterMethod != null) {
                        JCMethodDecl setter = (JCMethodDecl) setterMethod.get();
                        List<JCStatement> existingStatements = setter.body.stats;
                        JCStatement[] statements = new JCStatement[existingStatements.size() + 1];

                        // $propertyChanges.propertyChange
                        JCFieldAccess propertyChange = maker.Select(maker.Ident(typeNode.toName(FIELD_NAME_PROPERTY_CHANGES)), typeNode.toName("propertyChange"));

                        // $propertyChanges.propertyChange(fieldName, oldValue, newValue)
                        JCMethodInvocation apply = maker.Apply(nil(), propertyChange, List.of(
                                maker.Literal(f.getName()),
                                chainDotsString(typeNode, "this." + f.getName()),
                                maker.Ident(setter.params.get(0).getName())
                        ));
                        statements[0] = maker.Exec(apply);

                        for (int i = 1; i < statements.length; i++) {
                            statements[i] = existingStatements.get(i - 1);
                        }

                        setter.body.stats = List.from(statements);
                    }
                });
    }

    //////////////////////////////////////
    // AST manipulating - BODY CREATION //
    //////////////////////////////////////
    private JCBlock createConstructorBody(JavacNode typeNode, JavacNode idFieldNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        java.util.List<JCStatement> statements = Lists.newArrayList();

        JCTree.JCExpressionStatement assignElement = maker.Exec(maker.Assign(getThisField(typeNode, FIELD_NAME_ELEMENT), maker.Ident(typeNode.toName("element"))));
        statements.add(assignElement);

        // element.id
        JCFieldAccess id = maker.Select(maker.Ident(typeNode.toName("element")), typeNode.toName("id"));

        // ([type]) element.id()
        JCTypeCast castId = maker.TypeCast(((JCVariableDecl) idFieldNode.get()).vartype, maker.Apply(nil(), id, nil()));

        // this.$id = element.id();
        statements.add(maker.Exec(maker.Assign(getThisField(typeNode, idFieldNode.getName()), castId)));

        JCFieldAccess property = maker.Select(maker.Ident(typeNode.toName(PARAMETER_NAME_ELEMENT)), typeNode.toName("property"));
        getAccessorFields(typeNode)
                .stream()
                .forEach(f -> {
                    // this.field
                    JCFieldAccess field = getThisField(typeNode, f.getName());

                    JCVariableDecl fieldDeclaration = (JCVariableDecl) f.get();
                    Name fieldProperty = typeNode.toName(f.getName() + "Property");
                    JCIdent fieldPropertyIdentity = maker.Ident(fieldProperty);

                    // element.property(field)
                    JCMethodInvocation elementProperty = maker.Apply(nil(), property, List.of(maker.Literal(f.getName())));

                    // Property property = element.property(field)
                    JCVariableDecl propertyVariable = maker.VarDef(maker.Modifiers(FINAL), fieldProperty, chainDots(typeNode, splitNameOf(Property.class)), elementProperty);

                    // property.isPresent()
                    JCMethodInvocation isPresent = maker.Apply(nil(), maker.Select(fieldPropertyIdentity, typeNode.toName("isPresent")), nil());

                    // property.value()
                    JCMethodInvocation value = maker.Apply(nil(), maker.Select(fieldPropertyIdentity, typeNode.toName("value")), nil());

                    // (Type) property.value()
                    JCTypeCast cast = maker.TypeCast(fieldDeclaration.vartype, value);

                    // this.field = property.value()
                    JCAssign assignValue = maker.Assign(chainDotsString(typeNode, "this." + f.getName()), cast);

                    // this.field = [default_value_for_type]
                    JCAssign assignDefaultValue = maker.Assign(chainDotsString(typeNode, "this." + f.getName()), getDefaultASTValue(f));

                    // if(property.isPresent() this.field = [value] else this.field = [default_value])
                    JCIf anIf = maker.If(isPresent, maker.Exec(assignValue), maker.Exec(assignDefaultValue));

                    statements.add(propertyVariable);
                    statements.add(anIf);

                    maker.Assign(field, null);
                });

        return maker.Block(0, List.from(statements));
    }

    private JCBlock createGetPropertyChangesBody(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        // this.$propertyChanges
        JCFieldAccess propertyChanges = getThisField(typeNode, FIELD_NAME_PROPERTY_CHANGES);

        // this.$propertyChanges.getChanges
        JCFieldAccess getChanges = maker.Select(propertyChanges, typeNode.toName("getChanges"));

        // this.$propertyChanges.getChanges()
        JCMethodInvocation applyGetChanges = maker.Apply(nil(), getChanges, nil());

        return maker.Block(0, List.of(
                maker.Return(applyGetChanges)
        ));
    }

    private JCBlock createToKeyValuesBody(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        java.util.List<JCStatement> statements = new ArrayList<>();

        // List
        JCExpression list = chainDots(typeNode, splitNameOf(java.util.List.class));

        // List<Object>
        JCTypeApply typedList = maker.TypeApply(list, List.of(chainDots(typeNode, splitNameOf(Object.class))));

        // ArrayList
        JCExpression arrayList = chainDots(typeNode, splitNameOf(ArrayList.class));

        // ArrayList<>
        JCTypeApply typedNewArrayList = maker.TypeApply(arrayList, nil());

        // new ArrayList<>
        JCNewClass newArrayList = maker.NewClass(null, nil(), typedNewArrayList, nil(), null);

        // List<Object> arguments = new ArrayList<>();
        JCVariableDecl argumentsVariable = maker.VarDef(maker.Modifiers(0), typeNode.toName(VARIABLE_NAME_ARGUMENTS), typedList, newArrayList);
        statements.add(argumentsVariable);

        // arguments.add
        JCFieldAccess argumentsAdd = maker.Select(maker.Ident(typeNode.toName(VARIABLE_NAME_ARGUMENTS)), typeNode.toName("add"));

        // TODO add id field to arguments???
        getFields(typeNode)
                .stream()
                .filter(field -> getterExists(typeNode, field))
                .forEach(field -> {
                    // arguments.add("fieldName"); arguments.add(field);
                    JCMethodInvocation addKey = maker.Apply(nil(), argumentsAdd, List.of(maker.Literal(field.getName())));
                    JCMethodInvocation addValue = maker.Apply(nil(), argumentsAdd, List.of(maker.Ident(typeNode.toName(field.getName()))));

                    statements.add(maker.Exec(addKey));
                    statements.add(maker.Exec(addValue));
                });

        // arguments.toArray()
        JCMethodInvocation toArrayCall = maker.Apply(nil(), maker.Select(maker.Ident(typeNode.toName(VARIABLE_NAME_ARGUMENTS)), typeNode.toName("toArray")), nil());

        // return arguments.toArray();
        JCReturn returnStatement = maker.Return(
                maker.Exec(toArrayCall).getExpression()
        );
        statements.add(returnStatement);

        return maker.Block(0, List.from(statements));
    }

    private JCBlock createGetIdBody(JavacNode typeNode, JavacNode fieldNode) {
        JCVariableDecl fieldVariable = (JCVariableDecl) fieldNode.get();
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCReturn idReturn = maker.Return(chainDotsString(typeNode, "this." + fieldVariable.getName().toString()));
        return maker.Block(0, List.of(idReturn));
    }

    private JCBlock createGetElementBody(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        return maker.Block(0, List.of(
                maker.Return(getThisField(typeNode, FIELD_NAME_ELEMENT))
        ));
    }
}
