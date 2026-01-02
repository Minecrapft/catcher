package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * UIRenderer is responsible for rendering all UI elements and screens in the game.
 * It handles:
 * - Main menu screen with play and highscore buttons
 * - Highscore display screen
 * - In-game UI (score and lives display)
 * - Pause menu overlay
 * - Game over screen with retry and menu options
 */
public class UIRenderer {
	
	// Reference to the main game panel and state manager
	private GamePanel gamePanel;
	private GameStateManager stateManager;
	private SoundHandler soundHandler;
	
	// Button objects for different menus
	private ImageButton playButton;
	private ImageButton highscoreButton;
	private ImageButton continueButton;
	private ImageButton pauseMenuButton;
	private ImageButton retryButton;
	private ImageButton gameoverMenuButton;
	private ImageButton backButton;

	
	// Background and decorative images
	private BufferedImage background;
	private BufferedImage cat;
	private BufferedImage dead;
	private BufferedImage sleepcat;
	private BufferedImage catcher;
	private BufferedImage highScoreBufferedImage;
	private BufferedImage gameOverBufferedImage;
	private BufferedImage pauseBufferedImage;
	
	
	// Menu button images (normal and highlighted states)
	private BufferedImage playButtonNormal;
	private BufferedImage playButtonHighlighted;
	private BufferedImage highscoreButtonNormal;
	private BufferedImage highscoreButtonHighlighted;
	
	// Pause menu button images
	private BufferedImage continueButtonNormal;
	private BufferedImage continueButtonHighlighted;
	private BufferedImage pauseMenuButtonNormal;
	private BufferedImage pauseMenuButtonHighlighted;
	
	// Game over menu button images
	private BufferedImage retryButtonNormal;
	private BufferedImage retryButtonHighlighted;
	private BufferedImage gameoverMenuButtonNormal;
	private BufferedImage gameoverMenuButtonHighlighted;
	
	// Highscore back button images
	private BufferedImage backButtonNormal;
	private BufferedImage backButtonHighlighted;
	
	/**
	 * Constructor for UIRenderer
	 * Initializes the renderer with game panel and state manager references
	 * Loads all button images from the res directory
	 */
	public UIRenderer(GamePanel gamePanel, GameStateManager stateManager) {
		this.gamePanel = gamePanel;
		this.stateManager = stateManager;
		// Get SoundHandler from GamePanel after it's initialized
		this.soundHandler = gamePanel.soundHandler;
		loadButtonImages();
	}
	
