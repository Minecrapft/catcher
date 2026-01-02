package main;

import entity.Player;

public class InputHandler {
	
	private GamePanel gamePanel;
	private GameStateManager stateManager;
	private UIRenderer uiRenderer;
	private KeyHandler keyH;
	private MouseHandler mouseH;
	
	public InputHandler(GamePanel gamePanel, GameStateManager stateManager, UIRenderer uiRenderer,
					   KeyHandler keyH, MouseHandler mouseH) {
		this.gamePanel = gamePanel;
		this.stateManager = stateManager;
		this.uiRenderer = uiRenderer;
		this.keyH = keyH;
		this.mouseH = mouseH;
	}
	
	public void handleInput() {
		// DEBUG: Press L to go to game over screen
		if (keyH.lPressed) {
			stateManager.setGameState(GameStateManager.GAMEOVER_STATE);
			stateManager.setGameOverSelection(0);
			keyH.lPressed = false;
		}
		
		if (stateManager.isPaused()) {
			handlePauseMenuInput();
		} else if (stateManager.getGameState() == GameStateManager.GAMEOVER_STATE) {
			handleGameOverInput();
		} else if (stateManager.getGameState() == GameStateManager.MENU_STATE) {
			handleMenuInput();
		} else if (stateManager.getGameState() == GameStateManager.HIGHSCORE_STATE) {
			handleHighscoreInput();
		} else if (stateManager.getGameState() == GameStateManager.GAME_STATE) {
			handleGameInput();
		}
	}
	
	private void handleMenuInput() {
		// UP and DOWN arrow keys to navigate
		if (keyH.upPressed && !keyH.upWasPressed) {
			stateManager.setMenuSelection((stateManager.getMenuSelection() - 1 + 2) % 2);
			keyH.upWasPressed = true;
		}
		if (keyH.downPressed && !keyH.downWasPressed) {
			stateManager.setMenuSelection((stateManager.getMenuSelection() + 1) % 2);
			keyH.downWasPressed = true;
		}
		
		if (!keyH.upPressed) keyH.upWasPressed = false;
		if (!keyH.downPressed) keyH.downWasPressed = false;
		
		// Update button selection states
		uiRenderer.getPlayButton().setSelected(stateManager.getMenuSelection() == 0);
		uiRenderer.getHighscoreButton().setSelected(stateManager.getMenuSelection() == 1);
		
		// Mouse hover detection
		if (uiRenderer.getPlayButton().isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			stateManager.setMenuSelection(0);
			if (mouseH.leftClicked) {
				startNewGame();
				mouseH.resetClick();
			}
		}
		
		if (uiRenderer.getHighscoreButton().isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			stateManager.setMenuSelection(1);
			if (mouseH.leftClicked) {
				stateManager.setGameState(GameStateManager.HIGHSCORE_STATE);
				mouseH.resetClick();
			}
		}
		
		// SPACE to select
		if (keyH.spacePressed && !keyH.spaceWasPressed) {
			if (stateManager.getMenuSelection() == 0) {
				startNewGame();
			} else if (stateManager.getMenuSelection() == 1) {
				stateManager.setGameState(GameStateManager.HIGHSCORE_STATE);
			}
			keyH.spaceWasPressed = true;
		}
		if (!keyH.spacePressed) keyH.spaceWasPressed = false;
	}
	
	private void handleHighscoreInput() {
		// Back button collision detection
		uiRenderer.getBackButton().setSelected(uiRenderer.getBackButton().isMouseOver(mouseH.mouseX, mouseH.mouseY));
		
		if (uiRenderer.getBackButton().isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			if (mouseH.leftClicked) {
				stateManager.setGameState(GameStateManager.MENU_STATE);
				stateManager.setMenuSelection(0);
				mouseH.resetClick();
			}
		}
		
		// SPACE or mouse click to go back to menu
		if (keyH.spacePressed && !keyH.spaceWasPressed) {
			stateManager.setGameState(GameStateManager.MENU_STATE);
			stateManager.setMenuSelection(0);
			keyH.spaceWasPressed = true;
		}
		if (!keyH.spacePressed) keyH.spaceWasPressed = false;
	}
	
