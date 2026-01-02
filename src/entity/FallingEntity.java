package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;

// ========================================
// FALLING ENTITY CLASS
// Represents objects that fall from top
// Can be caught by the player
// ========================================
public class FallingEntity extends Entity{
    GamePanel gp; // Reference to the game panel
    
    // MOVEMENT
    int fallSpeed = 3; // How many pixels per frame to fall
    
    // STATE
    boolean isActive = true; // Is this object still in the game?
    BufferedImage currentImage; // The image to display
    int imageType; // 0=bomb, 1=chicken, 2=fish
    boolean hasHitPlayer = false; // Track if bomb already hit player
    int points = 0; // Points awarded when caught (0=bomb, 1=chicken, 2=fish)
    boolean wasCaught = false; // Track if entity was caught vs fell off screen

    // ========================================
    // CONSTRUCTOR - Creates a new falling object
    // @param gp - Game panel reference
    // @param spawnX - X position to spawn at (top of screen)
    // @param imageType - Which image: 0=bomb, 1=chicken, 2=fish
    // ========================================
    public FallingEntity(GamePanel gp, int spawnX, int imageType){
        this.gp = gp;
        this.worldX = spawnX; // Spawn at random X position
        this.worldY = 0; // Always start at top (Y = 0)
        this.direction = "fall";
        
        // CREATE COLLISION BOX
        solidArea = new Rectangle();
        solidArea.x = 5; // Offset from left
        solidArea.y = 5; // Offset from top
        solidArea.width = 40; // Width of collision box
        solidArea.height = 40; // Height of collision box
        
        // Load all the falling object images from files
        getFallingObjectImage();
        
        // Pick which image to use based on imageType parameter
        selectImageType(imageType);
    }

    // ========================================
    // LOAD IMAGES - Load all falling object images from /fallingObjects folder
    // ========================================
    public void getFallingObjectImage(){
        try {
            // Try to load each image from resources
            bomb = ImageIO.read(getClass().getResourceAsStream("/fallingObjects/Bomb.png"));
            chicken = ImageIO.read(getClass().getResourceAsStream("/fallingObjects/Chicken.png"));
            heart = ImageIO.read(getClass().getResourceAsStream("/fallingObjects/Heart.png"));
            fish = ImageIO.read(getClass().getResourceAsStream("/fallingObjects/Fish.png"));
            hotdog = ImageIO.read(getClass().getResourceAsStream("/fallingObjects/Hotdog.png"));
            healingPotion = ImageIO.read(getClass().getResourceAsStream("/fallingObjects/HealingPotion.png")); 
        } catch (IOException e) {
            // If image fails to load, print error
            e.printStackTrace(); 
        }
    }
    
    // ========================================
    // SELECT IMAGE - Pick which image to display based on type
    // @param imageType - 0=bomb, 1=chicken, 2=fish
    // ========================================
    private void selectImageType(int imageType) {
        this.imageType = imageType; // Store image type for later reference
        switch(imageType) {
            case 0: // Bomb
                currentImage = bomb;
                points = 0; // Bombs give 0 points when caught
                break;
            case 1: // Chicken
                currentImage = chicken;
                points = 50; 
                break;
            case 2: // Fish
                currentImage = fish;
                points = 100; 
                break;
            case 3:
                currentImage = hotdog;
                points = 25;
                break;
            case 4:
                currentImage = healingPotion;
                points = 0;
            default: // Default to chicken if unknown type
                currentImage = chicken;
                points = 50;
        }
    }

    // ========================================
    // UPDATE - Called every frame to move the falling object
    // @param gameTime - Current game time in frames for difficulty scaling
    // ========================================
    public void update(long gameTime){
        // If this object is no longer active, do nothing
        if (!isActive) return;
        
        // Calculate difficulty scaling: increase speed over time
        // Avoid redundant floating-point math on every frame
        float difficultyMultiplier = 1.0f + (gameTime * 0.0005f);
        float currentFallSpeed = fallSpeed * (difficultyMultiplier > 10 ? 10 : difficultyMultiplier);
        
        // Move down by scaled fall speed pixels each frame
        worldY += (int) currentFallSpeed;
        
        // Check if object fell off bottom of screen
        if (worldY > gp.screenHeight) {
            isActive = false; // Deactivate so it gets removed
        }
    }

