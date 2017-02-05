import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Iterate here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Iterate extends Button
{
    public Iterate(MyWorld w){
        super(w,new GreenfootImage("Buttons/iterate.jpg"));
    }  
    public void rest(){}
    public void clickAction(){
        w.sandboxTurn();
    }  
}
