import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Clear here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Clear extends Button
{
    public Clear(MyWorld w){
        super(w,new GreenfootImage("Buttons/clear.png"));
    }
    public void rest(){}
    public void clickAction(){
        w.moveNumber = 0;
        w.totalMoves = 0;
        w.gameEnd = false;
        w.sandbox();
    }
}
