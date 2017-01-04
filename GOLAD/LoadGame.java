import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class LoadGame here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LoadGame extends Button
{
    public LoadGame(MyWorld w){
        super(w,new GreenfootImage("loadGame.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        w.readBoard("quicksave.txt");
    }
}
