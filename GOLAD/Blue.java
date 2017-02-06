import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Blue here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Blue extends Button
{
    public Blue(MyWorld w){
        super(w,new GreenfootImage("Tiles/blueLive.jpg"));
    }  
    public void rest(){}
    public void clickAction(){
        w.setBrush(2,getImage());
    }
}
