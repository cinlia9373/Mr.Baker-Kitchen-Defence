import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * Represents an arrow
 * Contains the damage, speed, image of arrow, x and y coordinates of arrow, column and row of the arrow
 * @author Cindy Liang
 */

public class Arrow {
    private int damage;
    private double speed;
    private BufferedImage arrowImage;

    //current position
    private double x;
    private int y;
    private int col;
    private int row;

    /**
     * Creates an arrow
     * @param startRow  row that arrow starts at
     * @param startCol column that arrow starts at
     */
    public Arrow(int startRow, int startCol){
        this.damage = 15;
        this.speed = 1.5;

        try{
            this.arrowImage = ImageIO.read(new File("images/PretzelArrow.png"));
        } catch(Exception e){
            e.printStackTrace();
        }

        //initial position
        this.x = 125 + (startCol*65); //maybe add the 25 more pixels so it is a little backed up on the tile
        this.y = 50 + (startRow*65);
        this.col = startCol;
        this.row = startRow;
    }

    /**
     * Get the attack of the arrow
     * @return arrow's attack
     */
    public int getDamage(){
        return this.damage;
    }

    /**
     * Get the speed of the arrow
     * @return arrow's speed
     */
    public double getSpeed(){
        return this.speed;
    }

    /**
     * The arrow moves towards the left
     */
    public void moveArrow(){
        this.x-=this.speed; //arrow is moving towards the left
    }

    /**
     * get the current x coordinate of the arrow
     * @return x coordinate of the arrow
     */
    public double x(){
        return this.x;
    }

    /**
     * get the current y coordinate of the arrow
     * @return y coordinate of the arrow
     */
    public int y(){
        return this.y;
    }

    /**
     * get the row the arrow is flying in
     * @return row arrow is in
     */
    public int getRow(){
        return this.row;
    }

    /**
     * get current column the arrow is flying in
     * @return column arrow is in
     */
    public int getCol(){
        return this.col;
    }

    /**
     * get the image of the arrow
     * @return image of the arrow
     */
    public BufferedImage getImage(){
        return this.arrowImage;
    }

}
