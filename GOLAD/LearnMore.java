import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.net.URI;
/**
 * Write a description of class LearnMore here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LearnMore extends Button
{
    public LearnMore(MyWorld w){
        super(w,new GreenfootImage("Buttons/learnMore.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        try{
            java.awt.Desktop.getDesktop().browse(new URI(
            "http://conwaylife.com/wiki/Conway%27s_Game_of_Life"));
        }catch(Exception e){}
    }
}
