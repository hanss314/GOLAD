import greenfoot.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
class SecondLayer implements Callable<double[][]> {
    private Tile[][] allTiles = new Tile[0][0];
    private final int xPos;
    private final int yPos;
    private ArrayList<Tile> reds = new ArrayList();
    ArrayList<Tile> blues = new ArrayList();
    private final boolean redTurn;
    private final boolean blueTurn;
    private final MyWorld w;
    private final int WIDTH;
    private final int HEIGHT;
    public SecondLayer(Tile[][] board, int x, int y,boolean redTurn, boolean blueTurn, MyWorld world){
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
        this.redTurn = redTurn;
        this.blueTurn = blueTurn;
        this.xPos=x;
        this.yPos=y;
        this.w = world;
    }
    public double[][] call(){
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
                    newMove[2]=getFutureRatio(coords, 1, board);
                    if((redTurn && newMove[2]>move[2])||(blueTurn && newMove[2]<move[2])){
                        move = newMove;
                    }
                }
            }
        }
        double[][] returnValue = {{move[0],move[1],move[2]}};
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
        toKill.setState(0);
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
}
