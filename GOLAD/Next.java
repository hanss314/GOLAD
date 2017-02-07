import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
/**
 * Write a description of class endMove here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Next extends Button
{
    public Next(MyWorld w){
        super(w,new GreenfootImage("Buttons/next.jpg"));
    }  
    public void rest(){}
    public void clickAction(){
        int screen = w.screen;
        if(screen==5){
            w.tutorial2();
        }else if(screen==6){
            w.tutorial3();
        }           
    }
}
