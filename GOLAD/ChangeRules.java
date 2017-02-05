import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ChangeRules here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ChangeRules extends Button
{
    public ChangeRules(MyWorld w){
        super(w,new GreenfootImage("Buttons/changeRules.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        w.getRules();
    }
}
