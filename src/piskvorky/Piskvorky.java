/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package piskvorky;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
    public logic.GameCore hra;
    public StackPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        for (int i = WIDTH_OF_ONE_SQUARE; i < WIDTH; i += WIDTH_OF_ONE_SQUARE) {
            for (int j = WIDTH_OF_ONE_SQUARE; j < HEIGHT; j += WIDTH_OF_ONE_SQUARE) {
                tiles.add(new Tile(""));
            }
        }
        repaint(root);

        //MAX - Deklarace hry
        hra = new GameCore(WIDTH / 20 - 1, HEIGHT / 20 - 1, this);
        //hra.setWaitTime(500);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F1) {
                hra.setPlayerToAI(FieldType.CROSS);
            } else if (event.getCode() == KeyCode.F2) {
                hra.setPlayerToAI(FieldType.WHEEL);
            }
        });
        //

        primaryStage.setTitle("Piškvorky");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

    private Parent createContent() {
        return null;
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

    public class Tile extends StackPane {

        private Text text = new Text();

        public Tile(String value) {
            Rectangle border = new Rectangle(WIDTH_OF_ONE_SQUARE, WIDTH_OF_ONE_SQUARE);
            border.setFill(null);
            border.setStroke(Color.BLACK);

            int text_size = 25;
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
            hra.playMove(this);
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
        
        
    }

    public void paintEndOfGame(List<Pair<Integer, Integer>> endCoords) {
        List<Pair<Integer, Integer>> recountCoordsForTiles = new ArrayList<>();
        for (Pair<Integer, Integer> endCoord : endCoords) {
            recountCoordsForTiles.add(new Pair<>((endCoord.getKey() * WIDTH_OF_ONE_SQUARE)
                    + WIDTH_OF_ONE_SQUARE/2,(endCoord.getValue()* WIDTH_OF_ONE_SQUARE) 
                            + WIDTH_OF_ONE_SQUARE/2));        
        }
        for (Pair<Integer, Integer> recountCoordsForTile : recountCoordsForTiles) {
            for (Tile tile : tiles) {
                if (tile.getTranslateX() == recountCoordsForTile.getKey() && tile.getTranslateY() == recountCoordsForTile.getValue()){
                    tile.getText().setStroke(Color.RED);
                    break;
                }
            }
        }
        
        
    }
}
