package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import entity.Player;
import tile.TileManager;
import entity.FallingEntity;

public class GamePanel extends JPanel implements Runnable{
	
	//Screen Settings
	final int originalTileSize = 16; /* 16 by 16 tile */
	final int scale = 3;
	
	public final int tileSize = originalTileSize * scale; //48 by 48 tile
	public final int maxScreenCol = 20;	
	public final int maxScreenRow = 15;
	
	// WORLD SETTINGS
	
	public final int maxWorldCol = maxScreenCol;
	public final int maxWorldRow = maxScreenRow;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	
	
	//FPS of the game
	int FPS = 60;
	
	
	//Screen size
	
	public final int screenWidth = maxScreenCol * tileSize; // 768 pixel width
	public final int screenHeight = maxScreenRow * tileSize; //576 pixel in height
	
	KeyHandler keyH = new KeyHandler();
	MouseHandler mouseH = new MouseHandler();
	TileManager tileM = new TileManager(this);
	ArrayList<FallingEntity> fallingEntities = new ArrayList<>();

	Random random = new Random();
	
	// FOOD (Chicken/Fish) Spawn Settings
	int foodSpawnCounter = 0;
	int foodSpawnRate = 60; // Spawn food every 60 frames
	
	// BOMB Spawn Settings
	int bombSpawnCounter = 0;
	int bombSpawnRate = 120; // Spawn bomb every 120 frames (less frequent initially)
	
	long gameTime = 0; // Track game time in frames
	
	// GAME STATE
	public static final int MENU_STATE = 0;
	public static final int GAME_STATE = 1;
	public static final int HIGHSCORE_STATE = 2;
	public static final int PAUSE_STATE = 3;
	public static final int GAMEOVER_STATE = 4;
	int gameState = MENU_STATE; // Start with menu
	
	// MENU NAVIGATION
	int menuSelection = 0; // 0=Play, 1=Highscore
	int highscore = 0; // Track highest score
	
	// PAUSE MENU
	boolean isPaused = false;
	int pauseMenuSelection = 0; // 0=Continue, 1=Menu
	
	// GAMEOVER MENU
	int gameOverSelection = 0; // 0=Retry, 1=Menu
	
	// ========================================
	// BUTTON OBJECTS - ImageButton handles rendering and collision
	// ========================================
	private ImageButton playButton;
	private ImageButton highscoreButton;
	private ImageButton continueButton;
	private ImageButton pauseMenuButton;
	private ImageButton retryButton;
	private ImageButton gameoverMenuButton;
	
	
	
	// Raw button images for loading
	private BufferedImage playButtonNormal;
	private BufferedImage playButtonHighlighted;
	private BufferedImage highscoreButtonNormal;
	private BufferedImage highscoreButtonHighlighted;
	private BufferedImage background;
	private BufferedImage cat;
	private BufferedImage catcher;
	private BufferedImage continueButtonNormal;
	private BufferedImage continueButtonHighlighted;
	private BufferedImage pauseMenuButtonNormal;
	private BufferedImage pauseMenuButtonHighlighted;
	
	// Gameover Menu Button Images
	private BufferedImage retryButtonNormal;
	private BufferedImage retryButtonHighlighted;
	private BufferedImage gameoverMenuButtonNormal;
	private BufferedImage gameoverMenuButtonHighlighted;
	
