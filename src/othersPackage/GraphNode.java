package othersPackage;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import visGraphPackage.java.graph.VisNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class GraphNode {

    public String classFilePath;
    public String classQualifiedName;
    public ASTNode node;
    public Set<GraphNode> parents;
    public Set<GraphNode> children;

    public VisNode visNode = null;

    public GraphNode() {
        parents = new HashSet<GraphNode>();
        children = new HashSet<GraphNode>();
    }

    public GraphNode(ASTNode node) {
        this.node = node;
        parents = new HashSet<GraphNode>();
        children = new HashSet<GraphNode>();
    }

    public ASTNode getNode() {
        return node;
    }

    public void setNode(ASTNode node) {
        this.node = node;
    }

    public Set<GraphNode> getParents() {
        return parents;
    }

    public void setParents(Set<GraphNode> parents) {
        this.parents = parents;
    }

    public Set<GraphNode> getChildren() {
        return children;
    }

    public void setChildren(Set<GraphNode> children) {
        this.children = children;
    }

    public String getClassFilePath() {
        return classFilePath;
    }

    public void setClassFilePath(String classFilePath) {
        this.classFilePath = classFilePath;
    }

    public String getNodeLineString ()
    {
        String nodeLineString = "";

        /*String rootString = node.getRoot().toString();*/

        int lineNumber = ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());

        int counter = 1;

        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(classFilePath));
            String line = reader.readLine();

            while (line != null) {
                if (lineNumber==counter)
                {
                    nodeLineString = line;
                    break;
                }
                // read next line
                counter++;
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Scanner scanner = new Scanner(rootString);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (lineNumber==counter)
            {
                nodeLineString = line;
                break;
            }

            counter++;
            // process the line
        }
        scanner.close();*/

        System.out.println("Node");
        System.out.println(node);
        System.out.println("Node Line");
        System.out.println(lineNumber);
        System.out.println("Line String");
        System.out.println(nodeLineString);

        return nodeLineString;
    }
}
