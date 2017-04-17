import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class image here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Image extends Actor
{
    public Image(GreenfootImage img){
        setImage(img);
    } 
    public Image(String filename){
        setImage(new GreenfootImage(filename));
    }
}
