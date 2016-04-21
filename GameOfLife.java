package gol;

import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameOfLife extends Application {

    //WIDTH_HEIGHT should not be changed by the user
    private static final int WIDTH_HEIGHT = 10;

    private static final int SIZE = 400;
    //board size will then be WIDTH_HEIGHT/SIZE
    //SETTINGSSIZE is used to set the size of the big pane outside the gameboardpane
    private static final int TIME = 80;
    
    private static final int SETTINGSSIZE = SIZE+100;

    private Map<String, StackPane> boardMap = new HashMap<>();
    private Board board = new Board(SIZE /WIDTH_HEIGHT);

    @Override
    public void start(Stage primaryStage) {
        final Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new EventHandler() {
            @Override
            public void handle(Event event) {
                iterateBoard();
            }
        }), new KeyFrame(Duration.millis(TIME)));

        timeline.setCycleCount(Timeline.INDEFINITE);

        board.randomBoard(0.5);
        //board.firstShape(0.5);
        //board.secondShape(0.5);
        //board.thirdShape(0.5);
        //board.fourthShape(0.5);
        //board.fifthShape(0.5);
        //board.sixthShape(0.5);

        //New pane, Settingswindow with buttons(?)
        Pane settings = new Pane();
        Scene settingsscene = new Scene(settings, SETTINGSSIZE, SETTINGSSIZE);
        settingsscene.getStylesheets().add("gol/settings.css");
        Button button1 = new Button("Play/Pause");

        Pane root = new Pane();
        Scene scene = new Scene(root, SIZE, SIZE);
        scene.getStylesheets().add("gol/gol.css");

        // Create a board with dead cells (blank board)
        for (int x = 0; x < SIZE; x = x + WIDTH_HEIGHT) {
            for (int y = 0; y < SIZE; y = y + WIDTH_HEIGHT) {
                StackPane cell = StackPaneBuilder.create().layoutX(x).layoutY(y).prefHeight(WIDTH_HEIGHT).prefWidth(WIDTH_HEIGHT).styleClass("dead-cell").build();
                root.getChildren().add(cell);

                //Store the cell in hashmap so it can be easily accessed in the iterateBoard method.
                boardMap.put(x + " " + y, cell);
            }
        }

        primaryStage.setTitle("Game of Life");
        primaryStage.setScene(scene);
        primaryStage.show();

        timeline.play();
    }

    private void iterateBoard() {
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
