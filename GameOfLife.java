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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.glass.ui.CommonDialogs;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private static final int WIDTH_HEIGHT = 8;
    private static final int SIZE = 600;
    public static final int CELLCOUNT = SIZE / WIDTH_HEIGHT;
    private static final int TIME = 80;
    private static final int SETTING_SIZE = SIZE+170;

    private Map<String, StackPane> boardMap = new HashMap<>();
    private Board board = new Board(SIZE / WIDTH_HEIGHT);

    private FileChooser fileChooser;
    public File file;
    private Desktop desktop = Desktop.getDesktop();

    /**
     *
     * @param primaryStage starts a game with the board defined in Board.j ava
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        final Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, event -> iterateBoard()), new KeyFrame(Duration.millis(TIME)));

        timeline.setCycleCount(Timeline.INDEFINITE);


        /**
         * The boards below which are commented out are just for testing purposes. It is shapes that I know how will behave
         * to test the rules of the game
         * @see checkBoard to see the rules
         */
        //board.randomBoard(0.5);
        //board.firstShape(1);
        //board.secondShape(1);
        //board.thirdShape(1);
        //board.fourthShape(1);
        //board.fifthShape(1);
        //board.sixthShape(1);

        Pane root = new Pane();
        Scene scene = new Scene(root, SETTING_SIZE, SIZE);
        scene.getStylesheets().add("gol/gol.css");

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

            Button stepBtn = new Button();
            stepBtn.setText("STEP");
            root.getChildren().add(stepBtn);
            stepBtn.setLayoutX(SIZE + 10);
            stepBtn.setLayoutY(25);
            stepBtn.setOnMousePressed(event -> iterateBoard());

            Button playBtn = new Button();
            playBtn.setText("Play");
            root.getChildren().add(playBtn);
            playBtn.setLayoutX(SIZE + 10);
            playBtn.setLayoutY(75);
            playBtn.setOnMousePressed(event -> timeline.play());

            Button stopBtn = new Button();
            stopBtn.setText("Stop");
            root.getChildren().add(stopBtn);
            stopBtn.setLayoutX(SIZE + 50);
            stopBtn.setLayoutY(75);
            stopBtn.setOnMousePressed(event -> timeline.stop());

            TextField field = new TextField();
            field.setOnKeyPressed(new EventHandler<KeyEvent>(){
                @Override
                public void handle(KeyEvent e){
                    if(e.getCode() == KeyCode.ENTER){
                        board.RunLengthEncoding(field.getText());
                    }
                }
            });
            root.getChildren().add(field);
            field.setLayoutX(SIZE+1);
            field.setLayoutY(115);

            fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*txt"),
                    new FileChooser.ExtensionFilter("All files", "*.*")
            );

            Button browse = new Button();
            browse.setText("Browse");
            root.getChildren().add(browse);
            browse.setLayoutX(SIZE + 50);
            browse.setLayoutY(150);
            browse.setOnAction(e -> {
                file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    try {
                        desktop.open(file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }

        primaryStage.setTitle("Game of Life");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void iterateBoard() {
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