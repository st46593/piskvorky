package logic;

import java.util.ArrayList;
import javafx.util.Pair;

/**
 *
 * @author Kuba
 */
public class MiniMaxV2 {
    
    private static GameCore gc;
    
    public static void Init(GameCore hra){
        gc=hra;
    }
    
    public static Pair<Integer,Integer> MiniMax(){
        if (gc==null) {
            throw new NullPointerException("Use MiniMaxV2.Init(GameCore) first!");
        }
        ArrayList<Pair<Integer,Integer>> moves = gc.board.getActions();
        int best = Integer.MIN_VALUE;
        Pair<Integer,Integer> action=null;
        for (Pair<Integer, Integer> move : moves) {
            Stav state = gc.board.getCoppyWithMove(move.getKey(), move.getValue(), gc.onTurn);
            state.setStepToThisState(move);
            int value = calculate(state, (gc.onTurn==FieldType.CROSS?gc.playerX:gc.playerO));
            if (value>best) {
                best=value;
                action=move;
            }
        //    System.out.println(move+" -> "+value);
        }
        System.out.println("***"+gc.onTurn+"***-> "+action);
        return action;
    }

    private static int calculate(Stav coppy, Player who) {
        if (gc.posibleEndGame(coppy.getStepToThisState(), coppy) || coppy.getDeep()==0) {
            return coppy.getHeuristicFor(who);
        }else{
            int best = Integer.MIN_VALUE;
            ArrayList<Pair<Integer,Integer>> moves = coppy.getActions();
            for (Pair<Integer, Integer> move : moves) {
                Stav state = coppy.getCoppyWithMove(move.getKey(), move.getValue(), who.ENEMY);
                state.setStepToThisState(move);
                int value = calculate(state,
                        (who==gc.playerO?gc.playerX:gc.playerO));
                best = Math.max(value, best);
              //  System.out.println(who.PLAYER+"-- "+move+" -> "+value);
            }
            return best*(-1);
        }
    }
}
