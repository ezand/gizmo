package lombok.javac.handlers;

import static com.ezand.tinkerpop.gizmo.ASTUtil.findGetter;
import static com.ezand.tinkerpop.gizmo.ASTUtil.findIdField;
import static com.ezand.tinkerpop.gizmo.ASTUtil.findSetter;
import static com.ezand.tinkerpop.gizmo.ASTUtil.getAccessorFields;
import static com.ezand.tinkerpop.gizmo.ASTUtil.getAnnotation;
import static com.ezand.tinkerpop.gizmo.ASTUtil.getAnnotationParameterValue;
import static com.ezand.tinkerpop.gizmo.ASTUtil.getDefaultASTValue;
import static com.ezand.tinkerpop.gizmo.ASTUtil.getFields;
import static com.ezand.tinkerpop.gizmo.ASTUtil.getRelationshipFields;
import static com.ezand.tinkerpop.gizmo.ASTUtil.getThisField;
import static com.ezand.tinkerpop.gizmo.ASTUtil.getterExists;
import static com.ezand.tinkerpop.gizmo.ASTUtil.isCollection;
import static com.ezand.tinkerpop.gizmo.ASTUtil.isIdField;
import static com.ezand.tinkerpop.gizmo.ASTUtil.isList;
import static com.ezand.tinkerpop.gizmo.ASTUtil.typed;
import static com.ezand.tinkerpop.gizmo.FieldBuilder.newField;
import static com.ezand.tinkerpop.gizmo.MethodBuilder.newMethod;
import static com.ezand.tinkerpop.gizmo.Names.ANNOTATION_PARAMETER_NAME_CASCADE;
import static com.ezand.tinkerpop.gizmo.Names.ANNOTATION_PARAMETER_NAME_DIRECTION;
import static com.ezand.tinkerpop.gizmo.Names.ANNOTATION_PARAMETER_NAME_FETCH_MODE;
import static com.ezand.tinkerpop.gizmo.Names.ANNOTATION_PARAMETER_NAME_ID_CLASS;
import static com.ezand.tinkerpop.gizmo.Names.ANNOTATION_PARAMETER_NAME_LABEL;
import static com.ezand.tinkerpop.gizmo.Names.CONSTRUCTOR_NAME;
import static com.ezand.tinkerpop.gizmo.Names.FIELD_NAME_ALREADY_FETCHED;
import static com.ezand.tinkerpop.gizmo.Names.FIELD_NAME_ELEMENT;
import static com.ezand.tinkerpop.gizmo.Names.FIELD_NAME_ID;
import static com.ezand.tinkerpop.gizmo.Names.FIELD_NAME_PROPERTY_CHANGES;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_ADD;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_CONTAINS;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_GET_CHANGES;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_GET_ELEMENT;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_GET_ID;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_GET_PROPERTY_CHANGES;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_HAS_NEXT;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_ID;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_MAP;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_NEXT;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_NON_PREFIX_GET_ELEMENT;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_OR_ELSE;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_PROPERTY;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_PROPERTY_CHANGE;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_RESOLVE_BEAN_CLASS;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_TO_ARRAY;
import static com.ezand.tinkerpop.gizmo.Names.METHOD_NAME_TO_KEY_VALUES;
import static com.ezand.tinkerpop.gizmo.Names.PARAMETER_NAME_ELEMENT;
import static com.ezand.tinkerpop.gizmo.Names.VARIABLE_NAME_ARGUMENTS;
import static com.ezand.tinkerpop.gizmo.Names.VARIABLE_NAME_NEXT;
import static com.ezand.tinkerpop.gizmo.Names.VARIABLE_NAME_VERTEX;
import static com.ezand.tinkerpop.gizmo.Names.splitNameOf;
import static com.ezand.tinkerpop.gizmo.ParameterBuilder.newParameter;
import static com.ezand.tinkerpop.gizmo.structure.Cascade.ALL;
import static com.ezand.tinkerpop.gizmo.structure.Direction.OUT;
import static com.ezand.tinkerpop.gizmo.structure.FetchMode.LAZY;
import static com.sun.tools.javac.code.Flags.FINAL;
import static com.sun.tools.javac.code.Flags.PRIVATE;
import static com.sun.tools.javac.code.Flags.PUBLIC;
import static com.sun.tools.javac.code.Type.ClassType;
import static com.sun.tools.javac.tree.JCTree.JCAnnotation;
import static com.sun.tools.javac.tree.JCTree.JCAssign;
import static com.sun.tools.javac.tree.JCTree.JCBlock;
import static com.sun.tools.javac.tree.JCTree.JCClassDecl;
import static com.sun.tools.javac.tree.JCTree.JCExpression;
import static com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
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
import static com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import static com.sun.tools.javac.util.List.nil;
import static java.util.stream.Collectors.toSet;
import static lombok.javac.Javac.CTC_NOT_EQUAL;
import static lombok.javac.handlers.JavacHandlerUtil.chainDots;
import static lombok.javac.handlers.JavacHandlerUtil.chainDotsString;
import static lombok.javac.handlers.JavacHandlerUtil.hasAnnotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import lombok.core.AnnotationValues;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

