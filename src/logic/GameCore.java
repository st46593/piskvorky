package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import piskvorky.Piskvorky;

/**
 *
 * @author Kuba
 */
public class GameCore {

    private long waitTimeMs = -1;
    private final int WIDTH, HEIGHT;
    private final Player playerX, playerO;
    private final Stav board;
    private final piskvorky.Piskvorky GUI;
    public FieldType onTurn;
    public boolean humanTurn = true;
    public boolean theEnd = false;
    private final int[][] DIRECTIONS = {{-1, 1, 0, 0, -1, 1, -1, 1}, {-1, 1, -1, 1, 1, -1, 0, 0}};
    private List<Pair<Integer, Integer>> endCoords = new ArrayList<>();

    private GameCore() {
        WIDTH = 3;
        HEIGHT = 3;
        playerO = new Player(FieldType.WHEEL, 3, 3);
        playerX = new Player(FieldType.CROSS, 3, 3);
        board = new Stav(WIDTH, HEIGHT);
        onTurn = FieldType.CROSS;
        GUI = null;
    }

    public GameCore(int width, int height, piskvorky.Piskvorky main) {
        WIDTH = Math.max(width, 5);
        HEIGHT = Math.max(height, 5);
        playerO = new Player(FieldType.WHEEL, WIDTH, HEIGHT);
        playerX = new Player(FieldType.CROSS, WIDTH, HEIGHT);
        board = new Stav(WIDTH, HEIGHT);
        onTurn = FieldType.CROSS;
        GUI = main;
    }

    public boolean setPlayerToAI(FieldType who) {
        if (who == FieldType.CROSS) {
            playerX.controledByAI = true;
        } else {
            playerO.controledByAI = true;
        }
        if (who == onTurn) {
            humanTurn = false;
          //  makeMoveByAI();
          return true;
        }
        return false;
    }

    public boolean makeMoveByAI() {
        if (waitTimeMs > 0) {
            try {
                Thread.sleep(waitTimeMs);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!theEnd) {
//             Pair<Integer, Integer> coords;
//            while (true) {
//                coords = new Pair<>((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT));
//                if (board.get(coords) == null) {
//                    break;
//                }
//            }
            Pair<Integer, Integer> coords = endOfMiniMax(onTurn, board);
            doMove(coords);
            GUI.repaint(GUI.root);
            return !humanTurn;
        }
        return false;
    }

    public boolean playMove(piskvorky.Piskvorky.Tile tile) {
        if (humanTurn && !theEnd) {
            Pair<Integer, Integer> coords = new Pair<>(((int) tile.getTranslateX() - 10) / 20,
                    ((int) tile.getTranslateY() - 10) / 20);
            if (board.get(coords) == null) {
                doMove(coords);
                return !humanTurn;
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

        board.applyMove(coord, onTurn);

        if (endGame(coord)) {
            GUI.paintEndOfGame(endCoords);
            System.out.println("HRA ZKONČILA, GRATULUJI " + onTurn);
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
            while (pokracuj) {
                c = new Pair<>(coord.getKey() + DIRECTIONS[0][i] * counter, coord.getValue() + DIRECTIONS[1][i] * counter);
                if (board.get(c) == onTurn) {
                    counter++;
                    pocet++;
                    if (endCoords.size() < 5) {
                        endCoords.add(c);
                    }
                } else {
                    pokracuj = false;
                }
            }
            counter = 1;
            pokracuj = true;
            while (pokracuj) {
                c = new Pair<>(coord.getKey() + DIRECTIONS[0][i + 1] * counter, coord.getValue() + DIRECTIONS[1][i + 1] * counter);
                if (board.get(c) == onTurn) {
                    counter++;
                    pocet++;
                    if (endCoords.size() < 5) {
                        endCoords.add(c);
                    }
                } else {
                    pokracuj = false;
                }
            }
            if (pocet >= 5) {
                theEnd = true;
                endCoords.add(coord);
                break;
            }
            if (!theEnd) {
                endCoords.clear();
            }
        }
        if (!theEnd && board.size() == (WIDTH * HEIGHT)) {
            theEnd = true;
            endCoords.clear();
        }
        return theEnd;
    }

    public Pair<Integer, Integer> endOfMiniMax(FieldType fl, Stav now) {
        ArrayList<Stav> ar = miniMax(fl, now);
        int value;
        if (onTurn == FieldType.WHEEL) {
            value = Integer.MAX_VALUE;
        } else {
            value = Integer.MIN_VALUE;
        }

        Pair<Integer, Integer> coord = null;
        for (int i = 0; i < ar.size(); i++) {
            if (onTurn == FieldType.WHEEL && value > ar.get(i).getHeuristic()) {
                value = ar.get(i).getHeuristic();
                coord = ar.get(i).getStepToThisState();
            }
            if (onTurn == FieldType.CROSS && value < ar.get(i).getHeuristic()) {
                value = ar.get(i).getHeuristic();
                coord = ar.get(i).getStepToThisState();
            }
        }
        return coord;
    }

    public ArrayList<Stav> miniMax(FieldType fl, Stav now) {
        ArrayList<Pair<Integer, Integer>> possibleActions = now.getActions();
        ArrayList<Stav> st = new ArrayList<>();
        for (Pair<Integer, Integer> move : possibleActions) {
            if (fl == FieldType.CROSS) {
                Stav s = now.getCoppyWithMove(move.getKey(), move.getValue(), FieldType.CROSS);
                s.setStepToThisState(move);
                s.setHeuristic(max(s, move));
                st.add(s);

            } else {
                Stav s = now.getCoppyWithMove(move.getKey(), move.getValue(), FieldType.WHEEL);
                s.setStepToThisState(move);
                s.setHeuristic(min(s, move));
                st.add(s);
            }
        }

        return st;
    }

    public int max(Stav s, Pair<Integer, Integer> move) {
        if (endGame(move) || s.getDeep() == 0) {
            int value = s.getHeuristicFor(playerX);
            s.setHeuristic(value);
            return value;
        } else {
            ArrayList<Stav> state = miniMax(FieldType.WHEEL, s);
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < state.size(); i++) {
                if (state.get(i).getHeuristic() > max) {
                    max = state.get(i).getHeuristic();
                }
            }
            return max;
        }
    }

    public int min(Stav s, Pair<Integer, Integer> move) {
        if (endGame(move) || s.getDeep() == 0) {
            int value = s.getHeuristicFor(playerX);
            s.setHeuristic(value);
            return value;
        } else {
            ArrayList<Stav> state = miniMax(FieldType.CROSS, s);
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < state.size(); i++) {
                if (state.get(i).getHeuristic() < min) {
                    min = state.get(i).getHeuristic();
                }
            }
            return min;
        }

    }

    public List<Pair<Integer, Integer>> getEndCoords() {
        return endCoords;
    }
}
