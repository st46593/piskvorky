package logic;

import java.util.HashMap;
import java.util.Set;
import javafx.util.Pair;

/**
 *
 * @author Kuba
 */
public class Player {

    public final FieldType PLAYER;
    public final FieldType ENEMY;
    public boolean controledByAI = false;
    private final int[][] DIRECTIONS = {{-1, 1, 0, 0, -1, 1, -1, 1}, {-1, 1, -1, 1, 1, -1, 0, 0}};
    private int width,heigth;


    private Player() {
        PLAYER = FieldType.CROSS;
        ENEMY = FieldType.WHEEL;
    }

    public Player(FieldType type,int width,int heigth) {
        PLAYER = type;
        ENEMY = (type == FieldType.CROSS ? FieldType.WHEEL : FieldType.CROSS);
        this.width=width;
        this.heigth=heigth;
    }
    
    private boolean isOnBoard(Pair<Integer,Integer> coord){
        if (coord.getKey()>=0 && coord.getKey()< width && coord.getValue()>=0 && coord.getValue() < heigth) {
            return true;
        }
        return false;
    }

    public int calculateHeuristic(HashMap<Pair<Integer, Integer>, FieldType> board) {
        Set<Pair<Integer,Integer>> coords = board.keySet();
        int total = 0;
        for (Pair<Integer, Integer> coord : coords) {
            total += countValueV2(coord, board.get(coord), board);
        }
        return total;
    }
    
    private int countValue(Pair<Integer, Integer> coord, FieldType mark, HashMap<Pair<Integer, Integer>, FieldType> board) {
        int counter;
        int pocet = 0;
        boolean pokracuj;
        int celkem=0;
        int closeTokens=0;
        Pair<Integer, Integer> c;
        for (int i = 0; i < 8; i += 2) {
            closeTokens=0;
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
                    }
                    pokracuj = false;
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
                    }
                    pokracuj = false;
                }
            }
            if (pocet >= 5) {
                celkem+=1000000;
            }else if (closeTokens==2) {
                celkem+=0;  //pokud je uzavřeno z obou stran, tak 0
            }else if (pocet == 4) {
                celkem+=250;
            }else if(pocet==3){
                celkem += 33;
            }else if (pocet==2) {
                celkem+=5;
            }else if (pocet==1) {
                celkem+=1;
            }
        }
        if (celkem<1000000 && board.size() == (width * heigth)) {
            celkem = 0;
        }
        
        return celkem * (mark==PLAYER?(1):(-1));
    }
    
    private int countValueV2(Pair<Integer, Integer> coord, FieldType mark, HashMap<Pair<Integer, Integer>, FieldType> board) {
        int counter;
        int pocet = 0;
        boolean pokracuj;
        int celkem=0;
        int closeTokens,openTokensL,openTokensP;
        Pair<Integer, Integer> c;
        for (int i = 0; i < 8; i += 2) {
            closeTokens=0;
            openTokensL=0;
            openTokensP=0;
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
                    }else{
                        openTokensL++;
                        if (openTokensL==2) {
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
                    }else if(isOnBoard(c)){
                        openTokensP++;
                        if (openTokensP==2) {
                            pokracuj = false;
                        }
                    }
                }
            }
            int totalOpenTokens=openTokensL+openTokensP;
            int totalCount=openTokensL+openTokensP+pocet;
            if (pocet >= 5) {
                celkem+=1000000;
            }else if (closeTokens==2 && (totalCount)<5) {
                celkem+=0;      //pokud je uzavřeno z obou stran, tak 0
            }else if (pocet==4 && openTokensL>0 && openTokensP>0) {
                celkem+=250;    //jednotahová vítězná konbinace
            }else if(pocet==3 && totalOpenTokens>2){
                celkem += 33;
            }else if(pocet==3 && totalOpenTokens==2){
                celkem += 8;
            }else if (pocet==2 && totalOpenTokens>=3) {
                celkem+=5;
            }else if (pocet==1 && totalOpenTokens==4) {
                celkem+=1;
            }
        }
        if (celkem<1000000 && board.size() == (width * heigth)) {
            celkem = 0;
        }
        
        return celkem * (mark==PLAYER?(1):(-1));
    }
    
    

}
