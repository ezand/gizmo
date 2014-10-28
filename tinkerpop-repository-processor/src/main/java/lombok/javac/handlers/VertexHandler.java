package lombok.javac.handlers;

import static com.ezand.tinkerpop.repository.ASTUtil.findSetter;
import static com.ezand.tinkerpop.repository.ASTUtil.getAccessorFields;
import static com.ezand.tinkerpop.repository.ASTUtil.getAnnotationParameterValue;
import static com.ezand.tinkerpop.repository.ASTUtil.getFields;
import static com.ezand.tinkerpop.repository.ASTUtil.getterExists;
import static com.ezand.tinkerpop.repository.FieldBuilder.newField;
import static com.ezand.tinkerpop.repository.MethodBuilder.newMethod;
import static com.ezand.tinkerpop.repository.Names.ANNOTATION_PARAMETER_NAME_ID_CLASS;
import static com.ezand.tinkerpop.repository.Names.FIELD_NAME_ID;
import static com.ezand.tinkerpop.repository.Names.FIELD_NAME_PROPERTY_CHANGES;
import static com.ezand.tinkerpop.repository.Names.METHOD_NAME_APPLY_ELEMENT;
import static com.ezand.tinkerpop.repository.Names.METHOD_NAME_GET_ID;
import static com.ezand.tinkerpop.repository.Names.METHOD_NAME_TO_KEY_VALUES;
import static com.ezand.tinkerpop.repository.Names.PARAMETER_NAME_ELEMENT;
import static com.ezand.tinkerpop.repository.Names.VARIABLE_NAME_ARGUMENTS;
import static com.ezand.tinkerpop.repository.Names.splitNameOf;
import static com.ezand.tinkerpop.repository.ParameterBuilder.newParameter;
import static com.sun.tools.javac.code.Flags.FINAL;
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

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

import org.mangosdk.spi.ProviderFor;

import com.ezand.tinkerpop.repository.annotations.Vertex;
import com.ezand.tinkerpop.repository.structure.GraphElement;
import com.ezand.tinkerpop.repository.structure.PropertyChanges;
import com.google.common.collect.Lists;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Property;

@ProviderFor(JavacAnnotationHandler.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@HandlerPriority(1)
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
        createToKeyValuesMethod(typeNode);
        JavacNode idFieldNode = createIdField(typeNode, idClass);
        createApplyElementMethod(typeNode, idFieldNode);
        createGetIdMethod(typeNode, idClass, idFieldNode);

        System.out.println(typeNode);
    }

    /////////////////////////////
    // AST manipulating - TYPE //
    /////////////////////////////
    private void implementInterfaces(JavacNode typeNode, ClassType idClass) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCClassDecl classDeclaration = (JCClassDecl) typeNode.get();

        // GraphElement
        JCExpression graphElement = chainDots(typeNode, splitNameOf(GraphElement.class));

        // GraphElement<typeClass, idClass>
        JCTypeApply typedGraphElement = maker.TypeApply(graphElement, List.of(
                chainDotsString(typeNode, typeNode.getName()),
                chainDotsString(typeNode, idClass.toString())
        ));

        Set<JCExpression> interfaces = classDeclaration.getImplementsClause()
                .stream()
                .collect(toSet());
        interfaces.add(typedGraphElement);

        classDeclaration.implementing = List.from(interfaces.toArray(new JCExpression[interfaces.size()]));
    }


    ///////////////////////////////
    // AST manipulating - FIELDS //
    ///////////////////////////////
    private void createPropertyChangesField(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        newField()
                .name(FIELD_NAME_PROPERTY_CHANGES)
                .type(PropertyChanges.class.getName())
                .modifiers(Flags.PRIVATE | FINAL)
                .defaultValue(
                        maker.NewClass(null, nil(), chainDots(typeNode, splitNameOf(PropertyChanges.class)), nil(), null)
                )
                .injectInto(typeNode);
    }

    private JavacNode createIdField(JavacNode typeNode, ClassType idClass) {
        return newField()
                .name(FIELD_NAME_ID)
                .modifiers(Flags.PRIVATE)
                .type(idClass == null ? Object.class.getName() : idClass.toString())
                .injectInto(typeNode);
    }

    ////////////////////////////////
    // AST manipulating - METHODS //
    ////////////////////////////////
    private void createToKeyValuesMethod(JavacNode typeNode) {
        newMethod()
                .modifiers(PUBLIC)
                .name(METHOD_NAME_TO_KEY_VALUES)
                .returnType(Object.class.getName())
                .returnTypeArray(true)
                .body(createToKeyValuesBody(typeNode))
                .injectInto(typeNode);
    }

    private void createApplyElementMethod(JavacNode typeNode, JavacNode idFieldNode) {
        newMethod()
                .modifiers(PUBLIC)
                .name(METHOD_NAME_APPLY_ELEMENT)
                .returnType(Void.class.getName())
                .parameters(List.of(
                        newParameter()
                                .name(PARAMETER_NAME_ELEMENT)
                                .type(Element.class.getName())
                                .isFinal(true)
                                .buildWith(typeNode)
                ))
                .body(createApplyElementBody(typeNode, idFieldNode))
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

    @SuppressWarnings("unchecked")
    private JCBlock createApplyElementBody(JavacNode typeNode, JavacNode idFieldNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        // element.property
        JCFieldAccess property = maker.Select(maker.Ident(typeNode.toName(PARAMETER_NAME_ELEMENT)), typeNode.toName("property"));

        java.util.List<JCStatement> statements = Lists.newArrayList();

        // TODO apply id

        // element.id
        JCFieldAccess id = maker.Select(maker.Ident(typeNode.toName("element")), typeNode.toName("id"));
        JCMethodInvocation apply1 = maker.Apply(nil(), id, nil());

        // (Type) element.id()
        JCTypeCast cast1 = maker.TypeCast(((JCVariableDecl) idFieldNode.get()).vartype, apply1);

        // this.$id = element.id()
        JCAssign assign1 = maker.Assign(chainDotsString(typeNode, "this." + idFieldNode.getName()), cast1);

        statements.add(maker.Exec(assign1));

        getAccessorFields(typeNode)
                .stream()
                .filter(f -> !f.getName().equals("class"))
                .forEach(f -> {
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
                    JCAssign assign = maker.Assign(chainDotsString(typeNode, "this." + f.getName()), cast);

                    // if(property.isPresent())
                    JCIf anIf = maker.If(isPresent, maker.Exec(assign), null);

                    statements.add(propertyVariable);
                    statements.add(anIf);
                });

        return maker.Block(0, List.from(statements));
    }

    private JCBlock createGetIdBody(JavacNode typeNode, JavacNode fieldNode) {
        JCVariableDecl fieldVariable = (JCVariableDecl) fieldNode.get();
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCReturn idReturn = maker.Return(chainDotsString(typeNode, "this." + fieldVariable.getName().toString()));
        return maker.Block(0, List.of(idReturn));
    }
}
