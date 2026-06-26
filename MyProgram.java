import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
/**
 * Represents a game item
 * Contains the item's costs, name, maxHP, currentHP, icon, whether it can block ants, speed modifier, attack, cooldown etc.
 * Some varaibles are for specific items (did not want to use inheritance because it would've been less efficient)
 * @author Cindy Liang
 */

public class Item {
    private String name;
    private int cost;
    private int maxHP;
    private int currentHP;
    private boolean blockAnts;
    private BufferedImage icon; //icon? 65 x 65

    private double speedModifier;
    private int attack;
    private int cooldown; //cooldown for pretzel cannon and bomb
    //Cake Pop Bomb
    private boolean[][] bombRange; //for the affected areas of the bomb. null or false will be no affect and true will be affected area
    private boolean hasDetonated; 

    /**
     * Creates an item with the given stats
     * @param name name of item
     * @param cost cost of item
     * @param attack attack of item
     * @param maxHP maxHP of item
     * @param blockAnts whether item blocks ants
     * @param speedModifier the speed modifier of item
     * @param iconFileName name of the icon image file for item
     */
    public Item(String name, int cost, int attack, int maxHP, boolean blockAnts, double speedModifier, String iconFileName){
        this.name = name;
        this.cost = cost;
        this.attack = attack;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.blockAnts = blockAnts;
        this.speedModifier = speedModifier;

    }

    /**
     * Creates an item based on the itemID.
     * If itemID = ...
     * 1 - Sticky Gum Trap
     * 2 - Cheese Block
     * 3 - Pretzel Cannon
     * 4 - Cake Pop Bomb
     * @param itemID itemID
     */
    public Item(int itemID){ //Should I make these inheritance classes? They have different abilities
        if(itemID==1){
            this.name = "Sticky Gum Trap";
            this.cost = 25;
            this.attack = 0;
            this.maxHP = 10; //doesn't take any damage
            this.currentHP = 10; //doesn't take any damage
            this.speedModifier = 0.5;
            this.blockAnts = false;
            //this.canShoot = false;

            try{
                this.icon = ImageIO.read(new File("images/StickyGumTrap.png"));
            }catch(Exception e){
                e.printStackTrace();
            }

        } else if(itemID==2){
            this.name = "Cheese Block"; //Rename? Gloucester Cheese Block, Wall of Cheese, Brick of Cheese
            this.cost = 50;
            this.attack = 0;
            this.maxHP = 150; //will depend on the ants attack speed, damage and stuff
            this.currentHP = 100;
            this.speedModifier=1; //or 0? but when it blocks ant, ants won't be able to move anyway
            this.blockAnts = true;
            //this.canShoot = false;

            try{
                this.icon = ImageIO.read(new File("images/CheeseBlock.png"));
            }catch(Exception e){
                e.printStackTrace();
            }

        } else if(itemID==3){
            this.name = "Pretzel Cannon";
            this.cost = 75;
            this.attack = 15; //depends on the ants health and how fast they move
            this.maxHP = 50;
            this.currentHP = 50;
            this.speedModifier = 1; //normal speed
            this.blockAnts = true; //will block ants until ants destroy it
            //this.canShoot = true;
            this.cooldown = 0; // 60 frames?

            try{
                this.icon = ImageIO.read(new File("images/PretzelLauncher.png"));
            }catch(Exception e){
                e.printStackTrace();
            }

        }
        else if(itemID==4){
            this.name="Cake Pop Bomb";
            this.cost = 125;
            this.attack = 60; //will instantly kill
            this.maxHP = 10; //doesn't matter
            this.currentHP = 10;
            this.speedModifier = 1;
            this.blockAnts = false; //ants can't destroy it
            this.bombRange = new boolean[5][8]; //Areas where the bomb will affect
            this.cooldown = 0;
            this.hasDetonated = false;

            try{
                this.icon = ImageIO.read(new File("images/CakePoppingBomb.png"));
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * Returns the name of the item
     * @return name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Returns the cost of the item
     * @return cost
     */
    public int getCost(){
        return this.cost;
    }

    /**
     * returns the attack of the item
     * @return attack
     */
    public int getAttack(){
        return this.attack;
    }

    /**
     * returns the maxHP of the item
     * @return maxHP
     */
    public int getMaxHP(){
        return this.maxHP;
    }

    /**
     * returns the currentHP of the item
     * @return currentHP
     */
    public int getCurrentHP(){
        return this.currentHP;
    }

    /**
     * returns the speed modifier of the item
     * @return speed modifier
     */
    public double getSlowFactor(){
        return this.speedModifier;
    }

    /**
     * returns if the item blocks ants
     * @return false if item does not block ants and true if it does block ants
     */
    public boolean blocksAnts(){
        return this.blockAnts;
    }

    /**
     * Item takes damage
     * @param damage damage
     */
    public void takeDamage(int damage){
        this.currentHP -= damage;
    }


    //For the toothpick launcher
    /**
     * Get the current cooldown
     * @return cooldown
     */
    public int getCooldown(){
        return this.cooldown;
    }

    /**
     * Reset the cooldown to 0
     */
    public void resetCooldown(){
        this.cooldown=0;
    }

    /**
     * Add 1 to cooldown
     */
    public void addCooldown(){
        this.cooldown++;
    }

    //For the candy bomb
    /**
     * add an area where bomb will affect it
     * @param row row of possible bomb range
     * @param col column of possible bomb range
     */
    public void addBombArea(int row, int col){
        //Only add if the row index is 0-4 and col index is 0-7
        if(row>=0&&row<5&&col>=0&&col<8){
            this.bombRange[row][col] = true;
        }
    }

    /**
     * Return if the tile is within the bomb range
     * @param row tile's row
     * @param col tile's column
     * @return true if the tile is within bomb range, false if the tile is not within bomb range
     */
    public boolean isInBombRange(int row, int col){
        return this.bombRange[row][col];
    }

    /**
     * get the bomb range or all the areas that the bomb will affect
     * @return the bomb range
     */
    public boolean[][] getBombRange(){
        return this.bombRange;
    }

    /**
     * return if the bomb has detonated or not
     * @return true if bomb has detonated and false if not
     */
    public boolean hasDetonated(){
        return this.hasDetonated;
    }

    /**
     * When the bomb explodes, the bomb has detonated
     */
    public void EXPLODED(){
        this.hasDetonated=true;
    }

    /**
     * Get the icon of the item
     * @return
     */
    public BufferedImage getIcon(){
        return this.icon;
    }

}
