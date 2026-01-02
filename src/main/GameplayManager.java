package main;

import java.util.Iterator;
import java.util.Random;
import entity.FallingEntity;

public class GameplayManager {

    private GamePanel gamePanel;
    private GameStateManager stateManager;
    private Random random = new Random();

    // FOOD SPAWN
    private int foodSpawnCounter = 0;
    private int foodSpawnRate = 60;

    // BOMB SPAWN
    private int bombSpawnCounter = 0;
    private int bombSpawnRate = 120;

    // MUSIC STATE FLAGS
    private boolean menuMusic = false;
    private boolean gameMusic = false;
    private boolean pauseMusic = false;
    private boolean gameOverMusic = false;
	private boolean musicPaused = false;


    // TIME
    private long gameTime = 0;

    public GameplayManager(GamePanel gamePanel, GameStateManager stateManager) {
        this.gamePanel = gamePanel;
        this.stateManager = stateManager;
    }

    public void update() {

    handleMusic();   // MUST be first

    if (stateManager.getGameState() != GameStateManager.GAME_STATE) {
        return;
    }

    if (stateManager.isPaused()) {
        return;
    }

    gamePanel.player.update();
    gameTime++;

    spawnFood();
    spawnBomb();
    updateEntities();
    checkGameOver();
	}


    // =========================
    // MUSIC CONTROLLER
    // =========================
    private void handleMusic() {

    int state = stateManager.getGameState();

    // =====================
    // PAUSE MUSIC (FLAG-BASED)
    // =====================
    if (stateManager.isPaused()) {
        if (!musicPaused) {
            gamePanel.gameSound.pauseBackgroundMusic();
            musicPaused = true;
        }
        return;
    } else {
        if (musicPaused) {
            gamePanel.gameSound.resumeBackgroundMusic();
            musicPaused = false;
        }
    }

    // =====================
    // MENU MUSIC
    // =====================
    if (state == GameStateManager.MENU_STATE && !menuMusic) {
        resetMusicFlags();
        gamePanel.gameSound.stopAllSounds();
        gamePanel.gameSound.playMenuMusic();
        menuMusic = true;
    }

    // =====================
    // GAME MUSIC
    // =====================
    else if (state == GameStateManager.GAME_STATE && !gameMusic) {
        resetMusicFlags();
        gamePanel.gameSound.stopAllSounds();
        gamePanel.gameSound.playBackgroundMusic();
        gameMusic = true;
    }

    // =====================
    // GAME OVER MUSIC
    // =====================
    else if (state == GameStateManager.GAMEOVER_STATE && !gameOverMusic) {
        resetMusicFlags();
        gamePanel.gameSound.stopAllSounds();
		gamePanel.gameSound.playDeadSound();
        gamePanel.gameSound.playGameOverMusic();
        gameOverMusic = true;
    }
	}


    private void resetMusicFlags() {
        menuMusic = false;
        gameMusic = false;
        pauseMusic = false;
        gameOverMusic = false;
    }

    // =========================
    // GAME SPAWN LOGIC
    // =========================
    private void spawnFood() {
        foodSpawnCounter++;
        if (foodSpawnCounter >= foodSpawnRate) {
            foodSpawnCounter = 0;
            int x = random.nextInt(gamePanel.screenWidth - 50);
            int type = random.nextInt(5) + 1;
            gamePanel.fallingEntities.add(new FallingEntity(gamePanel, x, type));
        }
    }

    private void spawnBomb() {
        int rate = Math.max(30, bombSpawnRate - (int)(gameTime / 900) * 15);
        bombSpawnCounter++;

        if (bombSpawnCounter >= rate) {
            bombSpawnCounter = 0;
            int x = random.nextInt(gamePanel.screenWidth - 50);
            gamePanel.fallingEntities.add(new FallingEntity(gamePanel, x, 0));
        }
    }

    // =========================
    // ENTITY UPDATES
    // =========================
    private void updateEntities() {
        Iterator<FallingEntity> it = gamePanel.fallingEntities.iterator();

        while (it.hasNext()) {
            FallingEntity e = it.next();
            e.update(gameTime);

            if (gamePanel.player.isCatching && e.checkCatch(gamePanel.player)) {

                if (e.isBomb()) {
                    gamePanel.player.losePoints(100);
                    gamePanel.player.loseLife();
                    gamePanel.gameSound.playBombHitSound();
                } else {
                    gamePanel.player.score += e.getPoints();
                    gamePanel.gameSound.playCoinCollectSound();
                }
                it.remove();
            }

            else if (e.isBomb() && e.checkCollision(gamePanel.player)) {
                gamePanel.player.losePoints(100);
                gamePanel.player.loseLife();
                gamePanel.gameSound.playBombHitSound();
                it.remove();
            }

            else if (e.fellOffScreen()) {
                if (!e.isBomb()) {
                    gamePanel.player.losePoints(20);
                    gamePanel.gameSound.playFoodCollectSound();
                }
                it.remove();
            }

            else if (!e.isActive()) {
                it.remove();
            }
        }
    }

    // =========================
    // GAME OVER
    // =========================
    private void checkGameOver() {
        if (gamePanel.player.lives <= 0) {
            stateManager.updateHighscoreIfNeeded(gamePanel.player.score);
            stateManager.setGameState(GameStateManager.GAMEOVER_STATE);
            stateManager.setGameOverSelection(0);
            gamePanel.gameSound.playDeadSound();
        }
    }

    // =========================
    // RESET
    // =========================
    public void reset() {
        gameTime = 0;
        foodSpawnCounter = 0;
        bombSpawnCounter = 0;
        gamePanel.fallingEntities.clear();
        gamePanel.player.setDefaultValues();
        gamePanel.player.score = 0;
        gamePanel.player.lives = 9;
        resetMusicFlags();
    }

    public long getGameTime() {
        return gameTime;
    }
}
