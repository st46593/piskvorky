package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import piskvorky.Piskvorky;
import piskvorky.Piskvorky.Tile;

/**
 *
 * @author Kuba
 */
public class GameCore {

    private long waitTimeMs = -1;
    private final int WIDTH, HEIGHT;
    private final Player playerX, playerO;
    private final HashMap<Pair<Integer, Integer>, FieldType> board;
    private final piskvorky.Piskvorky GUI;
    public FieldType onTurn;
    public boolean humanTurn = true;
    public boolean theEnd = false;
    private final int[][] DIRECTIONS = {{-1, 1, 0, 0, -1, 1, -1, 1}, {-1, 1, -1, 1, 1, -1, 0, 0}};
    private List<Pair<Integer, Integer>> endCoords = new ArrayList<>();

    private GameCore() {
        WIDTH = 3;
        HEIGHT = 3;
        playerO = new Player(FieldType.WHEEL);
        playerX = new Player(FieldType.CROSS);
        board = new HashMap<>();
        onTurn = FieldType.CROSS;
        GUI = null;
    }

    public GameCore(int width, int height, piskvorky.Piskvorky main) {
        WIDTH = Math.max(width, 5);
        HEIGHT = Math.max(height, 5);
        playerO = new Player(FieldType.WHEEL);
        playerX = new Player(FieldType.CROSS);
        board = new HashMap<>();
        onTurn = FieldType.CROSS;
        GUI = main;
    }

    public void setPlayerToAI(FieldType who) {
        if (who == FieldType.CROSS) {
            playerX.controledByAI = true;
        } else {
            playerO.controledByAI = true;
        }
        if (who == onTurn) {
            humanTurn = false;
            makeMoveByAI();
        }
    }

    private void makeMoveByAI() {
        if (waitTimeMs > 0) {
            try {
                Thread.sleep(waitTimeMs);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!theEnd) {
            Pair<Integer, Integer> coords;
            while (true) {
                coords = new Pair<>((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT));
                if (board.get(coords) == null) {
                    break;
                }
            }
            doMove(coords);
            GUI.repaint(GUI.root);
            if (!humanTurn) {
                makeMoveByAI();
            }
        }
    }

    public boolean playMove(piskvorky.Piskvorky.Tile tile) {
        if (humanTurn && !theEnd) {
            Pair<Integer, Integer> coords = new Pair<>(((int) tile.getTranslateX() - 10) / 20,
                    ((int) tile.getTranslateY() - 10) / 20);
            if (board.get(coords) == null) {
                doMove(coords);
                if (!humanTurn) {
                    makeMoveByAI();
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private void doMove(Pair<Integer, Integer> coord) {
        piskvorky.Piskvorky.Tile tile = null;
        Pair<Integer, Integer> c;
        for (Piskvorky.Tile t : GUI.tiles) {
            c = new Pair<>(((int) t.getTranslateX() - 10) / 20,
                    ((int) t.getTranslateY() - 10) / 20);
            if ((c.getKey() == coord.getKey()) && (c.getValue() == coord.getValue())) {
                tile = t;
                break;
            }
        }
        tile.fillField(onTurn);

        board.put(coord, onTurn);

        if (endGame(coord)) {
            System.out.println("HRA ZKONČILA, GRATULUJI " + onTurn);
            GUI.paintEndOfGame(endCoords);
        }

        onTurn = (onTurn == FieldType.CROSS ? FieldType.WHEEL : FieldType.CROSS);
        if (onTurn == FieldType.CROSS) {
            humanTurn = !playerX.controledByAI;
        } else {
            humanTurn = !playerO.controledByAI;
        }
    }

    public void setWaitTime(long millisec) {
        waitTimeMs = millisec;
    }

    private boolean endGame(Pair<Integer, Integer> coord) {
        int counter;
        int pocet = 0;
        boolean pokracuj;
        Pair<Integer, Integer> c;
        for (int i = 0; i < 8; i += 2) {
            counter = 1;
            pocet = 1;
            pokracuj = true;
            while (counter < 6 && pokracuj) {
                c = new Pair<>(coord.getKey() + DIRECTIONS[0][i] * counter, coord.getValue() + DIRECTIONS[1][i] * counter);
                if (board.get(c) == onTurn) {
                    counter++;
                    pocet++;
                    endCoords.add(c);
                } else {
                    pokracuj = false;
                }
            }
            counter = 1;
            pokracuj = true;
            while (counter < 6 && pokracuj) {
                c = new Pair<>(coord.getKey() + DIRECTIONS[0][i + 1] * counter, coord.getValue() + DIRECTIONS[1][i + 1] * counter);
                if (board.get(c) == onTurn) {
                    counter++;
                    pocet++;
                    endCoords.add(c);
                } else {
                    pokracuj = false;
                }
            }
            if (pocet >= 5) {
                theEnd = true;
                endCoords.add(coord);
            }
            if (!theEnd) {
                endCoords.clear();
            }
        }
        if (!theEnd && board.size() == (WIDTH * HEIGHT)) {
            theEnd = true;

        }
        return theEnd;
    }

    public List<Pair<Integer, Integer>> getEndCoords() {
        return endCoords;
    }

}
