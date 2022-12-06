package visGraphPackage.java.gui;

import javafx.scene.image.Image;
import visGraphPackage.java.graph.VisGraph;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class GraphView extends Application {

    public GraphView(VisGraph graph){
        this.graph = graph;
    }

    private VisGraph graph;

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        // create the scene
        Image sliceIcon = new Image(new FileInputStream("resources/slicer-logo.png"));
        stage.setTitle("System Dependence Graph");
        stage.getIcons().add(sliceIcon);
        Scene scene = new Scene(new Browser(graph), 800, 400, Color.web("#666970"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
class Browser extends Region {

    private final WebView browser = new WebView();
    private final WebEngine webEngine = browser.getEngine();
    private VisGraph graph;

    public Browser(VisGraph g) {
        this.graph = g;
        //apply the styles
        getStyleClass().add("browser");
        // load the web page
        webEngine.load((getClass().getClassLoader().getResource("visGraphPackage/resources/baseGraph.html")).toString());
        //add the web view to the scene
        getChildren().add(browser);
        setGraph();

    }

    private void setGraph(){
        String script = "setTheData(" + graph.getNodesJson() +  "," + graph.getEdgesJson() + ")";
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == Worker.State.SUCCEEDED)
                webEngine.executeScript(script);
        });
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double height) {
        return 1000;
    }

    @Override
    protected double computePrefHeight(double width) {
        return 500;
    }
}
