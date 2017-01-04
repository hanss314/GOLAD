import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Undo here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Undo extends Button
{
    public Undo(MyWorld w){
        super(w,new GreenfootImage("undo.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        w.moveNumber--;
        w.readBoard("save"+Integer.toString(w.moveNumber));   
        w.displayMoves();
    }
}
