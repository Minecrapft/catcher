package entity;

import main.SoundHandler;

/**
 * Sounds - Manages all game audio through SoundHandler
 * Load all your music and sound effects here with custom names
 */
public class sounds {
    
    private SoundHandler soundHandler;
    
    /**
     * Constructor - Initialize the sound handler
     * 
     * @param soundHandler Reference to the SoundHandler instance
     */
    public sounds(SoundHandler soundHandler) {
        this.soundHandler = soundHandler;
        loadAllSounds();
    }
    
    /**
     * Load all game sounds and music
     * Add your sound files here with custom names
     */
    private void loadAllSounds() {
        // === BACKGROUND MUSIC ===
        soundHandler.loadSound("menu_music", "res/sounds/menumusic.WAV");
        soundHandler.loadSound("game_over_music", "res/sounds/gameovermusic.WAV");
        soundHandler.loadSound("background_music", "res/sounds/backgroundmusic.WAV");
        soundHandler.loadSound("deadSound", "res/sounds/dead.WAV");
        soundHandler.loadSound("coin_collect", "res/sounds/eating(1).wav");

        soundHandler.loadSound("bomb_hit", "res/sounds/bomb.WAV");

        // === BUTTON SOUNDS ===
        soundHandler.loadSound("button_hover", "res/sounds/selecting2.WAV");
        soundHandler.loadSound("button_click", "res/sounds/click.WAV");
    }

    public void pauseBackgroundMusic() {
    soundHandler.pauseSound("background_music");
    }

    public void resumeBackgroundMusic() {
        soundHandler.resumeSound("background_music");
    }

    
    /**
     * Play background music on loop
     */
    public void playBackgroundMusic() {
        soundHandler.loopSound("background_music");
    }
    
    /**
     * Play menu music on loop
     */
    public void playMenuMusic() {
        soundHandler.loopSound("menu_music");
    }
    
    /**
     * Play game over music on loop
     */
    public void playGameOverMusic() {
        soundHandler.loopSound("game_over_music");
    }
    
    /**
     * Play coin collection sound
     */
    public void playCoinCollectSound() {
        soundHandler.playSound("coin_collect");
    }
    
    /**
     * Play food collection sound
     */
    public void playFoodCollectSound() {
        soundHandler.playSound("food_collect");
    }
    
    /**
     * Play bomb hit sound
     */
    public void playBombHitSound() {
        soundHandler.playSound("bomb_hit");
    }
    
    /**
     * Play jump sound
     */
    public void playJumpSound() {
        soundHandler.playSound("jump");
    }
    
    /**
     * Play lose life sound
     */
    public void playLoseLifeSound() {
        soundHandler.playSound("lose_life");
    }
    
    /**
     * Play menu selection sound
     */
    public void playMenuSelectSound() {
        soundHandler.playSound("menu_select");
    }
    
    /**
     * Play pause sound
     */
    public void playPauseSound() {
        soundHandler.playSound("pause");
    }

    /**
     * Play pause sound
     */
    public void playDeadSound() {
        soundHandler.playSound("deadSound");
    }
    
    /**
     * Stop all sounds
     */
    public void stopAllSounds() {
        soundHandler.stopAllSounds();
    }
    
    /**
     * Stop a specific sound by name
     * 
     * @param soundName The name of the sound to stop
     */
    public void stopSound(String soundName) {
        soundHandler.stopSound(soundName);
    }
    
    /**
     * Set master volume for all sounds
     * 
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        soundHandler.setMasterVolume(volume);
    }

}
