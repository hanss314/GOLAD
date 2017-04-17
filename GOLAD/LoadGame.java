import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javax.swing.JOptionPane;
import javax.swing.JFrame;
/**
 * Write a description of class LoadGame here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LoadGame extends Button
{
    public LoadGame(MyWorld w){
        super(w,new GreenfootImage("Buttons/loadGame.png"));
    }
    public void rest(){}
    public void clickAction(){
        JFrame frame = new JFrame("Load");
        String input = JOptionPane.showInputDialog(frame, 
            "Enter Game String", "Load Save", 1);
        w.readBoard(input,true);
    }
}
