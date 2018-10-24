package application;

import javafx.scene.paint.Color;

public class ColoredText {
	private final String text ;
    private final Color color ;
    private boolean isGroup;
    private String ID;
    
    public ColoredText(String text, Color color) {
        this.text = text ;
        this.color = color ;
    }
    
    public ColoredText(String text,Color color, boolean isGroup, String ID ) {
    	this.text = text ;
        this.color = color ;
        this.isGroup = isGroup;
        this.ID = ID;
    }
    
    // getter
    public String getText() {
        return text ;
    }

    public Color getColor() {
        return color ;
    }
    
    public String getID() {
    	return ID;
    }
    
    public boolean getisGroup() {
    	return isGroup;
    }
    
    // setter
    public void setID(String ID) {
    	this.ID = ID;
    }
    
    public void setisGroup(boolean isGroup) {
    	this.isGroup = isGroup;
    }
    
    
}
