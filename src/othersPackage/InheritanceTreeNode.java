package othersPackage;

import java.util.HashSet;
import java.util.Set;

public class InheritanceTreeNode {

    public String node;
    public Set<InheritanceTreeNode> parents;
    public Set<InheritanceTreeNode> children;

    public InheritanceTreeNode(String node) {
        this.node = node;
        this.parents = new HashSet<>();
        this.children = new HashSet<>();
    }

    public InheritanceTreeNode() {
        this.parents = new HashSet<>();
        this.children = new HashSet<>();
    }
}
