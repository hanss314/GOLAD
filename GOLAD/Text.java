import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
/**
 * Write a description of class text here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Text extends Actor
{
    Actor follow;
    public Text(String text,Color color,int size,Actor follow)
    {
        this(text, color, size);
        this.follow = follow;
    }
    public Text(String text,Color color,int size)
    {
        setImage(new GreenfootImage(text,size,color,new Color(0,0,0,0)));
    }
    public Text(String text, int size){
        setImage(new GreenfootImage(text,size,Color.black,new Color(0,0,0,0)));
    }
    /**
     * Act - do whatever the text wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        if(follow != null && getWorld() != null)
        {
            try{
                setLocation(follow.getX(),follow.getY());
            }catch(IllegalStateException e){}
        }
    }    
}