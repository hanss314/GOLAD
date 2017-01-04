import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Redo here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Redo extends Button
{
    public Redo(MyWorld w){
        super(w,new GreenfootImage("redo.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        if(w.moveNumber<w.totalMoves){
            w.moveNumber++;
            w.readBoard("save"+Integer.toString(w.moveNumber));   
            w.displayMoves();
        }
    }
}
