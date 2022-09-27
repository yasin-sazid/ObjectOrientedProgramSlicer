package othersPackage;

import org.eclipse.jdt.core.dom.ITypeBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDG
{
    public GraphNode sdgRoot;
    public List<GraphNode> classRoots = new ArrayList<>();
    Map<ITypeBinding,GraphNode> mapForClassBinding = new HashMap<>();

    public SDG() {
        this.sdgRoot = new GraphNode();
    }

    public void operations ()
    {
        FolderProcessor folderProcessor = new FolderProcessor("src/sourcePackage");

        for (File javaFile: folderProcessor.getFiles())
        {
            System.out.println(javaFile);
            Operation op = new Operation();
            op.operations(javaFile.getAbsolutePath());
            classRoots.add(op.root);
        }

        for (GraphNode classRoot: classRoots)
        {
            sdgRoot.getChildren().add(classRoot);
            classRoot.getParents().add(sdgRoot);
        }

        for (GraphNode classRoot: sdgRoot.children)
        {
            System.out.println(classRoot.node);
        }
    }
}
