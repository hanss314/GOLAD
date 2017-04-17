import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ViewGame here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ViewGame extends Button
{
    public ViewGame(MyWorld w){
        super(w,new GreenfootImage("Buttons/viewGames.png"));
    }
    public void rest(){}
    public void clickAction(){
        w.gameView();
    }   
}
