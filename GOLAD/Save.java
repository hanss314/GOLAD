import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javax.swing.JOptionPane;
import javax.swing.JFrame;
/**
 * Write a description of class Save here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Save extends Button
{
    public Save(MyWorld w){
        super(w,new GreenfootImage("Buttons/save.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        JFrame frame = new JFrame("Save");
        String input = JOptionPane.showInputDialog(frame, 
            "Enter File Name.", "Save", 1);
        w.writeBoard(input,w.allTiles);
    }
}
