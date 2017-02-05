import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Red here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Red extends Button
{
    public Red(MyWorld w){
        super(w,new GreenfootImage("Tiles/redLive.jpg"));
    }  
    public void rest(){}
    public void clickAction(){
        w.setBrush(1,getImage());
    }
}
