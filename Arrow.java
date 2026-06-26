import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * Represents an ant
 * Contains the name of the ant (the type of ant), the maxHP, attack, the base speed, whether the ant is alive, attack cooldown
 * image of the ant, current HP, current speed, x and y coordinates and current row and column of the ant (current position of the ant)
 * @author Cindy Liang
 */
public class Ant {
    //Basic stats
    private String name;
    private int maxHP;
    private int attack;
    private double baseSpeed; //base speed is subpixels per frame
    private boolean isAlive; // whether ant is dead or not. Can be used to keep track of ants in a wave
    private BufferedImage antImage;

    //Changed stats
    private int currentHP;
    private double currentSpeed;

    //Position of ant
    private double x; //double for smoother movement?
    private int y;
    private int row;
    private int col;

    //Cooldown for attacking
    private int attackCooldown;
    
    /**
     * Creates an ant based on the given information and stats
     * @param name type of ant
     * @param maxHP maxHP of ant
     * @param attack attack of ant
     * @param baseSpeed base speed of ant
     * @param startRow row that ant walks in
     * @param antFileName file name of the ant image
     */
    public Ant(String name, int maxHP, int attack, double baseSpeed, int startRow, String antFileName){ //BufferedImage of ant or Filename of ant image?
        this.name = name;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.attack = attack;
        this.baseSpeed = baseSpeed;
        this.isAlive = true;

        try{
            this.antImage = ImageIO.read(new File("images/" + antFileName));
        } catch(Exception e){
            e.printStackTrace();
        }

        this.currentHP = maxHP;
        this.currentSpeed = baseSpeed;

        this.col = 0;
        this.row = startRow;
        this.x = 0; //Starts on the far left
        this.y = 50+ (startRow*65); //the ten pixels is to move the ant down to the center of the tile

        this.attackCooldown = 0; 

        //Can create different types of ants with this. 
        // Do not need to do the same data thing with items because all the ants preform the same thing
    }

    /**
     * Get current HP of the ant
     * @return current HP of the ant
     */
    public int getCurrentHP(){
        return this.currentHP;
    }

    /**
     * Get the type of ant
     * @return type of ant
     */
    public String getName(){
        return this.name;
    }

    /**
     * get the attack of the ant
     * @return attack of the ant
     */
    public int getAttack(){
        return this.attack;
    }

    /**
     * Get maxHP of the ant
     * @return maxHP of ant
     */
    public int getMaxHP(){
        return this.maxHP;
    }

    /**
     * Return whether the ant is still alive
     * @return true if ant is still alive and false if ant is dead
     */
    public boolean isAlive(){
        return this.isAlive;
    }

    /**
     * Get the image of ant
     * @return image of the ant
     */
    public BufferedImage getAntImage(){
        return this.antImage;
    }
    
    /**
     * ant takes damage
     * @param damage damage taken
     */
    public void takeDamage(int damage){
        this.currentHP -= damage;
        if(this.currentHP<=0){
            this.currentHP=0;
            this.isAlive=false;
        }
    }

    /**
     * modify the speed of the ant
     * @param speedModifier factor of which the speed changes
     */
    public void modifySpeed(double speedModifier){
        this.currentSpeed = baseSpeed*speedModifier;
    }

    /**
     * the current x-coordinate of the ant
     * @return current x-coordinate of the ant
     */
    public double x(){
        return this.x;
    }

    /**
     * The ant moves towards the right
     */
    public void move(){
        this.x+=this.currentSpeed;
    }

    /**
     * the current y-coordinate of the ant
     * @return current y-coordinate of the ant
     */
    public int y(){
        return this.y;
    }

    /**
     * Change the current column ant is in
     * @param col column ant is currently in
     */
    public void setCol(int col){
        this.col = col;
    }

    /**
     * Get the current column ant is in
     * @return column ant is currently in
     */
    public int col(){
        return this.col;
    }

    /**
     * Get row the ant is walking in
     * @return row ant is in
     */
    public int row(){
        return this.row;
    }

    /**
     * get attack cooldown of the ant
     * @return attack cooldown of the ant
     */
    public int getCooldown(){
        return this.attackCooldown;
    }

    /**
     * add 1 to the attack cooldown
     */
    public void addCooldown(){
        this.attackCooldown++;
    }

    /**
     * reset the attack cooldown to 0
     */
    public void resetCooldown(){
        this.attackCooldown=0;
    }

}
