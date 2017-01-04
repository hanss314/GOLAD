/**
 * Write a description of class Timer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Stopwatch  
{
    private long startTime;
    private long recordedTime;
    private boolean paused = false;
    public Stopwatch(){
        reset();
    }
    public void reset(){
        startTime = System.nanoTime();
        recordedTime = 0;
    }
    public long getElapsedTime(){
        if(paused){
            return recordedTime;           
        }else{
            return System.nanoTime()-startTime+recordedTime;
        }
    }
    public int[] getStndTime(){
        long nanoseconds = getElapsedTime();
        int milliseconds = (int)(nanoseconds/1000000)%1000;
        int seconds = (int)(nanoseconds/1000/1000000)%60;
        int minutes = (int)(nanoseconds/60/1000/1000000)%60;
        int hours = (int)(nanoseconds/60/60/1000/1000000);
        int[] value = {hours,minutes,seconds,milliseconds};
        return value;
    }
    public void pause(){
        recordedTime = getElapsedTime();
        paused = true;
    }
    public void unpause(){
        if(paused){
            startTime = System.nanoTime();
            paused = false;
        }
    }
}
