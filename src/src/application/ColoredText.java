package application;

import java.util.List;

import javafx.scene.paint.Color;

public class ColoredText {
	private final String text ;
    private final Color color ;
    private boolean isGroup;
    private String ID;
    private List<String> participants;
    private String online_status;
    
    public ColoredText(String text, Color color) {
        this.text = text ;
        this.color = color ;
    }
    
    public ColoredText(String text,Color color, boolean isGroup, String ID, String online_status) {
    	this.text = text ;
        this.color = color ;
        this.isGroup = isGroup;
        this.ID = ID;
        this.online_status = online_status;
    }
    
    public ColoredText(String text,Color color, boolean isGroup, String ID, List<String> participants) {
    	this.text = text ;
        this.color = color ;
        this.isGroup = isGroup;
        this.ID = ID;
        this.participants = participants;
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
    
    public List<String> getparticipants(){
    	return participants;
    }
    
    public String getOnlineStatus() {
    	return online_status;
    }
    
    // setter
    public void setID(String ID) {
    	this.ID = ID;
    }
    
    public void setisGroup(boolean isGroup) {
    	this.isGroup = isGroup;
    }
    
    public void setOnlineStatus(String online_status) {
    	this.online_status = online_status;
    }
    
}