	// Method to load all button images
	private void loadButtonImages() {
		try {
			// Load menu button images
			background = ImageIO.read(new File("res/maps/bg.png"));
			cat = ImageIO.read(new File("res/maps/smile.png"));
			catcher = ImageIO.read(new File("res/maps/catcher.png"));
			playButtonNormal = ImageIO.read(new File("res/maps/play.png"));
			playButtonHighlighted = ImageIO.read(new File("res/maps/play.png"));
			highscoreButtonNormal = ImageIO.read(new File("res/maps/highscore.png"));
			highscoreButtonHighlighted = ImageIO.read(new File("res/maps/highscore.png"));
			
			// Load pause menu button images
			continueButtonNormal = ImageIO.read(new File("res/maps/continue.png"));
			continueButtonHighlighted = ImageIO.read(new File("res/maps/continue.png"));
			pauseMenuButtonNormal = ImageIO.read(new File("res/maps/menu.png"));
			pauseMenuButtonHighlighted = ImageIO.read(new File("res/maps/menu.png"));
			
			// Load gameover menu button images
			retryButtonNormal = ImageIO.read(new File("res/maps/start.again.png"));
			retryButtonHighlighted = ImageIO.read(new File("res/maps/start.again.png"));
			gameoverMenuButtonNormal = ImageIO.read(new File("res/maps/menu.png"));
			gameoverMenuButtonHighlighted = ImageIO.read(new File("res/maps/menu.png"));
			
			// Create ImageButton objects for menu
			playButton = new ImageButton(playButtonNormal, playButtonHighlighted, 
					(screenWidth - 250) / 2, 380, 300, 200);
			highscoreButton = new ImageButton(highscoreButtonNormal, highscoreButtonHighlighted, 
					(screenWidth - 250) / 2, 490, 300, 200);
			
			// Create ImageButton objects for pause menu
			continueButton = new ImageButton(continueButtonNormal, continueButtonHighlighted, 
					(screenWidth - 250) / 2, 280, 300, 200);
			pauseMenuButton = new ImageButton(pauseMenuButtonNormal, pauseMenuButtonHighlighted, 
					(screenWidth - 250) / 2, 390, 100, 200);
			
			// Create ImageButton objects for gameover menu
			retryButton = new ImageButton(retryButtonNormal, retryButtonHighlighted, 
					(screenWidth - 250) / 2, 280, 300, 200);
			gameoverMenuButton = new ImageButton(gameoverMenuButtonNormal, gameoverMenuButtonHighlighted, 
					(screenWidth - 250) / 2, 490, 300, 200);
			
		} catch (IOException e) {
			System.out.println("Error loading button images: " + e.getMessage());
		}
	}
	
	Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public Player player = new Player(this, keyH); 
	
	 
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.addMouseMotionListener(mouseH);
		this.setFocusable(true);
		
