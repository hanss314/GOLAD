import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
/**
 * Write a description of class endMove here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class EndMove extends Button
{
    public EndMove(MyWorld w){
        super(w,new GreenfootImage("Buttons/endMove.jpg"));
    }  
    public void rest(){}
    public void clickAction(){
        w.doTurn();   
        if(w.screen == 7){
            if(w.blues.size()==0){
                w.tutorial4();
                JFrame frame = new JFrame("Tutorial");
                JOptionPane.showMessageDialog(frame, "Good Job!", "Tutorial", 1);
            }else{
                w.tutorial3();
                JFrame frame = new JFrame("Tutorial");
                JOptionPane.showMessageDialog(frame, "Nope.", "Tutorial", 1);
            }
        }else if(w.screen ==8){
            if(w.reds.size()!=0){
                w.mainMenu();
                JFrame frame = new JFrame("Tutorial");
                JOptionPane.showMessageDialog(frame, "Good Job!\nYou completed the Tutorial!", "Tutorial", 1);
            }else{
                w.tutorial4();
                JFrame frame = new JFrame("Tutorial");
                JOptionPane.showMessageDialog(frame, "Nope.", "Tutorial", 1);
            }
        } 
    }
}
