import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.lang.Math.*;
import java.lang.Integer.*;
/**
 * Write a description of class Tile here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tile extends Button
{
    //states
    boolean isRed = false;
    boolean isBlue = false;
    boolean isDead = true;
    boolean willDie = true;
    boolean willRed = false;
    boolean willBlue = false;
    boolean actionDone = false;
    boolean[] previousState = new boolean[3];
    //rules
    int[] birth;
    int[] survive;
    //assets
    GreenfootImage redLive = new GreenfootImage("redLive.jpg");
    GreenfootImage redDying = new GreenfootImage("redDying.jpg");
    GreenfootImage blueLive = new GreenfootImage("blueLive.jpg");
    GreenfootImage blueDying = new GreenfootImage("blueDying.jpg");
    GreenfootImage deadBlue = new GreenfootImage("deadBlue.jpg");
    GreenfootImage deadRed = new GreenfootImage("deadRed.jpg");
    GreenfootImage dead = new GreenfootImage("dead.jpg");
    
    public Tile(MyWorld w, boolean isRed, boolean isBlue){
        this(w);
        this.isRed = isRed;
        this.isBlue = isBlue;
        if(isRed || isBlue){
            isDead = false;
        }
    }
    public Tile(MyWorld w){
        super(w, new GreenfootImage("dead.jpg")); 
        getLifeRules();
    }
    public void rest(){}
    public void clickAction(){
        if(!actionDone){
            previousState[0]=isRed;
            previousState[1]=isBlue;
            previousState[2]=isDead;
            if(isRed&& w.blueTurn && w.blueMoves>0){
                isRed=false;
                isBlue = false;
                isDead = true;
                w.blueMoves--;
                actionDone = true;
            }else if(isBlue && w.redTurn && w.redMoves>0){
                isRed=false;
                isBlue = false;
                isDead = true;
                w.redMoves--;
                actionDone = true;
            }else if(isDead && w.redTurn && w.redMoves>0){
                isRed=true;
                isBlue = false;
                isDead = false;
                w.redMoves -= 2;
                actionDone = true;
            }else if(isDead && w.blueTurn && w.blueMoves>0){
                isRed=false;
                isBlue = true;
                isDead = false;
                w.blueMoves -= 2;
                actionDone = true;
            }
        }else{
            isRed = previousState[0];
            isBlue = previousState[1];
            isDead = previousState[2];
            if(w.redTurn){
                w.redMoves = 1;
            }else if(w.blueTurn){
                w.blueMoves = 1;
            }           
            actionDone = false;
        }
        preupdate();
        updateImg();
        updateNeighbours();
    }
    private void getLifeRules(){
        this.birth = w.birth;
        this.survive = w.survive;
    }
    public int getState(){
        if(isRed){
            return 1;
        }else if(isBlue){
            return 2;
        }else{
            return 0;
        }
    }
    public int[] getNeighbours(){
        int redCount = 0;
        int blueCount = 0;
        for(int x=-1; x<2; x++){
            for(int y=-1; y<2; y++){
                if(x==0 && y==0){}
                else{
                    Tile neighbour = (Tile)getOneObjectAtOffset(x*30,y*30,Tile.class);
                    if(neighbour != null){
                        if(neighbour.getState()==1){
                            redCount++;
                        }
                        else if(neighbour.getState()==2){
                            blueCount++;
                        }
                    }
                }
            }
        }
        int[] returnValue = {redCount, blueCount};
        return returnValue;
    }
    public void preupdate(){
        int[] surroundings = getNeighbours();
        int numReds = surroundings[0];
        int numBlues = surroundings[1];
        int totalNeighbours = numReds + numBlues;
        willRed = false;
        willBlue = false;
        willDie = true;
        if(isDead){
            for(int i:birth){
                if(totalNeighbours == i){
                    if(numReds>numBlues){
                        willRed = true;
                    }else if(numBlues>numReds){
                        willBlue = true;
                    }else if(Greenfoot.getRandomNumber(1)==0){
                        willRed = true;
                    }else{
                        willBlue = true;
                    }
                    willDie = false;
                    break;
                }
            }
        }else{
            for(int i:survive){
                if(totalNeighbours==i){
                    willDie = false;
                    willRed = isRed;
                    willBlue = isBlue;
                    break;
                }
            }
        }
        updateImg();
    }
    public void update(){
        isRed = willRed;
        isBlue = willBlue;
        isDead = willDie;
    }
    public void updateImg(){
        if(isRed){
            if(willDie){
                setImage(redDying);
            }else{
                setImage(redLive);
            }
        }else if(isBlue){
            if(willDie){
                setImage(blueDying);
            }else{
                setImage(blueLive);
            }
        }else if(isDead){
            if(willRed){
                setImage(deadRed);
            }else if(willBlue){
                setImage(deadBlue);
            }else{
                setImage(dead);
            }
        }
    }
    public void updateNeighbours(){
        for(int x=-1; x<2; x++){
            for(int y=-1; y<2; y++){                
                Tile neighbour = (Tile)getOneObjectAtOffset(x*30,y*30,Tile.class);
                if(neighbour != null){
                    neighbour.preupdate();
                }
            }
        }
    }
}