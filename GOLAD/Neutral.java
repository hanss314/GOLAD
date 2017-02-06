import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Neutral here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Neutral extends Button
{
    public Neutral(MyWorld w){
        super(w,new GreenfootImage("Tiles/neutral.jpg"));
    }  
    public void rest(){}
    public void clickAction(){
        w.setBrush(3,getImage());
    }
}
