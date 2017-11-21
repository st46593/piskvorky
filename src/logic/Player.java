package logic;

/**
 *
 * @author Kuba
 */
public class Player {

    public final FieldType PLAYER;
    public final FieldType ENEMY;
    public boolean controledByAI = false;

    private Player() {
        PLAYER = FieldType.CROSS;
        ENEMY = FieldType.WHEEL;
    }

    public Player(FieldType type) {
        PLAYER = type;
        ENEMY = (type == FieldType.CROSS ? FieldType.WHEEL : FieldType.CROSS);
    }
    
    

}
