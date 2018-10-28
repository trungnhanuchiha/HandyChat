package application;

import javafx.scene.paint.Color;

public class GroupItems {
	private final String text ;
    private final Color color ;
    private String ID;
    
    public GroupItems(String text, Color color) {
        this.text = text ;
        this.color = color ;
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
    
    // setter
    public void setID(String ID) {
    	this.ID = ID;
    }
    
}
