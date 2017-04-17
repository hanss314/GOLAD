import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class StartGame here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class StartGame extends Button
{
    public StartGame(MyWorld w){
        super(w,new GreenfootImage("Buttons/startGame.png"));
    }
    public void rest(){}
    public void clickAction(){
        w.gameEnd = false;
        w.playerSelect();
    }
}
