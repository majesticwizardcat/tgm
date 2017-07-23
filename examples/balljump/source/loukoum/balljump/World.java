package loukoum.balljump;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

public class World {

	public static final float UNIT = 100f; // 100p / unit
	public static final float GRAVITY = 13.5f * UNIT;
	public static final float MAX_PLATFORM_WIDTH = 2f * UNIT;
	public static final float MIN_PLATFORM_WIDTH = 1f * UNIT;
	public static final float CENTER_X = (Renderer.WIDTH - MAX_PLATFORM_WIDTH) / 2;
	public static final float BOT_Y = Renderer.HEIGHT - Platform.HEIGHT - 10;
	public static final float Y_DIFF = 1.25f * UNIT;
	public static final float INIT_BALL_X = (Renderer.WIDTH - Ball.SIZE) / 2;
	public static final float INIT_BALL_Y = BOT_Y - Ball.SIZE - 50;
	public static final float DIF_FACTOR = 0.01f;
	public static final float INIT_PLATFORM_DROP_SPEED = 1.5f * UNIT;
	public static final float MAX_DROP_SPEED = 2.75f * UNIT;
	public static final float INC_DIF_TIMER = 1.25f;
	public static final float SLIDE_SPEED = 2.5f * UNIT;

	public static final int INIT_PLATFORMS = 10;

	public static Random RGEN = new Random();

	private Ball ball;
	private ArrayList<Platform> platforms;
	private Wall walls[];

	private float currentPlatformWidth;
	private float platformDropSpeed;
	private float difTimer;
	private float diff;

	private String score;

	private boolean canRestart;

	public static final int TGM_INS = 10; // 45 for all
	public static final int TGM_MOVES = 3;
	private TGMControl tgmcontrol;
	private float[] tgmvision = new float[TGM_INS];
	private boolean shouldRestart;
	private static final boolean NOT_USE_RANDOM = false;

	public void setControl(TGMControl control) {
		tgmcontrol = control;
	}

	public World() {
		createLevel();
	}

	private void createLevel() {
		if (NOT_USE_RANDOM) {
			RGEN = new Random(0);
		}

		ball = new Ball(INIT_BALL_X, INIT_BALL_Y);
		platforms = new ArrayList<Platform>();

		currentPlatformWidth = MAX_PLATFORM_WIDTH;
		platformDropSpeed = INIT_PLATFORM_DROP_SPEED;
		difTimer = 0;
		diff = 0;

		walls = new Wall[2];
		walls[0] = new Wall(0);
		walls[1] = new Wall(Renderer.WIDTH - Wall.WIDTH);
		
		// Add initial platforms
		
		platforms.add(new Platform(CENTER_X, BOT_Y, MAX_PLATFORM_WIDTH, platformDropSpeed));

		for (int i = 1; i < INIT_PLATFORMS; ++i) {
			platforms.add(new Platform(getRandomX(), BOT_Y - i * Y_DIFF, 
						MAX_PLATFORM_WIDTH, platformDropSpeed));
		}
	}

	public void startGame() {
		shouldRestart = true;
	}

	private void updateTGMVision() {
		tgmvision[0] = clamp(ball.getX() / (float) Renderer.WIDTH);
		tgmvision[1] = clamp(ball.getY() / (float) Renderer.HEIGHT);
		tgmvision[2] = ball.getSpeedX() > 0 ? 1.0f : -1.0f;
		tgmvision[3] = ball.getSpeedY() > 0 ? 1.0f : -1.0f;
		tgmvision[4] = ball.isAbleToJump() ? 1.0f : -1.0f;

		Platform closest = getClosestPlatform();
		tgmvision[5] = clamp(closest.getX() / (float) Renderer.WIDTH);
		tgmvision[6] = clamp(closest.getY() / (float) Renderer.HEIGHT);
		tgmvision[7] = closest.getWidth() / MAX_PLATFORM_WIDTH;
		tgmvision[8] = closest.hasBonus() ? 1.0f : -1.0f;

		/*
		for (int i = 0; i < 10; ++i) {
			tgmvision[5 + (i * 4)] = platforms.get(i).getX() / (float) Renderer.WIDTH;
			tgmvision[5 + (i * 4 + 1)] = platforms.get(i).getY() / (float) Renderer.HEIGHT;
			tgmvision[5 + (i * 4 + 2)] = platforms.get(i).getWidth() / (float) MAX_PLATFORM_WIDTH;
			tgmvision[5 + (i * 4 + 3)] = platforms.get(i).hasBonus() ? 1.0f : -1.0f;
		}
		*/
	}

	public void update(float delta) {
		if ((shouldRestart || Input.isKeyDown(KeyEvent.VK_R)) && canRestart) {
			createLevel();
			canRestart = false;
			shouldRestart = false;
		}

		else if (Input.isKeyUp(KeyEvent.VK_R)) {
			canRestart = true;
		}

		if (tgmcontrol.isOn()) {
			updateTGMVision();
			ball.makeMove(tgmcontrol.getMove(tgmvision));
		}

		ball.update(delta);

		if (ball.hasLost()) {
			tgmcontrol.stopPlaying();
		}

		if (ball.shouldStart()) {
			for (Platform p : platforms) {
				p.update(delta);
			}

			difTimer += delta;
			updateLevel();
		}

		checkCollisions(delta);
	}

