package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
	
	public boolean upPressed, downPressed, leftPressed, rightPressed, shiftKeyPressed, spacePressed, escPressed, wPressed, sPressed;
	public boolean upWasPressed = false, downWasPressed = false, spaceWasPressed = false, escWasPressed = false, wWasPressed = false, sWasPressed = false;

	@Override
	public void keyTyped(KeyEvent egit)	 {
	
	}

	@Override
	public void keyPressed(KeyEvent egit) {
		int code = egit.getKeyCode();
		
		if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
			upPressed = true;
			
		}
		if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
			downPressed = true;
			
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = true;
			
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = true;
			
		}if(code == KeyEvent.VK_SHIFT) {
			shiftKeyPressed = true;
			
		}if(code == KeyEvent.VK_SPACE) {
			spacePressed = true;
			
		}if(code == KeyEvent.VK_ESCAPE) {
			escPressed = true;
			
		}
		
	}

	@Override
	public void keyReleased(KeyEvent egit) {
		int code = egit.getKeyCode();
		
		if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
			upPressed = false;
			
			
		}
		if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
			downPressed = false;
			
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = false; 
			
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = false;
			
		}if(code == KeyEvent.VK_SHIFT) {
			shiftKeyPressed = false;
			
		}if(code == KeyEvent.VK_SPACE) {
			spacePressed = false;
			
		}if(code == KeyEvent.VK_ESCAPE) {
			escPressed = false;
			
		}
		
		
	
	}
	 
}
