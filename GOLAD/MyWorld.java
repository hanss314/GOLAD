import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.lang.Integer;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyWorld extends World
{
    int[] birth = {3};
    int[] survive = {2,3};
    ArrayList<Tile> reds = new ArrayList();
    ArrayList<Tile> blues = new ArrayList();
    Tile[][] allTiles = new Tile[20][20];
    boolean redTurn = true;
    boolean blueTurn = false;
    int redMoves = 1;
    int blueMoves = 0;
    Stopwatch redTimer = new Stopwatch();
    Stopwatch blueTimer = new Stopwatch();
    Text redNumber = null;
    Text blueNumber = null;
    Text redTime = null;
    Text blueTime = null;
    int moveNumber = 0;
    int totalMoves = 0;
    Text moveDisplay = null;
    Text winText = null;
    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public MyWorld()
    {
        super(870, 600, 1); 
        createGrid();
        randomizeGrid();
        addObject(new Image("redDisplay.jpg"),735,45);
        addObject(new Image("blueDisplay.jpg"),735,136);
        addObject(new Text("Player 1",30),660,20);
        addObject(new Text("Cells:",30),650,70);
        addObject(new Text("Player 2",30),660,111);
        addObject(new Text("Cells:",30),650,161);
        addObject(new EndMove(this),735,240);
        addObject(new Undo(this),661,360);
        addObject(new Redo(this),809,360);
        addObject(new NewGame(this),661,470);
        addObject(new LoadGame(this),809,470);
        displayMoves();
        updateNumbers();
        redTimer.reset();
        blueTimer.reset();
        blueTimer.pause();
        writeBoard("save"+Integer.toString(moveNumber));
        Greenfoot.start();
    }
    public void act(){
        drawTime();
    }
    public void createGrid(){
        for(int x = 0; x < 20; x++){
            for(int y = 0; y < 20; y++){
                Tile toAdd = new Tile(this);
                allTiles[x][y]=toAdd;
                addObject(toAdd, 30*x+15, 30*y+15);
            }
        }        
    }
    public void iterate(){
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.update();
            }
        }
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.preupdate();
            }
        }
        updateLists();
    }
    public void updateLists(){
        reds.clear();
        blues.clear();
        for(Tile[] ts : allTiles){
            for(Tile t : ts){
                if(t.isRed){
                    reds.add(t);
                }else if(t.isBlue){
                    blues.add(t);
                }
            }
        }
    }
    public void doTurn(){
        iterate();
        if(redTurn){
            blueMoves++;
        }else if(blueTurn){
            redMoves++;
        }
        if(redMoves > 1){
            redMoves = 1;
        }
        if(blueMoves > 1){
            blueMoves = 1;
        }
        redTurn = !redTurn;
        blueTurn = !blueTurn;
        updateNumbers();
        if(redMoves > 0 && redTurn){
            redTimer.unpause();
            blueTimer.pause();
        }else if(blueMoves > 0 && blueTurn){
            redTimer.pause();
            blueTimer.unpause();
        }else{
            redTimer.pause();
            blueTimer.pause();
        }      
        moveNumber++;
        totalMoves = moveNumber;              
        displayMoves();
        writeBoard("save"+Integer.toString(moveNumber));
        if(reds.size()==0 && blues.size()==0){
            winText = new Text("It's a tie!",40);
            addObject(winText,735,550);
        }else if(reds.size()==0){
            winText = new Text("Blue Wins!",40);
            addObject(winText,735,550);
        }else if(blues.size()==0){
            winText = new Text("Red Wins!",40);
            addObject(winText,735,550);
        }
    }
    public void randomizeGrid(){
        for(int x = 0; x<20; x++){
            for(int y = 0; y<20; y++){
                Tile t = allTiles[x][y];
                int state = Greenfoot.getRandomNumber(6);
                if(state == 0){
                    t.isRed = true;
                    t.isBlue = false;
                    t.isDead = false;
                    //reds.add(t);
                }else if(state == 1){
                    t.isRed = false;
                    t.isBlue = true;
                    t.isDead = false;
                    //blues.add(t);
                }else{
                   t.isRed = false;
                   t.isBlue = false;
                   t.isDead = true; 
                }
            }
        }
        updateLists();
        while(reds.size()!=blues.size()){
            int x = 0;
            int y = 0;
            do{
                x = Greenfoot.getRandomNumber(20);
                y = Greenfoot.getRandomNumber(20);
            }while(!(allTiles[x][y].isDead)); 
            allTiles[x][y].isDead = true;
            if(reds.size() > blues.size()){
                allTiles[x][y].isBlue = true;
                blues.add(allTiles[x][y]);
            }else if(blues.size()>reds.size()){
                allTiles[x][y].isRed = true;
                reds.add(allTiles[x][y]);
            }
        }
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.preupdate();
            }
        }      
        updateLists();
    }
    public void iterate(int times){
        for(int i = 0; i<times; i++){
            iterate();
        }
    }
    public void updateNumbers(){
        if(redNumber!=null){
            removeObject(redNumber);
        }
        if(blueNumber!=null){
            removeObject(blueNumber);
        }
        redNumber = new Text(Integer.toString(reds.size()),30);
        blueNumber = new Text(Integer.toString(blues.size()),30);
        addObject(redNumber,700,70);
        addObject(blueNumber,700,161);
    }
    public void drawTime(){
        int[] redTimes = redTimer.getStndTime();
        int[] blueTimes = blueTimer.getStndTime();
        if(redTime != null){
            removeObject(redTime);
        }
        if(blueTime != null){
            removeObject(blueTime);
        }
        String redS = "";
        String blueS = "";
        for(int i = 0; i<4; i++){            
            if(i==3){
                redS+=Integer.toString(redTimes[i]);
                blueS+=Integer.toString(blueTimes[i]);
            }else{
                redS+=(Integer.toString(redTimes[i])+":");
                blueS+=(Integer.toString(blueTimes[i])+":");
            }
        }
        redTime = new Text(redS,30);
        blueTime = new Text(blueS,30);
        addObject(redTime,790,20);
        addObject(blueTime,790,111);
    }
    public void writeBoard(String filename){
        PrintWriter writer = null;
        try{
            writer = new PrintWriter("saves/"+filename,"UTF-8");
            Tile t = null;
            for(int y = 0; y<20; y++){
                for(int x = 0; x<20; x++){
                    t = allTiles[x][y];
                    if(t.isRed){
                        writer.print("R");
                    }else if(t.isBlue){
                        writer.print("B");
                    }else{
                        writer.print(".");
                    }
                }
                writer.println();
            }
            if(redTurn){
                writer.print("R");
            }else if(blueTurn){
                writer.print("B");
            }
        }catch(Exception e){}
        finally{
            if(writer!=null){
                writer.close();
            }
        }
    }
    public void readBoard(String filename){
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader("saves/"+filename));
            String currentLine = null;
            int x = 0;
            int y = 0;
            while((currentLine=reader.readLine()) != null){
                for(char c:(currentLine.toCharArray())){
                    Tile toSet = allTiles[x][y];
                    if(c == 'R'){
                        toSet.isRed = true;
                        toSet.isBlue = false;
                        toSet.isDead = false;
                    }else if(c == 'B'){
                        toSet.isRed = false;
                        toSet.isBlue = true;
                        toSet.isDead = false;
                    }else{
                        toSet.isRed = false;
                        toSet.isBlue = false;
                        toSet.isDead = true;
                    }
                    x++;
                }
                x=0;
                y++;
                if(y==20){
                    break;
                }
            }
            char c = (reader.readLine().toCharArray())[0];
            if(c=='R'){
                redTurn = true;
                blueTurn = false;
            }else if(c=='B'){
                redTurn = false;
                blueTurn = true;
            }
        }catch(FileNotFoundException e){
            System.out.println("File not found");
        }catch(IOException e){
            System.out.println("Could not read file");
        }finally{
            try{
                reader.close();
            }catch(Exception e){}
        }
        updateLists();
        updateNumbers();
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.preupdate();
            }
        } 
    }
    public void displayMoves(){
        if(moveDisplay != null){
            removeObject(moveDisplay);
        }
        moveDisplay = new Text(("Move "+moveNumber+"/"+totalMoves),30);
        addObject(moveDisplay,735,300);
    }
    public void newBoard(){
        moveNumber = 0;
        totalMoves = 0;
        redTurn = true;
        blueTurn = false;
        randomizeGrid();
        displayMoves();
        updateNumbers();        
        redTimer.reset();
        redTimer.unpause();
        blueTimer.reset();
        blueTimer.pause();
        writeBoard("save"+Integer.toString(moveNumber));
        if(winText != null){
            removeObject(winText);
        }
    }
}
