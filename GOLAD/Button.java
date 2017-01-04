import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
/**
 * Write a description of class Button here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Button extends Actor
{
    MyWorld w;
    GreenfootImage img;
    //Text text;
    int height;
    int width;
    int timer=0;
    boolean mouseWasDown = false;
    /*public Button(MyWorld w, Color backColor, int height,
                  int width, String text, Color textColor, int textSize){
        this(w, backColor, height, width);
        this.text = new Text(text,textColor,textSize,this);
        w.addObject(this.text,300,200);
    }
    public Button(MyWorld w, Color backColor, int height, int width){
        this.w=w;
        this.height = height;
        this.width = width;
        img = new GreenfootImage(width*2,height*2);
        img.setColor(backColor);
        img.setTransparency(255);
        img.fillRect(width/2,height/2,0,0);
        this.setImage(img);
    }*/
    public Button(MyWorld w, GreenfootImage img){
        this.w=w;
        this.img = img;
        this.height = img.getHeight();
        this.width = img.getWidth();
        this.setImage(img);
    }
    public void act()
    {
        if(timer < 50){
            timer++;
        }
        checkMouse();
    }
    public boolean mouseTouching()
    {
        MouseInfo cursor = Greenfoot.getMouseInfo();
        if(cursor != null &&
           cursor.getX() >= (getX()-width/2)&&
           cursor.getX() <= (getX()+width/2) &&
           cursor.getY() >= (getY()-height/2) &&
           cursor.getY() <= (getY()+height/2))
        {          
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean hoverClicked()
    {
        if(mouseTouching() && Greenfoot.getMouseInfo().getButton() == 1){
            mouseWasDown = true;
            return false;
        }
        else if(mouseTouching() && timer==50 && mouseWasDown && Greenfoot.getMouseInfo().getButton() != 1){
            mouseWasDown = false;
            timer = 0;
            return true;
        }
        else{
            mouseWasDown = false;
            return false;
        }
    }
    public void checkMouse()
    {
        if(hoverClicked()){
            clickAction();
        }else{
            rest();
        }
    }
    public abstract void clickAction();
    public abstract void rest();
}
