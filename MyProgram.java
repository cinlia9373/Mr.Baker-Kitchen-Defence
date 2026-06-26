import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class MyProgram extends JPanel implements ActionListener {
    
    // what we use to talk to the keyboard and mouse - DONT DELETE
    InputHandler input; 
    // how fast we try and update the screen - DONT DELETE
    int FPS = 60;
    
    int WIDTH = 720;
    int HEIGHT = 450;
    
    // VARIABLES FOR YOUR GAME
    Item[][] kitchenGrid = new Item[5][8];
    int itemID = 0; //0 means nothing
    boolean remove = false; //for remove button
    //boolean waveMode = false; //to indicate if a wave is happening, if true: wave is in progress. if false: in preparation mode
    int waveNumber = 1; //Max of 3 waves
    int antIncomingCooldown =0; //For ant spawning
    int clickCooldown = 0;

    //List of items (is there a more efficient way to do this?)
    /*I'm doing this this way because the items have different abilities and I don't want to make a million inheritance classes (otherwise it is not efficient) 
    and cast them all in my code, so I'm automatically setting the data in the items class itself
    */

    //The ants - there is no fixed amount of ants so will use array lists
    ArrayList<Ant> totalPests = new ArrayList<>(); //total pests in the wave
    ArrayList<Ant> activePests = new ArrayList<>();  // ants on the grid

    ArrayList<Arrow> activeArrows = new ArrayList<>(); // total arrows that are currently fired

    //For the start screen
    BufferedImage[] startingScenes = new BufferedImage[7]; //All the starting scenes
    int sceneNumber = 0; //current scene

    String mode = "Start Screen"; // Current mode
    BufferedImage endScreen = null; //the ending scene which depends if the player wins or loses
    /** All the modes:
     * Start Screen
     * Prep Mode
     * Wave Mode
     * Game Over //Game Over and Victory function pretty much the same just different end screens
     * Victory
     */

    //Size of each grid space 
    int TILE_SIZE = 65;

    int ITEM_SIZE = 50;

    //Player stats
    int bakersGold = 600;
    int cakeHealth = 100;


    // use this method to load in images, arrays, or any other variables that need set up before the game runs
    public void setup(){
        for(int row = 0; row<kitchenGrid.length; row++){
            for(int col = 0; col<kitchenGrid.length; col++){
                kitchenGrid[row][col] = null;
            }
        }
        //Set up the starting scene images here
        for(int i = 0; i<startingScenes.length;i++){
            try{
                startingScenes[i] = ImageIO.read(new File("images/StartingScene"+i+".png"));
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        
        
    }
    

    // all of the "game logic" goes here 
    // This is the game heartbeat and is updated at a specific rate given by FPS
    @Override
    public void actionPerformed(ActionEvent e) {
        if(clickCooldown>0){
            clickCooldown--;
        }

        if(mode.equals("Start Screen")){
            if(input.isLeftMouseDown()){
                if(clickCooldown==0){ //per click, go to the next scene
                    sceneNumber++;
                    if(sceneNumber==startingScenes.length){// once the player has gone through all the scenes then player enters prep mode
                        mode = "Prep Mode";
                    }
                    clickCooldown = 20;
                }
                    
            }
        }
 
        //When player is in preparation mode
        if(mode.equals("Prep Mode")){
            if (input.isLeftMouseDown()) {
                
                //Can delete later
                System.out.println("Mouse Click! X: " + input.getMouseX() + " Y: " + input.getMouseY());

                //Player selecting items
                // Sticky Gum Trap (X: 105-225, Y: 380-430)
                if (input.getMouseX() >= 105 && input.getMouseX() <= 225 && input.getMouseY() >= 380 && input.getMouseY() <= 430) {
                    itemID = 1;
                    System.out.println("Selected Item 1");
                }
                
                // Cheese Block (X: 235-355, Y: 380-430)
                else if (input.getMouseX() >= 235 && input.getMouseX() <= 355 && input.getMouseY() >= 380 && input.getMouseY() <= 430) {
                    itemID = 2;
                    System.out.println("Selected Item 2");
                }
                
                // Pretzel Cannon (X: 365-485, Y: 380-430)
                else if (input.getMouseX() >= 365 && input.getMouseX() <= 485 && input.getMouseY() >= 380 && input.getMouseY() <= 430) {
                    itemID = 3;
                    System.out.println("Selected Item 3");
                }

                // Cake Pop Bomb (X: 495-615, Y: 380-430)
                //Button start (495, 380), End(615, 430)
                else if(input.getMouseX() >= 495 && input.getMouseX()<=615 && input.getMouseY()>=380 && input.getMouseY()<=430){
                    itemID=4;
                    System.out.println("Selected Item 4");
                }
                
                // When player clicks on the grid (X: 100-620, Y: 50-375)
                else if (input.getMouseX() >= 100 && input.getMouseX() <= 620 && input.getMouseY() >= 50 && input.getMouseY() <= 375) {
                    int clickedCol = (input.getMouseX() - 100) / TILE_SIZE; // 65 is TILE_SIZE
                    int clickedRow = (input.getMouseY() - 50) / TILE_SIZE;

                    if (clickedRow >= 0 && clickedRow < 5 && clickedCol >= 0 && clickedCol < 8) {
                        if (kitchenGrid[clickedRow][clickedCol] == null && itemID != 0) {
                            Item item = new Item(itemID);
                            if(bakersGold>=item.getCost()){
                                kitchenGrid[clickedRow][clickedCol] = item;
                                bakersGold -= item.getCost();
                            } 
                            if(itemID==4){ //if the item is a Cake Pop Bomb
                                //Top row
                                item.addBombArea(clickedRow-1, clickedCol-1);
                                item.addBombArea(clickedRow-1, clickedCol);
                                item.addBombArea(clickedRow-1, clickedCol+1);
                                //Current row
                                item.addBombArea(clickedRow, clickedCol-1);
                                item.addBombArea(clickedRow, clickedCol);
                                item.addBombArea(clickedRow, clickedCol+1);
                                //Bottom row
                                item.addBombArea(clickedRow+1, clickedCol-1);
                                item.addBombArea(clickedRow+1, clickedCol);
                                item.addBombArea(clickedRow+1, clickedCol+1);

                            }
                        } 
                    }
                }

                //Player clicks the remove button
                //Remove Item Button - change to green when it is being used
                // if player wants to get out of delete mode, click on trash again
                // Start: (10,385), End: (90,425)
                else if(input.getMouseX()>=10 && input.getMouseX()<=90 && input.getMouseY()>=385 && input.getMouseY()<=425){
                    if(clickCooldown==0){
                        if(remove){
                            remove = false; //Remove mode deactivated
                        } else{
                            remove = true; //Remove mode activated
                        }
                        itemID = 0;
                        clickCooldown=15;
                    }
                    
                    
                }
                
                //When player is in remove mode
                if (remove&&input.getMouseX() >= 100 && input.getMouseX() <= 620 && input.getMouseY() >= 50 && input.getMouseY() <= 375) {
                    int clickedCol = (input.getMouseX() - 100) / TILE_SIZE; // 65 is TILE_SIZE
                    int clickedRow = (input.getMouseY() - 50) / TILE_SIZE;
                    
                    if (clickedRow >= 0 && clickedRow < 5 && clickedCol >= 0 && clickedCol < 8 && kitchenGrid[clickedRow][clickedCol]!= null) {
                        bakersGold += kitchenGrid[clickedRow][clickedCol].getCost(); //get money back
                        kitchenGrid[clickedRow][clickedCol] = null; //remove the item
                    }
                }
                
                //Player clicks on start wave
                //Start Wave Button
                //Start: (625,385), End: (713,425)
                if(input.getMouseX()>=625 && input.getMouseX()<=713 && input.getMouseY()>= 385 && input.getMouseY()<=425){
                    mode="Wave Mode";
                    remove = false;
                    itemID = 0;


                    //Create the wave
                    int totalAnts = 0;
                    int totalNormalAnts = 0; //Basic ants
                    int totalFighterAnts = 0; //Ants with high attack
                    int totalSonicAnts = 0; //Ants with a high base speed
                    int totalTankAnts =0; //Ants with a lot of HP
                    //Can we combine this all into one for loop? Maybe move this all into the "Start Wave" function
                    if(waveNumber==1){ //Easy first Wave
                        totalAnts = 15; //Lets just start with normal ants
                        totalNormalAnts = totalAnts;
                    } else if(waveNumber==2){ //Medium difficulty
                        totalAnts = 25;
                        totalNormalAnts = (int)(Math.random()*(15-10)+1) + 10; // generate 10-15 normal ants
                        totalSonicAnts = (int)(Math.random()*(totalAnts-totalNormalAnts)+1); //generate 0-leftover ants of sonic ants
                        totalTankAnts = totalAnts - totalNormalAnts - totalSonicAnts; //There can be 0 tank ants. Leftovers will be tank ants

                    } else if(waveNumber==3){
                        totalAnts = 35;
                        totalNormalAnts = (int)(Math.random()*(15-10)+1) + 10; // generate 10-15 normal ants
                        totalSonicAnts = (int)(Math.random()*(6-0)+1)+0; //generate 0-6 sonic ants
                        totalTankAnts = (int)(Math.random()*(6-0)+1)+0; //generate 0-6 tank ants
                        totalFighterAnts = totalAnts - totalNormalAnts - totalSonicAnts; //leftovers will be fighter ants
                    }

                    //Prepare for the wave by adding all the ants. Originally I was going to design the different type of ants but did not end up having time
                    for(int i = 0; i<totalNormalAnts; i++){
                        int startRow = (int)(Math.random()*5); //pick an index between 0-4
                        Ant normalAnt = new Ant("Normal Ant", 30, 10, 0.8, startRow, "normalAnt.png"); //Remember to match the file name
                        totalPests.add(normalAnt);
                    }
                    for(int i = 0; i<totalSonicAnts; i++){
                        int startRow = (int)(Math.random()*5); //pick an index between 0-4
                        Ant sonicAnt = new Ant("Sonic Ant", 30, 10, 1.6, startRow, "normalAnt.png"); //Remember to match the file name
                        totalPests.add(sonicAnt);
                    }
                    for(int i = 0; i<totalTankAnts; i++){
                        int startRow = (int)(Math.random()*5); //pick an index between 0-4
                        Ant tankAnt = new Ant("Tank Ant", 60, 10, 0.8, startRow, "normalAnt.png"); //Remember to match the file name
                        totalPests.add(tankAnt);
                    }
                    for(int i = 0; i<totalFighterAnts; i++){
                        int startRow = (int)(Math.random()*5); //pick an index between 0-4
                        Ant fighterAnt = new Ant("Fighter Ant", 30, 25, 0.8, startRow, "normalAnt.png"); //Remember to match the file name
                        totalPests.add(fighterAnt);
                    }
                    //Initial ants to start
                    for(int i = 0; i<5; i++){
                        Ant ant = totalPests.get(0);
                        activePests.add(ant); // ant is out on the field
                        totalPests.remove(0);
                    }
                }
            }
        }

        //Here is where all the logistics for the items and the ants go.
        if(mode.equals("Wave Mode")){
            
            //Send the ants in groups of 5 per 150 frames
            if(totalPests.size()>0){
                antIncomingCooldown++;
                if(antIncomingCooldown == 150){
                    //Send the next group of ants to the battlefield
                    if(totalPests.size()<5){ //For some reason there was an out of bounds kind of problem so I added this. 
                        //If there are less than 5 ants left, just send the rest of them out on the field
                        for(int j = 0; j<totalPests.size(); j++){
                            Ant ant = totalPests.get(0);
                            activePests.add(ant); // ant is out on the field
                            totalPests.remove(0);
                            j--;
                        }
                    } else { //Send 5 ants
                        for(int i = 0; i<5; i++){
                            Ant ant = totalPests.get(0);
                            activePests.add(ant); // ant is out on the field
                            totalPests.remove(0);
                        }
                    }
                    
                    antIncomingCooldown=0;
                }
            }
            
            //Tracks the ants whether they are dead or reached the cake
            for(int i = 0; i<activePests.size();i++){// check each ant
                Ant ant = activePests.get(i);
                if(ant.isAlive()==false){ //if ant is dead
                    bakersGold += 20; //20 gold per ant killed
                    activePests.remove(i); //remove from field
                    i--;
                    continue;
                }

                //when ant reaches the cake
                if(ant.x()>=WIDTH-100){
                    cakeHealth-=20; //Ant takes a big chomp of cake
                    activePests.remove(i);
                    i--;
                    continue;
                }

                
                //Once ant enters the grid
                if(ant.x()>=100&&ant.x()<=620){
                    ant.setCol((int)((ant.x()-100)/TILE_SIZE)); //update the ant's column and row location (row stays the same)

                    //when ant reaches an item and how it interacts with it
                    if(kitchenGrid[ant.row()][ant.col()]!=null){
                        Item gridTileItem = kitchenGrid[ant.row()][ant.col()];
                        //if ants reach an item that blocks them (the cheese block or the pretzel cannon)
                        if(gridTileItem.blocksAnts()==true){
                            ant.modifySpeed(0); //ant's current speed is 0
                            
                            ant.addCooldown(); //Maybe every 30 frames?
                            //Ant starts to chew away the item
                            if(ant.getCooldown()==30){
                                gridTileItem.takeDamage(ant.getAttack()); //Ant chews down the item
                                ant.resetCooldown();
                            }

                            if(gridTileItem.getCurrentHP()<=0){ //Once defence item breaks
                                kitchenGrid[ant.row()][ant.col()] = null;
                                ant.resetCooldown(); //ant will not be chewing anymore
                            }
                        } else if(gridTileItem.getName().equals("Sticky Gum Trap")){ //if ant meets a glue trap
                            ant.modifySpeed(gridTileItem.getSlowFactor()); //ant gets slow
                            ant.resetCooldown(); //ant starts walking again so it is reseted
                        } else if(gridTileItem.getName().equals("Cake Pop Bomb") && gridTileItem.hasDetonated()==false){// if ant is on a cake pop bomb
                            gridTileItem.EXPLODED();
                            //check for the bomb range and if any ants are in the bomb range
                            ant.takeDamage(gridTileItem.getAttack()); //Ant that detonated the bomb takes damage
                            //Check if other ants were affected by the bomb
                            for(int j=0; j<activePests.size(); j++){
                                Ant otherAnt = activePests.get(j);
                                //If other ant(s) were in the bomb range then they take damage
                                if(gridTileItem.isInBombRange(otherAnt.row(), otherAnt.col())){
                                    otherAnt.takeDamage(gridTileItem.getAttack());
                                }
                            }
                                                        
                        }

                    } else { //no item
                        ant.modifySpeed(1);
                        ant.resetCooldown(); //just an extra precaution
                    }
                }
                //if ant can move then move. (current speed can be at 0)
                ant.move();
            }

            //logistics for the pretzel cannon
            for(int row = 0; row<kitchenGrid.length;row++){
                for(int col = 0; col<kitchenGrid[row].length; col++){
                    //For every pretzel cannon
                    if(kitchenGrid[row][col]!=null){
                        if(kitchenGrid[row][col].getName().equals("Pretzel Cannon")){
                            //check for pests in the line of fire to start shooting
                            Item pretzelCannon = kitchenGrid[row][col];
                            //Check for incoming ants (ants in the same row as the toothpick launcher)
                            ArrayList<Ant> incomingAnts = new ArrayList<>();
                            for(int h = 0; h<activePests.size(); h++){
                                Ant a = activePests.get(h);
                                if(a.row()==row){
                                    incomingAnts.add(a);
                                }
                            }

                            if(incomingAnts.size()>0){
                                //start cooldown
                                pretzelCannon.addCooldown();
                                if(pretzelCannon.getCooldown()==100){
                                    Arrow b = new Arrow(row,col);
                                    activeArrows.add(b);
                                    pretzelCannon.resetCooldown();
                                }
                            }
                        }
                        //For the explosion effect afterwards
                        if(kitchenGrid[row][col]!=null&&kitchenGrid[row][col].getName().equals("Cake Pop Bomb")&&kitchenGrid[row][col].hasDetonated()){
                            Item bomb = kitchenGrid[row][col];
                            bomb.addCooldown();
                            if(bomb.getCooldown()>=100){
                                kitchenGrid[row][col] = null; 
                            }
                        }
                    }
                }
            }

            if(activeArrows.size()>0){
                for(int g = 0; g<activeArrows.size(); g++){
                    //Will move towards the ants
                    Arrow a = activeArrows.get(g);
                    a.moveArrow();
                    for(int j = 0; j< activePests.size(); j++){
                        Ant ant = activePests.get(j);
                        if(ant.row() == a.getRow()){ 
                            if(a.x() >= ant.x() && a.x() <= (ant.x() + 65)){ // 65 hit box
                                ant.takeDamage(a.getDamage()); //ant takes damage
                                activeArrows.remove(g); //arrow disappears
                                g--; //index changes
                                break; //stop checking for ants since arrow is gone
                            }
                        }
                    }

                    if(a.x()==0){
                        activeArrows.remove(g);
                    }
                }
            }

            //Results
            if(cakeHealth<=0){
                mode = "Game Over";
                
            } else if(cakeHealth>0&&totalPests.size()==0&&activePests.size()==0&&waveNumber!=3){
                activeArrows.clear();
                waveNumber++;
                mode = "Prep Mode";
            } else if(cakeHealth>0&&totalPests.size()==0&&activePests.size()==0&&waveNumber==3){
                mode = "Victory";

            }
        }

        if(mode.equals("Game Over")||mode.equals("Victory")){
            if(mode.equals("Game Over")){
                try{
                    endScreen = ImageIO.read(new File("images/GameOver.png"));
                } catch(Exception b){
                    b.printStackTrace();
                }
            }

            if(mode.equals("Victory")){
                try{
                    endScreen = ImageIO.read(new File("images/Victory.png"));
                }catch(Exception c){
                    c.printStackTrace();
                }
            }

            //Play again button
            //Button X: 260-460 Y:338-443
            if(input.isLeftMouseDown()&&input.getMouseX()>=260&& input.getMouseY()<=460 && input.getMouseY()>=338 &&input.getMouseY()<=443){
                
                //Reset everything
                kitchenGrid = new Item[5][8];
                //In case there are some leftover toothpicks from the last game
                activeArrows.clear();

                //Remove the leftover ants (if the player lost)
                activePests.clear();
                totalPests.clear();

                sceneNumber = -1; //Have to do this because it will skip the first scene
                endScreen = null;
                bakersGold = 600; //Same as the original starting amount.
                cakeHealth = 100;
                itemID = 0; //0 means nothing
                antIncomingCooldown =0;
                waveNumber = 1;
                mode = "Start Screen";

            }
        }
        

        // Refreshes your graphics loop
        repaint();
    }
    // all of the drawing logic goes here
    // This isn't called consistently so we want to game major game logics out of here
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //Display the story scene
        if(mode.equals("Start Screen")&&sceneNumber>=0){
            g.drawImage(startingScenes[sceneNumber],0,0,WIDTH,HEIGHT,null);
        }
        
        //Prep Mode and Wave mode have the same graphics layout
        if(mode.equals("Prep Mode") || mode.equals("Wave Mode")){
            BufferedImage layout = null;
            try{
                layout = ImageIO.read(new File("images/Layout.png"));
            } catch(Exception e){
                e.printStackTrace();
            }
            g.drawImage(layout,0,0,WIDTH,HEIGHT,null);

            //Draw the 5x8 Grid (Shifted right by 100px and down by 50px)
            //Size of grid (8*65, 5*65) = 520 by 325
            // Start of grid: (100,50), End of grid: (620,375)
            for(int row = 0; row < kitchenGrid.length; row++) {
                for(int col = 0; col < kitchenGrid[row].length; col++) {
                    
                    int xCoord = 100 + (col * TILE_SIZE);
                    int yCoord = 50 + (row * TILE_SIZE);
                    
                    if(kitchenGrid[row][col]!=null){
                        g.drawImage(kitchenGrid[row][col].getIcon(),xCoord,yCoord,TILE_SIZE,TILE_SIZE,null);
                    }
                    
                    // Draw grid lines
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(xCoord, yCoord, TILE_SIZE, TILE_SIZE);
                }
            }
            
            // Player Stats text inside the Menu Bar
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Baker's Gold: " + bakersGold, 20, 30);
            g.drawString("Cake Health: " + cakeHealth + "%", 220, 30);
            g.drawString("Wave: " + waveNumber, 450, 30);

            //Start Wave Button
            //Start: (625,385), End: (713,425)
            g.setColor(Color.RED);
            g.fillRect(625, 385, 88, 40);
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.setColor(Color.BLACK);
            g.drawString("START WAVE", 625, 400);

            //Remove Item Button (trash can logo) - Can change to green or something when it is being used
            // Start: (10,385), End: (90,425)
            if(remove==false){
                g.setColor(Color.RED);
            } else if(remove==true){
                g.setColor(Color.GREEN);
            }
            g.fillRect(10,385,80,40);
            g.setColor(Color.BLACK);
            g.drawString("TRASH", 15, 400);

            BufferedImage gumTrap = null;
            BufferedImage cheeseBlock = null;
            BufferedImage pretzelCannon = null;
            BufferedImage CakePopBomb = null;
            
            try{
                gumTrap = ImageIO.read(new File("images/StickyGumTrapButton.png"));
                cheeseBlock = ImageIO.read(new File("images/CheeseBlockButton.png"));
                pretzelCannon = ImageIO.read(new File("images/PretzelCannonButton.png"));
                CakePopBomb = ImageIO.read(new File("images/CakePopBombButton.png"));
            } catch(Exception e){
                e.printStackTrace();
            }
            //Items: 120 x 65 pixel, 10 pix apart for each?
            //Sticky glue or honey patch
            //Button start (105,380) End (225,430)
            g.drawImage(gumTrap,105,380,120,ITEM_SIZE,null);


            //Glouchester cheese block
            //Button start (235,380), End (355,430)
            g.drawImage(cheeseBlock,235,380, 120,ITEM_SIZE,null);

            //The toothpick launcher (maybe not a spoon. But something that attacks the ants)
            //Button start (365,380), End (485,430)
            g.drawImage(pretzelCannon, 365, 380, 120,ITEM_SIZE,null);

            //A bomb maybe. (A attack item that does damage to surrounding ants)
            //Button start (495, 380), End(615, 430)
            g.drawImage(CakePopBomb, 495, 380, 120, ITEM_SIZE, null);


            //When wave starts, load the ants and have them start walking.
            if(mode.equals("Wave Mode")){
                //for every ant
                for(int i = 0; i<activePests.size(); i++){
                    Ant ant = activePests.get(i);
                    g.drawImage(ant.getAntImage(),(int)ant.x(),ant.y(),TILE_SIZE,TILE_SIZE, null);
                }

                for (int h = 0; h < activeArrows.size(); h++) {
                    Arrow a = activeArrows.get(h);
                    g.drawImage(a.getImage(),(int)a.x(),a.y(),TILE_SIZE,TILE_SIZE,null);
                }

                //When the popping candy bomb detonates, it shows an explosion affect momentarily
                for(int row = 0; row<kitchenGrid.length; row++){
                    for(int col = 0; col<kitchenGrid[row].length; col++){
                        if(kitchenGrid[row][col] != null &&kitchenGrid[row][col].getName().equals("Cake Pop Bomb")&&kitchenGrid[row][col].hasDetonated()){
                            boolean[][] bombRange = kitchenGrid[row][col].getBombRange();
                            for(int bombRow = 0; bombRow<bombRange.length; bombRow++){
                                for(int bombCol=0; bombCol<bombRange[bombRow].length; bombCol++){
                                    if(bombRange[bombRow][bombCol]==true){
                                        int xCoord = 100 + (bombCol * TILE_SIZE);
                                        int yCoord = 50 + (bombRow * TILE_SIZE);
                                        g.setColor(Color.PINK);
                                        g.fillRect(xCoord,yCoord,TILE_SIZE,TILE_SIZE);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

        //End of the game
        if(mode.equals("Game Over") || mode.equals("Victory")){
            g.drawImage(endScreen, 0, 0, WIDTH, HEIGHT, null);

            //Play again button
            g.setColor(Color.PINK);
            //Button X: 260-460 Y:338-443
            g.fillRect(260,338,200,55);
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.setColor(Color.BLACK);
            g.drawString("PLAY AGAIN", 320, 360);
        }
    }


    // -------- DO NOT MODIFY ANYTHING DOWN HERE ----------
    
    Timer timer;
    
    // launch the game window
    public static void main(String[] args) {
        JFrame f = new JFrame("Clean Game Engine");
        f.add(new MyProgram());
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
    
    public MyProgram() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        
        // create an input handler
        input = new InputHandler();
        
        // do any of the initial game set up logic
        setup();
        
        // attach the handler to the window
        this.addKeyListener(input);
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        this.setFocusable(true);

        // update at the proper FPS
        timer = new Timer(1000/FPS, this);
        timer.start();
    }
    
    public BufferedImage loadImage(String filename) {
        // create a temp image variable with nothing loaded in
        BufferedImage img = null;
        try{
            // try and load in the image file
            img = ImageIO.read(new File(filename));
        }catch(Exception e){
            // if it doesn't load, print the error
            e.printStackTrace();
        }
        return img;
    }
}
