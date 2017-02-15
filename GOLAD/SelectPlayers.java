import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SelectBot here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SelectPlayers extends Button
{
    int difficulty;
    public SelectPlayers(MyWorld w, int diff){
        super(w,new GreenfootImage("Buttons/player.jpg"));
        this.difficulty = diff;
    }
    public void rest(){}
    public void clickAction(){
        difficulty+=2;
        difficulty = difficulty%8;
        if(this.getY()<150){
            if(difficulty != 6){
                w.redBot = true;
                w.redDepth = difficulty;
            }else{
                w.redBot = false;
            }
        }else if(this.getY()>150){
            if(difficulty != 6){
                w.blueBot = true;
                w.blueDepth = difficulty;
            }else{
                w.blueBot = false;
            }
        }
        if(difficulty==0){
            this.setImage("Buttons/botEasy.jpg");
        }else if(difficulty == 2){
            this.setImage("Buttons/botMed.jpg");
        }else if(difficulty == 4){
            this.setImage("Buttons/botHard.jpg");
        }else if(difficulty == 6){
            this.setImage("Buttons/player.jpg");
        }
    }
}
