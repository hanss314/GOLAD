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
    public SelectPlayers(MyWorld w, int diff){
        super(w,new GreenfootImage("Buttons/botEasy.jpg"));
        this.difficulty = diff;
    }
    public void rest(){}
    public void clickAction(){
        difficulty+=2;
        if(this.getX()<435){
            w.redBot = true;
            w.redDepth = difficulty;
        }else if(this.getX()>435){
            w.blueBot = true;
            w.blueDepth = difficulty;
        }

        if(difficulty == 2){
            this.setImage("Buttons/botMed.jpg");
        }else if(difficulty == 4){
            this.setImage("Buttons/botHard.jpg");
        }else if(difficulty == 6){
            w.addObject(new SelectPlayer(w),getX(),getY());
        }
    }
}
