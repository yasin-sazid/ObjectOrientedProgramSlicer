package visGraphPackage.java.examples;

import visGraphPackage.java.api.VisFx;
import visGraphPackage.java.graph.VisEdge;
import visGraphPackage.java.graph.VisGraph;
import visGraphPackage.java.graph.VisNode;
import javafx.application.Application;
import javafx.stage.Stage;

public class TwoNodeGraph extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VisFx.graphNetwork(getDummyGraph(),primaryStage);
    }

    private VisGraph getDummyGraph(){
        VisGraph graph = new VisGraph();
        VisNode node1 = new VisNode(1,"a");
        VisNode node2 = new VisNode(2,"b");
        VisEdge edge = new VisEdge(node1,node2,"to","part_of");
        graph.addNodes(node1,node2);
        graph.addEdges(edge);
        return graph;
    }
}
