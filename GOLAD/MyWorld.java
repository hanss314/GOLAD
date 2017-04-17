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
import javax.swing.JTextField;
import java.lang.String;
import java.awt.Color;
//import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.lang.Math;
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
    int WIDTH=20;
    int HEIGHT=20;
    Tile[][] allTiles = new Tile[WIDTH][HEIGHT];
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
    Image brushImage = new Image("Tiles/dead.png");
    Text tutText;
    boolean redBot = false;
    boolean blueBot = false;
    int selected = 0;
    int counter = 0;
    int redDepth = 0;
    int blueDepth = 0;
    int[] timeLimit = {Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE};
    int timeBonus = 0;
    boolean gameEnd = false;
    Text ruleText;
    Text tileAText;
    Text tileBText;
    int fontScale = 0;
    ExecutorService service=null;
    Future<int[][]> task=null;
    String moveSequence="";
    String boardStart="";
    String[] moveArray = {};
    final String charTable = "0123456789:;ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public MyWorld()
    {
        super(870, 600, 1); 
        mainMenu();
        setPaintOrder(Text.class,Tile.class);
        if(OSDetector.isUnix()){
            fontScale = -5;
        }
        Greenfoot.start();
    }
    public String getCaryString(){
        int humanNum = 0;
        int timeLimitNum=99999;
        int timeBonusNum=99999;
        if(redBot){
            humanNum+=(2*redDepth+4);//(redDepth/2+1)*4
        }
        if(blueBot){
            humanNum+=(blueDepth/2+1);
        }
        if(timeLimit[1]!=Integer.MAX_VALUE){
            timeLimitNum = timeLimit[1];
            timeBonusNum = timeBonus;
        }
        if(screen==2){
            boardStart = getBoard();
            moveSequence = "";
        }
        String result = rules+","+Math.max(WIDTH,HEIGHT)+","+timeLimitNum+","+timeBonusNum+","+humanNum+","+boardStart+moveSequence;
        return result;
    }
    public void stringToBoard(String in,boolean update){
        int position = 0;
        int sum = 0;
        int positionInString = 0;
        char[] s = in.toCharArray();
        reds.clear();
        blues.clear();
        int exp = 3;
        int bas = 4;
        removeObjects(getObjects(Tile.class));
        allTiles = new Tile[WIDTH][HEIGHT];
        createGrid();
        try{
            for (int x = 0; x < WIDTH; x++) {
                for (int y = HEIGHT-1; y >=0 ; y--) {
                    if (position == 0) {
                        sum = charTable.indexOf(s[positionInString]);                       
                        positionInString++;
                    }
                    int value = (int)(sum / Math.pow(bas,(exp-1)-position));
                    sum -= value * (int)Math.pow(bas,(exp-1)-position);
                    allTiles[x][y].setState(value);
                    position++;
                    if (position >= exp) {
                        position = 0;
                    }
                }
            }
        }catch(Exception e){
            for (Tile[] ts:allTiles) {
                for (Tile t:ts) {
                    t.setState(0);
                }
            }
        }
        for (Tile[] ts:allTiles) {
            for (Tile t:ts) {
                if(update){
                    t.preupdate(allTiles);
                }else{
                    t.willDie = t.isDead;
                    t.updateImg();
                }
            }   
        }
        moveNumber = 0;
        totalMoves = 0;
        updateLists();
    }
    public void gameView(){
        killAll();
        screen = 4;
        createGrid();
        moveSequence = "";   
        addObject(new Image("Backgrounds/redDisplay.png"),735,45);
        addObject(new Image("Backgrounds/blueDisplay.png"),735,136);
        addObject(new Text("Player 1",30),660,20);
        addObject(new Text("Cells:",30),650,70);
        addObject(new Text("Player 2",30),660,111);
        addObject(new Text("Cells:",30),650,161);
        addObject(new Move(this),663,227);
        addObject(new Save(this), 661, 371);
        addObject(new Undo(this),809,371);
        addObject(new LoadGame(this),809,444);
        addObject(new MainMenu(this),735,580);
        displayMoves();
        updateNumbers();
        boardStart = getBoard();
        drawRules();      
    }
    public void nextMove(){
        if(moveNumber<totalMoves){
            makeMove(moveArray[moveNumber]);
            moveNumber++;
            saveTiles();
            
        }
    }
    public String getBoard(){
        String result="";
        int counter=2;
        int sum=0;
        ArrayList<Integer> triplets = new ArrayList<Integer>();
        for(int x=0; x<WIDTH; x++){
            for(int y=HEIGHT-1;y>=0;y--){
                Tile t=allTiles[x][y];
                sum+=(Math.pow(4,counter)*t.getState());
                if(counter==0){
                    triplets.add(sum);
                    sum=0;
                    counter=3;
                }
                counter--;
            }
        }
        if(counter!=2){
            triplets.add(sum);
        }
        for(int i:triplets){
            result+=charTable.toCharArray()[i];
        }
        return result;
    }
    public void readBoard(String in,boolean update){   
        moveSequence = "";
        redTurn = true;
        blueTurn = false;
        try{
            String[] parts;
            boolean newMode = false;
            char[] id = in.toCharArray();
            if(id[0]=='B'){
                int indexOfFirstComma = in.indexOf(",");
                int indexOfFirstSlash = in.indexOf("/");
                birth.clear();
                survive.clear();
                for(int i=1;i<indexOfFirstSlash;i++){
                    birth.add(Character.getNumericValue(id[i]));
                }
                for(int i=indexOfFirstSlash+2;i<indexOfFirstComma;i++){
                    survive.add(Character.getNumericValue(id[i]));
                }
                parts = in.substring(indexOfFirstComma+1).split(",");
            }else{
                parts = in.split(",");
            }
            if (parts.length < 5) {
                throw new IOException();
            }
 
            WIDTH = HEIGHT = Math.min(Math.max(Integer.parseInt(parts[0]),3),20);
            timeLimit[0]=0;
            timeLimit[1] = Integer.parseInt(parts[1]);
            timeLimit[2]=0;
            timeLimit[3]=0;
            timeBonus = Integer.parseInt(parts[2]);
            int humanNum = Integer.parseInt(parts[3]);
            stringToBoard(parts[4],update);
            /*redDepth = (int)(((humanNum/4)-1)*2);
            blueDepth = (((humanNum%4)-1)*2);
            if(redDepth<0){
                redBot = false;
            }else{
                redBot = true;
            }
            if(blueDepth<0){
                blueBot = false;
            }else{
                redBot = true;
            }*/
            int noSwapsUntil = -1;
            String[] moves = new String[parts.length-5];
            for(int i = 0; i<moves.length; i++){
                moves[i]=parts[i+5];
            } 
            int moveLimit = Integer.MAX_VALUE;
            if(screen==4){
                moveArray = moves;        
                totalMoves = moveArray.length;
                moveNumber = 0;
                saveTiles();
            }else{
                saveTiles();
                for (int t = 0; t < moves.length; t++) { // Go through all the moves.
                    makeMove(moves[t]);
                } 
            }
            updateNumbers();
            updateLists();            
            boardStart = getBoard();
        }catch (Exception e) {
            JFrame frame = new JFrame("Error");
            JOptionPane.showMessageDialog(frame, "Illegal game string", "Error", 0);
        }       
    }
    public void makeMove(String s){
        String[] moveParts = s.split("\\+");
        if (moveParts.length == 3) {
            redTimer.set(Integer.parseInt(moveParts[1])*(long)(0.01f));
            blueTimer.set(Integer.parseInt(moveParts[2])*(long)(0.01f));
        }else{
            redTimer.set(0);
            blueTimer.set(0);
        }
        int moveType = charTable.indexOf(moveParts[0].charAt(moveParts[0].length()-1))-12;
        if (moveType <= 3) {
            
            int x = charTable.indexOf(moveParts[0].charAt(0))-12;
            int y = charTable.indexOf(moveParts[0].charAt(1))-12;
            if(moveType==1){
                if(redTurn){
                    allTiles[x][HEIGHT-y-1].setState(1);
                }else if(blueTurn){
                    allTiles[x][HEIGHT-y-1].setState(2);
                }
            }else{
                int swapWith = 0;
                allTiles[x][HEIGHT-y-1].setState(0);
            }
            allTiles[x][HEIGHT-y-1].preupdate(allTiles);
            allTiles[x][HEIGHT-y-1].updateNeighbours(allTiles);
        }else if (moveType == 4){
            //moveNumber = 0;
            //totalMoves = 0;
            doTurn();
        }else{
            int endGameCause = moveType-5;
            endGameCause = 2+3*(moveType-5);
            return;
        }
    }
    public void act(){
        if(screen == 1 && !gameEnd){
            drawTime();
            if(redBot && redTurn && task==null){             
                service = Executors.newFixedThreadPool(1);        
                task = service.submit((Callable)new AI(allTiles,redDepth,redTurn,blueTurn,this));
            }else if(blueBot && blueTurn && task==null){  
                service = Executors.newFixedThreadPool(1);        
                task = service.submit((Callable)new AI(allTiles,redDepth,redTurn,blueTurn,this));
            }
        }
        if(task!=null&&task.isDone()){
            try{
                int[][] changeList = task.get();
                for(int[] is:changeList){
                    Tile toChange = allTiles[is[0]][is[1]];
                    toChange.setState(is[2]);
                    toChange.preupdate(allTiles);
                    toChange.actionDone = true;
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
        HEIGHT = 20;
        WIDTH = 20;
        birth.clear();
        survive.clear();
        birth.add(3);
        survive.add(2);
        survive.add(3);
        redBot = false;
        blueBot = false;
        timeLimit = new int[]{Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE};
        timeBonus = 0;
        addObject(new Text("Player 1", Color.red, 70+fontScale), 200, 40);
        addObject(new SelectPlayers(this, 6),200,120);
        addObject(new Text("Player 2", Color.blue, 70+fontScale), 200, 200);
        addObject(new SelectPlayers(this, 6),200,280);
        addObject(new BoardSize(this),500,120);
        addObject(new Text("20x20",30),500,150);
        addObject(new ChangeRules(this),500,240);
        drawRules();
        addObject(new TimeLimit(this),750,120);
        addObject(new Text("Untimed",30),750,180);
        addObject(new TimeBonus(this),750,240);
        addObject(new Text("0s",30),750,300);
        addObject(new PlayGame(this),435,450);
        addObject(new MainMenu(this), 435,530);      
    }
    public void setBoardSize(int width,int height){
        WIDTH=width;
        HEIGHT=height;        
        if(WIDTH >20){
            WIDTH = 20;
        }else if(WIDTH<2){
            WIDTH = 2;
        }
        if(HEIGHT >20){
            HEIGHT = 20;
        }else if(HEIGHT<2){
            HEIGHT = 2;
        }
        allTiles = new Tile[WIDTH][HEIGHT];
        if(screen==3){
            removeObjects(getObjectsAt(500,150,Text.class));
            String dimensions = String.valueOf(WIDTH)+"x"+String.valueOf(HEIGHT);
            addObject(new Text(dimensions,30),500,150);
        }else if(screen==1){
            playGame();
        }
    }
    public void setTimeLimit(int hours,int minutes,int seconds, int milliseconds){
        timeLimit = new int[]{hours,minutes,seconds,milliseconds};
        if(screen==3){
            removeObjects(getObjectsAt(750,180,Text.class));
            if(hours==Integer.MAX_VALUE){
                addObject(new Text("Untimed",30),750,180);
            }else{
                String maxTimeString = String.valueOf(hours)+":"+
                    String.valueOf(minutes)+":"+
                    String.valueOf(seconds);
                addObject(new Text(maxTimeString,30),750,180);
            }
        }
    }
    public void setTimeBonus(int seconds){
        timeBonus = seconds;
        if(screen==3){
            removeObjects(getObjectsAt(750,300,Text.class));
            addObject(new Text(String.valueOf(seconds)+"s",30),750,300);
        }
    }
    public void mainMenu(){
        killAll();
        addObject(new StartGame(this), 600, 100);
        addObject(new Sandbox(this), 600, 200);
        addObject(new Tutorial(this), 600, 300);
        addObject(new ViewGame(this),600,400);
        addObject(new CaryLink(this), 532, 500);
        addObject(new Quit(this), 668, 500);
        addObject(new Image("Backgrounds/menu.png"), 200, 300);
        screen = 0;
    }
    public void createGrid(){
        allTiles = new Tile[WIDTH][HEIGHT];
        for(int x = 0; x < WIDTH; x++){
            for(int y = 0; y < HEIGHT; y++){
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
        ArrayList<String> moveList = new ArrayList<String>();
        boolean isSummonMove = false;
        int sacraficeNumber = 0;
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                if(t.actionDone){
                    String move = String.valueOf(charTable.charAt(t.xpos+12));
                    move += charTable.charAt(HEIGHT-t.ypos+11);
                    if(!t.isDead){
                        move += "B";
                        isSummonMove = true;
                    }
                    moveList.add(move);
                    t.actionDone = false;
                }
            }
        }
        for(int i=0;i<moveList.size();i++){
            String s = moveList.get(i);
            if(!isSummonMove){
                s += "A";
            }else if(isSummonMove && s.length()==2){
                switch(sacraficeNumber){
                    case 0: s+="C";
                            sacraficeNumber++;
                            break;
                    default: s+="D";
                            break;
                }
            }
            moveList.set(i,s);
        }
        for(char c = 'A'; c<='D'; c++){
            for(String s:moveList){
                if(s.charAt(2)==c){
                    moveSequence+=",";
                    moveSequence+=s;
                }
            }
        }
        iterate(allTiles);
        redTurn = !redTurn;
        blueTurn = !blueTurn;
        updateNumbers();
        if(redTurn){
            redTimer.unpause();
            blueTimer.pause();
            blueTimer.add(-1*timeBonus);
        }else if(blueTurn){
            redTimer.pause();
            blueTimer.unpause();
            redTimer.add(-1*timeBonus);
        }else{
            redTimer.pause();
            blueTimer.pause();
        }      

        if(screen ==1){
            moveNumber++;
            totalMoves = moveNumber;
            saveTiles();
        }        
        moveSequence += ",E";
        if(screen==1){
            displayMoves();
            if(reds.size()==0 && blues.size()==0 && !gameEnd){
                JFrame frame = new JFrame("Game Over");
                JOptionPane.showMessageDialog(frame, "It's a tie!", "Game Over", 1);
                redTimer.pause();
                blueTimer.pause();
                moveSequence+=",L";
                gameEnd = true;
            }else if(reds.size()==0 && !gameEnd){
                JFrame frame = new JFrame("Game Over");
                JOptionPane.showMessageDialog(frame, "Blue Wins!", "Game Over", 1);
                redTimer.pause();
                blueTimer.pause();
                moveSequence+=",I";
                gameEnd = true;
            }else if(blues.size()==0 && !gameEnd){
                JFrame frame = new JFrame("Game Over");
                JOptionPane.showMessageDialog(frame, "Red Wins!", "Game Over", 1);
                redTimer.pause();
                blueTimer.pause();
                moveSequence+=",F";
                gameEnd = true;
            }
        }
        
        sacrafices = 0;
        doneTurn = false;
    }
    public void randomizeGrid(){
        for(int x = 0; x<(WIDTH/2); x++){
            for(int y = 0; y<HEIGHT; y++){
                Tile t = allTiles[x][y];
                Tile pair = allTiles[WIDTH-1-x][HEIGHT-1-y];
                int state = Greenfoot.getRandomNumber(5);
                switch(state){
                    case 0: t.setState(1);
                            pair.setState(2);
                            break;
                    case 1: t.setState(2);
                            pair.setState(1);
                            break;
                    default:t.setState(0);
                            pair.setState(0);

                }
            }
        }
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                t.preupdate(allTiles);
            }
        }      
        updateLists();
        boardStart = getBoard();
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
        if(Stopwatch.isGreaterThan(redTimes,timeLimit)){
            JFrame frame = new JFrame("Game Over");
            JOptionPane.showMessageDialog(frame, "Blue Wins!", "Game Over", 1);
            moveSequence+=",J";
            gameEnd=true;
        }else if(Stopwatch.isGreaterThan(blueTimes,timeLimit)){
            JFrame frame = new JFrame("Game Over");
            JOptionPane.showMessageDialog(frame, "Red Wins!", "Game Over", 1);
            moveSequence+=",G";
            gameEnd=true;
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
    public void writeBoard(Tile[][] board){
        /*PrintWriter writer = null;
        try{
            writer = new PrintWriter("saves/"+filename,"UTF-8");
            for(int y = 0; y<HEIGHT; y++){
                for(int x = 0; x<WIDTH; x++){
                    int state = board[x][y].getState();
                    switch(state){
                        case 0: writer.print(".");
                                break;
                        case 1: writer.print("R");
                                break;
                        case 2: writer.print("B");
                                break;
                        case 3: writer.print("N");
                                break;
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
        }*/
        Object[] out = {new JTextField(getCaryString())};
        JOptionPane.showConfirmDialog(null, out, "Board String", JOptionPane.DEFAULT_OPTION);
    }
    /*public void readBoard(String filename,boolean update){
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
                        toSet.setState(1);
                        toSet.willRed = true;
                        toSet.willBlue = false;
                        toSet.willDie = false;
                    }else if(c == 'B'){
                        toSet.setState(2);
                        toSet.willRed = false;
                        toSet.willBlue = true;
                        toSet.willDie = false;
                    }else if(c == 'N'){
                        toSet.setState(3);
                        toSet.willRed = false;
                        toSet.willBlue = false;
                        toSet.willDie = false;
                    }else{
                        toSet.setState(0);
                    }
                    toSet.actionDone = false;
                    x++;
                }
                x=0;
                y++;
                if(y==HEIGHT){
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
        }catch(FileNotFoundException FNFE){
            try{
                readCaryString(filename,update);
                return;
            }catch(Exception e){
                JFrame frame = new JFrame("Error");
                JOptionPane.showMessageDialog(frame, "File not found or illegal game string", "Error", 0);
            }
        }catch(IOException IOE){
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
                if(update){
                    t.preupdate(allTiles);
                }else{
                    t.updateImg();
                }
            }
        } 
        if(redBot&&redTurn){
            service = Executors.newFixedThreadPool(1);        
            task = service.submit((Callable)new AI(allTiles,redDepth,redTurn,blueTurn,this));
        }else if(blueBot&&blueTurn){
            service = Executors.newFixedThreadPool(1);        
            task = service.submit((Callable)new AI(allTiles,blueDepth,redTurn,blueTurn,this));
        }
        if(screen != 4){
            totalMoves = 0;
            moveNumber = 0;
        }
        boardStart = getBoard();
    }*/
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
        }else if(screen==3){
            addObject(ruleText, 500,280);
        }
    }
    public void newBoard(){
        if(screen!=4){
            moveNumber = 0;
            totalMoves = 0;
        }
        moveSequence = "";     
        redTurn = true;
        blueTurn = false;
        randomizeGrid();
        displayMoves();
        updateNumbers();        
        redTimer.reset();
        redTimer.unpause();
        blueTimer.reset();
        blueTimer.pause();
        boardStart = getBoard();
        saveTiles();
    }
    public void clearBoard(){
        if(screen != 4){
            moveNumber = 0;
            totalMoves = 0;
        }
        moveSequence = "";        
        createGrid();
        displayMoves();
        updateNumbers();  
        boardStart = getBoard();
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
        moveSequence = "";       
        createGrid();
        randomizeGrid();
        addObject(new Image("Backgrounds/redDisplay.png"),735,45);
        addObject(new Image("Backgrounds/blueDisplay.png"),735,136);
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
        boardStart = getBoard();
        drawRules();
    }
    public void sandbox(){
        killAll();
        screen = 2;
        createGrid();
        moveNumber = 0;
        totalMoves = 0;
        moveSequence = "";
        
        addObject(new Image("Backgrounds/redDisplay.png"),735,45);
        addObject(new Image("Backgrounds/blueDisplay.png"),735,136);
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
        boardStart = getBoard();
        saveTiles();
        drawRules();
    }
    public void getRules(){
        rules = "B";
                        
        JTextField birthString = new JTextField();
        JTextField surviveString= new JTextField();
        Object[] message = {
            "Birth:", birthString,
            "Survive:", surviveString
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Rules", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            birth.clear();
            survive.clear();
            try{
                rules += birthString.getText()+"/S"+surviveString.getText();;
                char[] birthArray = birthString.getText().toCharArray();
                char[] surviveArray = surviveString.getText().toCharArray();
                for(char c:birthArray){
                    birth.add(Integer.parseInt(String.valueOf(c)));
                }
                for(char c:surviveArray){
                    survive.add(Integer.parseInt(String.valueOf(c)));
                }
                if(screen != 3){
                    for(Tile[] ts:allTiles){
                        for(Tile t:ts){
                            t.preupdate(allTiles);
                        }
                    }
                    newBoard();
                }     
                drawRules();
            }catch(Exception e){
                JFrame frame = new JFrame("Error");
                JOptionPane.showMessageDialog(frame, "Invalid Rules", "Error", 0);
            }
        }
        
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
    public void tutorialTileCount(){
        if(tileAText != null){
            removeObject(tileAText);
        }
        if(tileBText != null){
            removeObject(tileBText);
        }
        String aText = "Tile A is ";
        String bText = "Tile B is ";
        Tile a = allTiles[2][3];
        Tile b = allTiles[7][6];
        if(a.isDead){
            aText+="dead and has\n";
        }else{
            aText+="alive and has\n";
        }
        if(b.isDead){
            bText+="dead and has\n";
        }else{
            bText+="alive and has\n";
        }
        aText += String.valueOf(a.getNeighbours(allTiles).size());
        bText += String.valueOf(b.getNeighbours(allTiles).size());
        aText += " neighbours.";
        bText += " neighbours.";
        tileAText = new Text(aText,25+fontScale);
        tileBText = new Text(bText,25+fontScale);
        addObject(tileAText, 730, 510);
        addObject(tileBText, 730, 565);
    }
    public void tutorial(){
        killAll();
        WIDTH = 20;
        HEIGHT = 20;
        createGrid();
        screen = 5;
        killAll();
        createGrid();
        moveNumber = 0;
        totalMoves = 0;
        moveSequence = "";
        addObject(new Image("Backgrounds/redDisplay.png"),735,45);
        addObject(new Image("Backgrounds/blueDisplay.png"),735,136);
        addObject(new Text("Player 1",30),660,20);
        addObject(new Text("Cells:",30),650,70);
        addObject(new Text("Player 2",30),660,111);
        addObject(new Text("Cells:",30),650,161);
        tutText= new Text(
            "Conway's Game of Life\n"+
            "consists of live(white)\n"+
            "and dead(black) cells\n"+
            "Click around to experiment.\n"+
            "Click \"Next\" to continue",
            Color.black, 25+fontScale);
        addObject(new Next(this),640,227);
        addObject(new MainMenu(this), 735, 300);
        addObject(tutText, 730, 420);
        addObject(new Text("A",30+fontScale),75,105);
        addObject(new Text("B",30+fontScale),225,195);
        readBoard("B3/S23,20,99999,99999,0,00zkw3k030AAkk0A0kn30000w3003300000Dwznw00kkn3A0000Az00Dw00000Ak0Dw003003000000k03zk00k0An00zk0kA00000000zw00003Ak0000A300000000000000",
        false);
        boardStart = getBoard();
        tutorialTileCount();
    }
    public void tutorial2(){
        screen = 6;
        if(tileAText != null){
            removeObject(tileAText);
        }
        if(tileBText != null){
            removeObject(tileBText);
        }
        moveNumber = 0;
        totalMoves = 0;
        moveSequence = "";
        removeObject(tutText);
        removeObjects(getObjectsAt(75,105,Text.class));
        removeObjects(getObjectsAt(225,195,Text.class));
        addObject(new LearnMore(this),750,227);
        addObject(new EndMove(this), 735, 360);
        tutText= new Text(
            "When a turn ends, dead cells\n"+
            "with exactly 3 neighbours\n"+
            "become alive. Live cells\n"+
            "only stay alive if they have\n"+
            "2 or 3 neighbours.\n"+
            "dot in the center of a\n"+
            "cell show what it will\n"+
            "be in the next generation.", 
            Color.black, 24+fontScale);
        readBoard("B3/S23,20,99999,99999,0,00zkw3k030AAkk0A0kn30000w3003300000Dwznw00kkn3A0000Az00Dw00000Ak0Dw003003000000k03zk00k0An00zk0kA00000000zw00003Ak0000A300000000000000",
        true);
        boardStart = getBoard();
        addObject(tutText, 735, 500);
    }
    public void tutorial5(){
        screen = 9;
        moveNumber = 0;
        totalMoves = 0;
        moveSequence = "";
        removeObject(tutText);
        removeObjects(getObjects(LearnMore.class));
        createGrid();
        randomizeGrid();
        tutText= new Text(
            "Let's add colors to the\n"+
            "living cells. When cells\n"+
            "are born, they take on the\n"+
            "color of the majority of\n"+
            "their neighbours. They stay\n"+
            "that color until they die.", 
            Color.black, 24+fontScale);
        addObject(tutText, 735, 500);
    }
    public void tutorial3(){
        screen = 7;
        sacrafices = 0;
        moveNumber = 0;
        totalMoves = 0;
        moveSequence = "";
        doneTurn = false;
        removeObjects(getObjects(LearnMore.class));
        removeObjects(getObjects(Next.class));
        removeObject(tutText);
        tutText= new Text(
            "Every turn, you can kill an\n"+
            "enemy cell. The goal of the\n"+
            "game is to kill all enemy \n"+
            "cells. Try it!(You are red)",
        Color.black, 25+fontScale);;
        addObject(tutText, 730, 500);
        readBoard("B3/S23,20,99999,99999,0,00000000000000000I000001E000000000000000000000000000000000000000000000U000002000000800000000000000000000000000000000000000000000000000",
        true);
        boardStart = getBoard();
    }
    public void tutorial4(){
        screen=8;
        sacrafices = 0;
        moveNumber = 0;
        totalMoves = 0;
        moveSequence = "";
        redTurn = true;
        blueTurn = false;
        doneTurn = false;
        removeObject(tutText);
        tutText=new Text(
            "You can also sacrafice\n"+
            "two of your own cells to\n"+
            "create a new cell! Click\n"+
            "on two of your own cells,\n"+
            "then click on a dead cell.\n"+
            "Try to save your cells.",
        Color.black, 25+fontScale);
        addObject(tutText, 730, 500);
        readBoard("B3/S23,20,99999,99999,0,00000000000000000E00000100E0000000000000000000000000000000000000000000U00000200000080000000000E000000000000000000000000000000000000000",
        true);
        boardStart = getBoard();
    }
    public void setTurn(int turnNumber){
        for(Tile[] ts:allTiles){
            for(Tile t:ts){
                boolean[] tileState = t.previousStates.get(turnNumber);
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
        if(screen==4&&moveArray[turnNumber].equals("E")){
            redTurn = !redTurn;
            blueTurn = !blueTurn;
        }
        updateNumbers();
    }
}