    // ========================================
    // DRAW - Display the falling object on screen
    // @param g2 - Graphics2D object for drawing
    // ========================================
    public void draw(Graphics2D g2){
        // If not active, don't draw anything
        if (!isActive) return;
        
        int size = 60; // Size to draw the image
        
        // Draw the image if it loaded successfully
        if (currentImage != null) {
            g2.drawImage(currentImage, worldX, worldY, size, size, null);
        } else {
            // Debug message if image failed to load
            System.out.println("WARNING: currentImage is null! worldX=" + worldX + ", worldY=" + worldY + ", isActive=" + isActive);
        }
        
        // ================================
        // DEBUG VISUALIZATION
        // ================================
        // Draw a blue rectangle showing the collision box
        g2.setColor(new Color(0, 0, 0, 0)); // Semi-transparent blue
        g2.drawRect(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
    }
    
    // ========================================
    // CHECK CATCH - See if player caught this object
    // @param player - The player object
    // @return true if player catches this, false otherwise
    // ========================================
    public boolean checkCatch(Player player) {
        // If object already caught/removed, can't catch again
        if (!isActive) return false;
        
        // Get player's collision box center
        int playerCenterX = player.worldX + (player.solidArea.width / 2);
        int playerCenterY = player.worldY + (player.solidArea.height / 2);
        
        // Get this entity's center
        int entityCenterX = this.worldX + (solidArea.width / 2) + solidArea.x;
        int entityCenterY = this.worldY + (solidArea.height / 2) + solidArea.y;
        
        // Calculate distance between centers
        int distance = (int) Math.sqrt(
            Math.pow(entityCenterX - playerCenterX, 2) + 
            Math.pow(entityCenterY - playerCenterY, 2)
        ); 
        
        // LARGER CATCH RADIUS for more forgiving catching
        // 68 pixel radius = tile size (48) + extra margin (20)
        int catchRadius = gp.tileSize + 20;
        
        // IF DISTANCE IS SMALL ENOUGH, IT'S CAUGHT!
        if (distance < catchRadius) {
            this.isActive = false; // Remove the object
            this.wasCaught = true; // Mark that this entity was caught
            return true; // Tell GamePanel it was caught
        }
        
        return false; // Object not in catch area
    }
    
    // ========================================
    // IS ACTIVE - Check if this object is still active
    // @return true if active, false if removed
    // ========================================
    public boolean isActive() {
        return isActive;
    }
    
    // ========================================
    // CHECK COLLISION - Check if this entity collides with player
    // This is different from checkCatch - happens when player doesn't catch
    // @param player - The player object
    // @return true if collision detected, false otherwise
    // ========================================
    public boolean checkCollision(Player player) {
        // Only bombs can damage player
        if (imageType != 0) return false;
        
        // If already hit player or not active, can't hit again
        if (!isActive || hasHitPlayer) return false;
        
        // Get collision box coordinates
        int entityLeftWorldX = this.worldX + this.solidArea.x;
        int entityRightWorldX = this.worldX + this.solidArea.x + this.solidArea.width;
        int entityTopWorldY = this.worldY + this.solidArea.y;
        int entityBottomWorldY = this.worldY + this.solidArea.y + this.solidArea.height;
        
        int playerLeftWorldX = player.worldX + player.solidArea.x;
        int playerRightWorldX = player.worldX + player.solidArea.x + player.solidArea.width;
        int playerTopWorldY = player.worldY + player.solidArea.y;
        int playerBottomWorldY = player.worldY + player.solidArea.y + player.solidArea.height;
        
        // Check for rectangular collision (AABB - Axis-Aligned Bounding Box)
        if (entityLeftWorldX < playerRightWorldX &&
            entityRightWorldX > playerLeftWorldX &&
            entityTopWorldY < playerBottomWorldY &&
            entityBottomWorldY > playerTopWorldY) {
            
            hasHitPlayer = true; // Mark that this bomb has hit the player
            return true; // Collision detected
        }
        
        return false; // No collision
    }
    
    // ========================================
    // IS BOMB - Check if this entity is a bomb
    // @return true if this is a bomb (imageType == 0)
    // ========================================
    public boolean isBomb() {
        return imageType == 0;
    }
    
    // ========================================
    // GET POINTS - Get the point value for this entity when caught
    // @return number of points to award
    // ========================================
    public int getPoints() {
        return points;
    }
    
    // ========================================
    // FELL OFF SCREEN - Check if entity fell off screen without being caught
    // @return true if entity is inactive and wasn't caught (fell off screen)
    // ========================================
    public boolean fellOffScreen() {
        return !isActive && !wasCaught;
    }
}





