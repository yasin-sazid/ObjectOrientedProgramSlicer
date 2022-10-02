package sample;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        //directoryChooser.setInitialDirectory(new File("src"));
        FolderProcessor folderProcessor = new FolderProcessor();

        Button button = new Button("Select Project");
        button.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);

            try {
                folderProcessor.setFolder(selectedDirectory.getAbsolutePath());

                ComboBox combo_box =
                        new ComboBox(FXCollections
                                .observableArrayList(folderProcessor.getPathCodeMap().keySet().toArray()));

                // Label to display the selected menuitem
                //Label selected = new Label((String) folderProcessor.getPathCodeMap().keySet().toArray()[0]);

                String selected = folderProcessor.getPathCodeMap().keySet().toArray()[0].toString();
                combo_box.setValue(selected);

                InlineCssTextArea codeArea = new InlineCssTextArea();

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
                            }
                        };

                // Set on action
                combo_box.setOnAction(event);


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


                GridPane root = new GridPane();
                root.add(combo_box, 0, 0);
                root.add(new VirtualizedScrollPane<>(codeArea), 0, 1);

                Scene scene = new Scene(root, 1000, 500);

                scene.getStylesheets().add("java-keywords.css");
                primaryStage.setScene(scene);
                primaryStage.setTitle("Java Keywords Demo");
                primaryStage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(button);
        Scene scene = new Scene(root, 1000, 500);
        scene.getStylesheets().add("java-keywords.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Java Keywords Demo");
        primaryStage.show();
    }
}