package main;

public class GameStateManager {
	
	// GAME STATE CONSTANTS
	public static final int MENU_STATE = 0;
	public static final int GAME_STATE = 1;
	public static final int HIGHSCORE_STATE = 2;
	public static final int PAUSE_STATE = 3;
	public static final int GAMEOVER_STATE = 4;
	
	// State tracking
	private int gameState = MENU_STATE;
	private boolean isPaused = false;
	
	// MENU NAVIGATION
	private int menuSelection = 0; // 0=Play, 1=Highscore
	private int highscore = 0;
	
	// PAUSE MENU
	private int pauseMenuSelection = 0; // 0=Continue, 1=Menu
	
	// GAMEOVER MENU
	private int gameOverSelection = 0; // 0=Retry, 1=Menu
	
	// Getters
	public int getGameState() { return gameState; }
	public boolean isPaused() { return isPaused; }
	public int getMenuSelection() { return menuSelection; }
	public int getHighscore() { return highscore; }
	public int getPauseMenuSelection() { return pauseMenuSelection; }
	public int getGameOverSelection() { return gameOverSelection; }
	
	// Setters
	public void setGameState(int state) { gameState = state; }
	public void setPaused(boolean paused) { isPaused = paused; }
	public void setMenuSelection(int selection) { menuSelection = selection; }
	public void setHighscore(int score) { highscore = score; }
	public void setPauseMenuSelection(int selection) { pauseMenuSelection = selection; }
	public void setGameOverSelection(int selection) { gameOverSelection = selection; }
	
	// Helper methods
	public void updateHighscoreIfNeeded(int currentScore) {
		if (currentScore > highscore) {
			highscore = currentScore;
		}
	}
	
	public void resetGameState() {
		gameState = MENU_STATE;
		isPaused = false;
		menuSelection = 0;
		pauseMenuSelection = 0;
		gameOverSelection = 0;
		
	}
}
