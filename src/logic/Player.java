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

    public int calculateHeuristic(HashMap<Pair<Integer, Integer>, FieldType> board) {
        Set<Pair<Integer,Integer>> coords = board.keySet();
        int total = 0;
        for (Pair<Integer, Integer> coord : coords) {
            total += countValue(coord, board.get(coord), board);
        }
        return total;
    }
    
    private int countValue(Pair<Integer, Integer> coord, FieldType mark, HashMap<Pair<Integer, Integer>, FieldType> board) {
        int counter;
        int pocet = 0;
        boolean pokracuj;
        int celkem=0;
        Pair<Integer, Integer> c;
        for (int i = 0; i < 8; i += 2) {
            counter = 1;
            pocet = 1;
            pokracuj = true;
            while (pokracuj) {
                c = new Pair<>(coord.getKey() + DIRECTIONS[0][i] * counter, coord.getValue() + DIRECTIONS[1][i] * counter);
                if (board.get(c) == mark) {
                    counter++;
                    pocet++;
                } else {
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
                    pokracuj = false;
                }
            }
            if (pocet >= 5) {
                celkem+=1000000;
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
    
    

}
