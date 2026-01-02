package main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundHandler - Manages all game audio and music
 * Allows loading, playing, and managing sound effects and background music
 */
public class SoundHandler {
	
	// Map to store file paths for sounds (to reload streams)
	private Map<String, String> soundPathMap = new HashMap<>();
	private Map<String, Clip> clipMap = new HashMap<>();
	
	// Volume control (0.0 to 1.0)
	private float masterVolume = 0.8f;
	
	/**
	 * Load a sound file and store it with a custom name
	 * 
	 * @param soundName The name to reference this sound (e.g., "jump", "coin_collect")
	 * @param filePath The path to the audio file (e.g., "res/sounds/jump.wav")
	 */
	public void loadSound(String soundName, String filePath) {
		try {
			File soundFile = new File(filePath);
			if (!soundFile.exists()) {
				System.out.println("Sound file not found: " + filePath);
				return;
			}
			
			// Store the file path for later use
			soundPathMap.put(soundName, filePath);
			System.out.println("Sound loaded: " + soundName + " from " + filePath);
		} catch (Exception e) {
			System.out.println("Error loading sound '" + soundName + "': " + e.getMessage());
		}
	}
	
	/**
	 * Play a sound effect once
	 * 
	 * @param soundName The name of the sound to play
	 */
	public void playSound(String soundName) {
		try {
			if (!soundPathMap.containsKey(soundName)) {
				System.out.println("Sound not found: " + soundName);
				return;
			}
			
			// Get the file path and create a fresh audio stream
			String filePath = soundPathMap.get(soundName);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
			
			// Create a new clip for each play
			Clip clip = AudioSystem.getClip();
			
			// Open the audio stream and play
			clip.open(audioStream);
			setVolume(clip, masterVolume);
			clip.start();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			System.out.println("Error playing sound '" + soundName + "': " + e.getMessage());
		}
	}
	
	/**
	 * Loop a sound continuously (good for background music)
	 * 
	 * @param soundName The name of the sound to loop
	 */
	public void loopSound(String soundName) {
		try {
			if (!soundPathMap.containsKey(soundName)) {
				System.out.println("Sound not found: " + soundName);
				return;
			}
			
			// Get the file path and create a fresh audio stream
			String filePath = soundPathMap.get(soundName);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
			
			// Stop any existing clip for this sound
			if (clipMap.containsKey(soundName)) {
				Clip existingClip = clipMap.get(soundName);
				if (existingClip.isRunning()) {
					existingClip.stop();
				}
				existingClip.close();
			}
			
			// Create a new clip for looping
			Clip clip = AudioSystem.getClip();
			clipMap.put(soundName, clip);
			
			// Open the audio stream and loop
			clip.open(audioStream);
			setVolume(clip, masterVolume);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			System.out.println("Error looping sound '" + soundName + "': " + e.getMessage());
		}
	}
	
	/**
	 * Stop a sound that is currently playing
	 * 
	 * @param soundName The name of the sound to stop
	 */
	public void stopSound(String soundName) {
		if (clipMap.containsKey(soundName)) {
			Clip clip = clipMap.get(soundName);
			if (clip.isRunning()) {
				clip.stop();
			}
		}
	}
	
	/**
	 * Stop all currently playing sounds
	 */
	public void stopAllSounds() {
		for (Clip clip : clipMap.values()) {
			if (clip.isRunning()) {
				clip.stop();
			}
		}
	}

        /**
     * Pause a looping sound without resetting its position
     *
     * @param soundName the name of the sound to pause
     */
    public void pauseSound(String soundName) {
        if (clipMap.containsKey(soundName)) {
            Clip clip = clipMap.get(soundName);
            if (clip != null && clip.isRunning()) {
                clip.stop(); // keeps current frame position
            }
        }
    }

    /**
     * Resume a paused looping sound
     *
     * @param soundName the name of the sound to resume
     */
    public void resumeSound(String soundName) {
        if (clipMap.containsKey(soundName)) {
            Clip clip = clipMap.get(soundName);
            if (clip != null && !clip.isRunning()) {
                clip.start(); // resumes from paused position
            }
        }
    }

	
	/**
	 * Set the volume for a specific clip
	 * 
	 * @param clip The audio clip to adjust
	 * @param volume Volume level (0.0 to 1.0)
	 */
	private void setVolume(Clip clip, float volume) {
		if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			// Convert volume (0.0-1.0) to decibels
			float dB = (float) (Math.log(Math.max(volume, 0.0001)) / Math.log(10.0)) * 20.0f;
			gainControl.setValue(dB);
		}
	}
	
	/**
	 * Set the master volume for all sounds
	 * 
	 * @param volume Volume level (0.0 to 1.0)
	 */
	public void setMasterVolume(float volume) {
		this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
		// Update all currently playing clips
		for (Clip clip : clipMap.values()) {
			setVolume(clip, masterVolume);
		}
	}
	
	/**
	 * Get the current master volume
	 * 
	 * @return Current volume level (0.0 to 1.0)
	 */
	public float getMasterVolume() {
		return masterVolume;
	}
	
	/**
	 * Check if a sound is currently loaded
	 * 
	 * @param soundName The name of the sound to check
	 * @return true if the sound is loaded, false otherwise
	 */
	public boolean isSoundLoaded(String soundName) {
		return soundPathMap.containsKey(soundName);
	}
	
	/**
	 * Check if a sound is currently playing
	 * 
	 * @param soundName The name of the sound to check
	 * @return true if the sound is playing, false otherwise
	 */
	public boolean isSoundPlaying(String soundName) {
		if (clipMap.containsKey(soundName)) {
			return clipMap.get(soundName).isRunning();
		}
		return false;
	}
	
	/**
	 * Unload a sound from memory to free resources
	 * 
	 * @param soundName The name of the sound to unload
	 */
	public void unloadSound(String soundName) {
		stopSound(soundName);
		
		if (clipMap.containsKey(soundName)) {
			clipMap.get(soundName).close();
			clipMap.remove(soundName);
		}
		
		if (soundPathMap.containsKey(soundName)) {
			soundPathMap.remove(soundName);
		}
	}
	
	/**
	 * Unload all sounds and close all clips
	 */
	public void unloadAllSounds() {
		stopAllSounds();
		
		for (Clip clip : clipMap.values()) {
			clip.close();
		}
		clipMap.clear();
		soundPathMap.clear();
	}

} 