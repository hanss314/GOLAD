import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class NewGame here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NewGame extends Button
{
    public NewGame(MyWorld w){
        super(w,new GreenfootImage("Buttons/newGame.png"));
    }
    public void rest(){}
    public void clickAction(){
        w.gameEnd = false;
        w.playGame();
    }
}
