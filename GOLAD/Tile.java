import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.lang.Math.*;
import java.lang.Integer.*;
import java.util.ArrayList;
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
    final int xpos;
    final int ypos;
    ArrayList<boolean[]> previousStates = new ArrayList<boolean[]>();
    //rules
    ArrayList<Integer> birth = new ArrayList();
    ArrayList<Integer> survive = new ArrayList();
    //assets
    GreenfootImage redLive = new GreenfootImage("Tiles/redLive.png");
    GreenfootImage redDying = new GreenfootImage("Tiles/redDying.png");
    GreenfootImage blueLive = new GreenfootImage("Tiles/blueLive.png");
    GreenfootImage blueDying = new GreenfootImage("Tiles/blueDying.png");
    GreenfootImage deadBlue = new GreenfootImage("Tiles/deadBlue.png");
    GreenfootImage deadRed = new GreenfootImage("Tiles/deadRed.png");
    GreenfootImage dead = new GreenfootImage("Tiles/dead.png");
    GreenfootImage deadNeutral = new GreenfootImage("Tiles/deadNeutral.png");
    GreenfootImage neutralDying = new GreenfootImage("Tiles/neutralDying.png");
    GreenfootImage neutral = new GreenfootImage("Tiles/neutral.png");
    
    public Tile(MyWorld w, boolean isRed, boolean isBlue, int x, int y){
        this(w, x, y);
        this.isRed = isRed;
        this.isBlue = isBlue;
        if(isRed || isBlue){
            isDead = false;
        }
    }
    public Tile(MyWorld w, int x, int y){
        super(w, new GreenfootImage("Tiles/dead.png")); 
        this.xpos=x;
        this.ypos=y;
        getLifeRules();
    }
    public void rest(){}
    public void setState(int state){
        switch(state){
            case 0: isRed=false;
                    isBlue=false;
                    isDead=true;
                    break;
            case 1: isRed=true;
                    isBlue=false;
                    isDead=false;
                    break;
            case 2: isRed=false;
                    isBlue=true;
                    isDead=false;
                    break;
            case 3: isRed=false;
                    isBlue=false;
                    isDead=false;
                    break;
        }
    }
    public void clickAction(){
        if(((w.screen == 1) && ((!w.redBot || !w.redTurn)&&(!w.blueBot || !w.blueTurn))) || w.screen == 7 || w.screen ==8){
            if(!(isDead||isRed||isBlue)){
                return;
            }
            if(!actionDone && !w.doneTurn){
                previousState[0]=isRed;
                previousState[1]=isBlue;
                previousState[2]=isDead;
                if(isRed && w.sacrafices <2){
                    if(w.redTurn){
                        w.sacrafices++;
                    }else if(w.blueTurn && w.sacrafices >0){
                        return;
                    }else{
                        w.doneTurn = true;
                    }
                    setState(0);
                    actionDone = true;
                }else if(isBlue && w.sacrafices <2){
                    if(w.blueTurn){
                        w.sacrafices++;
                    }else if(w.redTurn && w.sacrafices >0){
                        return;
                    }else{
                        w.doneTurn = true;
                    }
                    setState(0);
                    actionDone = true;
                }else if(isDead && w.redTurn && w.sacrafices == 2 && !w.doneTurn){
                    setState(1);
                    actionDone = true;
                    w.doneTurn = true;
                    w.sacrafices = 0;
                }else if(isDead && w.blueTurn && w.sacrafices == 2 && !w.doneTurn){
                    setState(2);
                    actionDone = true;
                    w.doneTurn = true;
                    w.sacrafices = 0;
                }
            }else if(actionDone){
                if(w.doneTurn && isDead && (previousState[0]==w.redTurn)){
                    return;
                }
                isRed = previousState[0];
                isBlue = previousState[1];
                isDead = previousState[2];
                if(w.doneTurn && isDead){
                    w.sacrafices = 2;
                }else if(!w.doneTurn && !isDead){
                    if(w.redTurn){
                        if(!isBlue){
                            w.sacrafices--;
                        }   
                    }else if(w.blueTurn){
                        if(!isRed){
                            w.sacrafices--;
                        }
                    }    
                }
                actionDone = false;
                w.doneTurn = false;
            }
        }else if(w.screen == 2){
            setState(w.setColor);
        }else if(w.screen == 5){
            isDead = !isDead;
            willDie = isDead;
            updateImg();
            w.tutorialTileCount();
            return;
        }else if(w.screen == 6){
            isDead = !isDead;
            willDie = isDead;
        }else if(w.screen == 9){
            setState((getState()+1)%3);
        }
        updateImg();
        updateNeighbours(w.allTiles);
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
        }else if(!isDead){
            return 3;
        }else{
            return 0;
        }
    }
    public ArrayList<Tile> getNeighbours(Tile[][] board){
        ArrayList<Tile> returnValue = new ArrayList<Tile>();
        for(int x=-1; x<2; x++){
            for(int y=-1; y<2; y++){
                if(x==0 && y==0){}
                else{
                    try{
                        Tile neighbour = board[xpos+x][ypos+y];
                        if(neighbour != null){
                            if(!neighbour.isDead){
                                returnValue.add(neighbour);
                            }
                        }
                    }catch(Exception e){}
                }
            }
        }
        return returnValue;
    }
    public int[] getNeighbourCount(Tile[][] board){
        int redCount = 0;
        int blueCount = 0;
        int neutrals = 0;
        for(int x=-1; x<2; x++){
            for(int y=-1; y<2; y++){
                if(x==0 && y==0){}
                else{
                    try{
                        Tile neighbour = board[xpos+x][ypos+y];
                        if(neighbour != null){
                            switch(neighbour.getState()){
                                case 1: redCount++;
                                        break;
                                case 2: blueCount++;
                                        break;
                                case 3: neutrals++;
                                        break;
                            }
                        }
                    }catch(Exception e){}
                }
            }
        }
        int[] returnValue = {redCount, blueCount, neutrals};
        return returnValue;
    }
    public void preupdate(Tile[][] board){
        int[] surroundings = getNeighbourCount(board);
        int numReds = surroundings[0];
        int numBlues = surroundings[1];
        int totalNeighbours = numReds + numBlues + surroundings[2];
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
                    }else{
                        willRed = false;
                        willBlue = false;
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
        }else if(!isDead){
            if(willDie){
                setImage(neutralDying);
            }else{
                setImage(neutral);
            }
        }else{
            if(willRed){
                setImage(deadRed);
            }else if(willBlue){
                setImage(deadBlue);
            }else if(!willDie){
                setImage(deadNeutral);
            }else{
                setImage(dead);
            }
        }
    }
    public void updateNeighbours(Tile[][] board){
        for(int x=-1; x<2; x++){
            for(int y=-1; y<2; y++){                
                try{
                    Tile neighbour = board[xpos+x][ypos+y];
                    if(neighbour != null){
                        neighbour.preupdate(board);
                    }
                }catch(Exception e){}
            }
        }
    }
}