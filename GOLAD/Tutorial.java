import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Tutorial here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tutorial extends Button
{
    public Tutorial(MyWorld w){
        super(w,new GreenfootImage("Buttons/tutorial.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        w.gameEnd = false;
        w.tutorial();
    }   
}