	/**
	 * Loads all button and background images from the res/maps directory
	 * Creates ImageButton objects with proper positioning and sizing for each menu
	 * Handles IOException if image files cannot be loaded
	 */
	private void loadButtonImages() {
		try {
			// === BACKGROUND AND DECORATIVE IMAGES ===
			background = ImageIO.read(new File("res/maps/bg.png"));
			cat = ImageIO.read(new File("res/maps/smile.png"));
			dead = ImageIO.read(new File("res/maps/dead.png"));
			sleepcat = ImageIO.read(new File("res/maps/sleepause.png"));
			catcher = ImageIO.read(new File("res/maps/catcher.png"));
			highScoreBufferedImage =ImageIO.read(new File("res/maps/highscorebig.png"));
			gameOverBufferedImage	 =ImageIO.read(new File("res/maps/gameover.png"));

			// === MAIN MENU BUTTON IMAGES ===
			playButtonNormal = ImageIO.read(new File("res/maps/play.png"));
			playButtonHighlighted = ImageIO.read(new File("res/maps/play.png"));
			highscoreButtonNormal = ImageIO.read(new File("res/maps/highscore.png"));
			highscoreButtonHighlighted = ImageIO.read(new File("res/maps/highscore.png"));
			pauseBufferedImage = ImageIO.read(new File("res/maps/pause.png"));
			
			// === PAUSE MENU BUTTON IMAGES ===
			continueButtonNormal = ImageIO.read(new File("res/maps/continue.png"));
			continueButtonHighlighted = ImageIO.read(new File("res/maps/continue.png"));
			pauseMenuButtonNormal = ImageIO.read(new File("res/maps/menu.png"));
			pauseMenuButtonHighlighted = ImageIO.read(new File("res/maps/menu.png"));
			
			// === GAME OVER MENU BUTTON IMAGES ===
			retryButtonNormal = ImageIO.read(new File("res/maps/start.again.png"));
			retryButtonHighlighted = ImageIO.read(new File("res/maps/start.again.png"));
			gameoverMenuButtonNormal = ImageIO.read(new File("res/maps/menu.png"));
			gameoverMenuButtonHighlighted = ImageIO.read(new File("res/maps/menu.png"));
			
			// === HIGHSCORE BACK BUTTON IMAGES ===
			backButtonNormal = ImageIO.read(new File("res/maps/back.png"));
			backButtonHighlighted = ImageIO.read(new File("res/maps/back.png"));
			
			// === CREATE MAIN MENU BUTTONS ===
			// Positioned vertically centered with spacing
			playButton = new ImageButton(playButtonNormal, playButtonHighlighted, 
					(gamePanel.screenWidth - 250) / 2, 380, 300, 200, soundHandler);
			// Set collision area (adjust these values to match your button visual)
			playButton.setCollisionArea(playButton.getX()+40, playButton.getY()+60, playButton.getCollisionWidth()-80, playButton.getCollisionHeight()-130);
			
			highscoreButton = new ImageButton(highscoreButtonNormal, highscoreButtonHighlighted, 
					(gamePanel.screenWidth - 250) / 2, 490, 300, 200, soundHandler);
			// Set collision area (adjust these values to match your button visual)
			highscoreButton.setCollisionArea(highscoreButton.getX()+30, highscoreButton.getY()+55, highscoreButton.getCollisionWidth()-55, highscoreButton.getCollisionHeight()-125);
			 
			// === CREATE PAUSE MENU BUTTONS ===
			continueButton = new ImageButton(continueButtonNormal, continueButtonHighlighted, 
					(gamePanel.screenWidth - 250) / 2, 380, 300, 150, soundHandler);
			// Set collision area (adjust these values to match your button visual)
			continueButton.setCollisionArea(continueButton.getX()+15, continueButton.getY()+20,  continueButton.getCollisionWidth()-35, continueButton.getCollisionHeight()-50);
			
			pauseMenuButton = new ImageButton(pauseMenuButtonNormal, pauseMenuButtonHighlighted, 
					(gamePanel.screenWidth - 250) / 2, 490, 300, 200, soundHandler);
			// Set collision area (adjust these values to match your button visual)
			pauseMenuButton.setCollisionArea(pauseMenuButton.getX()+40, pauseMenuButton.getY()+60, pauseMenuButton.getWidth()-75, pauseMenuButton.getHeight()-130);
			
			// === CREATE GAME OVER MENU BUTTONS ===
			retryButton = new ImageButton(retryButtonNormal, retryButtonHighlighted, 
					(gamePanel.screenWidth - 250) / 2, 380, 300, 200, soundHandler);
			// Set collision area (adjust these values to match your button visual)
			retryButton.setCollisionArea(retryButton.getX()+40, retryButton.getY()+60, retryButton.getWidth()-70, retryButton.getHeight()-130);
			
			gameoverMenuButton = new ImageButton(gameoverMenuButtonNormal, gameoverMenuButtonHighlighted, 
					(gamePanel.screenWidth - 250) / 2, 490, 300, 200, soundHandler);
			// Set collision area (adjust these values to match your button visual)
			gameoverMenuButton.setCollisionArea(gameoverMenuButton.getX()+40, gameoverMenuButton.getY()+60, gameoverMenuButton.getWidth()-70, gameoverMenuButton.getHeight()-130);
			
			// === CREATE HIGHSCORE BACK BUTTON ===
			backButton = new ImageButton(backButtonNormal, backButtonHighlighted, 
					(gamePanel.screenWidth - 250) / 2, 450, 300, 200, soundHandler);
			// Set collision area (adjust these values to match your button visual)
			backButton.setCollisionArea(backButton.getX()+30, backButton.getY()+60, backButton.getWidth()-55, backButton.getHeight()-130);
			
		} catch (IOException e) {
			System.out.println("Error loading button images: " + e.getMessage());
		}
	}
	
