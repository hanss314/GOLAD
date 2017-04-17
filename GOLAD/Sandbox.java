import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Sandbox here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Sandbox extends Button
{
    public Sandbox(MyWorld w){
        super(w,new GreenfootImage("Buttons/sandbox.png"));
    }
    public void rest(){}
    public void clickAction(){
        w.sandbox();
    }   
}
