import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.lang.Integer;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.lang.String;
/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyWorld extends World
{
    ArrayList<Integer> birth = new ArrayList<>(Arrays.asList(3));
    ArrayList<Integer> survive = new ArrayList<>(Arrays.asList(2,3));
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
    int sacrafices = 0;
    boolean hasCreated = false;
    Text moveDisplay = null;
    Text winText = null;
    int screen = 0;
    String rules = "B3/S23";
    int setColor = 0;
    Image brushImage = new Image("Tiles/dead.jpg");
    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public MyWorld()
    {
        super(870, 600, 1); 
        mainMenu();
        //playGame();
        //sandbox();
        Greenfoot.start();
    }
    public void act(){
        if(screen == 1){
            drawTime();
        }
    }
    public void mainMenu(){
        killAll();
        addObject(new StartGame(this), 435, 150);
        addObject(new Sandbox(this), 435, 250);
        screen = 0;
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
            JFrame frame = new JFrame("Game Over");
            JOptionPane.showMessageDialog(frame, "It's a tie!");
            redTimer.pause();
            blueTimer.pause();
        }else if(reds.size()==0){
            JFrame frame = new JFrame("Game Over");
            JOptionPane.showMessageDialog(frame, "Blue Wins!");
            redTimer.pause();
            blueTimer.pause();
        }else if(blues.size()==0){
            JFrame frame = new JFrame("Game Over");
            JOptionPane.showMessageDialog(frame, "Red Wins!");
            redTimer.pause();
            blueTimer.pause();
        }
        sacrafices = 0;
        hasCreated = false;
    }
    public void randomizeGrid(){
        for(int x = 0; x<20; x++){
            for(int y = 0; y<20; y++){
                Tile t = allTiles[x][y];
                int state = Greenfoot.getRandomNumber(3);
                if(state == 0){
                    t.isRed = true;
                    t.isBlue = false;
                    t.isDead = false;
                    //reds.add(t);
                }else{
                    t.isRed = false;
                    t.isBlue = false;
                    t.isDead = true; 
                }
            }
        }
        for(int x = 0; x<20; x++){
            for(int y = 0; y<20; y++){
                Tile t = allTiles[x][y];
                Tile tochange = allTiles[19-x][19-y];
                if(t.isRed && tochange.isRed){
                    t.isRed = false;
                    t.isBlue = false;
                    t.isDead = true; 
                    tochange.isRed = false;
                    tochange.isBlue = false;
                    tochange.isDead = true; 
                }else if(t.isRed && tochange.isDead){
                    tochange.isRed = false;
                    tochange.isBlue = true;
                    tochange.isDead = false; 
                }
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
    public void sandboxTurn(){
        iterate();
        updateNumbers();
        moveNumber++;
        totalMoves = moveNumber;  
        displayMoves();
        writeBoard("save"+Integer.toString(moveNumber));
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
            }else if(i==2){
                redS+=(Integer.toString(redTimes[i])+".");
                blueS+=(Integer.toString(blueTimes[i])+".");
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
        addObject(moveDisplay,735,283);
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
    }
    public void clearBoard(){
        moveNumber = 0;
        totalMoves = 0;
        createGrid();
        displayMoves();
        updateNumbers();        
        writeBoard("save"+Integer.toString(moveNumber));
    }
    public void killAll(){
        removeObjects(getObjects(Actor.class));
    }
    public void playGame(){
        killAll();
        createGrid();
        randomizeGrid();
        addObject(new Image("Backgrounds/redDisplay.jpg"),735,45);
        addObject(new Image("Backgrounds/blueDisplay.jpg"),735,136);
        addObject(new Text("Player 1",30),660,20);
        addObject(new Text("Cells:",30),650,70);
        addObject(new Text("Player 2",30),660,111);
        addObject(new Text("Cells:",30),650,161);
        addObject(new EndMove(this),735,227);
        addObject(new Undo(this),661,340);
        addObject(new Redo(this),809,340);
        addObject(new NewGame(this),661,435);
        addObject(new LoadGame(this),809,435);
        addObject(new ChangeRules(this),735,516);
        addObject(new MainMenu(this),735,580);
        displayMoves();
        updateNumbers();
        redTimer.reset();
        blueTimer.reset();
        blueTimer.pause();
        writeBoard("save"+Integer.toString(moveNumber));
        screen = 1;
    }
    public void sandbox(){
        killAll();
        createGrid();
        addObject(new Image("Backgrounds/redDisplay.jpg"),735,45);
        addObject(new Image("Backgrounds/blueDisplay.jpg"),735,136);
        addObject(new Text("Player 1",30),660,20);
        addObject(new Text("Cells:",30),650,70);
        addObject(new Text("Player 2",30),660,111);
        addObject(new Text("Cells:",30),650,161);
        addObject(new Iterate(this),663,227);
        addObject(new Dead(this), 800, 212);
        addObject(new Red(this), 830, 212);
        addObject(new Blue(this), 800, 242);
        addObject(new Neutral(this), 830, 242);
        addObject(brushImage, 760, 227);
        addObject(new Undo(this),661,340);
        addObject(new Redo(this),809,340);
        addObject(new Clear(this),661,435);
        addObject(new LoadGame(this),809,435);
        addObject(new ChangeRules(this),735,516);
        addObject(new MainMenu(this),735,580);
        displayMoves();
        updateNumbers();
        writeBoard("save"+Integer.toString(moveNumber));
        screen = 2;
    }
    public void getRules(){
        rules = "B";
        birth.clear();
        survive.clear();                
        JFrame frame = new JFrame("Birth Rules");
        String input = JOptionPane.showInputDialog(frame, 
            "Enter the birth rules without commas");  
        rules += (input+"/S");
        char[] numbers = input.toCharArray();
        for(char c:numbers){
            birth.add(Integer.parseInt(String.valueOf(c)));
        }
        frame = new JFrame("Survival Rules");
        input = JOptionPane.showInputDialog(frame, 
            "Enter the survival rules without commas");
        rules += input;
        numbers = input.toCharArray();
        for(char c:numbers){
            survive.add(Integer.parseInt(String.valueOf(c)));
        }
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.preupdate();
            }
        }
        newBoard();
    }
    public void setBrush(int color, GreenfootImage img){
        setColor = color;
        brushImage.setImage(img);
    }
}
