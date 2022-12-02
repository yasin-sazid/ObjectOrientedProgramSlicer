package othersPackage;

import org.eclipse.jdt.core.dom.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SDG
{
    private String projectPath;
    private int criterionLineNumber;
    private String criterionFilePath;

    public Map<String, Set<Integer>> getForwardSlicingMapForClassLineNumbers() {
        return forwardSlicingMapForClassLineNumbers;
    }

    public Map<String, Set<Integer>> getBackwardSlicingMapForClassLineNumbers() {
        return backwardSlicingMapForClassLineNumbers;
    }

    public GraphNode sdgRoot = new GraphNode();
    public List<GraphNode> classRoots = new ArrayList<>();
    Map<String,GraphNode> mapForPathClassRoot = new HashMap<>();

    Map<String,Set<GraphNode>> mapOfClassQualifiedNameToMethodGraphNodes = new HashMap<>();

    public boolean isValidCriterion = false;

    public SDG(String projectPath, String criterionFilePath, int criterionLineNumber) throws IOException {
        this.projectPath = projectPath;
        this.criterionFilePath = criterionFilePath;
        this.criterionLineNumber = criterionLineNumber;
        operations();
    }

    ASTNode startingNode;
    Set<ASTNode> nodesForBackwardSlicing = new TreeSet<>(Comparator.comparing(ASTNode::getStartPosition));
    Set<ASTNode> nodesForForwardSlicing = new TreeSet<>(Comparator.comparing(ASTNode::getStartPosition));

    Map<String,Set<Integer>> forwardSlicingMapForClassLineNumbers = new HashMap<>();
    Map<String,Set<Integer>> backwardSlicingMapForClassLineNumbers = new HashMap<>();

    public void handleDerivedClasses() {
        for (GraphNode classRoot: classRoots)
        {
            for (GraphNode classRoot2: classRoots)
            {
                /*System.out.println(classRoot.node);
                System.out.println(classRoot2.node);
                System.out.println("--------------");*/
                if (((TypeDeclaration) classRoot.getNode()).getSuperclassType()!=null)
                {
                    //System.out.println("Dhukesi1");
                    String cRsuperclass = ((TypeDeclaration) classRoot.getNode()).getSuperclassType().toString();
                    String cR2class = ((TypeDeclaration)classRoot2.getNode()).getName().toString();

                    /*System.out.println(cRsuperclass);
                    System.out.println(cR2class);*/

                    if (cRsuperclass.equals(cR2class))
                    {
                        for (GraphNode node: classRoot2.children)
                        {
                            if (((MethodDeclaration)node.node).isConstructor())
                            {
                                continue;
                            }
                            else
                            {
                                int isInheritedMethod = 1;
                                for (MethodDeclaration m: ((TypeDeclaration) classRoot.getNode()).getMethods())
                                {
                                    if ((((MethodDeclaration)node.node).getName().toString()).equals(m.getName().toString()))
                                    {
                                        isInheritedMethod = 0;
                                        break;
                                    }
                                }

                                if (isInheritedMethod==1)
                                {
                                    /*System.out.println(classRoot.node);
                                    System.out.println(node.node);*/
                                    classRoot.children.add(node);
                                    node.parents.add(classRoot);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void handleClassInteractions ()
    {
        for (GraphNode classRoot : sdgRoot.getChildren())
        {
            for (GraphNode methodRoot : classRoot.getChildren())
            {
                for (GraphNode statementNode: methodRoot.getChildren())
                {
                    handleClassInteraction(statementNode);
                }
            }
        }
    }

    public void handleClassInteraction (GraphNode currentNode)
    {
        /*System.out.println(currentNode.node);
        System.out.println(currentNode.node.getNodeType());*/
        if (currentNode.node instanceof VariableDeclarationStatement)
        {
            //System.out.println(currentNode.node);
            for (GraphNode gn: currentNode.getChildren())
            {
                if(currentNode.node.toString().contains(gn.node.toString()))
                {
                    if(gn.node.getNodeType()==ASTNode.METHOD_INVOCATION) {
                        //System.out.println("Name: " + ((MethodInvocation) gn.node).getName());

                        Expression expression = ((MethodInvocation) gn.node).getExpression();
                        if (expression != null) {
                            //System.out.println("Expr: " + expression.toString());
                            ITypeBinding typeBinding = expression.resolveTypeBinding();
                            if (typeBinding != null) {
                                //System.out.println("Type: " + typeBinding.getName());
                                //System.out.println("Qualified name: " +typeBinding.getQualifiedName());

                                if (mapOfClassQualifiedNameToMethodGraphNodes.containsKey(typeBinding.getQualifiedName()))
                                {
                                    for (GraphNode methodNode : mapOfClassQualifiedNameToMethodGraphNodes.get(typeBinding.getQualifiedName()))
                                    {
                                /*System.out.println(methodNode.node);
                                System.out.println(((MethodDeclaration) methodNode.node).getName().toString());
                                System.out.println(((MethodInvocation) currentNode.node).getName());*/

                                        MethodDeclaration md = (MethodDeclaration) methodNode.node;
                                        MethodInvocation md2 = (MethodInvocation) gn.node;

                                        if (md.getName().toString().equals(md2.getName().toString()))
                                        {
                                            if (md.parameters().size()==md2.arguments().size())
                                            {
                                                List<String> mdParamsList = new ArrayList();
                                                List<String> md2ParamsList = new ArrayList();
                                                boolean matchmds = false;
                                                for (Object param : md.parameters())
                                                {
                                                    if (((SingleVariableDeclaration)param).getType().toString().equals("float")
                                                            || ((SingleVariableDeclaration)param).getType().toString().equals("double"))
                                                    {
                                                        mdParamsList.add("double");
                                                    }
                                                    else if (((SingleVariableDeclaration)param).getType().toString().equals("byte")
                                                            || ((SingleVariableDeclaration)param).getType().toString().equals("short")
                                                            || ((SingleVariableDeclaration)param).getType().toString().equals("long")
                                                            || ((SingleVariableDeclaration)param).getType().toString().equals("int"))
                                                    {
                                                        mdParamsList.add("int");
                                                    }
                                                    else if (((SingleVariableDeclaration)param).getType().toString().equals("char"))
                                                    {
                                                        mdParamsList.add("char");
                                                    }
                                                    else if (((SingleVariableDeclaration)param).getType().toString().equals("String"))
                                                    {
                                                        mdParamsList.add("String");
                                                    }
                                                    else if (((SingleVariableDeclaration)param).getType().toString().equals("boolean"))
                                                    {
                                                        mdParamsList.add("boolean");
                                                    }
                                                    else
                                                    {
                                                        mdParamsList.add("Object");
                                                    }
                                                }
                                                for (Object param : md2.arguments())
                                                {
                                                    if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.NumberLiteral"))
                                                    {
                                                        if (param.toString().contains("."))
                                                        {
                                                            md2ParamsList.add("double");
                                                        }
                                                        else
                                                        {
                                                            md2ParamsList.add("int");
                                                        }
                                                    }
                                                    else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.StringLiteral"))
                                                    {
                                                        md2ParamsList.add("String");
                                                    }
                                                    else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.CharacterLiteral"))
                                                    {
                                                        md2ParamsList.add("char");
                                                    }
                                                    else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.BooleanLiteral"))
                                                    {
                                                        md2ParamsList.add("boolean");
                                                    }
                                                    else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.NullLiteral"))
                                                    {
                                                        matchmds = true;
                                                        md2ParamsList.add("Object");
                                                    }
                                                    else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.TypeLiteral"))
                                                    {
                                                        md2ParamsList.add("Object");
                                                    }
                                                    else
                                                    {
                                                        matchmds = true;
                                                        md2ParamsList.add("Object");
                                                    }
                                                    //md2ParamsList.add(((SingleVariableDeclaration)param).getType().toString());
                                                }
                                                Collections.sort(mdParamsList);
                                                Collections.sort(md2ParamsList);

                                                if (matchmds)
                                                {
                                                    gn.children.add(methodNode);
                                                    methodNode.parents.add(gn);
                                                }
                                                else
                                                {
                                                    if (mdParamsList.equals(md2ParamsList))
                                                    {
                                                        //System.out.println(md.resolveBinding());
                                                        gn.children.add(methodNode);
                                                        methodNode.parents.add(gn);

                                                        if(!((MethodDeclaration) methodNode.node).getReturnType2().toString().equals("void"))
                                                        {
                                                            //System.out.println(methodNode.node);
                                                            handleReturnInteraction (currentNode, methodNode);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        IMethodBinding binding = ((MethodInvocation) gn.node).resolveMethodBinding();
                        if (binding != null) {
                            ITypeBinding type = binding.getDeclaringClass();
                            if (type != null) {
                                //System.out.println("Decl: " + type.getName());
                                //System.out.println("Qualified name: " + type.getQualifiedName());
                            }
                        }
                        //System.out.println("---------");
                    }
                }
            }

            if (currentNode.getChildren().size()!=0)
            {
                for (GraphNode childNode: currentNode.getChildren())
                {
                    handleClassInteraction(childNode);
                }
            }
        }
        else if (currentNode.node instanceof ClassInstanceCreation)
        {
            //System.out.println(((ClassInstanceCreation) currentNode.node).getExpression().resolveTypeBinding());
            //System.out.println(((ClassInstanceCreation) currentNode.node).getExpression());
            if(currentNode.node.getNodeType()==ASTNode.CLASS_INSTANCE_CREATION) {

                String qualifiedClassName = ((ClassInstanceCreation) currentNode.node).getType().resolveBinding().getPackage().getName().toString()
                        + '.' + ((ClassInstanceCreation) currentNode.node).getType().resolveBinding().getName();

                if (mapOfClassQualifiedNameToMethodGraphNodes.containsKey(qualifiedClassName))
                {
                    for (GraphNode methodNode : mapOfClassQualifiedNameToMethodGraphNodes.get(qualifiedClassName))
                    {
                                /*System.out.println(methodNode.node);
                                System.out.println(((MethodDeclaration) methodNode.node).getName().toString());
                                System.out.println(((MethodInvocation) currentNode.node).getName());*/
                        if (((MethodDeclaration) methodNode.node).getName().toString().equals(((ClassInstanceCreation) currentNode.node).getType().resolveBinding().getName()))
                        {
                            currentNode.children.add(methodNode);
                            methodNode.parents.add(currentNode);
                        }
                    }
                }
            }
        }
        /*else if (currentNode.node instanceof  Assignment)
        {
            //System.out.println(currentNode.node);
            Expression ex = ((Assignment) currentNode.node).getRightHandSide();
            //System.out.println(ex);
            if (ex instanceof MethodInvocation)
            {
                MethodInvocation mi = (MethodInvocation) ex;
                System.out.println(mi);
            }
            *//*currentNode.node.*//*
            currentNode.node.accept(new ASTVisitor() {
                public boolean visit (SimpleName child)
                {
                    //System.out.println(child);
                    *//*if(marking == 0)
                    {
                        s = (IVariableBinding) child.resolveBinding();
                        marking = 1;
                    }
                    else
                    {
                        //System.out.println(child);

                    }*//*
                    *//*if (!(child.resolveBinding() instanceof ITypeBinding || child.resolveBinding() instanceof  IMethodBinding))
                    {
                        if (child.resolveBinding()!=null)
                        {
                            *//**//*setOfVariableBinding.add((IVariableBinding) child.resolveBinding());*//**//*
                        }
                    }*//*
                    return true;
                }
            });
        }*/
        else if (currentNode.node instanceof MethodInvocation || currentNode.node instanceof Assignment)
        {
            MethodInvocation mi = null;

            if (currentNode.node instanceof Assignment)
            {
                Expression ex = ((Assignment) currentNode.node).getRightHandSide();
                //System.out.println(ex);
                if (((Assignment) currentNode.node).getRightHandSide() instanceof MethodInvocation)
                {
                    mi = (MethodInvocation) ex;
                    //System.out.println(mi);
                }
            }
            else
            {
                mi = (MethodInvocation) currentNode.node;
            }

            if (mi!=null)
            {
                if(mi.getNodeType()==ASTNode.METHOD_INVOCATION) {
                    //System.out.println("Name: " + ((MethodInvocation) currentNode.node).getName());

                    Expression expression = ((MethodInvocation) mi).getExpression();
                    if (expression != null) {
                        //System.out.println("Expr: " + expression.toString());
                        ITypeBinding typeBinding = expression.resolveTypeBinding();
                        if (typeBinding != null) {
                            //System.out.println("Type: " + typeBinding.getName());
                            //System.out.println(((ITypeBinding)typeBinding.getSuperclass()).getSuperclass());
                            //System.out.println("Qualified name: " +typeBinding.getQualifiedName());
                            if (mapOfClassQualifiedNameToMethodGraphNodes.containsKey(typeBinding.getQualifiedName()))
                            {
                                for (GraphNode methodNode : mapOfClassQualifiedNameToMethodGraphNodes.get(typeBinding.getQualifiedName()))
                                {
                                /*System.out.println(methodNode.node);
                                System.out.println(((MethodDeclaration) methodNode.node).getName().toString());
                                System.out.println(((MethodInvocation) currentNode.node).getName());*/

                                    MethodDeclaration md = (MethodDeclaration) methodNode.node;
                                    MethodInvocation md2 = mi;

                                    if (md.getName().toString().equals(md2.getName().toString()))
                                    {
                                        if (md.parameters().size()==md2.arguments().size())
                                        {
                                            List<String> mdParamsList = new ArrayList();
                                            List<String> md2ParamsList = new ArrayList();
                                            boolean matchmds = false;
                                            for (Object param : md.parameters())
                                            {
                                                if (((SingleVariableDeclaration)param).getType().toString().equals("float")
                                                        || ((SingleVariableDeclaration)param).getType().toString().equals("double"))
                                                {
                                                    mdParamsList.add("double");
                                                }
                                                else if (((SingleVariableDeclaration)param).getType().toString().equals("byte")
                                                        || ((SingleVariableDeclaration)param).getType().toString().equals("short")
                                                        || ((SingleVariableDeclaration)param).getType().toString().equals("long")
                                                        || ((SingleVariableDeclaration)param).getType().toString().equals("int"))
                                                {
                                                    mdParamsList.add("int");
                                                }
                                                else if (((SingleVariableDeclaration)param).getType().toString().equals("char"))
                                                {
                                                    mdParamsList.add("char");
                                                }
                                                else if (((SingleVariableDeclaration)param).getType().toString().equals("String"))
                                                {
                                                    mdParamsList.add("String");
                                                }
                                                else if (((SingleVariableDeclaration)param).getType().toString().equals("boolean"))
                                                {
                                                    mdParamsList.add("boolean");
                                                }
                                                else
                                                {
                                                    mdParamsList.add("Object");
                                                }
                                            }
                                            for (Object param : md2.arguments())
                                            {
                                                if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.NumberLiteral"))
                                                {
                                                    if (param.toString().contains("."))
                                                    {
                                                        md2ParamsList.add("double");
                                                    }
                                                    else
                                                    {
                                                        md2ParamsList.add("int");
                                                    }
                                                }
                                                else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.StringLiteral"))
                                                {
                                                    md2ParamsList.add("String");
                                                }
                                                else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.CharacterLiteral"))
                                                {
                                                    md2ParamsList.add("char");
                                                }
                                                else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.BooleanLiteral"))
                                                {
                                                    md2ParamsList.add("boolean");
                                                }
                                                else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.NullLiteral"))
                                                {
                                                    matchmds = true;
                                                    md2ParamsList.add("Object");
                                                }
                                                else if (param.getClass().getName().toString().equals("org.eclipse.jdt.core.dom.TypeLiteral"))
                                                {
                                                    md2ParamsList.add("Object");
                                                }
                                                else
                                                {
                                                    matchmds = true;
                                                    md2ParamsList.add("Object");
                                                }
                                                //md2ParamsList.add(((SingleVariableDeclaration)param).getType().toString());
                                            }
                                            Collections.sort(mdParamsList);
                                            Collections.sort(md2ParamsList);

                                            if (matchmds)
                                            {
                                                currentNode.children.add(methodNode);
                                                methodNode.parents.add(currentNode);
                                            }
                                            else
                                            {
                                                if (mdParamsList.equals(md2ParamsList))
                                                {
                                                    //System.out.println(md.resolveBinding());
                                                    currentNode.children.add(methodNode);
                                                    methodNode.parents.add(currentNode);

                                                    if(!((MethodDeclaration) methodNode.node).getReturnType2().toString().equals("void"))
                                                    {
                                                        //System.out.println(methodNode.node);
                                                        handleReturnInteraction (currentNode, methodNode);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    IMethodBinding binding = ((MethodInvocation) mi).resolveMethodBinding();
                    if (binding != null) {
                        ITypeBinding type = binding.getDeclaringClass();
                        if (type != null) {
                            //System.out.println("Decl: " + type.getName());
                            //System.out.println("Qualified name: " + type.getQualifiedName());
                        }
                    }
                    //System.out.println("---------");
                }
                //System.out.println(currentNode.node);
                //System.out.println(((MethodInvocation) currentNode.node).resolveMethodBinding());
            }
            else
            {
                /*System.out.println(currentNode.node);
                System.out.println(currentNode.getChildren().size());*/
                if (currentNode.getChildren().size()!=0)
                {
                    for (GraphNode childNode: currentNode.getChildren())
                    {
                        handleClassInteraction(childNode);
                    }
                }
            }
        }
    }

    private void handleReturnInteraction(GraphNode currentNode, GraphNode methodNode)
    {
        if (currentNode.node instanceof MethodInvocation)
        {
            if (currentNode.parents.size()!=0)
            {
                for (GraphNode miParent: currentNode.parents)
                {
                    if (miParent.node instanceof VariableDeclarationStatement)
                    {
                        int cnLineNum = ((CompilationUnit) currentNode.node.getRoot()).getLineNumber(currentNode.node.getStartPosition());
                        int mipLineNum = ((CompilationUnit) miParent.node.getRoot()).getLineNumber(miParent.node.getStartPosition());
                        if (cnLineNum==mipLineNum)
                        {
                            currentNode = miParent;
                        }
                    }
                }
            }
        }
        for (GraphNode mChild: methodNode.children)
        {
            if (mChild.node instanceof ReturnStatement)
            {
                /*System.out.println(currentNode.node);
                System.out.println(mChild.node);*/
                /*System.out.println(mChild.node);
                System.out.println(currentNode.node);*/
                mChild.children.add(currentNode);
                currentNode.parents.add(mChild);
            }
            else if (mChild.children.size()!=0)
            {
                handleReturnInteraction(currentNode, mChild);
            }
        }
    }

    public void handleSuperClasses (GraphNode classRoot)
    {
        Type superClassType = ((TypeDeclaration)classRoot.node).getSuperclassType();

        handleSuperClass(classRoot, superClassType);
    }

    public void handleSuperClass (GraphNode classRoot, Type superClassType)
    {
        String qualifiedSuperClassName = superClassType.resolveBinding().getPackage().getName()
                + '.' + superClassType.resolveBinding().getName();

        for (GraphNode mdSuper: mapOfClassQualifiedNameToMethodGraphNodes.get(qualifiedSuperClassName))
        {
            for (GraphNode mdChild: classRoot.children)
            {
                MethodDeclaration md = (MethodDeclaration) mdChild.node;
                MethodDeclaration md2 = (MethodDeclaration) mdSuper.node;

                /*System.out.println("-----");
                System.out.println(md);

                System.out.println(md2);
                System.out.println("-----");*/

                if (md.getName().toString().equals(md2.getName().toString()))
                {
                    if (md.parameters().size()==md2.parameters().size())
                    {
                        List<String> mdParamsList = new ArrayList();
                        List<String> md2ParamsList = new ArrayList();
                        for (Object param : md.parameters())
                        {
                            mdParamsList.add(((SingleVariableDeclaration)param).getType().toString());
                        }
                        for (Object param : md2.parameters())
                        {
                            md2ParamsList.add(((SingleVariableDeclaration)param).getType().toString());
                        }
                        Collections.sort(mdParamsList);
                        Collections.sort(md2ParamsList);

                        if (mdParamsList.equals(md2ParamsList))
                        {
                            //System.out.println(md.resolveBinding());

                            for (GraphNode gn: mdSuper.parents)
                            {
                                if (gn.node.getNodeType()==ASTNode.METHOD_INVOCATION || gn.node.getNodeType()==ASTNode.ASSIGNMENT)
                                {
                                    gn.children.add(mdChild);
                                    mdChild.parents.add(gn);

                                    /*System.out.println(mdChild.node);
                                    System.out.println(gn.node);*/
                                    /*System.out.println("ETA POLYMORPHISM");*/
                                    /*if (gn.parents.size()!=0)
                                    {
                                        for (GraphNode gnParent: gn.parents)
                                        {
                                            if (gnParent.node instanceof VariableDeclarationStatement)
                                            {
                                                handleReturnInteraction(gn, mdChild);
                                            }
                                        }
                                    }*/
                                    handleReturnInteraction(gn, mdChild);
                                    /*System.out.println("ETA POLYMORPHISM SHESH");*/
                                }
                            }

                        }
                    }
                }
            }
        }

        //System.out.println(classRoot.node);

        if (superClassType.resolveBinding().getSuperclass()!=null)
        {
            for (GraphNode classRoot2: classRoots)
            {
                if (classRoot2.classQualifiedName.equals(qualifiedSuperClassName))
                {
                    superClassType = ((TypeDeclaration)classRoot2.node).getSuperclassType();
                    break;
                }
            }

            if (superClassType!=null)
            {
                //System.out.println(superClassType.resolveBinding().getQualifiedName());
                handleSuperClass(classRoot, superClassType);
            }
        }
    }

    public void handlePolymorphism ()
    {
        for (GraphNode classRoot : sdgRoot.getChildren())
        {
            if (((TypeDeclaration) classRoot.node).getSuperclassType() != null)
            {
                handleSuperClasses(classRoot);
            }
        }
    }

    FolderProcessor folderProcessor;

    public void operations () throws IOException {
        folderProcessor = new FolderProcessor(projectPath);

        for (File javaFile: folderProcessor.getFiles())
        {
            System.out.println(javaFile);
            Operation op = new Operation(folderProcessor.getEnvironment());
            op.operations(javaFile.getAbsolutePath());
            classRoots.add(op.root);
            mapOfClassQualifiedNameToMethodGraphNodes.put(op.classQualifiedName,op.getSetOfMethodDeclarationGraphNodes());
            mapForPathClassRoot.put(javaFile.getAbsolutePath(), op.root);
        }

        for (GraphNode classRoot: classRoots)
        {
            sdgRoot.getChildren().add(classRoot);
            classRoot.getParents().add(sdgRoot);
        }

        handleDerivedClasses();

        handleClassInteractions();

        handlePolymorphism();

        //debug system->class->method
        /*for (GraphNode classRoot: sdgRoot.children)
        {
            System.out.println(classRoot.node);

            for (GraphNode methodRoot: classRoot.children)
            {
                System.out.println(methodRoot.node);
            }
        }

        System.out.println("-----------------");*/

        parser();
    }

    ASTNode currentParent;

    int forwardReturnMarker = 0;

    void recursionForForwardSlicing (GraphNode g)
    {
        /*if(g==null)
            return;*/

        if (forwardSlicingMapForClassLineNumbers.containsKey(g.classFilePath))
        {
            forwardSlicingMapForClassLineNumbers.get(g.classFilePath).add(((CompilationUnit) g.node.getRoot()).getLineNumber(g.node.getStartPosition()));
        }
        else
        {
            Set<Integer> tempSet = new TreeSet<>(Comparator.comparing(Integer::intValue));
            tempSet.add(((CompilationUnit) g.node.getRoot()).getLineNumber(g.node.getStartPosition()));
            forwardSlicingMapForClassLineNumbers.put(g.classFilePath, tempSet);
        }

        nodesForForwardSlicing.add(g.node);

        for(GraphNode gg : g.children)
        {
            int pickNode = 1;
            for (ASTNode fs: nodesForForwardSlicing)
            {
                if (gg.node.toString().equals(fs.toString())&&gg.node.getStartPosition()==fs.getStartPosition())
                {
                    pickNode = 0;
                }
            }

            if (currentParent instanceof ReturnStatement && g.node instanceof MethodInvocation && gg.node instanceof MethodDeclaration)
            {
                pickNode = 0;
            }

            if ((currentParent instanceof MethodInvocation || currentParent instanceof VariableDeclarationStatement) && g.node instanceof MethodDeclaration)
            {
                forwardReturnMarker = 1;
            }

            if (forwardReturnMarker==1 && g.node instanceof ReturnStatement)
            {
                pickNode = 0;
            }

            ASTNode tempu = currentParent;

            if (pickNode == 1)
            {
                currentParent = g.node;
                recursionForForwardSlicing(gg);
                if (g.node instanceof MethodDeclaration)
                {
                    forwardReturnMarker = 0;
                }
                currentParent = tempu;
            }
        }
    }

    Set<GraphNode> visited = new HashSet<>();

    GraphNode getStartingNode (GraphNode node)
    {
        GraphNode foundStartingNode = null;

        //System.out.println(node.node);
        for(GraphNode g : node.children)
        {
            if(g.node.getStartPosition()==startingNode.getStartPosition())
            {
                return g;
            }

            int pickNode = 1;
            for (GraphNode n: visited)
            {
                if (g.node.getStartPosition()==n.node.getStartPosition() && g.getNodeLineString().equals(n.getNodeLineString()))
                {
                    pickNode = 0;
                }
            }

            if (pickNode == 1)
            {
                visited.add(g);
                foundStartingNode = getStartingNode(g);
            }

            if(foundStartingNode!=null)
                break;
        }
        return foundStartingNode;
    }

    int backwardReturnMarker = 0;

    void recursionForBackwardSlicing (GraphNode g)
    {
        if (backwardSlicingMapForClassLineNumbers.containsKey(g.classFilePath))
        {
            backwardSlicingMapForClassLineNumbers.get(g.classFilePath).add(((CompilationUnit) g.node.getRoot()).getLineNumber(g.node.getStartPosition()));
        }
        else
        {
            Set<Integer> tempSet = new TreeSet<>(Comparator.comparing(Integer::intValue));
            tempSet.add(((CompilationUnit) g.node.getRoot()).getLineNumber(g.node.getStartPosition()));
            backwardSlicingMapForClassLineNumbers.put(g.classFilePath, tempSet);
        }
        nodesForBackwardSlicing.add(g.node);

        if(classRoots.contains(g))
        {
            return;
        }

        for(GraphNode gg : g.parents)
        {
            int pickNode = 1;
            for (ASTNode bs: nodesForBackwardSlicing)
            {
                //System.out.println(bs.toString());
                //System.out.println(gg.node.toString());
                if (gg.node.toString().equals(bs.toString())&&gg.node.getStartPosition()==bs.getStartPosition())
                {
                    pickNode = 0;
                }
            }

            if (currentParent instanceof MethodDeclaration && g.node instanceof MethodInvocation && gg.node instanceof ReturnStatement)
            {
                pickNode = 0;
            }

            if ((currentParent instanceof MethodInvocation || currentParent instanceof VariableDeclarationStatement) && g.node instanceof ReturnStatement)
            {
                backwardReturnMarker = 1;
            }

            if (backwardReturnMarker==1 && g.node instanceof MethodDeclaration)
            {
                pickNode = 0;
            }

            /*System.out.println(gg.node.getNodeType());
            System.out.println(gg.node);
            System.out.println(backwardReturnMarker);*/

            ASTNode tempu = currentParent;

            if (pickNode == 1)
            {
                //System.out.println("going to child");
                currentParent = g.node;
                recursionForBackwardSlicing(gg);
                if (g.node instanceof ReturnStatement)
                {
                    backwardReturnMarker = 0;
                }
                currentParent = tempu;
            }
        }
    }

    public String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        reader.close();

        return  fileData.toString();
    }

    public void parser () throws IOException {

        GraphNode classRoot = mapForPathClassRoot.get(criterionFilePath);
        //System.out.println(classRoot.node);

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setSource(readFileToString(criterionFilePath).toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setEnvironment(null, folderProcessor.getEnvironment(), null, true);
        //parser.setEnvironment(null, null, null, true);
        parser.setUnitName("Saal.java");
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept(new ASTVisitor() {

            public void preVisit (ASTNode node) {
                if((node instanceof Statement && !(node instanceof Block))|| node instanceof TypeDeclaration || node instanceof MethodDeclaration)
                {
                    startingNode = node;

                    if (((CompilationUnit) startingNode.getRoot()).getLineNumber(startingNode.getStartPosition())==criterionLineNumber)
                    {
                        isValidCriterion = true;
                        GraphNode foundNode;
                        if (classRoot.node.toString().equals(startingNode.toString()))
                        {
                            foundNode = classRoot;
                        }
                        else
                        {
                            foundNode = getStartingNode(classRoot);
                        }
                        //System.out.println(foundNode.node);
                        System.out.print("Line Number: ");
                        System.out.println(((CompilationUnit) foundNode.node.getRoot()).getLineNumber(foundNode.node.getStartPosition()));
                        recursionForBackwardSlicing(foundNode);
                        recursionForForwardSlicing(foundNode);
                        System.out.println("Backward slicing:");
                        for (String filePath: backwardSlicingMapForClassLineNumbers.keySet())
                        {
                            System.out.println(filePath);
                            for (int lineNumber: backwardSlicingMapForClassLineNumbers.get(filePath))
                            {
                                System.out.println(lineNumber);
                            }
                        }
                        /*for(ASTNode astNode: nodesForBackwardSlicing)
                        {
                            //System.out.println(astNode);
                            System.out.println(((CompilationUnit) astNode.getRoot()).getLineNumber(astNode.getStartPosition()));
                        }*/
                        System.out.println("Forward slicing:");
                        for (String filePath: forwardSlicingMapForClassLineNumbers.keySet())
                        {
                            System.out.println(filePath);
                            for (int lineNumber: forwardSlicingMapForClassLineNumbers.get(filePath))
                            {
                                System.out.println(lineNumber);
                            }
                        }
                        /*for(ASTNode astNode: nodesForForwardSlicing)
                        {
                            //System.out.println(astNode);
                            System.out.println(((CompilationUnit) astNode.getRoot()).getLineNumber(astNode.getStartPosition()));
                        }*/
                        System.out.println("---------------------------------");
                        nodesForBackwardSlicing.clear();
                        nodesForForwardSlicing.clear();
                        visited.clear();
                    }
                }
            }

        });
    }

    public boolean isValidCriterion() {
        return isValidCriterion;
    }
}
