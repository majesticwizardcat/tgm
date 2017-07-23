package loukoum.balljump;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;

import static loukoum.balljump.World.UNIT;
import static loukoum.balljump.World.RGEN;
import static loukoum.balljump.World.GRAVITY;

public class Ball {

	public static final float SIZE = 0.5f * UNIT;
	public static final float JUMP_SPEED = 7f * UNIT;
	public static final float MOVE_SPEED = 3f * UNIT;

	public static final int BONUS_POINTS = 5;

	private float x;
	private float y;
	private float speedX;
	private float speedY;

	private boolean start;
	private boolean lost;
	private boolean canJump;

	private boolean jumpKeyDown;
	private boolean leftKeyDown;
	private boolean rightKeyDown;

	private int score;
	
	public Ball(float x, float y) {
		this.x = x;
		this.y = y; 

		canJump = true;
	}

	public void makeMove(int move) {
		if (!start && move != 2) {
			lost = true;
			start = false;
		}

		if (lost) {
			return;
		}
		
		switch(move) {
			case 0:
				if (start) {
					speedX = -MOVE_SPEED;
				}

				break;
			case 1:
				if (start) {
					speedX = MOVE_SPEED;
				}

				break;
			case 2:
				if (canJump) {
					if (!start) {
					start = true;
					speedX = RGEN.nextBoolean() ? MOVE_SPEED : -MOVE_SPEED;
					}

					speedY = -JUMP_SPEED;
					canJump = false;
					
				}
				break;
			default:
				break;
		}
	}

	public void update(float delta) {
		if (!lost && canJump && !jumpKeyDown && Input.isKeyDown(KeyEvent.VK_SPACE)) {
			if (!start) {
				start = true;
				speedX = RGEN.nextBoolean() ? MOVE_SPEED : -MOVE_SPEED;
			}

			speedY = -JUMP_SPEED;
			jumpKeyDown = true;
			canJump = false;
		}

		else if (Input.isKeyUp(KeyEvent.VK_SPACE)) {
			jumpKeyDown = false;
		}

		if (start && !leftKeyDown && Input.isKeyDown(KeyEvent.VK_A)) {
			speedX = -MOVE_SPEED;
			leftKeyDown = true;
		}

		else if (Input.isKeyUp(KeyEvent.VK_A)) {
			leftKeyDown = false;
		}

		if (start && !rightKeyDown && Input.isKeyDown(KeyEvent.VK_D)) {
			speedX = MOVE_SPEED;
			rightKeyDown = true;
		}

		else if (Input.isKeyUp(KeyEvent.VK_D)) {
			rightKeyDown = false;
		}

		if (y > Renderer.HEIGHT + SIZE) {
			lost = true;
			start = false;
		}

		speedY += GRAVITY * delta;

		x += speedX * delta;
		y += speedY * delta;
	}

	public void render(Graphics g) {
		if (canJump) {
			g.setColor(Color.MAGENTA);
		}
		else {
			g.setColor(Color.RED);
		}

		g.fillOval((int) x, (int) y, (int) SIZE, (int) SIZE);
	}

	public void point() {
		score++;
	}

	public void bonus() {
		score += BONUS_POINTS;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getSpeedX() {
		return speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void setSpeedX(float s) {
		speedX = s;
	}
	
	public void setSpeedY(float s) {
		speedY = s;
	}

	public boolean isAbleToJump() {
		return canJump;
	}

	public void canJump() {
		canJump = true;
	}

	public boolean shouldStart() {
		return start;
	}

	public boolean hasLost() {
		return lost;
	}

	public int getScore() {
		return score;
	}
}