import org.mangosdk.spi.ProviderFor;

import com.ezand.tinkerpop.gizmo.annotations.Relationship;
import com.ezand.tinkerpop.gizmo.annotations.Vertex;
import com.ezand.tinkerpop.gizmo.structure.Cascade;
import com.ezand.tinkerpop.gizmo.structure.Direction;
import com.ezand.tinkerpop.gizmo.structure.FetchMode;
import com.ezand.tinkerpop.gizmo.structure.GizmoElement;
import com.ezand.tinkerpop.gizmo.structure.PropertyChanges;
import com.ezand.tinkerpop.gizmo.utils.GizmoMapper;
import com.ezand.tinkerpop.gizmo.utils.GizmoUtil;
import com.google.common.collect.Lists;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.List;
import com.tinkerpop.gremlin.process.graph.GraphTraversal;
import com.tinkerpop.gremlin.structure.Element;

@ProviderFor(JavacAnnotationHandler.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class VertexHandler extends JavacAnnotationHandler<Vertex> {
    private JavacTypes typesUtil;
    private JavacElements elementUtils;

    @Override
    public void handle(AnnotationValues<Vertex> annotation, JCAnnotation ast, JavacNode annotationNode) {
        JavacNode typeNode = annotationNode.up();
        JCAnnotation annotationAst = (JCAnnotation) annotationNode.get();

        JavacProcessingEnvironment processingEnvironment = JavacProcessingEnvironment.instance(annotationNode.getContext());

        this.typesUtil = processingEnvironment.getTypeUtils();
        this.elementUtils = processingEnvironment.getElementUtils();

        // Annotation parameter values
        ClassType idClass = (ClassType) getAnnotationParameterValue(annotationAst, ANNOTATION_PARAMETER_NAME_ID_CLASS, null);

        // Annotated id field
        JavacNode idFieldNode = findIdField(typeNode);

        // Modify the AST
        implementInterfaces(typeNode, idClass);
        createPropertyChangesField(typeNode);
        createAlreadyFetchedField(typeNode);
        modifyPropertySetters(typeNode);
        modifyRelationshipGetters(typeNode);
        modifyRelationshipSetters(typeNode);
        createGetPropertyChangesMethod(typeNode);
        createToKeyValuesMethod(typeNode, idFieldNode);
        JavacNode gizmoIdFieldNode = createIdField(typeNode, idClass);
        createElementField(typeNode);
        createGetElementMethod(typeNode);
        createConstructor(typeNode, gizmoIdFieldNode, idFieldNode);
        createGetIdMethod(typeNode, idClass);

        System.out.println(typeNode);
    }

    /////////////////////////////
    // AST manipulating - TYPE //
    /////////////////////////////
    private void implementInterfaces(JavacNode typeNode, ClassType idClass) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCClassDecl classDeclaration = (JCClassDecl) typeNode.get();

        // GizmoElement
        JCExpression gizmoElement = chainDots(typeNode, splitNameOf(GizmoElement.class));

        // GizmoElement<idClass>
        JCTypeApply typedGizmoElement = maker.TypeApply(gizmoElement, List.of(
                chainDotsString(typeNode, idClass.toString())
        ));

        Set<JCExpression> interfaces = classDeclaration.getImplementsClause()
                .stream()
                .collect(toSet());
        interfaces.add(typedGizmoElement);

        classDeclaration.implementing = List.from(interfaces.toArray(new JCExpression[interfaces.size()]));
    }

    private void createConstructor(JavacNode typeNode, JavacNode gizmoIdFieldNode, JavacNode idFieldNode) {
        newMethod()
                .name(CONSTRUCTOR_NAME)
                .modifiers(PUBLIC)
                .parameters(List.of(
                        newParameter()
                                .type(Element.class.getName())
                                .name(PARAMETER_NAME_ELEMENT)
                                .isFinal(true)
                                .buildWith(typeNode)
                ))
                .body(createConstructorBody(typeNode, gizmoIdFieldNode, idFieldNode))
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

    private void createAlreadyFetchedField(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        newField()
                .name(FIELD_NAME_ALREADY_FETCHED)
                .type(Set.class.getName())
                .typeArgs(List.of(String.class))
                .modifiers(PRIVATE | FINAL)
                .defaultValue(
                        maker.NewClass(null, nil(), typed(typeNode, chainDots(typeNode, splitNameOf(HashSet.class))), nil(), null)
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
                .returnType(Map.class.getName())
                .returnTypeTypeArgs(List.of(String.class, Object.class))
                .body(createGetPropertyChangesBody(typeNode))
                .injectInto(typeNode);
    }

    private void createToKeyValuesMethod(JavacNode typeNode, JavacNode idFieldNode) {
        newMethod()
                .modifiers(PUBLIC)
                .name(METHOD_NAME_TO_KEY_VALUES)
                .returnType(Object.class.getName())
                .returnTypeArray(true)
                .body(createToKeyValuesBody(typeNode, idFieldNode))
                .injectInto(typeNode);
    }

    private void createGetIdMethod(JavacNode typeNode, ClassType idClass) {
        newMethod()
                .modifiers(PUBLIC | FINAL)
                .name(METHOD_NAME_GET_ID)
                .returnType(idClass == null ? Object.class.getName() : idClass.toString())
                .body(createGetIdBody(typeNode))
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
    private void modifyPropertySetters(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        getFields(typeNode)
                .stream()
                .filter(f -> !hasAnnotation(Relationship.class, f))
                .forEach(f -> {
                    JavacNode setterMethod = findSetter(typeNode, f);
                    if (setterMethod != null) {
                        JCMethodDecl setter = (JCMethodDecl) setterMethod.get();
                        List<JCStatement> existingStatements = setter.body.stats;
                        JCStatement[] statements = new JCStatement[existingStatements.size() + 1];

                        // $propertyChanges.propertyChange
                        JCFieldAccess propertyChange = maker.Select(getThisField(typeNode, FIELD_NAME_PROPERTY_CHANGES), typeNode.toName(METHOD_NAME_PROPERTY_CHANGE));

                        // $propertyChanges.propertyChange(fieldName, oldValue, newValue)
                        JCMethodInvocation apply = maker.Apply(nil(), propertyChange, List.of(
                                maker.Literal(f.getName()),
                                getThisField(typeNode, f.getName()),
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

    private void modifyRelationshipGetters(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        getRelationshipFields(typeNode)
                .stream()
                .forEach(f -> {
                    JCVariableDecl field = (JCVariableDecl) f.get();
                    JavacNode getterMethod = findGetter(typeNode, f);
                    if (getterMethod != null) {
                        JCMethodDecl getter = (JCMethodDecl) getterMethod.get();
                        JCAnnotation annotation = getAnnotation(field.mods.annotations, Relationship.class);
                        boolean isCollection = isCollection(typesUtil, elementUtils, field.vartype.type.asElement());

                        String label = (String) getAnnotationParameterValue(annotation, ANNOTATION_PARAMETER_NAME_LABEL, null);
                        Direction direction = Direction.valueOf(getAnnotationParameterValue(annotation, ANNOTATION_PARAMETER_NAME_DIRECTION, OUT).toString());
                        Cascade cascade = Cascade.valueOf(getAnnotationParameterValue(annotation, ANNOTATION_PARAMETER_NAME_CASCADE, ALL).toString());
                        FetchMode fetchMode = FetchMode.valueOf(getAnnotationParameterValue(annotation, ANNOTATION_PARAMETER_NAME_FETCH_MODE, LAZY).toString());

                        JCVariableDecl assignVertex = createVertexExpression(typeNode);
                        JCVariableDecl assignGraphTraversal = createGraphTraversalExpression(typeNode, label, direction);

                        // TODO
                        if (isCollection) {
                            // GizmoUtil.resolveBeanClass
                            JCFieldAccess resolveBeanClass = maker.Select(chainDots(typeNode, splitNameOf(GizmoUtil.class)), typeNode.toName(METHOD_NAME_RESOLVE_BEAN_CLASS));

                            // GizmoUtil.resolveBeanClass(next)
                            JCMethodInvocation invokeResolveBeanClass = maker.Apply(nil(), resolveBeanClass, List.of(maker.Ident(typeNode.toName(VARIABLE_NAME_NEXT))));

                            // GizmoMapper.map
                            JCFieldAccess map = maker.Select(chainDots(typeNode, splitNameOf(GizmoMapper.class)), typeNode.toName(METHOD_NAME_MAP));

                            // GizmoMapper.map(next, GizmoUtil.resolveBeanClass(next))
                            JCMethodInvocation invokeMap = maker.Apply(nil(), map, List.of(maker.Ident(typeNode.toName(VARIABLE_NAME_NEXT)), invokeResolveBeanClass));

                            // ([type]) GizmoMapper.map(next, GizmoUtil.resolveBeanClass(next))
                            List<Type> types = field.vartype.type.allparams();
                            String type = types == null || types.isEmpty() ? Object.class.getName() : types.get(0).toString();
                            JCTypeCast castResult = maker.TypeCast(chainDotsString(typeNode, type), invokeMap);

                            // [in|out]
                            JCIdent inOrOut = maker.Ident(typeNode.toName(direction.name().toLowerCase()));

                            // [in|out].hasNext()
                            JCMethodInvocation hasNext = maker.Apply(nil(), maker.Select(inOrOut, typeNode.toName(METHOD_NAME_HAS_NEXT)), nil());

                            // [in|out].next
                            JCFieldAccess next = maker.Select(inOrOut, typeNode.toName(METHOD_NAME_NEXT));

                            // Vertex next = [in|out].next()
                            JCVariableDecl assignNext = maker.VarDef(maker.Modifiers(FINAL), typeNode.toName(VARIABLE_NAME_NEXT), chainDots(typeNode, splitNameOf(com.tinkerpop.gremlin.structure.Vertex.class)), maker.Apply(nil(), next, nil()));


                            // [new ArrayList() | new HashSet()]
                            JCNewClass newCollection = maker.NewClass(null, nil(), typed(typeNode, chainDots(typeNode, splitNameOf(isList(typesUtil, elementUtils, field.vartype.type.asElement()) ? ArrayList.class : HashSet.class))), nil(), null);

                            // Set<[type]>|List<[type]> collection = [new ArrayList<[type]>() | new HashSet<[type]>()]
                            JCVariableDecl collection = maker.VarDef(maker.Modifiers(FINAL), typeNode.toName("collection"), field.vartype, newCollection);

                            // collection.add
                            JCFieldAccess add = maker.Select(maker.Ident(typeNode.toName("collection")), typeNode.toName("add"));

                            // collection.add(([type]) GizmoMapper.map(next, GizmoUtil.resolveBeanClass(next)))
                            JCMethodInvocation applyAdd = maker.Apply(nil(), add, List.of(castResult));

                            JCBlock block = maker.Block(0, List.of(
                                    assignNext,
                                    maker.Exec(applyAdd)
                            ));

                            // this.field = collection;
                            JCAssign assignField = maker.Assign(getThisField(typeNode, f.getName()), maker.Ident(typeNode.toName("collection")));

                            // while ([in|out].hasNext()) {...}
                            JCWhileLoop whileLoop = maker.WhileLoop(hasNext, block);

                            // this.$alreadyFetched.contains(field)
                            JCExpression contains = createContainsConditionExpression(typeNode, f);

                            // this.$alreadyFetched.add
                            JCFieldAccess addAlreadyFetched = maker.Select(getThisField(typeNode, FIELD_NAME_ALREADY_FETCHED), typeNode.toName(METHOD_NAME_ADD));

                            // this.$alreadyFetched.add(field)
                            JCMethodInvocation applyAddAlreadyFetched = maker.Apply(nil(), addAlreadyFetched, List.of(maker.Literal(f.getName())));

                            JCBlock fetchBlock = maker.Block(0, List.of(
                                    assignVertex,
                                    assignGraphTraversal,
                                    collection,
                                    whileLoop,
                                    maker.Exec(assignField),
                                    maker.Exec(applyAddAlreadyFetched)
                            ));

                            JCIf ifContains = maker.If(maker.Binary(CTC_NOT_EQUAL, contains, maker.Literal(true)), fetchBlock, null);
                            getter.body.stats = getter.body.stats.prepend(ifContains);
                        } else {
                            // TODO throw exception if more that one result

                            JCIdent inOrOut = maker.Ident(typeNode.toName(direction.name().toLowerCase()));

                            // [in|out].next
                            JCFieldAccess next = maker.Select(inOrOut, typeNode.toName(METHOD_NAME_NEXT));

                            // Vertex next = [in|out].next()
                            JCVariableDecl assignNext = maker.VarDef(maker.Modifiers(FINAL), typeNode.toName(VARIABLE_NAME_NEXT), chainDots(typeNode, splitNameOf(com.tinkerpop.gremlin.structure.Vertex.class)), maker.Apply(nil(), next, nil()));

                            // [in|out].hasNext
                            JCFieldAccess hasNext = maker.Select(inOrOut, typeNode.toName(METHOD_NAME_HAS_NEXT));

                            // GizmoUtil.resolveBeanClass
                            JCFieldAccess resolveBeanClass = maker.Select(chainDots(typeNode, splitNameOf(GizmoUtil.class)), typeNode.toName(METHOD_NAME_RESOLVE_BEAN_CLASS));

                            // GizmoUtil.resolveBeanClass(next)
                            JCMethodInvocation invokeResolveBeanClass = maker.Apply(nil(), resolveBeanClass, List.of(maker.Ident(typeNode.toName(VARIABLE_NAME_NEXT))));

                            // GizmoMapper.map
                            JCFieldAccess map = maker.Select(chainDots(typeNode, splitNameOf(GizmoMapper.class)), typeNode.toName(METHOD_NAME_MAP));

                            // GizmoMapper.map(next, GizmoUtil.resolveBeanClass(next))
                            JCMethodInvocation invokeMap = maker.Apply(nil(), map, List.of(maker.Ident(typeNode.toName(VARIABLE_NAME_NEXT)), invokeResolveBeanClass));

                            // ([type]) GizmoMapper.map(next, GizmoUtil.resolveBeanClass(next))
                            JCTypeCast castResult = maker.TypeCast(field.vartype, invokeMap);

                            // this.[field] = ([type]) GizmoMapper.map(next, GizmoUtil.resolveBeanClass(next))
                            JCAssign assignField = maker.Assign(getThisField(typeNode, f.getName()), castResult);

                            // this.$alreadyFetched.add
                            JCFieldAccess addAlreadyFetched = maker.Select(getThisField(typeNode, FIELD_NAME_ALREADY_FETCHED), typeNode.toName(METHOD_NAME_ADD));

                            // this.$alreadyFetched.add(field)
                            JCMethodInvocation applyAddAlreadyFetched = maker.Apply(nil(), addAlreadyFetched, List.of(maker.Literal(f.getName())));

                            JCBlock ifHasNextBlock = maker.Block(0, List.of(
                                    assignNext,
                                    maker.Exec(assignField),
                                    maker.Exec(applyAddAlreadyFetched)
                            ));

                            // if ([in|out].hasNext()) {...}
                            JCIf ifHasNext = maker.If(maker.Apply(nil(), hasNext, nil()), ifHasNextBlock, null);

                            // this.$alreadyFetched.contains(field)
                            JCExpression contains = createContainsConditionExpression(typeNode, f);

                            JCBlock fetchBlock = maker.Block(0, List.of(
                                    assignVertex,
                                    assignGraphTraversal,
                                    ifHasNext
                            ));

                            // if (this.$alreadyFetched.contains(field) != true)
                            JCIf ifContains = maker.If(maker.Binary(CTC_NOT_EQUAL, contains, maker.Literal(true)), fetchBlock, null);
                            getter.body.stats = getter.body.stats.prepend(ifContains);
                        }
                    }
                });
    }

    private void modifyRelationshipSetters(JavacNode typeNode) {
        // TODO
    }

    //////////////////////////////////////
    // AST manipulating - BODY CREATION //
    //////////////////////////////////////
    private JCBlock createConstructorBody(JavacNode typeNode, JavacNode gizmoIdFieldNode, JavacNode idFieldNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        java.util.List<JCStatement> statements = Lists.newArrayList();

        JCFieldAccess elementField = getThisField(typeNode, FIELD_NAME_ELEMENT);

        // this.element = element
        JCExpressionStatement assignElement = maker.Exec(maker.Assign(elementField, maker.Ident(typeNode.toName(PARAMETER_NAME_ELEMENT))));
        statements.add(assignElement);

        // element.id
        JCFieldAccess id = maker.Select(elementField, typeNode.toName(METHOD_NAME_ID));

        // ([type]) element.id()
        JCTypeCast castId = maker.TypeCast(((JCVariableDecl) gizmoIdFieldNode.get()).vartype, maker.Apply(nil(), id, nil()));

        // this.$id = ([type]) element.id();
        statements.add(maker.Exec(maker.Assign(getThisField(typeNode, gizmoIdFieldNode.getName()), castId)));

        if (idFieldNode != null) {
            // this.[idField] = this.$id;
            statements.add(maker.Exec(maker.Assign(getThisField(typeNode, idFieldNode.getName()), getThisField(typeNode, gizmoIdFieldNode.getName()))));
        }

        JCFieldAccess property = maker.Select(elementField, typeNode.toName(METHOD_NAME_PROPERTY));
        getAccessorFields(typeNode)
                .stream()
                .filter(f -> !isIdField(f, idFieldNode) && !hasAnnotation(Relationship.class, f))
                .forEach(f -> {
                    JCVariableDecl fieldDeclaration = (JCVariableDecl) f.get();

                    // element.property(field)
                    JCMethodInvocation elementProperty = maker.Apply(nil(), property, List.of(maker.Literal(f.getName())));

                    // element.property(field).orElse
                    JCFieldAccess orElse = maker.Select(elementProperty, typeNode.toName(METHOD_NAME_OR_ELSE));

                    // element.property(field).orElse([default_value])
                    JCMethodInvocation applyOrElse = maker.Apply(nil(), orElse, List.of(getDefaultASTValue(f)));

                    // ([type]) element.property(field).orElse([default_value])
                    JCTypeCast typeCast = maker.TypeCast(fieldDeclaration.vartype, applyOrElse);

                    // this.field = ([type]) element.property(field).orElse([default_value])
                    JCAssign assign = maker.Assign(getThisField(typeNode, f.getName()), typeCast);

                    statements.add(maker.Exec(assign));
                });

        getRelationshipFields(typeNode, FetchMode.EAGER)
                .stream()
                .forEach(f -> {
                    JavacNode getter = findGetter(typeNode, f);

                    // get[field]()
                    statements.add(maker.Exec(maker.Apply(nil(), maker.Ident(typeNode.toName(getter.getName())), nil())));
                });

        return maker.Block(0, List.from(statements));
    }

    private JCBlock createGetPropertyChangesBody(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        // this.$propertyChanges
        JCFieldAccess propertyChanges = getThisField(typeNode, FIELD_NAME_PROPERTY_CHANGES);

        // this.$propertyChanges.getChanges
        JCFieldAccess getChanges = maker.Select(propertyChanges, typeNode.toName(METHOD_NAME_GET_CHANGES));

        // this.$propertyChanges.getChanges()
        JCMethodInvocation applyGetChanges = maker.Apply(nil(), getChanges, nil());

        return maker.Block(0, List.of(
                maker.Return(applyGetChanges)
        ));
    }

    private JCBlock createToKeyValuesBody(JavacNode typeNode, JavacNode idFieldNode) {
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
        JCFieldAccess argumentsAdd = maker.Select(maker.Ident(typeNode.toName(VARIABLE_NAME_ARGUMENTS)), typeNode.toName(METHOD_NAME_ADD));

        getFields(typeNode)
                .stream()
                .filter(field -> getterExists(typeNode, field))
                .forEach(field -> {
                    if (!isIdField(field, idFieldNode) && !hasAnnotation(Relationship.class, field)) {
                        // arguments.add("fieldName"); arguments.add(field);
                        JCMethodInvocation addKey = maker.Apply(nil(), argumentsAdd, List.of(maker.Literal(field.getName())));
                        JCMethodInvocation addValue = maker.Apply(nil(), argumentsAdd, List.of(maker.Ident(typeNode.toName(field.getName()))));

                        statements.add(maker.Exec(addKey));
                        statements.add(maker.Exec(addValue));
                    }
                });

        // arguments.toArray()
        JCMethodInvocation toArrayCall = maker.Apply(nil(), maker.Select(maker.Ident(typeNode.toName(VARIABLE_NAME_ARGUMENTS)), typeNode.toName(METHOD_NAME_TO_ARRAY)), nil());

        // return arguments.toArray();
        JCReturn returnStatement = maker.Return(
                maker.Exec(toArrayCall).getExpression()
        );
        statements.add(returnStatement);

        return maker.Block(0, List.from(statements));
    }

    private JCBlock createGetIdBody(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        return maker.Block(0, List.of(
                maker.Return(getThisField(typeNode, FIELD_NAME_ID))
        ));
    }

    private JCBlock createGetElementBody(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        return maker.Block(0, List.of(
                maker.Return(getThisField(typeNode, FIELD_NAME_ELEMENT))
        ));
    }

    ////////////////////
    // Helper methods //
    ////////////////////
    private JCExpression createContainsConditionExpression(JavacNode typeNode, JavacNode fieldNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        // this.$alreadyFetched.contains
        JCFieldAccess contains = maker.Select(getThisField(typeNode, FIELD_NAME_ALREADY_FETCHED), typeNode.toName(METHOD_NAME_CONTAINS));

        // this.$alreadyFetched.contains(field)
        return maker.Apply(nil(), contains, List.of(maker.Literal(fieldNode.getName())));
    }

    private JCVariableDecl createGraphTraversalExpression(JavacNode typeNode, String label, Direction direction) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        String directionString = direction.name().toLowerCase();

        // vertex.[in|out]
        JCFieldAccess inOrOut = maker.Select(maker.Ident(typeNode.toName(VARIABLE_NAME_VERTEX)), typeNode.toName(directionString));

        // vertex.[in|out](label)
        JCMethodInvocation applyOut = maker.Apply(nil(), inOrOut, List.of(maker.Literal(label)));

        // GraphTraversal<Vertex, Vertex>
        JCTypeApply graphTraversal = maker.TypeApply(chainDots(typeNode, splitNameOf(GraphTraversal.class)), List.of(
                chainDots(typeNode, splitNameOf(com.tinkerpop.gremlin.structure.Vertex.class)),
                chainDots(typeNode, splitNameOf(com.tinkerpop.gremlin.structure.Vertex.class))
        ));

        // GraphTraversal<Vertex, Vertex> [in|out] = vertex.[in|out](label)
        return maker.VarDef(maker.Modifiers(FINAL), typeNode.toName(directionString), graphTraversal, applyOut);
    }

    private JCVariableDecl createVertexExpression(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();

        // GizmoUtil.getElement
        JCFieldAccess getElement = maker.Select(chainDots(typeNode, splitNameOf(GizmoUtil.class)), typeNode.toName(METHOD_NAME_NON_PREFIX_GET_ELEMENT));

        // GizmoUtil.getElement(this, Vertex.class)
        JCMethodInvocation applyGetElement = maker.Apply(nil(), getElement, List.of(
                chainDotsString(typeNode, "this"),
                maker.Select(chainDots(typeNode, splitNameOf(com.tinkerpop.gremlin.structure.Vertex.class)), typeNode.toName("class"))
        ));

        // Vertex vertex = GizmoUtil.getElement(this, Vertex.class)
        return maker.VarDef(maker.Modifiers(FINAL), typeNode.toName(VARIABLE_NAME_VERTEX), chainDots(typeNode, splitNameOf(com.tinkerpop.gremlin.structure.Vertex.class)), applyGetElement);
    }
}
