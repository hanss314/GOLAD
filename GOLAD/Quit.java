import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Quit here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Quit extends Button
{
    public Quit(MyWorld w){
        super(w,new GreenfootImage("Buttons/quit.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        System.exit(0);
    }
}
