package main;

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
		this.width = width;
		this.height = height;
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
	 * Check if the mouse is over this button
	 * @param mouseX Mouse X position
	 * @param mouseY Mouse Y position
	 * @return true if mouse is over the button, false otherwise
	 */
	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && 
		       mouseY >= y && mouseY <= y + height;
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
}