	public void render(Graphics g) {

		for (Platform p : platforms) {
			p.render(g);
		}

		for (Wall w : walls) {
			w.render(g);
		}

		score = "" + ball.getScore();
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(new Font(null, Font.BOLD, 72));
		g.drawString(score, (Renderer.WIDTH - 2 * 72 * score.length() / 3) / 2, 70);

		ball.render(g);
	}

	private void checkCollisions(float delta) {
		if (ball.getSpeedY() > 0) {
			for (Platform p : platforms) {
				if (pointInRect(ball.getX() +  Ball.SIZE / 2, 
						ball.getY() + Ball.SIZE + ball.getSpeedY() * delta,
						p.getX(), p.getY(),
						p.getWidth(), Platform.HEIGHT)) {

					ball.setSpeedY(0);
					ball.setY(p.getY() - Ball.SIZE);
					ball.canJump();

					if (p.hasBonus()) {
						ball.bonus();
						p.pickUpBonus();
					}

					break;
				}
			}

		}

		if (ball.getSpeedX() > 0) {
			if (ball.getX() + Ball.SIZE + ball.getSpeedX() * delta >= walls[1].getX()) {
				ball.setSpeedX(-ball.getSpeedX());
				ball.setX(walls[1].getX() - Ball.SIZE);
			}
		}

		else {
			if (ball.getX() + ball.getSpeedX() * delta <= Wall.WIDTH) {
				ball.setSpeedX(-ball.getSpeedX());
				ball.setX(walls[0].getX() + Wall.WIDTH);
			}
		}

		for (Platform p : platforms) {
			if (p instanceof MovingPlatform) {
				if (((MovingPlatform) (p)).getSlideSpeed() > 0
					&& p.getX() + p.getWidth() + ((MovingPlatform) (p)).getSlideSpeed() * delta
					>= walls[1].getX()) {
					((MovingPlatform) (p)).reverse();
				}

				else if (p.getX() + ((MovingPlatform) (p)).getSlideSpeed() * delta
					<= Wall.WIDTH) {
					((MovingPlatform) (p)).reverse();
				}
			}
		}
	}

	private void updateLevel() {
		if (platforms.get(0).getY() > Renderer.HEIGHT + Platform.HEIGHT) {
			platforms.remove(0);
			
			if (diff >= 1 - RGEN.nextFloat()) {
				platforms.add(new MovingPlatform(getRandomX(),
						platforms.get(platforms.size() - 1).getY() - Y_DIFF,
						currentPlatformWidth, platformDropSpeed, 
						RGEN.nextBoolean() ? RGEN.nextFloat() * diff * SLIDE_SPEED :
						-RGEN.nextFloat() * diff * SLIDE_SPEED));
			}
			else {
				platforms.add(new Platform(getRandomX(),
						platforms.get(platforms.size() - 1).getY() - Y_DIFF,
						currentPlatformWidth, platformDropSpeed));
			}

			ball.point();
		}

		if (difTimer >= INC_DIF_TIMER) {
			if (RGEN.nextBoolean()) {
				currentPlatformWidth = currentPlatformWidth <= MIN_PLATFORM_WIDTH ? MIN_PLATFORM_WIDTH : 
					currentPlatformWidth - currentPlatformWidth * DIF_FACTOR;
			} else {
				platformDropSpeed = platformDropSpeed >= MAX_DROP_SPEED ? MAX_DROP_SPEED :
					platformDropSpeed + platformDropSpeed * DIF_FACTOR;
			}

			difTimer -= INC_DIF_TIMER;
			diff = diff >= 1.0f ? 1.0f : diff + 0.01f;
		}

	}

	private boolean pointInRect(float px, float py, float rx, float ry, float rw, float rh) {
		return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
	}
	
	private float clamp(float value) {
		if (value < -1.0f) {
			return -1.0f;
		}
		if (value > 1.0f) {
			return 1.0f;
		}
		
		return value;
	}

	private float getRandomX() {
		return Wall.WIDTH + RGEN.nextFloat() * 
			(Renderer.WIDTH - currentPlatformWidth - 2 * Wall.WIDTH);
	}
	
	private float distance(Ball ball, Platform p) {
		return (float) Math.sqrt(Math.pow(ball.getX() - p.getX(), 2) + 
				Math.pow(ball.getY() - p.getY(), 2));
	}

	private Platform getClosestPlatform() {
		float leastDist = Float.MAX_VALUE;
		Platform clst = null;
		
		for (Platform p : platforms) {
			float dist = distance(ball, p);
			if (dist < leastDist) {
				leastDist = dist;
				clst = p;
			}
		}
		
		return clst;
	}

	public float getScore() {
		return ball.getScore();
	}
}
