import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SelectBot here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SelectBot extends Button
{
    int difficulty;
    public SelectBot(MyWorld w, int diff){
        super(w,new GreenfootImage("Buttons/botEasy.jpg"));
        this.difficulty = diff;
        if(diff == 2){
            this.setImage("Buttons/botMed.jpg");
        }else if(diff == 4){
            this.setImage("Buttons/botHard.jpg");
        }
    }
    public void rest(){}
    public void clickAction(){
        w.selected++;
        if(this.getX()<435){
            w.redBot = true;
            w.redDepth = difficulty;
            w.removeObjects(w.redSelects);
            w.redSelects.clear();
        }else if(this.getX()>435){
            w.blueBot = true;
            w.blueDepth = difficulty;
            w.removeObjects(w.blueSelects);
            w.blueSelects.clear();
        }
        
    }
}
