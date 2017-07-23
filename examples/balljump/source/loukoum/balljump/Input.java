package loukoum.balljump;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Input implements KeyListener {

	private static final boolean[] KEYS = new boolean[255];

	public void keyTyped(KeyEvent e) {
		
	}

	public void keyPressed(KeyEvent e) {
		KEYS[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		KEYS[e.getKeyCode()] = false;
	}

	public static boolean isKeyDown(int keycode) {
		return KEYS[keycode];
	}

	public static boolean isKeyUp(int keycode) {
		return !KEYS[keycode];
	}
}
