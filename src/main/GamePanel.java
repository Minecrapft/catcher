package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import entity.Player;
import entity.FallingEntity;
import entity.sounds;

public class GamePanel extends JPanel implements Runnable{
	
	// Screen Settings
	final int originalTileSize = 16;
	final int scale = 3;
	
	public final int tileSize = originalTileSize * scale;
	public final int maxScreenCol = 20;	
	public final int maxScreenRow = 15;
	
	// World Settings
	public final int maxWorldCol = maxScreenCol;
	public final int maxWorldRow = maxScreenRow;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;
	
	// FPS
	int FPS = 60;
	
	// Screen size
	public final int screenWidth = maxScreenCol * tileSize;
	public final int screenHeight = maxScreenRow * tileSize;
	
	// Handlers
	KeyHandler keyH = new KeyHandler();
	MouseHandler mouseH = new MouseHandler();
	
	// Game Components
	public Player player = new Player(this, keyH);
	public ArrayList<FallingEntity> fallingEntities = new ArrayList<>();
	
	// Managers
	private GameStateManager stateManager;
	public GameplayManager gameplayManager;
	private UIRenderer uiRenderer;
	private InputHandler inputHandler;
	public SoundHandler soundHandler;
	public sounds gameSound;
	
	Thread gameThread; 
	
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.addMouseMotionListener(mouseH);
		this.setFocusable(true);
		
		// Initialize sound system
		soundHandler = new SoundHandler();
		gameSound = new sounds(soundHandler);
		
		// Start menu music
		gameSound.stopAllSounds();
		gameSound.playMenuMusic();
		
		// Initialize managers
		stateManager = new GameStateManager();
		uiRenderer = new UIRenderer(this, stateManager);
		gameplayManager = new GameplayManager(this, stateManager);
		inputHandler = new InputHandler(this, stateManager, uiRenderer, keyH, mouseH);
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
			
			if(delta >= 1) {
				update();
				repaint();
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
		inputHandler.handleInput();
		gameplayManager.update();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		uiRenderer.render(g2);
		g2.dispose();
	}}