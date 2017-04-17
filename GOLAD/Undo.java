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
        super(w,new GreenfootImage("Buttons/undo.png"));
    }
    public void rest(){}
    public void clickAction(){
        w.redTurn = !w.redTurn;
        w.blueTurn = !w.blueTurn;
        if(w.moveNumber>0){
            w.moveNumber--;
            w.setTurn(w.moveNumber);   
            w.displayMoves();
        }
    }
}
