/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company;

import Kochfractal.Edge;
import Persistance.EdgeReader;
import Persistance.MappedFileWithLockReader;
import Persistance.SimpleWatchService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import timeutil.TimeStamp;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Nico Kuijpers
 */
@SuppressWarnings("ALL")
public class JSF31KochFractalFXReader extends Application {
    
    // Zoom and drag
    private double zoomTranslateX = 0.0;
    private double zoomTranslateY = 0.0;
    private double zoom = 1.0;
    private double startPressedX = 0.0;
    private double startPressedY = 0.0;
    private double lastDragX = 0.0;
    private double lastDragY = 0.0;
    
    // Current level of Koch fractal
    private int currentLevel = 1;
    
    // Labels for level, nr edges, calculation time, and drawing time
    private Label labelLevel;
    private Label labelNrEdges;
    private Label labelNrEdgesText;
    private Label labelCalc;
    private Label labelCalcText;
    private Label labelDraw;
    private Label labelDrawText;

    // Koch panel and its size
    private Canvas kochPanel;
    private final int kpWidth = 600;
    private final int kpHeight = 600;

    // Edge file reader
    private EdgeReader er = new EdgeReader();

    private SimpleWatchService watcher;
    @Override
    public void start(Stage primaryStage) {
       
        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        // For debug purposes
        // Make de grid lines visible
        // grid.setGridLinesVisible(true);
        
        // Drawing panel for Koch fractal
        kochPanel = new Canvas(kpWidth,kpHeight);
        grid.add(kochPanel, 0, 3, 25, 1);
        
        // Labels to present number of edges for Koch fractal
        labelNrEdges = new Label("Nr edges:");
        labelNrEdgesText = new Label();
        grid.add(labelNrEdges, 0, 0, 4, 1);
        grid.add(labelNrEdgesText, 3, 0, 22, 1);
        
        // Labels to present time of calculation for Koch fractal
        labelCalc = new Label("Calculating:");
        labelCalcText = new Label();
        grid.add(labelCalc, 0, 1, 4, 1);
        grid.add(labelCalcText, 3, 1, 22, 1);
        
        // Labels to present time of drawing for Koch fractal
        labelDraw = new Label("Drawing:");
        labelDrawText = new Label();
        grid.add(labelDraw, 0, 2, 4, 1);
        grid.add(labelDrawText, 3, 2, 22, 1);
        
        // Label to present current level of Koch fractal
        labelLevel = new Label("Level: " + currentLevel);
        grid.add(labelLevel, 0, 6);
        
        // Button to increase level of Koch fractal
        Button buttonReadFile = new Button();
        buttonReadFile.setText("Read file");
        buttonReadFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                readFileButtonActionPerformed(event);
            }
        });
        grid.add(buttonReadFile, 3, 6);

        // Button to decrease level of Koch fractal
        Button buttonReadBinaryFile = new Button();
        buttonReadBinaryFile.setText("read binary file");
        buttonReadBinaryFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                readBinaryFileButtonActionPerformed(event);
            }
        });
        grid.add(buttonReadBinaryFile, 5, 6);

        // Button to clear screen
        Button buttonClearScreen = new Button();
        buttonClearScreen.setText("Clear screen");
        buttonClearScreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearButtonActionPerformed(event);
            }
        });
        grid.add(buttonClearScreen, 7, 6);

        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, kpWidth+70, kpHeight+300);
        root.getChildren().add(grid);
        
        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal Reader");
        primaryStage.setScene(scene);
        primaryStage.show();

        //clearKochPanel();

        new SimpleWatchService(this).start();
    }

    private void clearButtonActionPerformed(ActionEvent event) {
        //clearKochPanel();
        TimeStamp ts = new TimeStamp();
        ts.setBegin("Start mapped");
        er.readFromMappedFile("mapped_8.txt");
        ts.setEnd("finished mapped");
        System.out.println(ts.toString());
        requestDrawEdges();
    }

    private void clearKochPanel() {
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        gc.clearRect(0.0,0.0,kpWidth,kpHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0,0.0,kpWidth,kpHeight);
    }
    
    private void drawEdge(Edge e) {

        // Graphics
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        
        // Adjust edge for zoom and drag
        Edge e1 = edgeAfterZoomAndDrag(e);

        // Set line color
        gc.setStroke(e1.color);
        
        // Set line width depending on level
       gc.setLineWidth(3);
        
        // Draw line
        gc.strokeLine(e1.X1,e1.Y1,e1.X2,e1.Y2);
    }

    public void requestDrawEdges() {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                System.out.println("Drawing edges");
                resetZoom();
                clearKochPanel();
                ArrayList<Edge> edges = er.getEdges();
                for (Edge e: edges) {
                    //System.out.println(e);
                    drawEdge(e);
                }
            }
        });
    }

    public void requestDrawEdge(Edge e) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                System.out.println("Drawing edge");
                resetZoom();
                drawEdge(e);
            }
        });
    }

    private void readFileButtonActionPerformed(ActionEvent event) {
        if (currentLevel < 12) {
            // resetZoom();
            currentLevel++;
            labelLevel.setText("Level: " + currentLevel);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
            if (file != null) {
                if(Objects.equals(getFileExtension(file), "txt")) {
                    System.out.println(file.getAbsolutePath());
                    er.readFromTextFile(showDIalog(), file);
                    requestDrawEdges();
                } else if (Objects.equals(getFileExtension(file), "ser")) {
                    System.out.println(file.getAbsolutePath());
                    er.readFromBinaryFile(showDIalog(), file);
                    requestDrawEdges();
                }
            }
        }
    } 
    
    private void readBinaryFileButtonActionPerformed(ActionEvent event) {
        if (currentLevel < 12) {
            // resetZoom();
            currentLevel++;
            labelLevel.setText("Level: " + currentLevel);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
            if (file != null) {
            }
        }
    }

    private void resetZoom() {
        int kpSize = Math.min(kpWidth, kpHeight);
        zoom = kpSize;
        zoomTranslateX = (kpWidth - kpSize) / 2.0;
        zoomTranslateY = (kpHeight - kpSize) / 2.0;
    }

    private Edge edgeAfterZoomAndDrag(Edge e) {
        return new Edge(
                e.X1 * zoom + zoomTranslateX,
                e.Y1 * zoom + zoomTranslateY,
                e.X2 * zoom + zoomTranslateX,
                e.Y2 * zoom + zoomTranslateY,
                e.color);
    }

    private boolean showDIalog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Buffer usage");
        alert.setHeaderText("Buffer");
        alert.setContentText("Do you want to use a buffer while reading the file?");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeYes){
            return true;
        } else if (result.get() == buttonTypeNo) {
            return false;
        } else {
            return false;
        }
    }

    // Code example from: http://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            System.out.println(name.substring(name.lastIndexOf(".") + 1));
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public void readfileFromWatcher() {
        er.readFromTextFile(true, new File("export\\mapped.bin"));
        requestDrawEdges();
    }

    public void fileIsReader(){
        clearKochPanel();
        Thread t1 = new Thread(new MappedFileWithLockReader(this));
        t1.start();
    }

    public void startWatching(){
        watcher = null;
        watcher = new SimpleWatchService(this);
        watcher.start();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
