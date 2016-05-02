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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
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
    private int WIDTH_HEIGHT = 8;
    private int SIZE = 600;
    private int TIME = 100;

    private Map<String, StackPane> boardMap = new HashMap<>();
    private Board board;
    Timeline timeline;

    private void startGame(Stage primaryStage) {
        timeline = new Timeline(new KeyFrame(Duration.ZERO, event -> iterateBoard()), new KeyFrame(Duration.millis(TIME)));

        timeline.setCycleCount(Timeline.INDEFINITE);
        board = new Board(SIZE / WIDTH_HEIGHT);
        int SETTING_SIZE = SIZE+270;
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
        clearBtn.setLayoutX(SIZE + 150);
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
        randomBtn.setLayoutY(225);
        randomBtn.setOnMousePressed(event -> randomBoard2());
        randomBtn.getStyleClass().add("random");

        Button stopBtn = new Button();
        stopBtn.setText("Stop");
        root.getChildren().add(stopBtn);
        stopBtn.setLayoutX(SIZE + 150);
        stopBtn.setLayoutY(25);
        stopBtn.setOnMousePressed(event -> timeline.stop());
        stopBtn.getStyleClass().add("stop");

        javafx.scene.control.TextField sizeField = new javafx.scene.control.TextField();
        sizeField.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                timeline.stop();
                String stringSize = sizeField.getText();
                SIZE = Integer.parseInt(stringSize);
                startGame(primaryStage);
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
                WIDTH_HEIGHT = Integer.parseInt(stringSize);
                startGame(primaryStage);
            }
        });
        root.getChildren().add(cellField);
        cellField.setLayoutX(SIZE+10);
        cellField.setLayoutY(175);
        cellField.setPromptText("Enter cell size (low values may cause lag)");
        cellField.getStyleClass().add("cellField");

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

        primaryStage.setTitle("Game of Life");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinHeight(510);
        primaryStage.setMinWidth(SIZE+270);

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
    }

    void iterateBoard() {
        board.nextGeneration();
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
    public static void main(String[] args) {
        launch(args);
    }
}