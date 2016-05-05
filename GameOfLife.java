/**
 * Game Of Life is a cellular automaton devised by the Britsh Mathematician John Conway in 1970.
 * The game is a zero-player game, meaning that the game does not require any further input after the initial settings.
 *
 * @author Haakon Lien
 * @author Hieu Tan Duong
 * @author Vegard Sørlie
 * @since
 *
 */

package gol;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameOfLife extends Application {

    /**
     * All of these are intended for the user to edit in settings except the SETTINGSSIZE
     * WIDTH_HEIGHT is the size of each cell.
     * SIZE is the size of the board. A board size of 400 would mean that the board is (SIZE/WIDTH_HEIGHT) in size.
     * TIME is the time in ms that it takes between generations. Less time = faster and smoother animation.
     * SETTINGSSIZE is used to set the size of the pane outside the board (where user-defined settings are displayed).
     */
    public static int WIDTH_HEIGHT = 8;
    public static int SIZE = 600;
    private int TIME = 100;
    public static int CELLCOUNT;

    private Map<String, StackPane> boardMap = new HashMap<>();
    private Board board;
    Timeline timeline;

    private FileChooser fileChooser;
    public File file;

    private void startGame(Stage primaryStage) {
        timeline = new Timeline(new KeyFrame(Duration.ZERO, event -> iterateBoard()), new KeyFrame(Duration.millis(TIME)));

        timeline.setCycleCount(Timeline.INDEFINITE);
        board = new Board(SIZE / WIDTH_HEIGHT);
        int SETTING_SIZE = SIZE+310;
        /**
         * The boards below which are commented out are just for testing purposes. It is shapes that I know how will behave
         * to test the rules of the game
         * @see checkBoard to see the rules
         */
        //board.firstShape(1);
        //board.secondShape(1);
        //board.thirdShape(1);
        //board.fourthShape(1);
        //board.fifthShape(1);
        //board.sixthShape(1);

        Pane root = new Pane();
        Scene scene = new Scene(root, SETTING_SIZE, SIZE);
        scene.getStylesheets().add("gol/gol.css");

        Button stepBtn = new Button();
        stepBtn.setText("Step");
        root.getChildren().add(stepBtn);
        stepBtn.setLayoutX(SIZE + 10);
        stepBtn.setLayoutY(75);
        stepBtn.setOnMousePressed(event -> stepBoard());
        stepBtn.getStyleClass().add("step");

        Button clearBtn = new Button();
        clearBtn.setText("Clear");
        root.getChildren().add(clearBtn);
        clearBtn.setLayoutX(SIZE + 170);
        clearBtn.setLayoutY(75);
        clearBtn.setOnMousePressed(event -> clearBoard());
        clearBtn.getStyleClass().add("clear");

        Button playBtn = new Button();
        playBtn.setText("Play");
        root.getChildren().add(playBtn);
        playBtn.setLayoutX(SIZE + 10);
        playBtn.setLayoutY(25);
        playBtn.setOnMousePressed(event -> timeline.play());
        playBtn.getStyleClass().add("play");

        Button randomBtn = new Button();
        randomBtn.setText("Create Random Board");
        root.getChildren().add(randomBtn);
        randomBtn.setLayoutX(SIZE+10);
        randomBtn.setLayoutY(345);
        randomBtn.setOnMousePressed(event -> randomBoard2());
        randomBtn.getStyleClass().add("random");

        Button stopBtn = new Button();
        stopBtn.setText("Stop");
        root.getChildren().add(stopBtn);
        stopBtn.setLayoutX(SIZE + 170);
        stopBtn.setLayoutY(25);
        stopBtn.setOnMousePressed(event -> timeline.stop());
        stopBtn.getStyleClass().add("stop");

        javafx.scene.control.TextField sizeField = new javafx.scene.control.TextField();
        sizeField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                timeline.stop();
                String stringSize = sizeField.getText();
                int stringToInt = Integer.parseInt(stringSize);
                if (stringToInt >= 10 && stringToInt <= 900){
                    SIZE = stringToInt;
                    if (SIZE % WIDTH_HEIGHT == 0){
                        startGame(primaryStage);
                    }
                    else {
                        int inf = SIZE;
                        int i = SIZE % WIDTH_HEIGHT;
                        int i2 = SIZE - i;
                        SIZE = i2;
                        startGame(primaryStage);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Whoops!");
                        alert.setContentText("The board-size ("+inf+") is not dividable by your entered cell-size ("+WIDTH_HEIGHT+"). So it got re-adjusted to "+i2+".");
                        alert.showAndWait();
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Whoops!");
                    alert.setContentText("Please input a size between 10 and "+(900)+". Board size has been reset to default");
                    alert.showAndWait();
                    startGame(primaryStage);
                }
            }
        });
        root.getChildren().add(sizeField);
        sizeField.setLayoutX(SIZE+10);
        sizeField.setLayoutY(125);
        sizeField.setPromptText("Enter board size (e.g 800)");
        sizeField.getStyleClass().add("sizeField");

        javafx.scene.control.TextField cellField = new javafx.scene.control.TextField();
        cellField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                String stringSize = cellField.getText();
                int stringToInt = Integer.parseInt(stringSize);
                if (stringToInt >= 2 && stringToInt <= 100){
                    if (SIZE % stringToInt == 0){
                        WIDTH_HEIGHT = stringToInt;
                        startGame(primaryStage);
                    }
                    else {
                        int inf = SIZE;
                        int i = SIZE % stringToInt;
                        int i2 = SIZE - i;
                        SIZE = i2;
                        WIDTH_HEIGHT = stringToInt;
                        startGame(primaryStage);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Whoops!");
                        alert.setContentText("The board-size ("+inf+") is not dividable by your entered integer ("+stringToInt+"). So it got re-adjusted to "+i2+".");
                        alert.showAndWait();
                    }
                }
                else {
                    WIDTH_HEIGHT = 8;
                    startGame(primaryStage);
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Whoops!");
                    alert.setContentText("The size of the cell must be between 2 and 100.");
                    alert.showAndWait();
                }
            }
        });
        root.getChildren().add(cellField);
        cellField.setLayoutX(SIZE+10);
        cellField.setLayoutY(175);
        cellField.setPromptText("Enter cell size (low values may cause lag)");
        cellField.getStyleClass().add("cellField");

        TextField field = new TextField();
        field.setPromptText("Paste URL address for RLE here and press enter");
        field.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                clearBoard();
                board.RunLengthEncoding(field.getText());
                iterateBoard();
            }
        });
        root.getChildren().add(field);
        field.setLayoutX(SIZE+10);
        field.setLayoutY(225);
        field.getStyleClass().add("field");

        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        Button browse = new Button();
        browse.setText("Browse Computer");
        root.getChildren().add(browse);
        browse.setLayoutX(SIZE + 10);
        browse.setLayoutY(275);
        browse.getStyleClass().add("browse");
        browse.setOnAction(e -> {
            file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                clearBoard();
                String filePath = file.getAbsolutePath();
                board.RunLengthEncodingFromFile(filePath);
                iterateBoard();
            }
        });

        /**
         * the for-loop below creates a board with dead cells
         */
        for (int x = 0; x < SIZE; x = x + WIDTH_HEIGHT) {
            for (int y = 0; y < SIZE; y = y + WIDTH_HEIGHT) {
                StackPane cell = StackPaneBuilder.create().layoutX(x).layoutY(y).prefHeight(WIDTH_HEIGHT).prefWidth(WIDTH_HEIGHT).styleClass("dead").build();
                root.getChildren().add(cell);

                /**
                 * Stores the cells in a hashmap so it's easier for the iterateBoard method to access it.
                 */
                boardMap.put(x + " " + y, cell);
            }

        }

        root.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {

                //Under får vi X og Y kordinatene til musen når "click"
                String msg =
                        "(x: "       + event.getX()/WIDTH_HEIGHT      + ", y: "       + event.getY()/WIDTH_HEIGHT       + ") -- " +
//                                "(sceneX: "  + event.getSceneX() + ", sceneY: "  + event.getSceneY()  + ") -- " +
//                                "(screenX: " + event.getScreenX()+ ", screenY: " + event.getScreenY()
                                ")";

                int x = (int) Math.floor(event.getX())/WIDTH_HEIGHT;
                int y = (int) Math.floor(event.getY())/WIDTH_HEIGHT;


                //Bruke X og Y koordinatene for finne ut hvilke celler som skal settes i live
                //Trenger en metode for å sette Cellene i live,
                System.out.println(msg);
                System.out.println("x = " + x);
                System.out.println("y = " + y);

                if (x > SIZE / WIDTH_HEIGHT || y > SIZE / WIDTH_HEIGHT || x < 0 || y < 0) {

                } else {
                    board.setStateOnClick(x, y);
                    updateBoard();
                }
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {

                int x = (int) Math.floor(event.getX())/WIDTH_HEIGHT;
                int y = (int) Math.floor(event.getY())/WIDTH_HEIGHT;

                if (x > SIZE / WIDTH_HEIGHT || y > SIZE / WIDTH_HEIGHT || x < 0 || y < 0) {

                } else {
                    board.setStateOnClick(x, y);
                    updateBoard();
                }
            }
        });

        primaryStage.setTitle("Game of Life");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(SIZE+310);

    }

    /**
     *
     * @param primaryStage starts a game with the board defined in Board.j ava
     */
    @Override
    public void start(Stage primaryStage) throws IOException{
        startGame(primaryStage);
    }

    private void stepBoard() {
        timeline.stop();
        iterateBoard();
    }

    private void randomBoard2() {
        board.randomBoard(0.5);
        iterateBoard();
    }

    private void clearBoard(){
        board.randomBoard(0);
        iterateBoard();
        iterateBoard();
        timeline.stop();
    }

    void updateBoard() {
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                StackPane pane = boardMap.get(x * WIDTH_HEIGHT + " " + y * WIDTH_HEIGHT);
                pane.getStyleClass().clear();
                // If the cell is a alive (=1) use css styling 'alive'
                // otherwise use the styling 'dead' (=0).
                if (board.getField(x, y) == 1) {
                    pane.getStyleClass().add("alive");
                } else {
                    pane.getStyleClass().add("dead");
                }
            }
        }
    }

    void iterateBoard() {
        board.nextGeneration();
        updateBoard();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
