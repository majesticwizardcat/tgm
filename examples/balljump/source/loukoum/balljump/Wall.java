package loukoum.balljump;

import java.awt.Graphics;
import java.awt.Color;

import static loukoum.balljump.World.UNIT;

public class Wall {

	public static final float WIDTH = 0.2f * UNIT;
	public static final float HEIGHT = Renderer.HEIGHT;

	public static final int Y = 0;

	private float x;

	public Wall(float x) {
		this.x = x;
	}

	public void render(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect((int) x, Y, (int) WIDTH, (int) HEIGHT);
	}

	public float getX() {
		return x;
	}

}
