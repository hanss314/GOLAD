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
    public void add(long seconds){
        recordedTime += seconds*1000*1000000;
    }
    public void set(long seconds){
        recordedTime = seconds*1000*1000000;
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
    public static boolean isGreaterThan(int[] a, int[] b){
        try{
            for(int i = 0; i<a.length; i++){
                if(a[i]>b[i]){
                    return true;
                }else if(a[i]<b[i]){
                    return false;
                }
            }
            return false;
        }catch(Exception e){
            return true;
        }
    }
    public static boolean isLessThan(int[] a, int[] b){
        try{
            for(int i = 0; i<a.length; i++){
                if(a[i]<b[i]){
                    return true;
                }else if(a[i]>b[i]){
                    return false;
                }
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }
}