	/**
	 * Main render method that determines which screen to draw based on game state
	 * Called every frame to update all UI elements
	 * 
	 * @param g2 Graphics2D object for drawing
	 */
	public void render(Graphics2D g2) {
		int gameState = stateManager.getGameState();
		
		// === RENDER BASED ON CURRENT GAME STATE ===
		if (gameState == GameStateManager.MENU_STATE) {
			drawMenu(g2);
		} else if (gameState == GameStateManager.HIGHSCORE_STATE) {
			drawHighscoreScreen(g2);
		} else if (gameState == GameStateManager.GAMEOVER_STATE) {
			drawGameOverScreen(g2);
		} else if (gameState == GameStateManager.GAME_STATE) {
			drawGame(g2);
			
			// Draw pause menu overlay on top if game is paused
			if (stateManager.isPaused()) {
				drawPauseMenu(g2);
			}
		}
	}
	
	/**
	 * Draws the main menu screen with background image and buttons
	 * Displays the game title and decorative elements
	 * 
	 * @param g2 Graphics2D object for drawing
	 */
	private void drawMenu(Graphics2D g2) {
		// Draw background image, or black if image is not loaded
		if (background != null) {
			g2.drawImage(background, 0, 0, gamePanel.screenWidth, gamePanel.screenHeight, null);
			g2.drawImage(catcher, (gamePanel.screenWidth - 350)/2, 25, 400, 200, null);
			g2.drawImage(cat, (gamePanel.screenWidth - 220)/2, 170, 300, 200, null);
		} else {
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);
		}
		
		// Draw menu buttons
		playButton.draw(g2);
		highscoreButton.draw(g2);
		
