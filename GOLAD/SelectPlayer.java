import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SelectPlayer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SelectPlayer extends Button
{
    public SelectPlayer(MyWorld w){
        super(w,new GreenfootImage("Buttons/player.jpg"));
    }
    public void rest(){}
    public void clickAction(){
        w.selected++;
        if(this.getX()<435){
            w.redBot = false;
            w.removeObjects(w.redSelects);
            w.redSelects.clear();
        }else if(this.getX()>435){
            w.blueBot = false;
            w.removeObjects(w.blueSelects);
            w.blueSelects.clear();
        }
    }
}