		// Load button images
		loadButtonImages();
	}
	
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();	
	}

	@Override
	public void run() {

		double drawInterval = 1000000000/FPS;
		double delta = 0;
		double lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;
		long drawCount = 0;
		
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime)/ drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;
			
			if(delta>= 1) {
				
				update(); // 1 UPDATE: update information such as position
				repaint(); // 2 DRAW: draw the screen with the updated information
				delta--;
				drawCount++;
			}
			if(timer >= 1000000000) {
						
				drawCount = 0;
				timer = 0;
			}
			
		}
	
	}
	public void update() {
		// Handle pause menu first (takes priority)
		if (isPaused) {
			updatePauseMenu();
		}
		// Handle game over screen input
		else if (gameState == GAMEOVER_STATE) {
			updateGameOverScreen();
		}
		// Handle menu input
		else if (gameState == MENU_STATE) {
			updateMenu();
		}
		// Handle highscore screen input
		else if (gameState == HIGHSCORE_STATE) {
			updateHighscoreScreen();
		}
		// Handle game logic
		else if (gameState == GAME_STATE) {
			updateGame();
		}
	}
	
	// ========================================
	// UPDATE PAUSE MENU - Handle pause menu navigation
	// ========================================
	private void updatePauseMenu() {
		// ESC to resume
		if (keyH.escPressed && !keyH.escWasPressed) {
			isPaused = false;
			keyH.escWasPressed = true;
		}
		if (!keyH.escPressed) keyH.escWasPressed = false;
		
		// UP and DOWN arrow keys to navigate
		if (keyH.upPressed && !keyH.upWasPressed) {
			pauseMenuSelection = (pauseMenuSelection - 1 + 2) % 2;
			keyH.upWasPressed = true;
		}
		if (keyH.downPressed && !keyH.downWasPressed) {
			pauseMenuSelection = (pauseMenuSelection + 1) % 2;
			keyH.downWasPressed = true;
		}
		
		if (!keyH.upPressed) keyH.upWasPressed = false;
		if (!keyH.downPressed) keyH.downWasPressed = false;
		
		// Update button selection states
		continueButton.setSelected(pauseMenuSelection == 0);
		pauseMenuButton.setSelected(pauseMenuSelection == 1);
		
		// Mouse detection for buttons
		if (continueButton.isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			pauseMenuSelection = 0;
			if (mouseH.leftClicked) {
				isPaused = false;
				mouseH.resetClick();
			}
		}
		
		if (pauseMenuButton.isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			pauseMenuSelection = 1;
			if (mouseH.leftClicked) {
				isPaused = false;
				gameState = MENU_STATE;
				menuSelection = 0;
				mouseH.resetClick();
			}
		}
		
		// SPACE to select
		if (keyH.spacePressed && !keyH.spaceWasPressed) {
			if (pauseMenuSelection == 0) {
				isPaused = false;
			} else if (pauseMenuSelection == 1) {
				isPaused = false;
				gameState = MENU_STATE;
				menuSelection = 0;
			}
			keyH.spaceWasPressed = true;
		}
		if (!keyH.spacePressed) keyH.spaceWasPressed = false;
	}
	
	// ========================================
	// UPDATE MENU - Handle menu navigation
	// ========================================
	private void updateMenu() {
		// UP and DOWN arrow keys to navigate
		if (keyH.upPressed && !keyH.upWasPressed) {
			menuSelection = (menuSelection - 1 + 2) % 2;
			keyH.upWasPressed = true;
		}
		if (keyH.downPressed && !keyH.downWasPressed) {
			menuSelection = (menuSelection + 1) % 2;
			keyH.downWasPressed = true;
		}
		
		// Release key tracking
		if (!keyH.upPressed) keyH.upWasPressed = false;
		if (!keyH.downPressed) keyH.downWasPressed = false;
		
		// Update button selection states
		playButton.setSelected(menuSelection == 0);
		highscoreButton.setSelected(menuSelection == 1);
		
		// Mouse hover detection
		if (playButton.isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			menuSelection = 0;
			if (mouseH.leftClicked) {
				startNewGame();
				gameState = GAME_STATE;
				mouseH.resetClick();
			}
		}
		
		if (highscoreButton.isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			menuSelection = 1;
			if (mouseH.leftClicked) {
				gameState = HIGHSCORE_STATE;
				mouseH.resetClick();
			}
		}
		
		// SPACE to select
		if (keyH.spacePressed && !keyH.spaceWasPressed) {
			if (menuSelection == 0) {
				startNewGame();
				gameState = GAME_STATE;
			} else if (menuSelection == 1) {
				gameState = HIGHSCORE_STATE;
			}
			keyH.spaceWasPressed = true;
		}
		if (!keyH.spacePressed) keyH.spaceWasPressed = false;
	}
	
	// ========================================
	// UPDATE HIGHSCORE SCREEN - Handle highscore navigation
	// ========================================
	private void updateHighscoreScreen() {
		// SPACE or mouse click to go back to menu
		if (keyH.spacePressed && !keyH.spaceWasPressed) {
			gameState = MENU_STATE;
			menuSelection = 0; // Reset to play option
			keyH.spaceWasPressed = true;
		}
		if (!keyH.spacePressed) keyH.spaceWasPressed = false;
		
		// Mouse click to go back
		if (mouseH.leftClicked) {
			gameState = MENU_STATE;
			menuSelection = 0;
			mouseH.resetClick();
		}
	}
	
	// ========================================
	// UPDATE GAME - Handle gameplay logic
	// ========================================
	private void updateGame() {
		// Check for pause
		if (keyH.escPressed && !keyH.escWasPressed) {
			isPaused = true;
			pauseMenuSelection = 0;
			keyH.escWasPressed = true;
		}
		if (!keyH.escPressed) keyH.escWasPressed = false;
		
		player.update();
		
		// Increment game time for difficulty scaling
		gameTime++;
		
		// ================================
		// SPAWN FOOD (Chicken/Fish)
		// ================================
		foodSpawnCounter++;
		if (foodSpawnCounter >= foodSpawnRate) {
			foodSpawnCounter = 0;
			int randomX = random.nextInt(screenWidth - 50);
			
			// Randomly spawn chicken (1) or fish (2)
			int randomType = random.nextInt(2) + 1;
			fallingEntities.add(new FallingEntity(this, randomX, randomType));
		}
		
		// ================================
		// SPAWN BOMB
		// ================================
		// Bomb spawn rate decreases (spawns more frequently) over time
		// Every 15 seconds (900 frames), decrease bomb spawn rate by 15 frames
		// This makes bombs spawn faster as the game progresses
		int currentBombSpawnRate = Math.max(30, bombSpawnRate - (int)(gameTime / 900) * 15);
		
		bombSpawnCounter++;
		if (bombSpawnCounter >= currentBombSpawnRate) {
			bombSpawnCounter = 0;
			int randomX = random.nextInt(screenWidth - 50);
			fallingEntities.add(new FallingEntity(this, randomX, 0)); // Type 0 = Bomb
		}
		
		// Update all falling entities and check for catches/collisions
		for (int i = 0; i < fallingEntities.size(); i++) {
			FallingEntity entity = fallingEntities.get(i);
			entity.update(gameTime); // Pass game time for difficulty scaling
			
			// Check if player catches this entity
			if (player.isCatching && entity.checkCatch(player)) {
				player.score += entity.getPoints(); // Add points based on entity type
				fallingEntities.remove(i);
				i--;
			}
			// Check if bomb collides with player (damages player)
			else if (entity.isBomb() && entity.checkCollision(player)) {
				player.losePoints(10); // Deduct 10 points when hit by bomb
				player.loseLife(); // Lose a life when hit by bomb
				fallingEntities.remove(i);
				i--;
			}
			// Check if entity fell off screen without being caught
			else if (entity.fellOffScreen()) {
				player.losePoints(1); // Deduct 1 point for missing entity
				fallingEntities.remove(i);
				i--;
			}
			// Remove inactive entities (fell off screen or were caught)
			else if (!entity.isActive()) {
				fallingEntities.remove(i);
				i--;
			}
		}
		
		// Check if player is out of lives - Game Over
		if (player.lives <= 0) {
			// Update highscore if current score is higher
			if (player.score > highscore) {
				highscore = player.score;
			}
			// Go to game over screen
			gameState = GAMEOVER_STATE;
			gameOverSelection = 0;
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// Draw based on game state
		if (gameState == MENU_STATE) {
			drawMenu(g2);
		} else if (gameState == HIGHSCORE_STATE) {
			drawHighscoreScreen(g2);
		} else if (gameState == GAMEOVER_STATE) {
			drawGameOverScreen(g2);
		} else if (gameState == GAME_STATE) {
			drawGame(g2);
			
			// Draw pause menu overlay if paused
			if (isPaused) {
				drawPauseMenu(g2);
			}
		}
		
		g2.dispose();
	}
	
	// ========================================
	// DRAW MENU - Display main menu
	// ========================================
	private void drawMenu(Graphics2D g2) {
		// Draw background image
		if (background != null) {
			
			g2.drawImage(background, 0, 0, screenWidth, screenHeight, null);
			g2.drawImage(catcher, (screenWidth - 350)/2, 25, 400, 200, null);
			g2.drawImage(cat, (screenWidth - 220)/2, 170, 300, 200, null);

		} else {
			// Fallback if image fails to load
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, screenWidth, screenHeight);
		}

		// Title
		

		// Draw buttons
		playButton.draw(g2);
		highscoreButton.draw(g2);
	}
	
	// ========================================
	// DRAW HIGHSCORE SCREEN - Display highscore
	// ========================================
	private void drawHighscoreScreen(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, screenWidth, screenHeight);
		
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 60));
		String title = "HIGHSCORE";
		int titleX = (screenWidth - g2.getFontMetrics().stringWidth(title)) / 2;
		g2.drawString(title, titleX, 150);
		
		// Display highscore
		g2.setFont(new Font("Arial", Font.BOLD, 72));
		String scoreText = String.valueOf(highscore);
		int scoreX = (screenWidth - g2.getFontMetrics().stringWidth(scoreText)) / 2;
		g2.drawString(scoreText, scoreX, 300);
		
		// Back button
		int backButtonX = (screenWidth - 200) / 2;
		int backButtonY = 400;
		int backButtonWidth = 330;
		int backButtonHeight = 50;
		
		g2.setColor(Color.WHITE);
		g2.drawRect(backButtonX - 10, backButtonY - 10, backButtonWidth + 20, backButtonHeight + 20);
		
		g2.setFont(new Font("Arial", Font.BOLD, 36));
		String backText = "BACK";
		int backX = (screenWidth - g2.getFontMetrics().stringWidth(backText)) / 2;
		g2.drawString(backText, backX, backButtonY + 35);
		
		// Instructions
		g2.setColor(Color.GRAY);
		g2.setFont(new Font("Arial", Font.PLAIN, 24));
		g2.drawString("Click button or press SPACE to go back", 60, screenHeight - 30);
	}
	
	// ========================================
	// DRAW GAME - Display gameplay
	// ========================================
	private void drawGame(Graphics2D g2) {
		//layer dapat by order and pag draw sa image 
		tileM.draw(g2); // 1st layer
		
		
		
		player.draw(g2); // 2nd layer and so on...

		// Draw all falling entities
		for (FallingEntity entity : fallingEntities) {
			entity.draw(g2);
		}
		
		// Display score on screen
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 24));
		g2.drawString("Score: " + player.score, 20, 40);
		
		// Display lives as hearts
		drawHearts(g2);
	}
	
	// ========================================
	// DRAW PAUSE MENU - Display pause overlay
	// ========================================
	private void drawPauseMenu(Graphics2D g2) {
		// Semi-transparent dark overlay
		g2.setColor(new Color(0, 0, 0, 200)); // 80% opacity
		g2.fillRect(0, 0, screenWidth, screenHeight);
		
		// Title
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 64));
		String title = "PAUSED";
		int titleX = (screenWidth - g2.getFontMetrics().stringWidth(title)) / 2;
		g2.drawString(title, titleX, 120);
		
		

		// Continue button
		int continueButtonX = (screenWidth - 270) / 2;
		int continueButtonY = 280;
		int buttonWidth = 300;
		int buttonHeight = 150;
		
		// Draw continue button with image or fallback
		if (continueButtonNormal != null && continueButtonHighlighted != null) {
			BufferedImage continueImg = (pauseMenuSelection == 0) ? continueButtonHighlighted : continueButtonNormal;
			g2.drawImage(continueImg, continueButtonX, continueButtonY, buttonWidth, buttonHeight, null);
		} else {
			// Fallback to text-based button
			if (pauseMenuSelection == 0) {
				g2.setColor(new Color(255, 200, 0)); // Yellow for selected
				g2.fillRect(continueButtonX - 10, continueButtonY - 10, buttonWidth + 20, buttonHeight + 20);
				g2.setColor(Color.BLACK);
			} else {
				g2.setColor(Color.WHITE);
				g2.drawRect(continueButtonX - 10, continueButtonY - 10, buttonWidth + 20, buttonHeight + 20);
			}
			
			g2.setFont(new Font("Arial", Font.BOLD, 44));
			String continueText = "CONTINUE";
			int continueX = (screenWidth - g2.getFontMetrics().stringWidth(continueText)) / 2;
			g2.drawString(continueText, continueX, continueButtonY + 42);
		}
		
		// Menu button
		int menuButtonX = (screenWidth - 270) / 2;
		int menuButtonY = 390;
		int menubuttonWidth = 300;
		int menubuttonHeight = 200;
		
		// Draw menu button with image or fallback
		if (pauseMenuButtonNormal != null && pauseMenuButtonHighlighted != null) {
			BufferedImage menuImg = (pauseMenuSelection == 1) ? pauseMenuButtonHighlighted : pauseMenuButtonNormal;
			g2.drawImage(menuImg, menuButtonX, menuButtonY, menubuttonWidth, menubuttonHeight, null);
		} else {
			// Fallback to text-based button
			if (pauseMenuSelection == 1) {
				g2.setColor(new Color(255, 200, 0)); // Yellow for selected
				g2.fillRect(menuButtonX - 10, menuButtonY - 10, menubuttonWidth + 20, menubuttonHeight + 20);
				g2.setColor(Color.BLACK);
			} else {
				g2.setColor(Color.WHITE);
				g2.drawRect(menuButtonX - 10, menuButtonY - 10, menubuttonWidth + 20, menubuttonHeight + 20);
			}
			
			g2.setFont(new Font("Arial", Font.BOLD, 44));
			String menuText = "MENU";
			int menuX = (screenWidth - g2.getFontMetrics().stringWidth(menuText)) / 2;
			g2.drawString(menuText, menuX, menuButtonY + 42);
		}
		
		// Instructions
		g2.setColor(Color.GRAY);
		g2.setFont(new Font("Arial", Font.PLAIN, 22));
		g2.drawString("ESC to resume | Click buttons or use SPACE", 20, screenHeight - 30);
	}
	
	// ========================================
	// UPDATE GAMEOVER SCREEN - Handle game over menu navigation
	// ========================================
	private void updateGameOverScreen() {
		// UP arrow - move selection up
		if (keyH.upPressed && !keyH.upWasPressed) {
			gameOverSelection = (gameOverSelection - 1 + 2) % 2;
			keyH.upWasPressed = true;
		}
		// DOWN arrow - move selection down
		if (keyH.downPressed && !keyH.downWasPressed) {
			gameOverSelection = (gameOverSelection + 1) % 2;
			keyH.downWasPressed = true;
		}
		
		if (!keyH.upPressed) keyH.upWasPressed = false;
		if (!keyH.downPressed) keyH.downWasPressed = false;
		
		// Update button selection states
		retryButton.setSelected(gameOverSelection == 0);
		gameoverMenuButton.setSelected(gameOverSelection == 1);
		
		// Mouse hover detection
		if (retryButton.isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			gameOverSelection = 0;
			if (mouseH.leftClicked) {
				handleGameOverSelection();
				mouseH.resetClick();
			}
		} else if (gameoverMenuButton.isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			gameOverSelection = 1;
			if (mouseH.leftClicked) {
				handleGameOverSelection();
				mouseH.resetClick();
			}
		}
		
		// SPACE to select
		if (keyH.spacePressed && !keyH.spaceWasPressed) {
			handleGameOverSelection();
			keyH.spaceWasPressed = true;
		}
		if (!keyH.spacePressed) keyH.spaceWasPressed = false;
	}
	
	private void handleGameOverSelection() {
		if (gameOverSelection == 0) {
			startNewGame();
			gameState = GAME_STATE;
		} else if (gameOverSelection == 1) {
			gameState = MENU_STATE;
			menuSelection = 0;
		}
	}
	
	// ========================================
	// DRAW GAMEOVER SCREEN - Display game over screen
	// ========================================
	private void drawGameOverScreen(Graphics2D g2) {
		// Dark overlay background
		g2.setColor(new Color(0, 0, 0, 200));
		g2.fillRect(0, 0, screenWidth, screenHeight);
		
		// GAME OVER title
		g2.setColor(Color.RED);
		g2.setFont(new Font("Arial", Font.BOLD, 80));
		String gameOverText = "GAME OVER";
		int gameOverX = (screenWidth - g2.getFontMetrics().stringWidth(gameOverText)) / 2;
		g2.drawString(gameOverText, gameOverX, 120);
		
		// Score display
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 40));
		String scoreText = "Your Score: " + player.score;
		int scoreX = (screenWidth - g2.getFontMetrics().stringWidth(scoreText)) / 2;
		g2.drawString(scoreText, scoreX, 200);
		
		// Highscore display
		g2.setFont(new Font("Arial", Font.PLAIN, 32));
		String highscoreText = "Highscore: " + highscore;
		int highscoreX = (screenWidth - g2.getFontMetrics().stringWidth(highscoreText)) / 2;
		g2.drawString(highscoreText, highscoreX, 250);
		
		// Retry button
		int retryButtonX = (screenWidth - 270) / 2;
		int retryButtonY = 250;
		int buttonWidth = 300;
		int buttonHeight = 200;
		
		// Draw retry button with image or fallback
		if (retryButtonNormal != null && retryButtonHighlighted != null) {
			BufferedImage retryImg = (gameOverSelection == 0) ? retryButtonHighlighted : retryButtonNormal;
			g2.drawImage(retryImg, retryButtonX, retryButtonY, buttonWidth, buttonHeight, null);
		} else {
			// Fallback to text-based button
			if (gameOverSelection == 0) {
				g2.setColor(Color.YELLOW);
				g2.setStroke(new java.awt.BasicStroke(3));
				g2.drawRect(retryButtonX - 10, retryButtonY - 10, buttonWidth + 20, buttonHeight + 20);
			}
			
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Arial", Font.PLAIN, 32));
			g2.drawRect(retryButtonX, retryButtonY, buttonWidth, buttonHeight);
			String retryText = "RETRY";
			int retryX = (screenWidth - g2.getFontMetrics().stringWidth(retryText)) / 2;
			g2.drawString(retryText, retryX, retryButtonY + 42);
		}
		
		// Menu button
		int menuButtonX = (screenWidth - 270) / 2;
		int menuButtonY = 350;
		
		// Draw menu button with image or fallback
		if (gameoverMenuButtonNormal != null && gameoverMenuButtonHighlighted != null) {
			BufferedImage menuImg = (gameOverSelection == 1) ? gameoverMenuButtonHighlighted : gameoverMenuButtonNormal;
			g2.drawImage(menuImg, menuButtonX, menuButtonY, buttonWidth, buttonHeight, null);
		} else {
			// Fallback to text-based button
			if (gameOverSelection == 1) {
				g2.setColor(Color.YELLOW);
				g2.setStroke(new java.awt.BasicStroke(3));
				g2.drawRect(menuButtonX - 10, menuButtonY - 10, buttonWidth + 20, buttonHeight + 20);
			}
			
			g2.setColor(Color.WHITE);
			g2.drawRect(menuButtonX, menuButtonY, buttonWidth, buttonHeight);
			String menuText = "MENU";
			int menuX = (screenWidth - g2.getFontMetrics().stringWidth(menuText)) / 2;
			g2.drawString(menuText, menuX, menuButtonY + 42);
		}
		
		// Instructions
		g2.setColor(Color.GRAY);
		g2.setFont(new Font("Arial", Font.PLAIN, 22));
		g2.drawString("SPACE/Click to select | UP/DOWN or W/S to navigate", 20, screenHeight - 30);
	}
	
	// ========================================
	// START NEW GAME - Reset game state for a new game
	// ========================================
	private void startNewGame() {
		// Reset game variables
		gameTime = 0;
		foodSpawnCounter = 0;
		bombSpawnCounter = 0;
		fallingEntities.clear();
		
		// Reset player
		player.setDefaultValues();
		player.score = 0;
		player.lives = 9;
	}
	
	// ========================================
	// DRAW HEARTS - Display the player's lives as heart icons
	// ========================================
	private void drawHearts(Graphics2D g2) {
		int heartSize = 80; // Size of each heart
		int heartSpacing = 40; // Space between hearts
		int startX = screenWidth - (9 * heartSpacing) - 50; // Right side, 9 hearts wide
		int startY = 5; // Near top
		
		// Draw 9 heart slots
		for (int i = 0; i < 9; i++) {
			int heartX = startX + (i * heartSpacing);
			int heartY = startY;
			
			// Draw filled heart if player still has this life
			if (i < player.lives && player.heart != null) {
				g2.drawImage(player.heart, heartX, heartY, heartSize, heartSize, null);
			} else {
				// Draw empty heart (semi-transparent or different color)
				g2.setColor(new Color(0,0,0,0)); // Dim gray for empty hearts
				g2.fillRect(heartX, heartY, heartSize, heartSize);
				g2.setColor(new Color(0,0,0,0)); // Lighter border
				g2.setStroke(new java.awt.BasicStroke(2));
				g2.drawRect(heartX, heartY, heartSize, heartSize);
			}
		}
	}	

}
