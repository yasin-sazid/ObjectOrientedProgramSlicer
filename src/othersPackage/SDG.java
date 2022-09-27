package othersPackage;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

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

        for (GraphNode classRoot: classRoots)
        {
            for (GraphNode classRoot2: classRoots)
            {
                System.out.println(classRoot.node);
                System.out.println(classRoot2.node);
                System.out.println("--------------");
                if (((TypeDeclaration) classRoot.getNode()).getSuperclassType()!=null)
                {
                    System.out.println("Dhukesi1");
                    String cRsuperclass = ((TypeDeclaration) classRoot.getNode()).getSuperclassType().toString();
                    String cR2class = ((TypeDeclaration)classRoot2.getNode()).getName().toString();

                    System.out.println(cRsuperclass);
                    System.out.println(cR2class);

                    if (cRsuperclass.equals(cR2class))
                    {
                        /*for (GraphNode node: classRoot2.children)
                        {
                            if (node.getNode())
                        }*/
                    }
                }
            }
        }



        //debug system->class->method
        /*for (GraphNode classRoot: sdgRoot.children)
        {
            System.out.println(classRoot.node);

            for (GraphNode methodRoot: classRoot.children)
            {
                System.out.println(methodRoot.node);
            }
        }*/
    }
}
