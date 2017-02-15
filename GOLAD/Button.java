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
    int canDoAction=-1;
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
    public void act(){
        MouseInfo cursor = Greenfoot.getMouseInfo();
        if(Greenfoot.mouseClicked(this)|| Greenfoot.mouseClicked(getOneIntersectingObject(Text.class))){
            MouseInfo mouse=Greenfoot.getMouseInfo();
            int mX=mouse.getX(), mY=mouse.getY();
            if(cursor != null &&
               mX >= (getX()-width/2)&&
               mX <= (getX()+width/2) &&
               mY >= (getY()-height/2) &&
               mY <= (getY()+height/2))
            {          
                clickAction();
            }
        }
        if(canDoAction <2){
            canDoAction++;
        }
        return;
    }
    public abstract void clickAction();
    public abstract void rest();
}
