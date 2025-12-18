package entity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {

	GamePanel gp;
	KeyHandler keyH;

	public final int screenX;
	public final int screenY;
	public int lives = 9; // Player has 9 lives

	public Player(GamePanel gp, KeyHandler keyH) {

		this.gp = gp;
		this.keyH = keyH;

		screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
		screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

		solidArea = new Rectangle();
		solidArea.x = 12;
		solidArea.y = 12;
		solidArea.width = 70;
		solidArea.height = 70;

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
			
			//mag initialize nan mga images	
			up1 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.up2.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.up3.png"));
			up3 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.up1.png"));

			down1 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.down2.png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.down3.png"));
			down3 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.down1.png"));

			left1 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.left2.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.left3.png"));
			left3 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.left1.png"));

			right1 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.right2.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.right3.png"));
			right3 = ImageIO.read(getClass().getResourceAsStream("/player/Meow.right1.png"));

			catchLeft = ImageIO.read(getClass().getResourceAsStream("/player/meow.jumpleft.png"));
			catchRight = ImageIO.read(getClass().getResourceAsStream("/player/meow.jumpright.png"));

			light = ImageIO.read(getClass().getResourceAsStream("/tiles/lights.png"));

			backGround = ImageIO.read(getClass().getResourceAsStream("/maps/backGround2.png"));
			
			// Load heart image for lives display
			heart = ImageIO.read(getClass().getResourceAsStream("/fallingObjects/Heart.png"));

		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}

	public void update() {
		
		// ================================
		//   CATCHING ANIMATION (TIMER-BASED)
		// ================================
		// Trigger catch only on key press (not held down)
		if(keyH.spacePressed && !spaceWasPressed) {
			catchTimer = 20; // Show catch animation for 20 frames (was 15, increased for better detection)
			isCatching = true;
		}
		
		// Update space key state for next frame
		spaceWasPressed = keyH.spacePressed;
		
		// Countdown catch timer and keep catching state active
		if(catchTimer > 0) {
			catchTimer--;
			isCatching = true;
		} else {
			isCatching = false;
		}
		
		// ================================
		//   SHIFT SPRINT LOGIC
		// ================================
		int baseSpeed = 5;
		speed = baseSpeed;
		if (keyH.shiftKeyPressed) {
			speed = baseSpeed * 2;
		}

		// ================================
		//   MOVEMENT LOGIC
		// ================================
		int velocityX = 0;
		int velocityY = 0;

		if (keyH.leftPressed) {
			velocityX -= speed;
			direction = "left";
		}
		if (keyH.rightPressed) {
			velocityX += speed;
			direction = "right";
		}

		if (velocityX != 0 || velocityY != 0) {
			moving = true;

			if (!gp.cChecker.checkTileCollisionX(this, velocityX)) {
				worldX += velocityX;
			}
			if (!gp.cChecker.checkTileCollisionY(this, velocityY)) {
				worldY += velocityY;
			}

		} else {
			moving = false;
		}

		// ================================
		//   ANIMATION UPDATE
		// ================================
		spriteCounter++;
		if (spriteCounter > 7) {
			spriteNum = (spriteNum == 1 ? 2 : 1);
			spriteCounter = 0;
		}
	}

	public void draw(Graphics2D g2) {

		BufferedImage image = null;

		// ================================
		//   WALKING ANIMATION
		// ================================
		if (moving) {
			switch (direction) {

			case "left":
				
				image = (spriteNum == 1) ? left1 : left2;
				
				//e prioretize ang catching animation pag e press ang jump
				if(isCatching == true) {
					image = (spriteNum == 1) ? catchLeft: catchLeft;
				}
				break;

			case "right":
				image = (spriteNum == 1) ? right1 : right2;
				
				//e prioretize ang catching animation pag e press ang jump
				if(isCatching == true) {
					image = (spriteNum == 1) ? catchRight: catchRight;
				}
				break;
			}
		}

		// ================================
		//   IDLE ANIMATION
		// ================================
		if (!moving) {
			switch (direction) {

			case "left":
				image = left3;
				//e prioretize ang catching animation pag e press ang jump
				if(isCatching == true) {
					image = (spriteNum == 1) ? catchLeft: catchLeft;
				
				}
				break;

			case "right":
				image = right3;
				//e prioretize ang catching animation pag e press ang jump
				if(isCatching == true) {
					image = (spriteNum == 1) ? catchRight: catchRight;
				}
				break;
			}
		}

		// ================================
		//   FINAL DRAW
		// ================================
		int catSize = 55;
		g2.drawImage(backGround, 0, 0, gp.screenWidth, gp.screenHeight, null);
		g2.drawImage(image, worldX, worldY, catSize * 2, catSize * 2, null);
		
		// ================================
		//   DEBUG: DRAW COLLISION BOXES
		// ================================
		// Draw player solid area (white rectangle)
		g2.setColor(new Color(0, 0, 0, 0));
		g2.drawRect(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
		
		// Draw catch radius when catching (red circle)
		if (isCatching) {
			int catchRadius = gp.tileSize * 2;
			int catchX = worldX;
			int catchY = worldY;
			
			if (direction.equals("left")) {
				catchX -= 1;
			} else {
				catchX += 1;
			}
			
			g2.setColor(new Color(0, 0, 0, 100));
			g2.drawOval(catchX , catchY, catchRadius, catchRadius);
		}

	}
	
	// ========================================
	// LOSE POINTS - Deduct points from player when hit by bomb
	// @param points - Number of points to deduct
	// ========================================
	public void losePoints(int points) {
		score -= points;
		// Ensure score doesn't go below 0
		if (score < 0) {
			score = 0;
		}
		System.out.println("Bomb hit! Lost " + points + " points. Score: " + score);
	}
	
	// ========================================
	// LOSE LIFE - Deduct one life from player
	// ========================================
	public void loseLife() {
		lives--;
		if (lives < 0) {
			lives = 0; // Ensure lives don't go below 0
		}
		System.out.println("Lost a life! Lives remaining: " + lives);
	}

}
