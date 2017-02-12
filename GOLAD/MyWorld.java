import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.lang.Integer;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.lang.String;
import java.awt.Color;
//import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    //int redMoves = 1;
    //int blueMoves = 0;
    Stopwatch redTimer = new Stopwatch();
    Stopwatch blueTimer = new Stopwatch();
    Text redNumber = null;
    Text blueNumber = null;
    Text redTime = null;
    Text blueTime = null;
    int moveNumber = 0;
    int totalMoves = 0;
    int sacrafices = 0;
    boolean doneTurn = false;
    Text moveDisplay = null;
    Text winText = null;
    int screen = 0;//menu = 0, game = 1, sandbox = 2, player select = 3, tutorial = 5-8
    String rules = "B3/S23";
    int setColor = 0;
    Image brushImage = new Image("Tiles/dead.jpg");
    Text tutText;
    boolean redBot = false;
    boolean blueBot = false;
    int selected = 0;
    ArrayList<Button> redSelects= new ArrayList<>();
    ArrayList<Button> blueSelects= new ArrayList<>();
    int counter = 0;
    int redDepth = 0;
    int blueDepth = 0;
    boolean gameEnd = false;
    Text ruleText;
    int fontScale = 0;
    ExecutorService service=null;
    Future<int[][]> task=null;
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
        //tutorial();
        //playerSelect();
        if(OSDetector.isUnix()){
            fontScale = -5;
        }
        Greenfoot.start();
    }
    public void act(){
        if(screen == 1){
            drawTime();
            if(redBot && redTurn && !gameEnd && task==null){             
                service = Executors.newFixedThreadPool(1);        
                task = service.submit((Callable)new AI(allTiles,redDepth,redTurn,blueTurn,this));
            }else if(blueBot && blueTurn && !gameEnd && task==null){  
                service = Executors.newFixedThreadPool(1);        
                task = service.submit((Callable)new AI(allTiles,blueDepth,redTurn,blueTurn,this));
            }
        }else if(screen == 3 && selected == 2){
            playGame();
            selected = 0;
        }
        if(task!=null&&task.isDone()){
            try{
                int[][] changeList = task.get();
                for(int[] is:changeList){
                    Tile toChange = allTiles[is[0]][is[1]];
                    if(is[2]==0){
                        toChange.isRed=false;
                        toChange.isBlue=false;
                        toChange.isDead=true;
                    }else if(is[2]==1){
                        toChange.isRed=true;
                        toChange.isBlue=false;
                        toChange.isDead=false;
                    }else if(is[2]==2){
                        toChange.isRed=false;
                        toChange.isBlue=true;
                        toChange.isDead=false;
                    }
                    toChange.preupdate(allTiles);
                    toChange.updateNeighbours(allTiles);
                }               
            }catch(Exception e){}
            finally{
                task=null;
                service=null;
                doTurn();
            }
        }
    }
    public void playerSelect(){
        killAll();
        selected = 0;
        screen = 3;
        addObject(new Text("Player 1", Color.red, 70+fontScale), 200, 40);
        addObject(new Text("Player 2", Color.blue, 70+fontScale), 670, 40);
        SelectPlayer redPlayer = new SelectPlayer(this);
        SelectPlayer bluePlayer = new SelectPlayer(this);
        SelectBot redEasy = new SelectBot(this, 0);
        SelectBot blueEasy = new SelectBot(this, 0);
        SelectBot redMed = new SelectBot(this, 2);
        SelectBot blueMed = new SelectBot(this, 2);
        SelectBot redHard = new SelectBot(this, 4);
        SelectBot blueHard = new SelectBot(this, 4);
        addObject(redPlayer, 200,150);
        addObject(redEasy, 200, 250);
        addObject(bluePlayer, 670,150);
        addObject(blueEasy, 670, 250);
        addObject(redMed, 200,350);
        addObject(redHard, 200, 450);
        addObject(blueMed, 670,350);
        addObject(blueHard, 670, 450);
        addObject(new MainMenu(this), 435,530);
        redSelects.addAll(Arrays.asList(redEasy, redMed, redHard ,redPlayer));
        blueSelects.addAll(Arrays.asList(blueEasy, blueMed, blueHard ,bluePlayer));
        
    }
    public void mainMenu(){
        killAll();
        addObject(new StartGame(this), 600, 150);
        addObject(new Sandbox(this), 600, 250);
        addObject(new Tutorial(this), 600, 350);
        addObject(new CaryLink(this), 532, 450);
        addObject(new Quit(this), 668, 450);
        addObject(new Image("Backgrounds/menu.jpg"), 200, 300);
        screen = 0;
    }
    public void createGrid(){
        for(int x = 0; x < 20; x++){
            for(int y = 0; y < 20; y++){
                Tile toAdd = new Tile(this, x, y);
                allTiles[x][y]=toAdd;
                addObject(toAdd, 30*x+15, 30*y+15);
            }
        }        
    }
    public void iterate(Tile[][] board){
        for(Tile[] ts:board){
            for(Tile t:ts){
                t.update();
            }
        }
        for(Tile[] ts:board){
            for(Tile t:ts){
                t.preupdate(board);
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
        iterate(allTiles);
        redTurn = !redTurn;
        blueTurn = !blueTurn;
        updateNumbers();
        if(redTurn){
            redTimer.unpause();
            blueTimer.pause();
        }else if(blueTurn){
            redTimer.pause();
            blueTimer.unpause();
        }else{
            redTimer.pause();
            blueTimer.pause();
        }      
        saveTiles();
        moveNumber++;
        totalMoves = moveNumber;  
        if(screen==1){
            displayMoves();
            if(reds.size()==0 && blues.size()==0 && !gameEnd){
                JFrame frame = new JFrame("Game Over");
                JOptionPane.showMessageDialog(frame, "It's a tie!", "Game Over", 1);
                redTimer.pause();
                blueTimer.pause();
                gameEnd = true;
            }else if(reds.size()==0 && !gameEnd){
                JFrame frame = new JFrame("Game Over");
                JOptionPane.showMessageDialog(frame, "Blue Wins!", "Game Over", 1);
                redTimer.pause();
                blueTimer.pause();
                gameEnd = true;
            }else if(blues.size()==0 && !gameEnd){
                JFrame frame = new JFrame("Game Over");
                JOptionPane.showMessageDialog(frame, "Red Wins!", "Game Over", 1);
                redTimer.pause();
                blueTimer.pause();
                gameEnd = true;
            }
        }
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.actionDone = false;
            }
        }
          
        sacrafices = 0;
        doneTurn = false;
    }
    public void randomizeGrid(){
        for(int x = 0; x<10; x++){
            for(int y = 0; y<20; y++){
                Tile t = allTiles[x][y];
                Tile pair = allTiles[19-x][19-y];
                int state = Greenfoot.getRandomNumber(5);
                if(state == 0){
                    t.isRed = true;
                    t.isBlue = false;
                    t.isDead = false;
                    pair.isRed = false;
                    pair.isBlue = true;
                    pair.isDead = false;
                }else if(state == 1){
                    t.isRed = false;
                    t.isBlue = true;
                    t.isDead = false;
                    pair.isRed = true;
                    pair.isBlue = false;
                    pair.isDead = false;
                }else{
                    t.isRed = false;
                    t.isBlue = false;
                    t.isDead = true; 
                    pair.isRed = false;
                    pair.isBlue = false;
                    pair.isDead = true;
                }
            }
        }
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.preupdate(allTiles);
            }
        }      
        updateLists();
    }
    public void iterate(int times, Tile[][] board){
        for(int i = 0; i<times; i++){
            iterate(board);
        }
    }
    public void updateNumbers(){
        if(redNumber!=null){
            removeObject(redNumber);
        }
        if(blueNumber!=null){
            removeObject(blueNumber);
        }
        redNumber = new Text(Integer.toString(reds.size()),30+fontScale);
        blueNumber = new Text(Integer.toString(blues.size()),30+fontScale);
        addObject(redNumber,700,70);
        addObject(blueNumber,700,161);
    }
    public void sandboxTurn(){
        iterate(allTiles);
        updateNumbers();
        moveNumber++;
        totalMoves = moveNumber;  
        displayMoves();
        saveTiles();
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
        redTime = new Text(redS,30+fontScale);
        blueTime = new Text(blueS,30+fontScale);
        addObject(redTime,790,20);
        addObject(blueTime,790,111);
    }
    public void writeBoard(String filename, Tile[][] board){
        PrintWriter writer = null;
        try{
            writer = new PrintWriter("saves/"+filename,"UTF-8");
            Tile t = null;
            for(int y = 0; y<20; y++){
                for(int x = 0; x<20; x++){
                    t = board[x][y];
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
            writer.print("B");
            for(int i:birth){
                writer.print(i);
            }
            writer.print("/S");
            for(int i:survive){
                writer.print(i);
            }
            writer.println();
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
        birth.clear();
        survive.clear();
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
                    toSet.actionDone = false;
                    x++;
                }
                x=0;
                y++;
                if(y==20){
                    break;
                }
            }
            currentLine=reader.readLine();
            boolean slashRead = false;
            for(char c:currentLine.toCharArray()){
                try{
                    if(c=='/'){
                        slashRead=true;
                    }else if(!slashRead){
                        birth.add(Integer.parseInt(String.valueOf(c)));
                    }else if(slashRead){
                        survive.add(Integer.parseInt(String.valueOf(c)));
                    }
                }catch(Exception e){}
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
            JFrame frame = new JFrame("Error");
            JOptionPane.showMessageDialog(frame, "File not found", "Error", 0);
        }catch(IOException e){
            JFrame frame = new JFrame("Error");
            JOptionPane.showMessageDialog(frame, "Could not read file", "Error", 0);
        }finally{
            try{
                reader.close();
            }catch(Exception e){}
        }
        redTimer.reset();
        blueTimer.reset();
        updateLists();
        updateNumbers();
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.preupdate(allTiles);
            }
        } 
        if(redBot&&redTurn){
            service = Executors.newFixedThreadPool(1);        
            task = service.submit((Callable)new AI(allTiles,redDepth,redTurn,blueTurn,this));
            doTurn();
        }else if(blueBot&&blueTurn){
            service = Executors.newFixedThreadPool(1);        
            task = service.submit((Callable)new AI(allTiles,blueDepth,redTurn,blueTurn,this));
            doTurn();
        }
    }
    public void displayMoves(){
        if(moveDisplay != null){
            removeObject(moveDisplay);
        }
        moveDisplay = new Text(("Move "+moveNumber+"/"+totalMoves),30);
        if(screen==1){
            addObject(moveDisplay,735,245);
        }else if(screen==2){
            addObject(moveDisplay,735,270);
        }
    }
    public void drawRules(){
        removeObject(ruleText);
        ruleText = new Text(rules, 30);
        if(screen==1){
            addObject(ruleText, 735, 264);
        }else if(screen==2){
            addObject(ruleText, 735, 299);
        }
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
        saveTiles();
    }
    public void clearBoard(){
        moveNumber = 0;
        totalMoves = 0;
        createGrid();
        displayMoves();
        updateNumbers();        
        saveTiles();
    }
    public void killAll(){
        removeObjects(getObjects(Actor.class));
    }
    public void playGame(){
        killAll();
        screen = 1;
        moveNumber = 0;
        totalMoves = 0;
        createGrid();
        randomizeGrid();
        addObject(new Image("Backgrounds/redDisplay.jpg"),735,45);
        addObject(new Image("Backgrounds/blueDisplay.jpg"),735,136);
        addObject(new Text("Player 1",30+fontScale),660,20);
        addObject(new Text("Cells:",30+fontScale),650,70);
        addObject(new Text("Player 2",30+fontScale),660,111);
        addObject(new Text("Cells:",30+fontScale),650,161);
        addObject(new EndMove(this),735,204);
        addObject(new Save(this), 661, 315);
        addObject(new Undo(this),661,371);
        addObject(new Redo(this),809,371);
        addObject(new NewGame(this),661,444);
        addObject(new LoadGame(this),809,444);
        addObject(new ChangeRules(this),735,520);
        addObject(new MainMenu(this),735,580);
        displayMoves();
        updateNumbers();
        redTimer.reset();
        blueTimer.reset();
        redTimer.unpause();
        blueTimer.pause();
        redTurn = true;
        blueTurn = false;
        saveTiles(); 
        drawRules();
    }
    public void sandbox(){
        killAll();
        screen = 2;
        createGrid();
        moveNumber = 0;
        totalMoves = 0;
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
        //saveFile;
        addObject(new Save(this), 661, 371);
        addObject(new Clear(this),661,444);
        addObject(new LoadGame(this),809,444);
        addObject(new ChangeRules(this),735,520);
        addObject(new MainMenu(this),735,580);
        displayMoves();
        updateNumbers();
        saveTiles();
        drawRules();
    }
    public void getRules(){
        rules = "B";
        birth.clear();
        survive.clear();                
        JFrame frame = new JFrame("Birth Rules");
        String input = JOptionPane.showInputDialog(frame, 
            "Enter the birth rules without commas", "Birth Rules", 1);  
        rules += (input+"/S");
        char[] numbers = input.toCharArray();
        for(char c:numbers){
            birth.add(Integer.parseInt(String.valueOf(c)));
        }
        frame = new JFrame("Survival Rules");
        input = JOptionPane.showInputDialog(frame, 
            "Enter the survival rules without commas", "Survival Rules", 1);
        rules += input;
        numbers = input.toCharArray();
        for(char c:numbers){
            survive.add(Integer.parseInt(String.valueOf(c)));
        }
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.preupdate(allTiles);
            }
        }
        newBoard();
        drawRules();
    }
    public void setBrush(int color, GreenfootImage img){
        setColor = color;
        brushImage.setImage(img);
    }
    public void saveTiles(){
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                boolean[] state = {t.isRed,t.isBlue,t.isDead};
                t.previousStates.ensureCapacity(totalMoves+10);
                t.previousStates.add(moveNumber, state);
            }
        }
    }
    public void tutorial(){
        screen = 5;
        killAll();
        createGrid();
        moveNumber = 0;
        totalMoves = 0;
        addObject(new Image("Backgrounds/redDisplay.jpg"),735,45);
        addObject(new Image("Backgrounds/blueDisplay.jpg"),735,136);
        addObject(new Text("Player 1",30),660,20);
        addObject(new Text("Cells:",30),650,70);
        addObject(new Text("Player 2",30),660,111);
        addObject(new Text("Cells:",30),650,161);
        tutText= new Text("Welcome to the\n"+
            "tutorial,\n"+ 
            "click \"Next\"\n"+
            "to continue", 
            Color.black, 30+fontScale);
        addObject(new Next(this),640,227);
        addObject(new MainMenu(this), 735, 330);
        addObject(tutText, 730, 500);      
    }
    public void tutorial2(){
        screen = 6;
        removeObject(tutText);
        addObject(new LearnMore(this),750,227);
        tutText= new Text("The basis of this game\n"+
            "is Conway's game of life.\n"+
            "Click \"Learn More\" to\n"+
            "learn more about\n"+
            "Conway's game of life.", 
            Color.black, 25+fontScale);
        addObject(tutText, 730, 500);
    }
    public void tutorial3(){
        screen = 7;
        sacrafices = 0;
        moveNumber = 0;
        totalMoves = 0;
        doneTurn = false;
        removeObjects(getObjects(LearnMore.class));
        removeObjects(getObjects(Next.class));
        removeObject(tutText);
        addObject(new EndMove(this),735,227);
        tutText= new Text("Every turn, you can kill an\n"+
        "enemy cell. The goal of the\n"+
        "game is to kill all enemy \n"+
        "cells. Try it!(You are red)",
        Color.black, 25+fontScale);;
        addObject(tutText, 730, 500);
        readBoard("tutorial/tutorial0");
    }
    public void tutorial4(){
        screen=8;
        sacrafices = 0;
        moveNumber = 0;
        totalMoves = 0;
        redTurn = true;
        blueTurn = false;
        doneTurn = false;
        removeObject(tutText);
        tutText=new Text("You can also sacrafice\n"+
        "two of your own cells to\n"+
        "create a new cell!\n"+
        "Try to save your cells.",
        Color.black, 25+fontScale);
        addObject(tutText, 730, 500);
        readBoard("tutorial/tutorial1");
    }
    /*public double getFutureRatio(int[] square, int depth, Tile[][] board){
        Tile toKill = board[square[0]][square[1]];
        double redCount = 0;
        double blueCount = 0;
        toKill.isRed = false;
        toKill.isBlue = false;
        toKill.isDead = true;
        toKill.updateNeighbours(board);
        //writeBoard("before.txt", board);
        iterate(depth, board);
        //writeBoard("after.txt", board);
        for(Tile[] ts:board){
            for(Tile t:ts){
                if(t.isRed){
                    redCount++;
                }else if(t.isBlue){
                    blueCount++;
                }
            }
        }
        if(blueCount==0){
            return 2147483647;
        }else{
            return redCount/blueCount;
        }
    }
    public boolean checkForSquares(){
        ArrayList<Tile> sacraficeList = new ArrayList<Tile>();
        int x=0;
        int y=0;
        findSquare:
        for(x=0; x<20; x++){
            for(y=0; y<20; y++){
                Tile toCheck = allTiles[x][y];
                if((toCheck.isBlue && redTurn) || (toCheck.isRed && blueTurn)){
                    int state=toCheck.getState();
                    try{
                        if(allTiles[x+1][y].getState()==state && allTiles[x+1][y+1].getState()==state && allTiles[x][y+1].getState()==state){
                            break findSquare;
                        }
                    }catch(Exception e){}
                }
                if(x==19 && y== 19){
                    return false;
                }
            }
        }
        findEasySacrafices:
        for(int x2=0; x2<20; x2++){
            for(int y2=0; y2<20; y2++){
                Tile toCheck = allTiles[x2][y2];
                if(((toCheck.isRed && redTurn) || (toCheck.isBlue && blueTurn)) && toCheck.willDie){
                    sacraficeList.add(toCheck);
                }
                if(sacraficeList.size()==2){
                    break findEasySacrafices;
                }
            }
        }
        if(sacraficeList.size()<2){
            findOtherSacrafices:
            for(int x2=0; x2<20; x2++){
                for(int y2=0; y2<20; y2++){
                    Tile toCheck = allTiles[x2][y2];
                    int[] neighbours = toCheck.getNeighbourCount(allTiles);
                    int neighbourCount = neighbours[0]+neighbours[1]+neighbours[2];
                    ArrayList<Tile> neighbourList = toCheck.getNeighbours(allTiles);
                    for(Tile t:neighbourList){
                        if(sacraficeList.contains(t)){
                            neighbourCount--;
                        }
                    }
                    if(((toCheck.isRed && redTurn) || (toCheck.isBlue && blueTurn)) && neighbourCount==3){
                        sacraficeList.add(toCheck);
                    }
                    if(sacraficeList.size()==2){
                        break findOtherSacrafices;
                    }
                }
            }
        }
        if(sacraficeList.size()<2){
            return false;
        }
        for(Tile t:sacraficeList){
            t.isBlue=false;
            t.isRed=false;
            t.isDead=true;
            t.preupdate(allTiles);
            t.updateNeighbours(allTiles);
        }
        for(int x2=-1; x2<=2; x2++){
            for(int y2=-1; y2<=2; y2++){
                if((x2>-1 && x2<2 && (y2==-1 || y2==2))||
                (y2>-1 && y2<2 && (x2==-1 || x2==2))){
                    try{
                        Tile toSummon = allTiles[x+x2][y+y2];
                        toSummon.isRed = redTurn;
                        toSummon.isBlue = blueTurn;
                        toSummon.isDead = false;
                        toSummon.preupdate(allTiles);
                        toSummon.updateNeighbours(allTiles);
                        return true;
                    }catch(Exception e){}
                }
            }
        }
        return false;
    }
    public boolean squareCrawl(){      
        int redX=0;
        int redY=0;
        int blueX=0;
        int blueY=0;
        findRedSquare:
        for(redX=0; redX<20; redX++){
            for(redY=0; redY<20; redY++){
                Tile toCheck = allTiles[redX][redY];
                if(toCheck.isRed){
                    int state=toCheck.getState();
                    try{
                        if(allTiles[redX+1][redY].getState()==state && allTiles[redX+1][redY+1].getState()==state && 
                        allTiles[redX][redY+1].getState()==state && allTiles[redX][redY-1].getState()==0 && allTiles[redX+1][redY-1].getState()==0 &&
                        allTiles[redX-1][redY].getState()==0 && allTiles[redX-1][redY+1].getState()==0 && allTiles[redX][redY+2].getState()==0 &&
                        allTiles[redX+1][redY+2].getState()==0 && allTiles[redX+2][redY+1].getState()==0 && allTiles[redX+2][redY].getState()==0){
                            break findRedSquare;
                        }
                    }catch(Exception e){}
                }
                if(redX==19 && redY == 19){
                    return false;
                }
            }
        }
        findBlueSquare:
        for(blueX=0; blueX<20; blueX++){
            for(blueY=0; blueY<20; blueY++){
                Tile toCheck = allTiles[blueX][blueY];
                if(toCheck.isBlue){
                    int state=toCheck.getState();
                    try{
                        if(allTiles[blueX+1][blueY].getState()==state && allTiles[blueX+1][blueY+1].getState()==state && 
                        allTiles[blueX][blueY+1].getState()==state && allTiles[blueX][blueY-1].getState()==0 && allTiles[blueX+1][blueY-1].getState()==0 &&
                        allTiles[blueX-1][blueY].getState()==0 && allTiles[blueX-1][blueY+1].getState()==0 && allTiles[blueX][blueY+2].getState()==0 &&
                        allTiles[blueX+1][blueY+2].getState()==0 && allTiles[blueX+2][blueY+1].getState()==0 && allTiles[blueX+2][blueY].getState()==0){
                            break findBlueSquare;
                        }
                    }catch(Exception e){}
                }
                if(blueX==19 && blueY == 19){
                    return false;
                }
            }
        }
        Tile sacrafice1=null;
        Tile sacrafice2=null;
        Tile summon=null;
        if(redY-blueY>2){
            if(redTurn){
                sacrafice1=allTiles[redX][redY+1];
                sacrafice2=allTiles[redX+1][redY+1];
                summon=allTiles[redX][redY-1];
            }else if(blueTurn){
                sacrafice1=allTiles[blueX][blueY];
                sacrafice2=allTiles[blueX+1][blueY];
                summon=allTiles[blueX][blueY+2];
            }
        }else if(redY-blueY<-2){
            if(redTurn){               
                sacrafice1=allTiles[redX][redY];
                sacrafice2=allTiles[redX+1][redY];
                summon=allTiles[redX][redY+2];
            }else if(blueTurn){
                sacrafice1=allTiles[blueX][blueY+1];
                sacrafice2=allTiles[blueX+1][blueY+1];
                summon=allTiles[blueX][blueY-1];
            }
        }else if(redX-blueX<0){
            if(redTurn){               
                sacrafice1=allTiles[redX][redY];
                sacrafice2=allTiles[redX][redY+1];
                summon=allTiles[redX+2][redY];
            }else if(blueTurn){
                sacrafice1=allTiles[blueX+1][blueY];
                sacrafice2=allTiles[blueX+1][blueY+1];
                summon=allTiles[blueX-1][blueY];
            }
        }else if(redX-blueX>0){
            if(redTurn){
                sacrafice1=allTiles[redX+1][redY];
                sacrafice2=allTiles[redX+1][redY+1];
                summon=allTiles[redX-1][redY];
            }else if(blueTurn){
                sacrafice1=allTiles[blueX][blueY];
                sacrafice2=allTiles[blueX][blueY+1];
                summon=allTiles[blueX+2][blueY];
            }
        }
        if(sacrafice1 != null && sacrafice2 !=null && summon !=null){
            sacrafice1.isRed=false;
            sacrafice1.isBlue=false;
            sacrafice1.isDead=true;
            sacrafice2.isRed=false;
            sacrafice2.isBlue=false;
            sacrafice2.isDead=true;
            summon.isDead=false;
            if(redTurn){
                summon.isBlue=false;
                summon.isRed=true;
            }else if(blueTurn){
                summon.isBlue=true;
                summon.isRed=false;
            }
        }
        sacrafice1.preupdate(allTiles);
        sacrafice1.updateNeighbours(allTiles);
        sacrafice2.preupdate(allTiles);
        sacrafice2.updateNeighbours(allTiles);
        summon.preupdate(allTiles);
        summon.updateNeighbours(allTiles);
        return true;
    }
    public void makeMove(int depth){
        if(depth == 0){
            makeRandomMove();
            return;
        }
        double[] move = {0,0,0};
        Tile[][] board = new Tile[20][20];
        if(blueTurn){
            move=new double[]{0,0,2147483647};
        }
        for(int x = 0; x < 20; x++){
            for(int y = 0; y < 20; y++){
                Tile toAdd = new Tile(this, x, y);
                board[x][y]=toAdd;
            }
        }
        for(int x=0; x<20; x++){
            for(int y=0; y<20; y++){
                if(allTiles[x][y].getState()==1||allTiles[x][y].getState()==2){
                    int[] coords = {x,y};
                    for(int tileX=0; tileX<20; tileX++){
                        for(int tileY=0; tileY<20; tileY++){
                            Tile newTile = board[tileX][tileY];
                            Tile toClone = allTiles[tileX][tileY];
                            newTile.isRed = toClone.isRed;
                            newTile.isBlue = toClone.isBlue;
                            newTile.isDead = toClone.isDead;
                            newTile.willDie = toClone.willDie;
                            newTile.willRed = toClone.willRed;
                            newTile.willBlue = toClone.willBlue;
                        }
                    }
                    double[] newMove = new double[3];
                    newMove[0]=x;
                    newMove[1]=y;
                    if((reds.size()+blues.size())<16){
                        newMove[2]=getFutureRatio(coords, 2, board);   
                    }else{
                        newMove[2]=getFutureRatio(coords, depth, board);       
                    }
                    if((redTurn && newMove[2]>move[2])||(blueTurn && newMove[2]<move[2])){
                        move = newMove;
                    }
                }
            }
        }
        if(((reds.size()/blues.size()) == move[2]) &&
           (redTurn && redDepth == 4 && ((blues.size() % 4)==0) && blues.size()==4 && reds.size()==4)||
           (blueTurn && blueDepth == 4 && ((reds.size()) % 4==0) && reds.size()==4 && blues.size()==4)){
            if(squareCrawl()){
                return;
            }
        }
        if(((reds.size()/blues.size()) == move[2]) &&
           (redTurn && redDepth == 4 && ((blues.size() % 4)==0) && blues.size()<=12 && reds.size()>4)||
           (blueTurn && blueDepth == 4 && ((reds.size()) % 4==0) && reds.size()<=12 && blues.size()>4)){
            if(checkForSquares()){
                return;
            }
        }
        Tile toMove = allTiles[(int)move[0]][(int)move[1]];
        toMove.isRed = false;
        toMove.isBlue = false;
        toMove.isDead = true;
        toMove.preupdate(allTiles);
        toMove.updateNeighbours(allTiles);
    }
    public void makeRandomMove(){
        int tileX = Greenfoot.getRandomNumber(20);
        int tileY = Greenfoot.getRandomNumber(20);
        Tile t = allTiles[tileX][tileY];
        while(!((t.isBlue && redTurn)||(t.isRed && blueTurn))){
            tileX = Greenfoot.getRandomNumber(20);
            tileY = Greenfoot.getRandomNumber(20);
            t = allTiles[tileX][tileY];
        }
        t.isRed=false;
        t.isBlue=false;
        t.isDead=true;
    }*/
    public void setTurn(int turnNumber){
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                boolean[] tileState = t.previousStates.get(turnNumber-1);
                t.isRed = tileState[0];
                t.isBlue = tileState[1];
                t.isDead = tileState[2];
            }
        }
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.preupdate(allTiles);
            }
        }
        updateNumbers();
    }
    public String test(){
        return System.getProperty("os.name");

    }
}
