import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.lang.Integer;
/**
 * Write a description of class TimeLimit here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TimeLimit extends Button
{
    public TimeLimit(MyWorld w){
        super(w,new GreenfootImage("Buttons/timeLimit.png"));
    }
    public void rest(){}
    public void clickAction(){
        JTextField hours = new JTextField();
        JTextField minutes = new JTextField();
        JTextField seconds = new JTextField();
        Object[] message = {
            "Hours", hours,
            "Minutes", minutes,
            "Seconds",seconds
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Time Limit", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try{
                w.setTimeLimit(Integer.parseInt(hours.getText()),
                Integer.parseInt(minutes.getText()),
                Integer.parseInt(seconds.getText()),0);
            }catch(Exception e){
                w.setTimeLimit(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
            }
        }
    }
}
