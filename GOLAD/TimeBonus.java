import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.lang.Integer;
/**
 * Write a description of class TimeBonus here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TimeBonus extends Button
{
    public TimeBonus(MyWorld w){
        super(w,new GreenfootImage("Buttons/timeBonus.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        JFrame frame = new JFrame("Birth Rules");
        String input = JOptionPane.showInputDialog(frame, 
            "Seconds each player\n gains per move", "Time Bonus", 1);
        try{
            w.setTimeBonus(Integer.parseInt(input));
        }catch(Exception e){
            w.setTimeBonus(0);
        }
    }
}
