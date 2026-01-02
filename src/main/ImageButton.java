package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * ImageButton - Handles button rendering and collision detection
 * Encapsulates button logic to reduce code duplication
 */
public class ImageButton {
	
	private BufferedImage normalImage;
	private BufferedImage highlightedImage;
	private int x;
	private int y;
	private int width;
	private int height;
	private boolean isSelected = false;
	private boolean isHovered = false;
	private int originalX;
	private int originalY;
	private final int HOVER_OFFSET = 5; // Pixels to move when hovered
	private SoundHandler soundHandler; // Reference to play hover and click sounds
	
	// Collision area parameters (can be edited separately from visual size)
	private int collisionX;
	private int collisionY;
	private int collisionWidth;
	private int collisionHeight;
	
	/**
	 * Constructor for ImageButton
	 * @param normalImage The button image when not selected
	 * @param highlightedImage The button image when selected/highlighted
	 * @param x X position of the button
	 * @param y Y position of the button
	 * @param width Width of the button
	 * @param height Height of the button
	 */
	public ImageButton(BufferedImage normalImage, BufferedImage highlightedImage, 
	                    int x, int y, int width, int height) {
		this.normalImage = normalImage;
		this.highlightedImage = highlightedImage;
		this.x = x;
		this.y = y;
		this.originalX = x;
		this.originalY = y;
		this.width = width;
		this.height = height;
		this.soundHandler = null; // No sound by default
		// Initialize collision area to match visual size
		this.collisionX = x;
		this.collisionY = y;
		this.collisionWidth = width;
		this.collisionHeight = height;
	}
	
	/**
	 * Constructor for ImageButton with sound
	 * @param normalImage The button image when not selected
	 * @param highlightedImage The button image when selected/highlighted
	 * @param x X position of the button
	 * @param y Y position of the button
	 * @param width Width of the button
	 * @param height Height of the button
	 * @param soundHandler Reference to SoundHandler for button sounds
	 */
	public ImageButton(BufferedImage normalImage, BufferedImage highlightedImage, 
	                    int x, int y, int width, int height, SoundHandler soundHandler) {
		this.normalImage = normalImage;
		this.highlightedImage = highlightedImage;
		this.x = x;
		this.y = y;
		this.originalX = x;
		this.originalY = y;
		this.width = width;
		this.height = height;
		this.soundHandler = soundHandler;
		// Initialize collision area to match visual size
		this.collisionX = x;
		this.collisionY = y;
		this.collisionWidth = width;
		this.collisionHeight = height;
	}
	
	/**
	 * Draw the button on the screen
	 * @param g2 Graphics2D object for drawing
	 */
	public void draw(Graphics2D g2) {
		if (isSelected && highlightedImage != null) {
			g2.drawImage(highlightedImage, x , y, width, height, null);
		} else if (normalImage != null) {
			g2.drawImage(normalImage, x, y, width, height, null);
		}
	}
	
	/**
	 * Draw the collision area as a debug rectangle
	 * @param g2 Graphics2D object for drawing
	 */
	public void drawCollisionArea(Graphics2D g2) {
		g2.setColor(new Color(0, 0, 0, 0)); // Semi-transparent red
		g2.fillRect(collisionX, collisionY, collisionWidth, collisionHeight);
	
	}
	
	/**
	 * Check if the mouse is over this button and update hover state
	 * @param mouseX Mouse X position
	 * @param mouseY Mouse Y position
	 * @return true if mouse is over the button, false otherwise
	 */
	public boolean isMouseOver(int mouseX, int mouseY) {
		boolean mouseOver = mouseX >= collisionX && mouseX < collisionX + collisionWidth && 
		                    mouseY >= collisionY && mouseY < collisionY + collisionHeight;
		
		// Update hover state and adjust button position
		if (mouseOver && !isHovered) {
			isHovered = true;
			x = originalX;
			y = originalY + HOVER_OFFSET;
			// Play hover sound when mouse first enters button
			if (soundHandler != null && soundHandler.isSoundLoaded("button_hover")) {
				soundHandler.playSound("button_hover");
			}
		} else if (!mouseOver && isHovered) {
			isHovered = false;
			x = originalX;
			y = originalY;
		}
		
		return mouseOver;
	}
	
	/**
	 * Set whether this button is selected/highlighted
	 * @param selected true to highlight, false otherwise
	 */
	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}
	
	/**
	 * Check if this button is selected
	 * @return true if selected, false otherwise
	 */
	public boolean isSelected() {
		return isSelected;
	}
	
	/**
	 * Check if this button is currently hovered
	 * @return true if hovered, false otherwise
	 */
	public boolean isHovered() {
		return isHovered;
	}
	
	/**
	 * Get the X position of the button
	 * @return X position
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get the Y position of the button
	 * @return Y position
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Get the width of the button
	 * @return Width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Get the height of the button
	 * @return Height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Set the position of the button
	 * @param x New X position
	 * @param y New Y position
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Set the size of the button
	 * @param width New width
	 * @param height New height
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Set the collision area independently from visual size
	 * @param collisionX X position of collision area
	 * @param collisionY Y position of collision area
	 * @param collisionWidth Width of collision area
	 * @param collisionHeight Height of collision area
	 */
	public void setCollisionArea(int collisionX, int collisionY, int collisionWidth, int collisionHeight) {
		this.collisionX = collisionX;
		this.collisionY = collisionY;
		this.collisionWidth = collisionWidth;
		this.collisionHeight = collisionHeight;
	}
	
	/**
	 * Get the collision X position
	 * @return Collision X position
	 */
	public int getCollisionX() {
		return collisionX;
	}
	
	/**
	 * Get the collision Y position
	 * @return Collision Y position
	 */
	public int getCollisionY() {
		return collisionY;
	}
	
	/**
	 * Get the collision width
	 * @return Collision width
	 */
	public int getCollisionWidth() {
		return collisionWidth;
	}
	
	/**
	 * Get the collision height
	 * @return Collision height
	 */
	public int getCollisionHeight() {
		return collisionHeight;
	}
	
	/**
	 * Play the click sound for this button
	 */
	public void playClickSound() {
		if (soundHandler != null && soundHandler.isSoundLoaded("button_click")) {
			soundHandler.playSound("button_click");
		}
	}
	
	/**
	 * Set the SoundHandler for this button (can be set later if needed)
	 * @param soundHandler Reference to SoundHandler
	 */
	public void setSoundHandler(SoundHandler soundHandler) {
		this.soundHandler = soundHandler;
	}
}