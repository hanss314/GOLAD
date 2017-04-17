import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Move here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Move extends Button
{
    public Move(MyWorld w){
        super(w,new GreenfootImage("Buttons/move.png"));
    }  
    public void rest(){}
    public void clickAction(){
        w.nextMove();
    }  
}
