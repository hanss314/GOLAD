import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Dead here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Dead extends Button
{
    public Dead(MyWorld w){
        super(w,new GreenfootImage("Tiles/dead.png"));
    }  
    public void rest(){}
    public void clickAction(){
        w.setBrush(0,getImage());
    }
}
