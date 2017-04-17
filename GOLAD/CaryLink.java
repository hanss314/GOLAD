import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.net.URI;
/**
 * Write a description of class CaryLink here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CaryLink extends Button
{
    public CaryLink(MyWorld w){
        super(w,new GreenfootImage("Buttons/caryLink.png"));
    }
    public void rest(){}
    public void clickAction(){
        try{
            java.awt.Desktop.getDesktop().browse(new URI(
            "https://www.youtube.com/user/carykh"));
        }catch(Exception e){}
    }
}
