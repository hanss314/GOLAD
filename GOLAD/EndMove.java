import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
/**
 * Write a description of class endMove here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class EndMove extends Button
{
    public EndMove(MyWorld w){
        super(w,new GreenfootImage("endMove.jpg"));
    }  
    public void rest(){}
    public void clickAction(){
        w.doTurn();
    }
}