	private void handleGameInput() {
		// Check for pause
		if (keyH.escPressed && !keyH.escWasPressed) {
			stateManager.setPaused(true);
			stateManager.setPauseMenuSelection(0);
			keyH.escWasPressed = true;
		}
		if (!keyH.escPressed) keyH.escWasPressed = false;
	}
	
	private void handlePauseMenuInput() {
		// ESC to resume
		if (keyH.escPressed && !keyH.escWasPressed) {
			stateManager.setPaused(false);
			keyH.escWasPressed = true;
		}
		if (!keyH.escPressed) keyH.escWasPressed = false;
		
		// UP and DOWN arrow keys to navigate
		if (keyH.upPressed && !keyH.upWasPressed) {
			stateManager.setPauseMenuSelection((stateManager.getPauseMenuSelection() - 1 + 2) % 2);
			keyH.upWasPressed = true;
		}
		if (keyH.downPressed && !keyH.downWasPressed) {
			stateManager.setPauseMenuSelection((stateManager.getPauseMenuSelection() + 1) % 2);
			keyH.downWasPressed = true;
		}
		
		if (!keyH.upPressed) keyH.upWasPressed = false;
		if (!keyH.downPressed) keyH.downWasPressed = false;
		
		// Update button selection states
		uiRenderer.getContinueButton().setSelected(stateManager.getPauseMenuSelection() == 0);
		uiRenderer.getPauseMenuButton().setSelected(stateManager.getPauseMenuSelection() == 1);
		
		// Mouse detection for buttons
		if (uiRenderer.getContinueButton().isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			stateManager.setPauseMenuSelection(0);
			if (mouseH.leftClicked) {
				stateManager.setPaused(false);
				mouseH.resetClick();
			}
		}
		
		if (uiRenderer.getPauseMenuButton().isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			stateManager.setPauseMenuSelection(1);
			if (mouseH.leftClicked) {
				stateManager.setPaused(false);
				stateManager.setGameState(GameStateManager.MENU_STATE);
				stateManager.setMenuSelection(0);
				mouseH.resetClick();
			}
		}
		
		// SPACE to select
		if (keyH.spacePressed && !keyH.spaceWasPressed) {
			if (stateManager.getPauseMenuSelection() == 0) {
				stateManager.setPaused(false);
			} else if (stateManager.getPauseMenuSelection() == 1) {
				stateManager.setPaused(false);
				stateManager.setGameState(GameStateManager.MENU_STATE);
				stateManager.setMenuSelection(0);
			}
			keyH.spaceWasPressed = true;
		}
		if (!keyH.spacePressed) keyH.spaceWasPressed = false;
	}
	
	private void handleGameOverInput() {
		
		
		// Update button selection states
		uiRenderer.getRetryButton().setSelected(stateManager.getGameOverSelection() == 0);
		uiRenderer.getGameoverMenuButton().setSelected(stateManager.getGameOverSelection() == 1);
		
		// Mouse hover detection
		if (uiRenderer.getRetryButton().isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			stateManager.setGameOverSelection(0);
			if (mouseH.leftClicked) {
				handleGameOverSelection();
				mouseH.resetClick();
			}
		} else if (uiRenderer.getGameoverMenuButton().isMouseOver(mouseH.mouseX, mouseH.mouseY)) {
			stateManager.setGameOverSelection(1);
			if (mouseH.leftClicked) {
				handleGameOverSelection();
				mouseH.resetClick();
			}
		}
		
		
	}
	
	private void handleGameOverSelection() {
		if (stateManager.getGameOverSelection() == 0) {
			startNewGame();
		} else if (stateManager.getGameOverSelection() == 1) {
			gamePanel.gameSound.playMenuMusic();
			stateManager.setGameState(GameStateManager.MENU_STATE);
			stateManager.setMenuSelection(0);
		}
	}
	
	private void startNewGame() {
		gamePanel.gameplayManager.reset();
		gamePanel.gameSound.playBackgroundMusic();
		stateManager.setGameState(GameStateManager.GAME_STATE);
	}
}
