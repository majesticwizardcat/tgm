package loukoum.balljump;

import java.awt.Graphics;
import java.awt.Color;

import static loukoum.balljump.World.UNIT;
import static loukoum.balljump.World.RGEN;

public class Platform {

	public static final float HEIGHT = 0.25f * UNIT;
	public static final float BONUS_PROB = 0.05f;
	public static final float COLOR_CHANGE_TIME = 0.5f;

	protected float x;

	private float y;
	private float width;
	private float dropSpeed;

	private boolean hasBonus;
	private Color bonusColor;
	private float colorChangeTimer;

	public Platform(float x, float y, float width, float dropSpeed) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.dropSpeed = dropSpeed;

		if (BONUS_PROB >= 1 - RGEN.nextFloat()) {
			hasBonus = true;
			bonusColor = randomColor();
		}
		else {
			hasBonus = false;
		}
	}

	public void update(float delta) {
		if (hasBonus) {
			colorChangeTimer += delta;
			if (colorChangeTimer >= COLOR_CHANGE_TIME) {
				bonusColor = randomColor();
				colorChangeTimer -= COLOR_CHANGE_TIME;
			}
		}

		y += dropSpeed * delta;
	}

	public void render(Graphics g) {
		if (hasBonus) {
			g.setColor(bonusColor);
		}
		else {
			g.setColor(Color.DARK_GRAY);
		}

		g.fillRect((int) x, (int) y, (int) width, (int) HEIGHT);
	}

	private Color randomColor() {
		return new Color(RGEN.nextFloat(), RGEN.nextFloat(), RGEN.nextFloat());
	}

	public boolean hasBonus() {
		return hasBonus;
	}

	public void pickUpBonus() {
		hasBonus = false;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getDropSpeed() {
		return dropSpeed;
	}

}
