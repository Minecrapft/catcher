package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entity.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
	
	//Screen Settings
	final int originalTileSize = 16; /* 16 by 16 tile */
	final int scale = 3;
	
	public final int tileSize = originalTileSize * scale; //48 by 48 tile
	public final int maxScreenCol = 27;	
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
	TileManager tileM = new TileManager(this);
	Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public Player player = new Player(this, keyH); 
	
	 
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
		 
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
		player.update();

		
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		//layer dapat by order and pag draw sa image 
		tileM.draw(g2); // 1st layer
		
		
		player.draw(g2); // 2nd layer and so on...
		
		// Display score on screen
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 24));
		g2.drawString("Nigga: " + player.score, 20, 40);
		
		g2.dispose();
			
	}	

}
