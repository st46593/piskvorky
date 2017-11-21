
package logic;

public enum FieldType {
    CROSS("X"),
    WHEEL("O");
    
    private final String text;
    
    private FieldType(String text){
        this.text = text;
    }
    
    @Override
    public String toString(){
        return text;
    }
}
