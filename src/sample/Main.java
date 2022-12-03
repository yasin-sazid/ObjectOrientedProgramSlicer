package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertexNode;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.stage.StageStyle;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.ListModification;
import org.reactfx.Subscription;
import othersPackage.GraphNode;
import othersPackage.SDG;
import visGraphPackage.java.api.VisFx;
import visGraphPackage.java.graph.VisEdge;
import visGraphPackage.java.graph.VisGraph;
import visGraphPackage.java.graph.VisNode;

public class Main extends Application {

    Map<String, Set<Integer>> forwardSlicingMapForClassLineNumbers;
    Map<String, Set<Integer>> backwardSlicingMapForClassLineNumbers;

    DirectoryChooser directoryChooser = new DirectoryChooser();
    //directoryChooser.setInitialDirectory(new File("src"));
    FolderProcessor folderProcessor = new FolderProcessor();

    File selectedDirectory;

    ComboBox combo_box =
            new ComboBox(FXCollections
                    .observableArrayList(folderProcessor.getPathCodeMap().keySet().toArray()));

    InlineCssTextArea codeArea = new InlineCssTextArea();

    TextField lineNumber = new TextField("Enter Line Number");

    String slicingType = "Backward Slicing";

    SDG sdg;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //primaryStage.setResizable(false);
        showHome(primaryStage);
    }

    public void showGraph (Stage primaryStage)
    {
        Digraph<String, String> g = new DigraphEdgeList<>();

        g.insertVertex("A");
        g.insertVertex("A");
        g.insertVertex("B");
        g.insertVertex("C");
        g.insertVertex("D");
        g.insertVertex("E");
        g.insertVertex("F");

        g.insertEdge("A", "B", "AB");
        g.insertEdge("B", "A", "AB2");
        g.insertEdge("A", "C", "AC");
        g.insertEdge("A", "D", "AD");
        g.insertEdge("B", "C", "BC");
        g.insertEdge("C", "D", "CD");
        g.insertEdge("B", "E", "BE");
        g.insertEdge("F", "D", "DF");
        g.insertEdge("F", "D", "DF2");

        //g.insertEdge("A", "A", "Loop");

        //g.ge

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(g, strategy);
        Scene scene = new Scene(graphView, 1024, 768);

        primaryStage.setTitle("JavaFXGraph Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
        //IMPORTANT - Called after scene is displayed so we can have width and height values
        graphView.init();
    }

    public void sliceProject(Stage primaryStage) {
        //System.out.println(lineNumber.getCharacters().toString());

        String criterionInput = lineNumber.getCharacters().toString();

        try {

            int criterionLineNumber = Integer.parseInt(criterionInput);

            codeArea.clear();
            for (int i = 0; i < folderProcessor.getPathCodeMap().get(combo_box.getValue()).length; i++) {
                if (i + 1 == criterionLineNumber) {
                    codeArea.replace(codeArea.getLength(), codeArea.getLength(), folderProcessor.getPathCodeMap().get(combo_box.getValue())[i], "-rtfx-background-color: red;");
                    codeArea.replace(codeArea.getLength(), codeArea.getLength(), "\n", "");
                } else {
                    codeArea.replace(codeArea.getLength(), codeArea.getLength(), folderProcessor.getPathCodeMap().get(combo_box.getValue())[i], "-rtfx-background-color: white;");
                    codeArea.replace(codeArea.getLength(), codeArea.getLength(), "\n", "");
                }
            }

            ComboBox combo_box2 =
                    new ComboBox(FXCollections
                            .observableArrayList(folderProcessor.getPathCodeMap().keySet().toArray()));

            // Label to display the selected menuitem
            //Label selected = new Label((String) folderProcessor.getPathCodeMap().keySet().toArray()[0]);

            String selected2 = combo_box.getValue().toString();

            try {
                sdg = new SDG(folderProcessor.getFolder().getAbsolutePath(), selected2, criterionLineNumber);
                createGraph(primaryStage, sdg.sdgRoot);
                if (sdg.isValidCriterion()) {
                    backwardSlicingMapForClassLineNumbers = sdg.getBackwardSlicingMapForClassLineNumbers();
                    forwardSlicingMapForClassLineNumbers = sdg.getForwardSlicingMapForClassLineNumbers();
                } else {
                    throw new NumberFormatException("Invalid Criterion");
                }
            } catch (IOException ex) {

            }

            combo_box2.setValue(selected2);

            InlineCssTextArea codeArea2 = new InlineCssTextArea();

            codeArea2.setPrefSize(500, 500);
            combo_box2.setPrefWidth(500);

            String[] lines2 = folderProcessor.getPathCodeMap().get(selected2);

            for (int i = 0; i < lines2.length; i++) {
                if (slicingType.equals("Backward Slicing")) {
                                /*System.out.println(slicingType);
                                System.out.println("dhukesi");*/
                    if (backwardSlicingMapForClassLineNumbers.get(selected2).contains(i + 1)) {
                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: red;");
                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                    } else {
                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: white;");
                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                    }
                }
                if (slicingType.equals("Forward Slicing")) {
                    if (forwardSlicingMapForClassLineNumbers.get(selected2).contains(i + 1)) {
                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: red;");
                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                    } else {
                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: white;");
                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                    }
                }

            }

            // add line numbers to the left of area
            codeArea2.setParagraphGraphicFactory(LineNumberFactory.get(codeArea2));

            // Create action event
            EventHandler<ActionEvent> event2 =
                    new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent e) {
                            String[] lines2 = folderProcessor.getPathCodeMap().get(combo_box2.getValue());

                            codeArea2.clear();

                            for (int i = 0; i < lines2.length; i++) {
                                if (slicingType.equals("Backward Slicing")) {
                                    if (backwardSlicingMapForClassLineNumbers.containsKey(combo_box2.getValue())) {
                                        if (backwardSlicingMapForClassLineNumbers.get(combo_box2.getValue()).contains(i + 1)) {
                                            codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: red;");
                                            codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                                        } else {
                                            codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: white;");
                                            codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                                        }
                                    } else {
                                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: white;");
                                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                                    }
                                }
                                if (slicingType.equals("Forward Slicing")) {
                                    if (forwardSlicingMapForClassLineNumbers.containsKey(combo_box2.getValue())) {
                                        if (forwardSlicingMapForClassLineNumbers.get(combo_box2.getValue()).contains(i + 1)) {
                                            codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: red;");
                                            codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                                        } else {
                                            codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: white;");
                                            codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                                        }
                                    } else {
                                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), lines2[i], "-rtfx-background-color: white;");
                                        codeArea2.replace(codeArea2.getLength(), codeArea2.getLength(), "\n", "");
                                    }
                                }
                            }
                            //selected.setText((String) combo_box.getValue());
                            //codeArea.replace(0, 0, folderProcessor.getPathCodeMap().get(combo_box.getValue()),"");
                        }
                    };

            // Set on action
            combo_box2.setOnAction(event2);


            // auto-indent: insert previous line's indents on enter
            final Pattern whiteSpace2 = Pattern.compile("^\\s+");
            codeArea2.addEventHandler(KeyEvent.KEY_PRESSED, KE ->
            {
                if (KE.getCode() == KeyCode.ENTER) {
                    int caretPosition = codeArea2.getCaretPosition();
                    int currentParagraph = codeArea2.getCurrentParagraph();
                    Matcher m0 = whiteSpace2.matcher(codeArea2.getParagraph(currentParagraph - 1).getSegments().get(0));
                    if (m0.find()) Platform.runLater(() -> codeArea2.insertText(caretPosition, m0.group()));
                }
            });


            codeArea2.getStylesheets().add("java-keywords.css");

            Button newProject = new Button("New Project");
            newProject.setOnAction(newProjectEvent -> {
                openNewProject(primaryStage);
            });

            newProject.setPrefSize(500, 30);

            Button newSlice = new Button("New Slice");
            newSlice.setOnAction(newSliceEvent -> {
                showGraph(primaryStage);
            });

            newSlice.setPrefSize(500, 30);

            GridPane root = new GridPane();
            root.add(combo_box, 0, 0);
            root.add(new VirtualizedScrollPane<>(codeArea), 0, 1);
            root.add(newSlice, 0, 2);
            root.add(combo_box2, 1, 0);
            root.add(new VirtualizedScrollPane<>(codeArea2), 1, 1);
            root.add(newProject, 1, 2);

            Scene scene = new Scene(root, 1000, 500);

            scene.getStylesheets().add("java-keywords.css");
            primaryStage.setScene(scene);
            primaryStage.setTitle("OOPSlicer");
            primaryStage.show();
        } catch (NumberFormatException ex) {
            if (ex.getMessage().equals("Invalid Criterion")) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Please input a valid slicing criterion");
                a.show();
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Please input a valid integer line number");
                a.show();
            }
        }
    }

    public void showNewProject(Stage primaryStage)
    {
        try {
            folderProcessor.setFolder(selectedDirectory.getAbsolutePath());

            combo_box =
                    new ComboBox(FXCollections
                            .observableArrayList(folderProcessor.getPathCodeMap().keySet().toArray()));

            // Label to display the selected menuitem
            //Label selected = new Label((String) folderProcessor.getPathCodeMap().keySet().toArray()[0]);

            String selected = folderProcessor.getPathCodeMap().keySet().toArray()[0].toString();
            combo_box.setValue(selected);

            codeArea = new InlineCssTextArea();

            codeArea.setPrefSize(500, 500);
            combo_box.setPrefWidth(500);

            String [] lines = folderProcessor.getPathCodeMap().get(selected);

            for (int i=0; i<lines.length; i++)
            {
                codeArea.replace(codeArea.getLength(), codeArea.getLength(), lines[i], "");
                codeArea.replace(codeArea.getLength(), codeArea.getLength(), "\n", "");
            }

            // add line numbers to the left of area
            codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));


            // auto-indent: insert previous line's indents on enter
            final Pattern whiteSpace = Pattern.compile( "^\\s+" );
            codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
            {
                if ( KE.getCode() == KeyCode.ENTER ) {
                    int caretPosition = codeArea.getCaretPosition();
                    int currentParagraph = codeArea.getCurrentParagraph();
                    Matcher m0 = whiteSpace.matcher( codeArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
                    if ( m0.find() ) Platform.runLater( () -> codeArea.insertText( caretPosition, m0.group() ) );
                }
            });


            codeArea.getStylesheets().add("java-keywords.css");

        /*codeArea.replace(0,0,sampleCode2, "-rtfx-background-color: red;");

        codeArea.replace(1, 1, sampleCode, "-rtfx-background-color: white;");*/

            lineNumber = new TextField("Enter Line Number");

            Button slicer = new Button();
            Image slicerImage = new Image(new FileInputStream("resources/slice.png"));
            ImageView slicerImageView = new ImageView(slicerImage);
            slicerImageView.setSmooth(true);
            slicerImageView.setFitHeight(20);
            slicerImageView.setPreserveRatio(true);
            slicerImageView.setFitWidth(150);
            //slicerImageView.setFitWidth(150);
            slicer.setGraphic(slicerImageView);
            slicer.setOnAction(slicingEvent -> {
                sliceProject(primaryStage);
            });

            // Create action event
            EventHandler<ActionEvent> event =
                    new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent e)
                        {
                            String [] lines = folderProcessor.getPathCodeMap().get(combo_box.getValue());

                            codeArea.clear();

                            for (int i=0; i<lines.length; i++)
                            {
                                codeArea.replace(codeArea.getLength(), codeArea.getLength(), lines[i], "");
                                codeArea.replace(codeArea.getLength(), codeArea.getLength(), "\n", "");
                            }
                            //selected.setText((String) combo_box.getValue());
                            //codeArea.replace(0, 0, folderProcessor.getPathCodeMap().get(combo_box.getValue()),"");

                            ComboBox slicingOperation =
                                    new ComboBox(FXCollections
                                            .observableArrayList(new String[]{"Backward Slicing", "Forward Slicing"}));

                            slicingOperation.setValue(slicingType);

                            EventHandler<ActionEvent> selectSlicingOperation =
                                    new EventHandler<ActionEvent>() {
                                        public void handle(ActionEvent e)
                                        {
                                            slicingType = slicingOperation.getValue().toString();
                                            //System.out.println(slicingType);
                                        }
                                    };

                            slicingOperation.setOnAction(selectSlicingOperation);

                            VBox vBox = new VBox();

                            vBox.setPadding(new Insets(150));

                            slicingOperation.setPrefSize(150, 30);
                            slicer.setPrefSize(150, 30);
                            lineNumber.setPrefSize(150, 30);

                            vBox.getChildren().add(lineNumber);
                            vBox.setMargin(lineNumber, new Insets(10));
                            vBox.getChildren().add(slicingOperation);
                            vBox.setMargin(slicingOperation, new Insets(10));
                            vBox.getChildren().add(slicer);
                            vBox.setMargin(slicer, new Insets(10));

                            GridPane root = new GridPane();
                            root.add(combo_box, 0, 0);
                            root.add(new VirtualizedScrollPane<>(codeArea), 0, 1);
                            root.add(vBox, 1, 1);

                            Scene scene = new Scene(root, 1000, 500);

                            scene.getStylesheets().add("java-keywords.css");
                            primaryStage.setScene(scene);
                            primaryStage.setTitle("OOPSlicer");
                            primaryStage.show();
                        }
                    };

            // Set on action
            combo_box.setOnAction(event);


            ComboBox slicingOperation =
                    new ComboBox(FXCollections
                            .observableArrayList(new String[]{"Backward Slicing", "Forward Slicing"}));

            slicingOperation.setValue(slicingType);

            EventHandler<ActionEvent> selectSlicingOperation =
                    new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent e)
                        {
                            slicingType = slicingOperation.getValue().toString();
                            //System.out.println(slicingType);
                        }
                    };

            slicingOperation.setOnAction(selectSlicingOperation);


            VBox vBox = new VBox();

            vBox.setPadding(new Insets(150));

                /*slicingOperation.setMinHeight(30);
                slicingOperation.setMinWidth(100);*/

            slicingOperation.setPrefSize(150, 30);
            slicer.setPrefSize(150, 30);
            lineNumber.setPrefSize(150, 30);

                /*slicer.setMinHeight(30);
                slicer.setMinWidth(100);

                lineNumber.setMinHeight(30);
                lineNumber.setMinWidth(100);*/

            vBox.getChildren().add(lineNumber);
            vBox.setMargin(lineNumber, new Insets(10));
            vBox.getChildren().add(slicingOperation);
            vBox.setMargin(slicingOperation, new Insets(10));
            vBox.getChildren().add(slicer);
            vBox.setMargin(slicer, new Insets(10));

            GridPane root = new GridPane();
            root.add(combo_box, 0, 0);
            root.add(new VirtualizedScrollPane<>(codeArea), 0, 1);
            root.add(vBox, 1, 1);
                /*root.add(lineNumber, 2, 1);
                root.add(slicingOperation, 2, 2);
                root.add(slicer, 2, 3);*/

            Scene scene = new Scene(root, 1000, 500);

            scene.getStylesheets().add("java-keywords.css");
            primaryStage.setScene(scene);
            primaryStage.setTitle("OOPSlicer");
            primaryStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void openNewProject (Stage primaryStage)
    {
        File sd = directoryChooser.showDialog(primaryStage);
        if (sd!=null)
        {
            selectedDirectory = sd;
            showNewProject(primaryStage);
        }
    }

    public void showHome (Stage primaryStage) throws FileNotFoundException {

        Image image = new Image(new FileInputStream("resources/oopslicer-logo-cropped.png"));
        /*image.isPreserveRatio()*/
        Rectangle rectangle = new Rectangle();
        rectangle.setFill(new ImagePattern(image));
        ImageView imageView = new ImageView(image);
        //alert.setGraphic(dp);

        Button button = new Button("Select Project");
        button.setOnAction(e -> {
            openNewProject(primaryStage);
        });

        VBox vBox = new VBox();

        //VisGraph as = new VisGraph ();

        imageView.setFitWidth(1000);
        imageView.setPreserveRatio(true);
        //imageView.setFitHeight(200);
        button.setPrefSize(150, 30);
        vBox.getChildren().add(imageView);
        vBox.setMargin(imageView, new Insets(50, 0, 0, 0));
        vBox.getChildren().add(button);
        vBox.setMargin(button, new Insets(10, 425, 110, 425));

        AnchorPane root = new AnchorPane();

        root.getChildren().add(vBox);
        Scene scene = new Scene(root, 1000, 500);
        scene.getStylesheets().add("java-keywords.css");
        //scene.setFill(Color.WHITE);
        root.setStyle("-fx-background-color: white;");
        primaryStage.setScene(scene);
        primaryStage.setTitle("OOPSlicer");
        primaryStage.getIcons().add(image);
        primaryStage.show();
    }

    public static int nodeCounter = 0;

    public static Set<VisNode> visited = new HashSet<>();

    public static void createEdge(VisGraph graph, Stage primaryStage, VisNode node1, GraphNode root)
    {

        for (GraphNode currentNode: root.children)
        {
            /*int pickNode = 1;
            for (VisNode n: visited)
            {
                if (currentNode.node.getStartPosition()==n.node.getStartPosition() && currentNode.getNodeLineString().equals(n.getLabel()))
                {
                    pickNode = 0;
                }
            }*/

            if (currentNode.visNode!=null)
            {
                VisNode node2 = graph.getNode(currentNode.visNode.getId()); //new VisNode(nodeCounter++,currentNode.getNodeLineString());
                //Add an edge
                VisEdge edge = new VisEdge(node1,node2,"to","");
                //Add nodes and edges to the graph.
                graph.addNodes(node1,node2);
                graph.addEdges(edge);

                //visited.add(currentNode);
            }
            else
            {
                VisNode node2 = new VisNode(nodeCounter++,currentNode.getNodeLineString());
                //Add an edge
                VisEdge edge = new VisEdge(node1,node2,"to","");
                //Add nodes and edges to the graph.
                graph.addNodes(node1,node2);
                graph.addEdges(edge);

                currentNode.visNode = node2;

                if (currentNode.children.size()!=0)
                {
                    createEdge(graph, primaryStage, node2, currentNode);
                }
            }
        }
    }

    public static void createGraph (Stage primaryStage, GraphNode root)
    {
        VisGraph graph = new VisGraph();

        //Create the nodes

        VisNode node1 = new VisNode(nodeCounter++,"Enter");

        createEdge(graph, primaryStage, node1, root);

        //Graph the network passing the graph itself and a JavaFX Stage.
        VisFx.graphNetwork(graph,primaryStage);
    }
}