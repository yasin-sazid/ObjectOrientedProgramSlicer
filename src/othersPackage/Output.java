package othersPackage;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Output {

    private Set<ASTNode> outputNodes = new TreeSet<>();
    private List<Integer> outputLineNumbers = new ArrayList<>();

    public Output(Set<ASTNode> outputNodes)
    {
        this.outputNodes = outputNodes;
    }

    public void getFormattedOutput ()
    {
        for(ASTNode astNode: this.outputNodes)
        {
            astNode.accept(new ASTVisitor() {

                public void preVisit (ASTNode node) {

                    if(node instanceof Block)
                    {

                    }
                }

            });
        }
    }
}
