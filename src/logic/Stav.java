package logic;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;

/**
 *
 * @author Kuba
 */
public class Stav {

    private final HashMap<Pair<Integer, Integer>, FieldType> board;
    private final int WIDTH, HEIGHT;
    private final HashMap<Pair<Integer, Integer>, Boolean> posibleActions;
    private int alfa;
    private int beta;
    private int heuristic;
    private int deep;
    private Pair<Integer, Integer> stepToThisState;
    public Stav before;
    public final int ACTUAL_DEEP = 5;

    private Stav() {
        board = new HashMap<>();
        posibleActions = new HashMap<>();
        WIDTH = 3;
        HEIGHT = 3;
        deep= ACTUAL_DEEP;
    }

    public Stav(int width, int height) {
        board = new HashMap<>();
        posibleActions = new HashMap<>();
        WIDTH = width;
        HEIGHT = height;
        alfa = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
        deep = ACTUAL_DEEP;
        before = null;
        stepToThisState = null;
    }

    private Stav(Stav s) {
        WIDTH = s.WIDTH;
        HEIGHT = s.HEIGHT;
        board = new HashMap<>();
        board.putAll(s.board);
        posibleActions = new HashMap<>();
        posibleActions.putAll(s.posibleActions);
        alfa = s.getAlfa();
        beta = s.getBeta();
        deep = s.getDeep() - 1;
        before = s;
        stepToThisState = null;
    }

    public void setStepToThisState(Pair<Integer, Integer> stepToThisState) {
        this.stepToThisState = stepToThisState;
    }

    public Pair<Integer, Integer> getStepToThisState() {
        return stepToThisState;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public int getDeep() {
        return deep;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public void setAlfa(int alfa) {
        if (alfa > this.alfa) {
            this.alfa = alfa;
        }
    }

    public void setBeta(int beta) {
        if (beta < this.beta) {
            this.beta = beta;
        }

    }
     public void setAlfaStart() {
        this.alfa = Integer.MIN_VALUE;
            }

    public void setBetaStart() {
        this.beta = Integer.MAX_VALUE;
           }

    public int getAlfa() {
        return alfa;
    }

    public int getBeta() {
        return beta;
    }

    public FieldType get(Pair<Integer, Integer> coord) {
        return board.get(coord);
    }
    

    public void applyMove(Pair<Integer, Integer> coord, FieldType mark) {
        board.put(coord, mark);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Pair<Integer, Integer> newCoord = new Pair<>(coord.getKey() + i, coord.getValue() + j);
                if (isEmtySpace(newCoord.getKey(), newCoord.getValue())) {
                    posibleActions.put(newCoord, true);
                }
            }
        }
    }

    public ArrayList<Pair<Integer, Integer>> getActions() {
        ArrayList<Pair<Integer, Integer>> moves = new ArrayList<>();
        moves.addAll(posibleActions.keySet());
        if (moves.isEmpty()) {
            //pro případ začátku hry
            moves.add(new Pair<>(WIDTH / 2, HEIGHT / 2));
            return moves;
        }

        //fore pro vyčištění od již neplatných akcích
        for (Pair<Integer, Integer> move : moves) {
            if (!isEmtySpace(move.getKey(), move.getValue())) {
                posibleActions.remove(move);
            }
        }
        moves.clear();
        moves.addAll(posibleActions.keySet());
        return moves;
    }

    public boolean isEmtySpace(int x, int y) {
        if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) {
            return false;
        } else {
            return board.get(new Pair<>(x, y)) == null;
        }
    }

    public Stav getCoppyWithMove(int x, int y, FieldType mark) {
        if (!isEmtySpace(x, y)) {
            throw new IllegalAccessError("Na toto pole nelze hrát!");
        }
        Stav stav = new Stav(this);
        stav.applyMove(new Pair(x, y), mark);
        return stav;
    }

    public int size() {
        return board.size();
    }

    public int getHeuristicFor(Player player) {
        return player.calculateHeuristic(board);
    }

}
