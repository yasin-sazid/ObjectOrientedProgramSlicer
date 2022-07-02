package othersPackage;

import org.eclipse.jdt.core.dom.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Operation {

    public GraphNode root;
    Stack<GraphNode> graphNodeStack = new Stack<GraphNode> ();
    Set<GraphNode> assertNodeSet = new HashSet<>();
    Set<GraphNode> tryBodySet = new HashSet<>();
    Map<IVariableBinding,Set<GraphNode>> map = new HashMap<>();
    int i = 0;
    int marker = 0;
    int marking = 0;
    IVariableBinding s;
    public Set<IVariableBinding> nameSet = new HashSet<>();
    ASTNode startingNode;
    Set<ASTNode> nodesForBackwardSlicing = new TreeSet<>(Comparator.comparing(ASTNode::getStartPosition));
    Set<ASTNode> nodesForForwardSlicing = new TreeSet<>(Comparator.comparing(ASTNode::getStartPosition));

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

    public void parse(String str) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(str.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        //parser.setEnvironment(new String[] {"C:\\Users\\ASUS\\Desktop\\demo\\out\\production\\demo"}, new String[] {"C:\\Users\\ASUS\\Desktop\\demo\\src"}, null, true);
        parser.setUnitName("Saal.java");
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);


        root = new GraphNode();
        graphNodeStack.push(root);

        cu.accept(new ASTVisitor() {
            public void preVisit (ASTNode node) {

            }

            public boolean visit (IfStatement node) {
                //System.out.println(node.getExpression());
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return true;
            }

            public boolean visit (SynchronizedStatement node) {
                //System.out.println(node.getExpression());
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return true;
            }

            public boolean visit (ForStatement node) {
                //System.out.println(node.getExpression());
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return true;
            }

            public boolean visit (EnhancedForStatement node) {
                //System.out.println(node.getExpression());
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return true;
            }

            public boolean visit (WhileStatement node) {
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return true;
            }

            public boolean visit (DoStatement node) {
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return true;
            }

            public boolean visit (SwitchStatement node) {
                //System.out.println(node.getExpression());
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return true;
            }

            public boolean visit (SwitchCase node) {
                //System.out.println(node.getExpression());
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return true;
            }

            public boolean visit (LabeledStatement node) {
                //System.out.println(node.getExpression());
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                return true;
            }

            public boolean visit (TryStatement node) {

                /*List<StructuralPropertyDescriptor> list = node.structuralPropertiesForType();
                for(StructuralPropertyDescriptor a : list)
                {
                    System.out.println(a.getId());
                }*/
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                marker = 1;
                return true;
            }

            public boolean visit (CatchClause node) {
                //System.out.println(node.getExpression());
                marker = 0;
                GraphNode temp;
                temp = new GraphNode(node);
                graphNodeStack.push(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                for(GraphNode g: tryBodySet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                return true;
            }

            public boolean visit (ExpressionStatement node) {
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.add(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return true;
            }

            public boolean visit (SimpleName node) {
                IBinding bind = node.resolveBinding();
                if(map.containsKey(bind))
                {
                    //System.out.println(node);
                }
                return false;
            }

            public boolean visit (BreakStatement node) {
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return false;
            }

            public boolean visit (ContinueStatement node) {
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return false;
            }

            public boolean visit (ReturnStatement node) {
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return false;
            }

            public boolean visit (ThrowStatement node) {
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);
                return false;
            }

            public boolean visit (AssertStatement node) {
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                assertNodeSet.add(temp);
                if(marker == 1)
                    tryBodySet.add(temp);
                return false;
            }

            public boolean visit (EmptyStatement node) {
                //System.out.println(node);
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                return false;
            }

            public void endVisit (ForStatement node) {
                graphNodeStack.pop();
            }

            public void endVisit (EnhancedForStatement node) {
                graphNodeStack.pop();
            }

            public void endVisit (WhileStatement node) {
                graphNodeStack.pop();
            }

            public void endVisit (DoStatement node) {
                GraphNode temp =  graphNodeStack.pop();
                graphNodeStack.peek().children.addAll(temp.children);
                for(GraphNode g : temp.children) {
                    g.parents.add(graphNodeStack.peek());
                }
            }

            public void endVisit (SwitchStatement node) {
                graphNodeStack.pop();
            }

            public void endVisit (SwitchCase node) {
                graphNodeStack.pop();
            }

            public void endVisit (ExpressionStatement node) {
                graphNodeStack.pop();
            }

            public void endVisit (LabeledStatement node) {
                graphNodeStack.pop();
            }

            public void endVisit (IfStatement node) {
                graphNodeStack.pop();
            }

            public void endVisit (SynchronizedStatement node) {
                graphNodeStack.pop();
            }

            public void endVisit (TryStatement node) {
                graphNodeStack.pop();
                tryBodySet.clear();
            }

            public void endVisit (CatchClause node) {
                graphNodeStack.pop();
            }

            public boolean visit (MethodDeclaration node) {
                GraphNode temp;
                temp = new GraphNode(node);
                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.push(temp);
                return true;
            }

            public void endVisit (MethodDeclaration node) {
                graphNodeStack.pop();
            }

            public void endVisit (VariableDeclarationStatement node) {
                graphNodeStack.pop();
            }

            public boolean visit (SingleVariableDeclaration node) {
                IVariableBinding bind = node.resolveBinding();
                Set<GraphNode> set = new HashSet<>();
                set.add(graphNodeStack.peek());
                map.put(bind,set);
                return true;
            }

            public boolean visit (VariableDeclarationFragment node) {
                IVariableBinding bind = node.resolveBinding();
                Set<GraphNode> set = new HashSet<>();
                set.add(graphNodeStack.peek());
                map.put(bind,set);
                node.accept(new ASTVisitor() {
                    public boolean visit (SimpleName child)
                    {
                        if(marking == 0)
                        {
                            s = (IVariableBinding) child.resolveBinding();
                            marking = 1;
                        }
                        else
                        {
                            nameSet.add((IVariableBinding) child.resolveBinding());
                        }
                        return false;
                    }
                });
                return true;
            }

            public void endVisit (VariableDeclarationFragment node) {
                for (IVariableBinding v : nameSet)
                {
                    graphNodeStack.peek().getParents().addAll(map.get(v));
                    for(GraphNode g : map.get(v))
                    {
                        g.children.add(graphNodeStack.peek());
                    }
                    map.get(s).add(graphNodeStack.peek());
                }
                nameSet.clear();
                marking = 0;
            }

            public boolean visit (VariableDeclarationStatement node) {
                //System.out.println(node.fragments());

                GraphNode temp;
                temp = new GraphNode(node);
                startingNode = temp.node;

                temp.parents.add(graphNodeStack.peek());
                graphNodeStack.peek().children.add(temp);
                graphNodeStack.add(temp);
                for(GraphNode g: assertNodeSet)
                {
                    g.children.add(temp);
                    temp.parents.add(g);
                }
                if(marker == 1)
                    tryBodySet.add(temp);

                return true;
            }

            public boolean visit (Assignment node) {
                node.accept(new ASTVisitor() {

                    public boolean visit (SimpleName child)
                    {
                        if(child.resolveTypeBinding() instanceof PrimitiveType)
                        {
                            if(marking == 0)
                            {
                                s = (IVariableBinding) child.resolveBinding();
                                marking = 1;
                            }
                            else
                            {
                                nameSet.add((IVariableBinding) child.resolveBinding());
                            }
                        }
                        return false;
                    }

                    public boolean visit (QualifiedName child)
                    {
                        if(!(child.resolveTypeBinding() instanceof PrimitiveType))
                        {
                            System.out.println("asd");

                        }
                        return false;
                    }
                });

                return false;
            }

            public boolean visit (MethodInvocation node) {
                List<Expression> list = node.arguments();
                for(Expression e : list)
                {
                    e.accept(new ASTVisitor() {
                        public boolean visit (SimpleName child) {
                            nameSet.add((IVariableBinding) child.resolveBinding());
                            return false;
                        }
                    });
                }
                return false;
            }

            public boolean visit (PrefixExpression node) {
                node.accept(new ASTVisitor() {
                    public boolean visit (SimpleName child) {
                        s = (IVariableBinding) child.resolveBinding();
                        return false;
                    }
                });
                return false;
            }

            public void endVisit (PrefixExpression node) {
                map.get(s).add(graphNodeStack.peek());
            }

            public boolean visit (PostfixExpression node) {
                node.accept(new ASTVisitor() {
                    public boolean visit (SimpleName child) {
                        s = (IVariableBinding) child.resolveBinding();
                        return false;
                    }
                });
                return false;
            }

            public void endVisit (PostfixExpression node) {
                map.get(s).add(graphNodeStack.peek());
            }

            public void endVisit (MethodInvocation node) {
                for (IVariableBinding v : nameSet)
                {
                    graphNodeStack.peek().getParents().addAll(map.get(v));
                    for(GraphNode g : map.get(v))
                    {
                        g.children.add(graphNodeStack.peek());
                    }
                }
                nameSet.clear();
            }

            public void endVisit (Assignment node) {
                if(nameSet.isEmpty())
                {
                    GraphNode tempuNode = null;
                    for(GraphNode g : map.get(s))
                    {
                        if(g.node instanceof VariableDeclarationStatement)
                        {
                            tempuNode = g;
                            break;
                        }
                    }
                    Set<GraphNode> tempuSet = new HashSet<>();
                    tempuSet.add(tempuNode);
                    tempuSet.add(graphNodeStack.peek());
                    map.replace(s,map.get(s),tempuSet);

                    tempuNode.children.add(graphNodeStack.peek());
                    graphNodeStack.peek().parents.add(tempuNode);
                }
                else
                {
                    for (IVariableBinding v : nameSet)
                    {
                        graphNodeStack.peek().getParents().addAll(map.get(v));
                        for(GraphNode g : map.get(v))
                        {
                            g.children.add(graphNodeStack.peek());
                        }
                        map.get(s).add(graphNodeStack.peek());
                    }
                }
                nameSet.clear();
                marking = 0;
            }

            public void postVisit (ASTNode node) {

            }
        });

    }

    void recursionForForwardSlicing (GraphNode g)
    {
        /*if(g==null)
            return;*/

        nodesForForwardSlicing.add(g.node);

        for(GraphNode gg : g.children)
        {
            recursionForForwardSlicing(gg);
        }
    }

    GraphNode getStartingNode (GraphNode node)
    {
        GraphNode foundStartingNode = null;
        for(GraphNode g : node.children)
        {
            if(g.node.toString().equals(startingNode.toString())&&g.node.getStartPosition()==startingNode.getStartPosition())
            {
                return g;
            }
            foundStartingNode = getStartingNode(g);

            if(foundStartingNode!=null)
                break;
        }
        return foundStartingNode;
    }

    void recursionForBackwardSlicing (GraphNode g)
    {
        if(g.equals(root))
            return;

        nodesForBackwardSlicing.add(g.node);

        for(GraphNode gg : g.parents)
        {
            recursionForBackwardSlicing(gg);
        }
    }

    public void parser (String str) {

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(str.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        //parser.setEnvironment(new String[] {"C:\\Users\\ASUS\\Desktop\\demo\\out\\production\\demo"}, new String[] {"C:\\Users\\ASUS\\Desktop\\demo\\src"}, null, true);
        parser.setUnitName("Saal.java");
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept(new ASTVisitor() {

            public void preVisit (ASTNode node) {
                if(node instanceof Statement && !(node instanceof Block))
                {
                    startingNode = node;
                    GraphNode foundNode = getStartingNode(root);
                    System.out.println(foundNode.node);
                    recursionForBackwardSlicing(foundNode);
                    recursionForForwardSlicing(foundNode);
                    System.out.println("Backward slicing:");
                    for(ASTNode astNode: nodesForBackwardSlicing)
                    {
                        System.out.println(astNode);
                    }
                    System.out.println("Forward slicing:");
                    for(ASTNode astNode: nodesForForwardSlicing)
                    {
                        System.out.println(astNode);
                    }
                    System.out.println("---------------------------------");
                    nodesForBackwardSlicing.clear();
                    nodesForForwardSlicing.clear();
                }
            }

        });
    }

    public void operations () {
        try {
            parse(readFileToString("src/sourcePackage/Saal.java"));

            //kut(root);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            parser(readFileToString("src/sourcePackage/Saal.java"));

            //kut(root);

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*for(ASTNode n : nodes)
        {
            System.out.println(n);
        }*/

    }
}
