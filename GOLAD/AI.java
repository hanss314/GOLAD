import greenfoot.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
class AI implements Callable<int[][]> {
    private Tile[][] allTiles = new Tile[0][0];
    private final int depth;
    private ArrayList<Tile> reds = new ArrayList();
    ArrayList<Tile> blues = new ArrayList();
    private final boolean redTurn;
    private final boolean blueTurn;
    private final MyWorld w;
    private final int WIDTH;
    private final int HEIGHT;
    public AI(Tile[][] board, int depth,boolean redTurn, boolean blueTurn, MyWorld world){
        WIDTH = board.length;
        HEIGHT = board[0].length;
        allTiles = new Tile[WIDTH][HEIGHT];
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                allTiles[i][j] = new Tile(world,i,j);
            }
        }
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                Tile toClone = board[i][j];
                Tile newTile = allTiles[i][j];
                newTile.isRed = toClone.isRed;
                newTile.isBlue = toClone.isBlue;
                newTile.isDead = toClone.isDead;
                newTile.willDie = toClone.willDie;
                newTile.willRed = toClone.willRed;
                newTile.willBlue = toClone.willBlue;
            }
        }
        this.allTiles = board;
        this.depth = depth;
        this.redTurn = redTurn;
        this.blueTurn = blueTurn;
        this.w = world;
    }
    public int[][] call() {
        if(depth == 0){
            return makeRandomMove();
        }
        double[] move = {0,0,0};
        Tile[][] board = new Tile[WIDTH][HEIGHT];
        if(blueTurn){
            move=new double[]{0,0,2147483647};
        }
        for(int x = 0; x < WIDTH; x++){
            for(int y = 0; y < HEIGHT; y++){
                Tile toAdd = new Tile(w, x, y);
                board[x][y]=toAdd;
            }
        }
        for(int x=0; x<WIDTH; x++){
            for(int y=0; y<HEIGHT; y++){
                if(allTiles[x][y].getState()==1||allTiles[x][y].getState()==2){
                    int[] coords = {x,y};
                    for(int tileX=0; tileX<WIDTH; tileX++){
                        for(int tileY=0; tileY<HEIGHT; tileY++){
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
           (redTurn && depth == 4 && ((blues.size() % 4)==0) && blues.size()==4 && reds.size()==4)||
           (blueTurn && depth == 4 && ((reds.size()) % 4==0) && reds.size()==4 && blues.size()==4)){
            int[][] returnValue = squareCrawl();
            if(returnValue.length !=0){
                return returnValue;
            }
        }
        if(((reds.size()/blues.size()) == move[2]) &&
           (redTurn && depth == 4 && ((blues.size() % 4)==0) && blues.size()<=12 && reds.size()>4)||
           (blueTurn && depth == 4 && ((reds.size()) % 4==0) && reds.size()<=12 && blues.size()>4)){
            int[][] returnValue = checkForSquares();
            if(returnValue.length !=0){
                return returnValue;
            }
        }
        int[][] returnValue = {{(int)move[0],(int)move[1],0}};
        return returnValue;
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
    public void iterate(int times, Tile[][] board){
        for(int i = 0; i<times; i++){
            iterate(board);
        }
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
        public double getFutureRatio(int[] square, int depth, Tile[][] board){
        Tile toKill = board[square[0]][square[1]];
        double redCount = 0;
        double blueCount = 0;
        toKill.isRed = false;
        toKill.isBlue = false;
        toKill.isDead = true;
        toKill.updateNeighbours(board);
        //writeBoard("before.txt", board);
        iterate(2, board);
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
    public int[][] checkForSquares(){
        ArrayList<Tile> sacraficeList = new ArrayList<Tile>();
        int x=0;
        int y=0;
        int sacra1X = 0;
        int sacra2X = 0;
        int sacra1Y=0;
        int sacra2Y=0;
        findSquare:
        for(x=0; x<WIDTH; x++){
            for(y=0; y<HEIGHT; y++){
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
                    int[][] returnValue = new int[0][0];
                    return returnValue;
                }
            }
        }
        findEasySacrafices:
        for(int x2=0; x2<WIDTH; x2++){
            for(int y2=0; y2<HEIGHT; y2++){
                Tile toCheck = allTiles[x2][y2];
                if(((toCheck.isRed && redTurn) || (toCheck.isBlue && blueTurn)) && toCheck.willDie){
                    sacraficeList.add(toCheck);
                    if(sacraficeList.size()==1){
                        sacra2X=x2;
                        sacra2Y=y2;
                    }else{
                        sacra1X=x2;
                        sacra1Y=y2;
                    }
                }
                if(sacraficeList.size()==2){
                    break findEasySacrafices;
                }
            }
        }
        if(sacraficeList.size()<2){
            findOtherSacrafices:
            for(int x2=0; x2<WIDTH; x2++){
                for(int y2=0; y2<HEIGHT; y2++){
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
                        if(sacraficeList.size()==1){
                            sacra2X=x2;
                            sacra2Y=y2;
                        }else{
                            sacra1X=x2;
                            sacra1Y=y2;
                        }
                    }
                    if(sacraficeList.size()==2){
                        break findOtherSacrafices;
                    }
                }
            }
        }
        if(sacraficeList.size()<2){
            int[][] returnValue = new int[0][0];
            return returnValue;
        }
        int[] sacra1={sacra1X,sacra1Y,0};
        int[] sacra2={sacra2X,sacra2Y,0};
        for(int x2=-1; x2<=2; x2++){
            for(int y2=-1; y2<=2; y2++){
                if((x2>-1 && x2<2 && (y2==-1 || y2==2))||
                (y2>-1 && y2<2 && (x2==-1 || x2==2))){
                    try{
                        Tile toSummon = allTiles[x+x2][y+y2];
                        int[] summon = new int[3];
                        summon[0] = x+x2;
                        summon[1] = y+y2;
                        if(redTurn){
                            summon[2] = 1;
                        }else if(blueTurn){
                            summon[2] = 2;
                        }
                        int[][] returnValue ={sacra1,sacra2,summon};
                        return returnValue;
                    }catch(Exception e){}
                }
            }
        }
        int[][] returnValue = new int[0][0];
        return returnValue;
    }
    public int[][] squareCrawl(){      
        int redX=0;
        int redY=0;
        int blueX=0;
        int blueY=0;
        findRedSquare:
        for(redX=0; redX<WIDTH; redX++){
            for(redY=0; redY<HEIGHT; redY++){
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
                    int[][] returnValue = new int[0][0];
                    return returnValue;
                }
            }
        }
        findBlueSquare:
        for(blueX=0; blueX<WIDTH; blueX++){
            for(blueY=0; blueY<HEIGHT; blueY++){
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
                    int[][] returnValue = new int[0][0];
                    return returnValue;
                }
            }
        }
        int sacrafice1X = 0;
        int sacrafice1Y = 0;
        int sacrafice2X = 0;
        int sacrafice2Y = 0;
        int summonX = 0;
        int summonY = 1;
        if(redY-blueY>2){
            if(redTurn){
                sacrafice1X =redX;
                sacrafice1Y =redY+1;
                sacrafice2X =redX+1;
                sacrafice2Y =redY+1;
                summonX =redX;
                summonY =redY-1;
            }else if(blueTurn){
                sacrafice1X =blueX;
                sacrafice1Y =blueY;
                sacrafice2X =blueX+1;
                sacrafice2Y =blueY;
                summonX =blueX;
                summonY =blueY+2;
            }
        }else if(redY-blueY<-2){
            if(redTurn){    
                sacrafice1X =redX;
                sacrafice1Y =redY;
                sacrafice2X =redX+1;
                sacrafice2Y =redY;
                summonX =redX;
                summonY =redY+2;
            }else if(blueTurn){
                sacrafice1X =blueX;
                sacrafice1Y =blueY+1;
                sacrafice2X =blueX+1;
                sacrafice2Y =blueY+1;
                summonX =blueX;
                summonY =blueY-1;
            }
        }else if(redX-blueX<0){
            if(redTurn){               
                sacrafice1X =redX;
                sacrafice1Y =redY;
                sacrafice2Y =redY+1;
                sacrafice2X =redX;
                summonY =redY;
                summonX =redX+2;
            }else if(blueTurn){
                sacrafice1Y =blueY;
                sacrafice1X =blueX+1;
                sacrafice2Y =blueY+1;
                sacrafice2X =blueX+1;
                summonY =blueY;
                summonX =blueX-1;
            }
        }else if(redX-blueX>0){
            if(redTurn){
                sacrafice1Y =redY;
                sacrafice1X =redX+1;
                sacrafice2Y =redY+1;
                sacrafice2X =redX+1;
                summonY =redY;
                summonX =redX-1;
            }else if(blueTurn){
                sacrafice1X =blueX;
                sacrafice1Y =blueY;
                sacrafice2Y =blueY+1;
                sacrafice2X =blueX;
                summonY =blueY;
                summonX =blueX+2;
            }
        }
        
        int[] sacrafice1 = {sacrafice1X,sacrafice1Y,0};
        int[] sacrafice2 = {sacrafice2X,sacrafice2Y,0};
        int[] summon = new int[3];
        summon[0] = summonX;
        summon[1] = summonY;
        if(redTurn){
            summon[2] = 1;
        }else if(blueTurn){
            summon[2] = 2;
        }
        int[][] returnValue ={sacrafice1,sacrafice2,summon};
        return returnValue;
    }    
    public int[][] makeRandomMove(){
        int tileX = Greenfoot.getRandomNumber(WIDTH);
        int tileY = Greenfoot.getRandomNumber(HEIGHT);
        Tile t = allTiles[tileX][tileY];
        while(!((t.isBlue && redTurn)||(t.isRed && blueTurn))){
            tileX = Greenfoot.getRandomNumber(WIDTH);
            tileY = Greenfoot.getRandomNumber(HEIGHT);
            t = allTiles[tileX][tileY];
        }
        int[] changes = {tileX,tileY,0};
        int[][] returnValue = {changes};
        return returnValue;
    }
}
