import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class mainMenu here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MainMenu extends Button
{
    public MainMenu(MyWorld w){
        super(w,new GreenfootImage("Buttons/mainMenu.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        w.mainMenu();
    }
}
