import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class PlayGame here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PlayGame extends Button
{
    public PlayGame(MyWorld w){
        super(w,new GreenfootImage("Buttons/startGame.png"));
    }
    public void rest(){}
    public void clickAction(){
        w.gameEnd = false;
        w.playGame();
    }
}
