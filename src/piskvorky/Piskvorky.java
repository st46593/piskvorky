package piskvorky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import logic.FieldType;
import logic.GameCore;

/**
 *
 * @author Mathew
 */
public class Piskvorky extends Application {

    private final int WIDTH_OF_ONE_SQUARE = 20;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private boolean isCross = true;
    public List<Tile> tiles = new ArrayList<>();
    private Line endLine = null;
    public logic.GameCore hra;
    public StackPane root;
    private static Timer timer;
    private static TimerTask waitForPaint;
    //private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();
//        timer = new Timer("casovac");
//        waitForPaint = new TimerTask() {
//            @Override
//            public void run() {
//                repaint(root);
//            }
//        };

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        for (int i = WIDTH_OF_ONE_SQUARE; i < WIDTH; i += WIDTH_OF_ONE_SQUARE) {
            for (int j = WIDTH_OF_ONE_SQUARE; j < HEIGHT; j += WIDTH_OF_ONE_SQUARE) {
                Tile t = new Tile("");
                t.getText().setStroke(Color.BLACK);
                tiles.add(t);
            }
        }

        repaint(root);
        //MAX - Deklarace hry
        hra = new GameCore(WIDTH / 20 - 1, HEIGHT / 20 - 1, this);
        //hra.setWaitTime(500);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F1) {
                if (hra.setPlayerToAI(FieldType.CROSS)) {
                    makeAImove();
                }
            } else if (event.getCode() == KeyCode.F2) {
                if (hra.setPlayerToAI(FieldType.WHEEL)) {
                    makeAImove();
                }
            } else if (event.getCode() == KeyCode.SPACE) {
                if (!hra.humanTurn) {
                    makeAImove();
                }
            } else if (event.getCode() == KeyCode.ENTER) {
                if (hra.theEnd) {
                    tiles.clear();
                    for (int i = WIDTH_OF_ONE_SQUARE; i < WIDTH; i += WIDTH_OF_ONE_SQUARE) {
                        for (int j = WIDTH_OF_ONE_SQUARE; j < HEIGHT; j += WIDTH_OF_ONE_SQUARE) {
                            Tile t = new Tile("");
                            t.getText().setStroke(Color.BLACK);
                            tiles.add(t);
                        }
                    }
                    hra = new GameCore(WIDTH / 20 - 1, HEIGHT / 20 - 1, this);
                    repaint(root);
                }
            }
        });
        //Platform.setImplicitExit(false);
        primaryStage.setTitle("Piškvorky");
        primaryStage.setScene(scene);
        primaryStage.show();
        //timer.schedule(waitForPaint, 10000, 1000);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

    public FieldType getFieldType() {
        if (isCross) {
            setIsCross(false);
            return FieldType.CROSS;
        } else {
            setIsCross(true);
            return FieldType.WHEEL;
        }
    }

    public void setIsCross(boolean isCross) {
        this.isCross = isCross;
    }

    public void repaint(StackPane root) {
        root.getChildren().clear();
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            tile.setTranslateX(WIDTH_OF_ONE_SQUARE * (i
                    % ((WIDTH - WIDTH_OF_ONE_SQUARE) / WIDTH_OF_ONE_SQUARE)) + (WIDTH_OF_ONE_SQUARE / 2));
            tile.setTranslateY(WIDTH_OF_ONE_SQUARE * (i
                    / ((WIDTH - WIDTH_OF_ONE_SQUARE) / WIDTH_OF_ONE_SQUARE)) + (WIDTH_OF_ONE_SQUARE / 2));
            root.getChildren().add(tile);
        }
    }

    public class Tile extends StackPane implements Comparable<Tile> {

        private Text text = new Text();

        public Tile(String value) {
            Rectangle border = new Rectangle(WIDTH_OF_ONE_SQUARE, WIDTH_OF_ONE_SQUARE);
            border.setFill(null);
            border.setStroke(Color.BLACK);

            text.setText(value);
            text.setFont(Font.font(20));

            setAlignment(Pos.BASELINE_LEFT);

            //MAX - změna onclick funkce aby pracovala přes jádro hry
            //setOnMouseClicked(event -> fillField(getFieldType()));
            setOnMouseClicked(event -> makeMove());
            //

            getChildren().addAll(border, text);
        }

        //MAX - funkce pro onclick
        public void makeMove() {
            if (hra.playMove(this)) {
                makeAImove();
            }
        }
        //

        public void fillField(FieldType ft) {
            if (text.getText().equals("")) {
                if (ft == FieldType.CROSS) {
                    text.setTranslateX(this.getLayoutX() + 5);
                    text.setTranslateY(this.getLayoutY() - 3);
                } else {
                    text.setTranslateX(this.getLayoutX() + 3);
                    text.setTranslateY(this.getLayoutY() - 3);
                }
                this.text.setText(ft.toString());
            } else {
                setIsCross(!isCross);
            }
        }

        public Text getText() {
            return text;
        }

        public double getCenterX() {
            return getTranslateX() + WIDTH_OF_ONE_SQUARE / 2;
        }

        public double getCenterY() {
            return getTranslateY() + WIDTH_OF_ONE_SQUARE / 2;
        }

        @Override
        public int compareTo(Tile t) {
            if (this.getTranslateX() != t.getTranslateX()) {
                return (int) this.getTranslateX() - (int) t.getTranslateX();
            } else {
                return (int) this.getTranslateY() - (int) t.getTranslateY();
            }

        }

    }

    public void makeAImove() {
        hra.makeMoveByAI();
    }

    public void paintEndOfGame(List<Pair<Integer, Integer>> endCoords) {
        while (endCoords.size() > 5) {
            endCoords.remove(endCoords.size() - 2);
        }
        for (Pair<Integer, Integer> endCoord : endCoords) {
            System.out.println(endCoord.getKey() + ", " + endCoord.getValue());
        }
        List<Pair<Integer, Integer>> recountCoordsForTiles = new ArrayList<>();
        for (Pair<Integer, Integer> endCoord : endCoords) {
            recountCoordsForTiles.add(new Pair<>((endCoord.getKey() * WIDTH_OF_ONE_SQUARE)
                    + WIDTH_OF_ONE_SQUARE / 2, (endCoord.getValue() * WIDTH_OF_ONE_SQUARE)
                    + WIDTH_OF_ONE_SQUARE / 2));
        }

        List<Tile> tilesForCrossLine = new ArrayList<>();
        for (Pair<Integer, Integer> recountCoordsForTile : recountCoordsForTiles) {
            for (Tile tile : tiles) {
                if (tile.getTranslateX() == recountCoordsForTile.getKey()
                        && tile.getTranslateY() == recountCoordsForTile.getValue()) {
                    tile.getText().setStroke(Color.RED);
                    tilesForCrossLine.add(tile);
                    break;
                }
            }
        }
        Collections.sort(tilesForCrossLine);
        setEndLine(tilesForCrossLine);
    }

    private void setEndLine(List<Tile> endTiles) {
        if (endTiles.get(0).getTranslateX() - endTiles.get(4).getTranslateX() < 0) {
            if (endTiles.get(0).getTranslateY() - endTiles.get(4).getTranslateY() == 0) {
                for (Tile endTile : endTiles) {
                    Line crossLine = new Line(0, 0, 20, 0);
                    crossLine.setTranslateY(-10);
                    endTile.getChildren().add(crossLine);
                }
            } else if (endTiles.get(0).getTranslateY() - endTiles.get(4).getTranslateY() < 0) {
                for (Tile endTile : endTiles) {
                    Line crossLine = new Line(0, 0, 20, 20);
                    endTile.getChildren().add(crossLine);
                }
            } else if (endTiles.get(0).getTranslateY() - endTiles.get(4).getTranslateY() > 0) {
                for (Tile endTile : endTiles) {
                    Line crossLine = new Line(0, 20, 20, 0);
                    endTile.getChildren().add(crossLine);
                }
            }
        } else {
            for (Tile endTile : endTiles) {
                Line crossLine = new Line(0, 0, 0, 20);
                crossLine.setTranslateX(10);
                endTile.getChildren().add(crossLine);
            }
        }
    }
}
