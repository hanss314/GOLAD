import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.lang.Integer;
/**
 * Write a description of class BoardSize here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BoardSize extends Button
{
    public BoardSize(MyWorld w){
        super(w,new GreenfootImage("Buttons/boardSize.png"));
    }
    public void rest(){}
    public void clickAction(){
        JTextField width = new JTextField();
        JTextField height = new JTextField();
        Object[] message = {
            "Width:", width,
            "Height:", height
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Board Size", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try{
                w.setBoardSize(Integer.parseInt(width.getText()),Integer.parseInt(height.getText()));
            }catch(Exception e){}
        }
    }
}
