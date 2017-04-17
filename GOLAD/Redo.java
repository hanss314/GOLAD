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
        super(w,new GreenfootImage("Buttons/redo.png"));
    }
    public void rest(){}
    public void clickAction(){
        if(w.moveNumber<w.totalMoves){
            w.redTurn = !w.redTurn;
            w.blueTurn = !w.blueTurn;
            w.moveNumber++;
            w.setTurn(w.moveNumber);   
            w.displayMoves();
        }
    }
}