		// DEBUG: Draw collision areas
		playButton.drawCollisionArea(g2);
		highscoreButton.drawCollisionArea(g2);
	}
	
	/**
	 * Draws the highscore screen with the current best score
	 * Displays a back button to return to the menu
	 * 
	 * @param g2 Graphics2D object for drawing
	 */
	private void drawHighscoreScreen(Graphics2D g2) {

		if (background != null) {
			g2.drawImage(background, 0, 0, gamePanel.screenWidth, gamePanel.screenHeight, null);
			g2.drawImage(highScoreBufferedImage, (gamePanel.screenWidth - 350)/2, 25, 400, 200, null);
			g2.drawImage(cat, (gamePanel.screenWidth - 220)/2, 300, 300, 200, null);
		} else {
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);
		}
		
		
		// === HIGHSCORE VALUE ===
		// Display the current highest score achieved
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 72));
		String scoreText = String.valueOf(stateManager.getHighscore());
		int scoreX = (gamePanel.screenWidth - g2.getFontMetrics().stringWidth(scoreText)) / 2;
		g2.drawString(scoreText, scoreX+20, 300);
		
		// === BACK BUTTON ===
		backButton.draw(g2);
		// DEBUG: Draw collision area
		backButton.drawCollisionArea(g2);
		
		// === INSTRUCTIONS ===
		g2.setColor(Color.GRAY);
		g2.setFont(new Font("Arial", Font.PLAIN, 24));
		g2.drawString("Click button or press SPACE to go back", 60, gamePanel.screenHeight - 30);
	}
	
	/**
	 * Draws the main game screen with game world and HUD elements
	 * Displays the tile map, player, falling entities, score, and lives
	 * 
	 * @param g2 Graphics2D object for drawing
	 */
	private void drawGame(Graphics2D g2) {
		// === DRAW GAME WORLD ===
		gamePanel.player.draw(g2);             // Draw player
		
		// Draw all falling entities (obstacles/collectibles)
		for (int i = 0; i < gamePanel.fallingEntities.size(); i++) {
			gamePanel.fallingEntities.get(i).draw(g2);
		}
		
		// === DRAW HUD ===
		// Display current score in top-left corner
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 24));
		g2.drawString("Score: " + gamePanel.player.score, 20, 40);
		
		// Display remaining lives as heart icons
		drawHearts(g2);
	}
	
	/**
	 * Draws the pause menu overlay that appears when the game is paused
	 * Allows player to continue game or return to main menu
	 * 
	 * @param g2 Graphics2D object for drawing
	 */
	private void drawPauseMenu(Graphics2D g2) {
		// === SEMI-TRANSPARENT OVERLAY ===
		// Creates a dark overlay without blocking the game underneath
		
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);
		g2.drawImage(pauseBufferedImage, (gamePanel.screenWidth - 350)/2, 25, 400, 200, null);
		g2.drawImage(sleepcat, ((gamePanel.screenWidth - 220)/2)-20, 200, 300, 200, null);

	
		// === CONTINUE BUTTON ===
		// Use the continue button object that was created in loadButtonImages()
		continueButton.draw(g2);
		// DEBUG: Draw collision area
		continueButton.drawCollisionArea(g2);
		
		// === MENU BUTTON ===
		// Use the pauseMenuButton object that was created in loadButtonImages()
		pauseMenuButton.draw(g2);
		// DEBUG: Draw collision area
		pauseMenuButton.drawCollisionArea(g2);
	}
	
	/**
	 * Draws the game over screen displayed when the player loses all lives
	 * Shows final score, highscore, and options to retry or return to menu
	 * 
	 * @param g2 Graphics2D object for drawing
	 */
	private void drawGameOverScreen(Graphics2D g2) {
		// === DARK OVERLAY BACKGROUND ===
		if (background != null) {
			g2.drawImage(background, 0, 0, gamePanel.screenWidth, gamePanel.screenHeight, null);
			g2.drawImage(gameOverBufferedImage, (gamePanel.screenWidth - 350)/2, 25, 400, 200, null);
			g2.drawImage(dead, (gamePanel.screenWidth - 220)/2, 150, 300-30, 200-30, null);
		} else {
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);
		}

		
		// === SCORE DISPLAY ===
		// Shows the score achieved in the just-finished game
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 40));
		String scoreText = "Your Score: " + gamePanel.player.score;
		int scoreX = (gamePanel.screenWidth - g2.getFontMetrics().stringWidth(scoreText)) / 2;
		g2.drawString(scoreText, scoreX+20, 350);
		
		// === HIGHSCORE DISPLAY ===
		// Shows the best score achieved across all games
		g2.setFont(new Font("Arial", Font.PLAIN, 32));
		String highscoreText = "Highscore: " + stateManager.getHighscore();
		int highscoreX = (gamePanel.screenWidth - g2.getFontMetrics().stringWidth(highscoreText)) / 2;
		g2.drawString(highscoreText, highscoreX+20, 400);
		
		// === RETRY BUTTON ===
		// Use the retry button object that was created in loadButtonImages()
		retryButton.draw(g2);
		// DEBUG: Draw collision area
		retryButton.drawCollisionArea(g2);
		
		// === MENU BUTTON ===
		// Use the gameoverMenuButton object that was created in loadButtonImages()
		gameoverMenuButton.draw(g2);
		// DEBUG: Draw collision area
		gameoverMenuButton.drawCollisionArea(g2);
		
		
	}
	
	/**
	 * Draws the player's remaining lives as heart icons
	 * Each heart represents one life (max 9 lives)
	 * Displays hearts in the top-right corner of the screen
	 * 
	 * @param g2 Graphics2D object for drawing
	 */
	private void drawHearts(Graphics2D g2) {
		int heartSize = 80;
		int heartSpacing = 40;
		int startX = gamePanel.screenWidth - (9 * heartSpacing) - 50;
		int startY = 5;
		
		// === DRAW 9 HEART SLOTS (ONE FOR EACH POSSIBLE LIFE) ===
		for (int i = 0; i < 9; i++) {
			int heartX = startX + (i * heartSpacing);
			int heartY = startY;
			
			// Draw filled heart if player has this life, otherwise draw empty space
			if (i < gamePanel.player.lives && gamePanel.player.heart != null) {
				g2.drawImage(gamePanel.player.heart, heartX, heartY, heartSize, heartSize, null);
			} else {
				// Draw transparent/empty space for lost lives
				g2.setColor(new Color(0,0,0,0));
				g2.fillRect(heartX, heartY, heartSize, heartSize);
				g2.setColor(new Color(0,0,0,0));
				g2.setStroke(new java.awt.BasicStroke(2));
				g2.drawRect(heartX, heartY, heartSize, heartSize);
			}
		}
	}
	
	// ===== BUTTON GETTER METHODS =====
	// These methods provide access to button objects for input handling
	// Used by InputHandler to detect mouse clicks on buttons
	
	public ImageButton getPlayButton() { return playButton; }
	public ImageButton getHighscoreButton() { return highscoreButton; }
	public ImageButton getContinueButton() { return continueButton; }
	public ImageButton getPauseMenuButton() { return pauseMenuButton; }
	public ImageButton getRetryButton() { return retryButton; }
	public ImageButton getGameoverMenuButton() { return gameoverMenuButton; }
	public ImageButton getBackButton() { return backButton; }
}
