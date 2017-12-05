package logic;

import java.util.ArrayList;
import java.util.Arrays;
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
    public boolean draw=false;
    private int alfa;
    private int beta;
    private int heuristic;
    private int deep;
    private Pair<Integer, Integer> stepToThisState;
    public Stav before;
    public final int ACTUAL_DEEP = 4;
    public int[] positionsX, positionsO;    //pro heuristiku
    public final int NUMBER_OF_FIGURES = 5;   //pro heuristiku
    private final int[][] DIRECTIONS = //pro heuristiku
            {{-1, 1, 0, 0, -1, 1, -1, 1}, {-1, 1, -1, 1, 1, -1, 0, 0}};

    private Stav() {
        board = new HashMap<>();
        posibleActions = new HashMap<>();
        WIDTH = 3;
        HEIGHT = 3;
        deep = ACTUAL_DEEP;
        positionsX = new int[NUMBER_OF_FIGURES];
        positionsO = new int[NUMBER_OF_FIGURES];
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
        positionsX = new int[NUMBER_OF_FIGURES];
        positionsO = new int[NUMBER_OF_FIGURES];
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
        positionsO = Arrays.copyOf(s.positionsO, NUMBER_OF_FIGURES);
        positionsX = Arrays.copyOf(s.positionsX, NUMBER_OF_FIGURES);
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
        boolean berlicka=false;
        if (before==null) {         //black magic for heuristic!
            before=new Stav(this);
            berlicka=true;
        }
        board.put(coord, mark);
        updateHeuristic(coord, mark);
        if (berlicka) {
            before=null;
        }
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
        if (draw) {
            return 0;
        }
        int celkemX = 0;
        if (positionsX[4]>0) {
                celkemX = 1000000;
            }else{
                celkemX += positionsX[0];
                celkemX += positionsX[1]*10;
                if (positionsX[2]>1) {
                    celkemX += (positionsX[2]-1)*1000;
                }else{
                celkemX += positionsX[2]*100;
                }
                celkemX += positionsX[3]*1000;
            }
        int celkemO = 0;
        if (positionsO[4]>0) {
                celkemO = 1000000;
            }else{
                celkemO += positionsO[0];
                celkemO += positionsO[1]*10;
                if (positionsO[2]>1) {
                    celkemO += (positionsO[2]-1)*1000;
                }else{
                celkemO += positionsO[2]*100;
                }
                celkemO += positionsO[3]*1000;
            }
        if (celkemX==1000000 && celkemO==1000000) {
            System.err.println("!!");
        }
        if (celkemX==1000000) {
            celkemO=0;
        }else if (celkemO==1000000) {
            celkemX=0;
        }
        return (celkemX-celkemO)*(player.PLAYER==FieldType.CROSS?(1):(-1));
    }

    private boolean isOnBoard(Pair<Integer, Integer> coord) {
        if (coord.getKey() >= 0 && coord.getKey() < WIDTH && coord.getValue() >= 0 && coord.getValue() < HEIGHT) {
            return true;
        }
        return false;
    }

    private void updateHeuristic(Pair<Integer, Integer> coord, FieldType mark) {
        int counter;
        int pocet;
        boolean pokracuj;
        int celkem = 0;
        int closeTokens, openTokensL, openTokensP;
        Pair<Integer, Integer> c;
        for (int i = 0; i < 8; i += 2) {
            closeTokens = 0;
            openTokensL = 0;
            openTokensP = 0;
            counter = 1;
            pocet = 1;
            pokracuj = true;
            while (pokracuj) {
                c = new Pair<>(coord.getKey() + DIRECTIONS[0][i] * counter, coord.getValue() + DIRECTIONS[1][i] * counter);
                if (board.get(c) == mark) {
                    counter++;
                    pocet++;
                } else {
                    if (board.get(c) != null) {
                        closeTokens++;
                        pokracuj = false;
                        checkHeuristicOn(c, DIRECTIONS[0][i], DIRECTIONS[1][i], true, openTokensL + 1);
                    } else if (isOnBoard(c)) {
                        openTokensL++;
                        if (openTokensL == 2) {
                            pokracuj = false;
                        }
                    }

                }
            }
            counter = 1;
            pokracuj = true;
            while (pokracuj) {
                c = new Pair<>(coord.getKey() + DIRECTIONS[0][i + 1] * counter, coord.getValue() + DIRECTIONS[1][i + 1] * counter);
                if (board.get(c) == mark) {
                    counter++;
                    pocet++;
                } else {
                    if (board.get(c) != null) {
                        closeTokens++;
                        pokracuj = false;
                        checkHeuristicOn(c, DIRECTIONS[0][i], DIRECTIONS[1][i], false, openTokensP + 1);
                    } else if (isOnBoard(c)) {
                        openTokensP++;
                        if (openTokensP == 2) {
                            pokracuj = false;
                        }
                    }
                }
            }
            int totalOpenTokens = openTokensL + openTokensP;
            int totalCount = openTokensL + openTokensP + pocet;
            if (mark == FieldType.CROSS) {
                if (pocet >= 5) {
                    positionsX[4]++;  //Vítězství
                    //není třeba upravovat ostatní figury
                } else if (closeTokens == 2 && (totalCount) < 5) {
                    //nehodnoceno
                } else if (pocet == 4 && openTokensL > 0 && openTokensP > 0) {
                    positionsX[3]++;    //jednotahová vítězná konbinace bez obrany (_XXXX_)
                    positionsX[2]--;
                } else if ((pocet == 3 && totalOpenTokens > 2) || (pocet == 4 && totalOpenTokens > 0)) {
                    positionsX[2]++;    //nebezpečná kombinace, lze se bránit pokud je jedna
                    positionsX[1]--;
                } else if (pocet == 2 && totalOpenTokens == 4) {
                    positionsX[1]++;    //volná dvojce
                    positionsX[0]--;
                } else if (pocet == 1 && totalOpenTokens == 4) {
                    positionsX[0]++;    //volný samotný znak
                } else {
                    //cokoli na co jsem zapoměl se nehodnotí  
                }
            }else{
            if (pocet >= 5) {
                    positionsO[4]++;  //Vítězství
                    //není třeba upravovat ostatní figury
                } else if (closeTokens == 2 && (totalCount) < 5) {
                    //nehodnoceno
                } else if (pocet == 4 && openTokensL > 0 && openTokensP > 0) {
                    positionsO[3]++;    //jednotahová vítězná konbinace bez obrany (_XXXX_)
                    positionsO[2]--;
                } else if ((pocet == 3 && totalOpenTokens > 2) || (pocet == 4 && totalOpenTokens > 0)) {
                    positionsO[2]++;    //nebezpečná kombinace, lze se bránit pokud je jedna
                    positionsO[1]--;
                } else if (pocet == 2 && totalOpenTokens == 4) {
                    positionsO[1]++;    //volná dvojce
                    positionsO[0]--;
                } else if (pocet == 1 && totalOpenTokens == 4) {
                    positionsO[0]++;    //volný samotný znak
                } else {
                    //cokoli na co jsem zapoměl se nehodnotí  
                }
            }
        }
        if (positionsO[4]==0 && positionsX[4]==0 && board.size() == (WIDTH * HEIGHT)) {
            draw=true;
        }
    }

    private void checkHeuristicOn(Pair<Integer, Integer> coord, int directX, int directY, boolean leftClose, int closedCount) {
        int closeTokens = 0;
        int openTokensL = 0;
        int openTokensP = 0;
        int counter = 1;
        int pocet = 1;
        Pair<Integer, Integer> c;
        FieldType mark = before.board.get(coord);
        boolean pokracuj = true;
        while (pokracuj) {
            c = new Pair<>(coord.getKey() + directX * counter, coord.getValue() + directY * counter);
            if (before.board.get(c) == mark) {
                counter++;
                pocet++;
            } else {
                if (before.board.get(c) != null) {
                    closeTokens++;
                    pokracuj = false;
                } else if (isOnBoard(c)) {
                    openTokensL++;
                    if (openTokensL == 2) {
                        pokracuj = false;
                    }
                }

            }
        }
        counter = 1;
        pokracuj = true;
        while (pokracuj) {
            c = new Pair<>(coord.getKey() - directX * counter, coord.getValue() - directY * counter);
            if (before.board.get(c) == mark) {
                counter++;
                pocet++;
            } else {
                if (before.board.get(c) != null) {
                    closeTokens++;
                    pokracuj = false;
                } else if (isOnBoard(c)) {
                    openTokensP++;
                    if (openTokensP == 2) {
                        pokracuj = false;
                    }
                }
            }
        }
        int totalOpenTokens = openTokensL + openTokensP;
        int totalCount = openTokensL + openTokensP + pocet;
        int oldFigure;
        if (pocet >= 5) {
            oldFigure = 4;
            //System.err.println("Error-01: Heuristika počítá i přes to, že hra skončila!");
        } else if (closeTokens == 2 && (totalCount) < 5) {
            oldFigure = -1;  //nehodnoceno
        } else if (pocet == 4 && openTokensL > 0 && openTokensP > 0) {
            oldFigure = 3;    //jednotahová vítězná konbinace bez obrany (_XXXX_)
        } else if ((pocet == 3 && totalOpenTokens > 2) || (pocet == 4 && totalOpenTokens > 0)) {
            oldFigure = 2;    //nebezpečná kombinace, lze se bránit pokud je jedna
        } else if (pocet == 2 && totalOpenTokens == 4) {
            oldFigure = 1;    //volná dvojce
        } else if (pocet == 1 && totalOpenTokens == 4) {
            oldFigure = 0;    //volný samotný znak
        } else {
            oldFigure = -1;   //cokoli na co jsem zapoměl se nehodnotí   
        }

        if (leftClose) {
            openTokensL = Math.max(0, openTokensL - closedCount);
        } else {
            openTokensP = Math.max(0, openTokensP - closedCount);
        }
        totalOpenTokens = openTokensL + openTokensP;
        totalCount = openTokensL + openTokensP + pocet;
        int newFigure;
        if (pocet >= 5) {
            newFigure = 4;
           // System.err.println("Error-02: Heuristika počítá i přes to, že hra skončila!");
        } else if (closeTokens == 2 && (totalCount) < 5) {
            newFigure = -1;  //nehodnoceno
        } else if (pocet == 4 && openTokensL > 0 && openTokensP > 0) {
            newFigure = 3;    //jednotahová vítězná konbinace bez obrany (_XXXX_)
        } else if ((pocet == 3 && totalOpenTokens > 2) || (pocet == 4 && totalOpenTokens > 0)) {
            newFigure = 2;    //nebezpečná kombinace, lze se bránit pokud je jedna
        } else if (pocet == 2 && totalOpenTokens == 4) {
            newFigure = 1;    //volná dvojce
        } else if (pocet == 1 && totalOpenTokens == 4) {
            newFigure = 0;    //volný samotný znak
        } else {
            newFigure = -1;   //cokoli na co jsem zapoměl se nehodnotí  
        }

        if (mark == FieldType.CROSS) {
            if (oldFigure >= 0) {
                positionsX[oldFigure]--;
            }
            if (newFigure >= 0) {
                positionsX[newFigure]++;
            }
        } else {
            if (oldFigure >= 0) {
                positionsO[oldFigure]--;
            }
            if (newFigure >= 0) {
                positionsO[newFigure]++;
            }
        }
    }
}
