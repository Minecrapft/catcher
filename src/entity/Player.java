package entity;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity{
	
	GamePanel gp;
	KeyHandler keyH;
	
	public final int screenX;
	public final int screenY;
	
	public Player(GamePanel gp, KeyHandler keyH) {
		
			this.gp = gp;
			this.keyH = keyH;
		
			screenX = gp.screenWidth / 2 - (gp.tileSize/2);
			screenY = gp.screenHeight /2- (gp.tileSize/2);
					
			solidArea = new Rectangle();
			solidArea.x = 10;
			solidArea.y = 17;
			solidArea.width = 22;
			solidArea.height = 22;
			
			setDefaultValues();
			getPlayerImage();
		
	}
		
	public void setDefaultValues() {
		worldX = 12 * 2;
		worldY = 47 * 13;
		speed = 7;
		direction = "right";
		playerPosX = worldX / gp.tileSize;
		playerPosY = worldY / gp.tileSize;
		
	} 
	
	public void getPlayerImage() {
		try {
			up1 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.up2.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.up3.png"));
			up3 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.up1.png"));
			
			down1 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.down2.png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.down3.png"));
			down3 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.down1.png"));
			
			left1 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.right2.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.right3.png"));
			left3 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.right1.png"));
			
			right1 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.left2.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.left3.png"));
			right3 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.left1.png"));
			
			light = ImageIO.read(getClass().getResourceAsStream("/tiles/lights.png"));
			backGround = ImageIO.read(getClass().getResourceAsStream("/maps/backGround2.png"));
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void update() {
		
		//MOVING - allow multiple keys for diagonal movement
		int velocityX = 0;
		int velocityY = 0;
		
		if(keyH.leftPressed == true) {
			velocityX -= speed;
			direction = "left";
		}
		if(keyH.rightPressed == true) {
			velocityX += speed;
			direction = "right";
		}
		
		// If any key was pressed, mark as moving
		if(velocityX != 0 || velocityY != 0) {
			moving = true;
			
			// Check collision separately for X and Y to allow sliding along walls
			if(!gp.cChecker.checkTileCollisionX(this, velocityX)) {
				worldX += velocityX;
			}
			if(!gp.cChecker.checkTileCollisionY(this, velocityY)) {
				worldY += velocityY;
			}
		}
		else {
			moving = false;
		}
		
		// Update sprite animation
		spriteCounter++;
		if(spriteCounter > 7) {
			if(spriteNum == 1) {
				spriteNum = 2;
			}
			else if(spriteNum == 2){
				spriteNum = 1;
			}
			spriteCounter = 0;
		}
		
	}
	public void draw(Graphics2D g2) {
		
		BufferedImage image = null;
		
		
		//Draw animation if moving
		if(moving == true) {
			switch(direction) {
			
			
				
		
				
			case "left":
				
				if(spriteNum == 1) {
					image = left1;
				}
				if(spriteNum == 2) {
					image = left2;
				}
				
				break;
				
			case "right":
				
				if(spriteNum == 1) {
					image = right1;
				}
				if(spriteNum == 2) {
					image = right2;
				}
			
				
				break;
			}
			
		}
		
		//draw idle image if not moving
		if(moving == false) {
			switch(direction) {
			case "up":
				
				if(spriteNum == 1) {
					image = up3;
				}
				if(spriteNum == 2) {
					image = up3;
				}
				
				break;
				
			case "down":
				
				if(spriteNum == 1) {
					image = down3;
				}
				if(spriteNum == 2) {
					image = down3;
				}
				
				break;
				
			case "left":
				
				if(spriteNum == 1) {
					image = left3;
				}
				if(spriteNum == 2) {
					image = left3;
				}
				
				break;
				
			case "right":
				
				if(spriteNum == 1) {
					image = right3;
				}
				if(spriteNum == 2) {
					image = right3;
				}
			
				
				break;
			}
			
		}
		
		int catSize = 55;
		 
		//	wddsg2.drawImage(light, 0, 0, gp.screenWidth, gp.screenHeight, null);
		g2.drawImage(backGround, 0, 0, gp.screenWidth, gp.screenHeight, null);
		g2.drawImage(image, worldX, worldY, catSize* 2,  catSize * 2, null);
		
	}
	
}
