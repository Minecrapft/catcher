package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener {
	
	// Mouse position
	public int mouseX = 0;
	public int mouseY = 0;
	
	// Mouse button states
	public boolean leftClicked = false;
	public boolean leftPressed = false;
	public boolean rightClicked = false;
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftPressed = true;
			leftClicked = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			rightClicked = true;
		}
		
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftPressed = false;
		}
		
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Not used, handled in mousePressed/Released
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Not used
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Not used
	}
	
	// ========================================
	// IS MOUSE OVER RECT - Check if mouse is over a rectangle area
	// @param x, y, width, height - Rectangle bounds
	// @return true if mouse is over the rectangle
	// ========================================
	public boolean isMouseOverRect(int x, int y, int width, int height) {
		return mouseX >= x && mouseX <= x + width && 
		       mouseY >= y && mouseY <= y + height;
	}
	
	// ========================================
	// RESET CLICK - Reset the click flag after use
	// ========================================
	public void resetClick() {
		leftClicked = false;
	}
}
