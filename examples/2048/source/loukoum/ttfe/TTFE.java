package loukoum.ttfe;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import loukoum.tgm.TGM;

public class TTFE {
	
	public static final int TRAINING_SESSIONS = 100;
	
	public static final Color EMPTY_TILE_COLOR = new Color(153, 217, 234);
	public static final Color BACKGROUND_COLOR = new Color(0, 162, 232);
	
	public static TGM tgm;
	private TGMControl ctrl;
	
	private Board board;
	
	private int points;

	private boolean canRestart;
	private boolean shouldRestart;
	private boolean playing;
	private boolean lost;
	private boolean moved;

	public TTFE() {
		this(4, 4);
	}
	
	public TTFE(int tilesX, int tilesY) {
		board = new Board(tilesX, tilesY);
		ctrl = new TGMControl(this);
		canRestart = true;
		shouldRestart = false;
		moved = false;
		playing = false;
		lost = false;
		points = 0;
	}
	
	public void startGame() {
		shouldRestart = true;
	}
	
	public void restartGame() {
		points = 0;
		playing = true;
		lost = false;
		shouldRestart = false;
		board.restart();
	}
	
	private void playMove() {
		float vision[] = board.getTGMVision();
		int move = ctrl.getMove(vision);
		
		switch (move) {
		case 0:
			points += board.moveUp();
			break;
		case 1:
			points += board.moveDown();
			break;
		case 2:
			points += board.moveLeft();
			break;
		case 3:
			points += board.moveRight();
			break;
		default:
			System.out.println("NO MOVE FOUND");
			break;
		}
		
		float after[] = board.getTGMVision();
		
		for (int i = 0; i < vision.length; ++i) {
			if (vision[i] != after[i]) {
				return;
			}
		}
		
		lostGame();
	}

	public void update(float delta) {

		/*
		if (Input.isKeyDown(KeyEvent.VK_R) && canRestart) {
			restartGame();
			canRestart = false;
		}
		
		if (Input.isKeyUp(KeyEvent.VK_R)) {
			canRestart = true;
		}
		
		if (Input.isKeyDown(KeyEvent.VK_A) && !moved && playing) {
			points += board.moveLeft();
			moved = true;
		}

		else if (Input.isKeyDown(KeyEvent.VK_S) && !moved && playing) {
			points += board.moveDown();
			moved = true;
		}
		
		else if (Input.isKeyDown(KeyEvent.VK_W) && !moved && playing) {
			points += board.moveUp();
			moved = true;
		}

		else if (Input.isKeyDown(KeyEvent.VK_D) && !moved && playing) {
			points += board.moveRight();
			moved = true;
		}
		
		if (Input.isKeyUp(KeyEvent.VK_A)
			&& Input.isKeyUp(KeyEvent.VK_D)
			&& Input.isKeyUp(KeyEvent.VK_S)
			&& Input.isKeyUp(KeyEvent.VK_W)) {

			moved = false;
		}
		*/
		
		if (shouldRestart) {
			restartGame();
		}
		
		if (playing) {
			playMove();
		}

		if (board.isFull() && playing) {
			lostGame();
		}

		board.update(delta);
	}

	public void render(Graphics g) {
		board.render(g);
		
		g.setColor(Color.black);
		g.setFont(new Font("TimesNewRoman", Font.PLAIN, 36));
		g.drawString("Points: " + points, 20, Renderer.WIDTH + 40);

		if (lost) {
			g.setColor(Color.MAGENTA);
			g.setFont(new Font("TimesNewRoman", Font.PLAIN, 72));
			g.drawString("GAME OVER", 0, 100);
		}
			
	}
	
	private void lostGame() {
		playing = false;
		lost =  true;
		ctrl.stopPlaying();
	}
	
	public int getPoints() {
		return points;
	}
	
	public TGMControl getControl() {
		return ctrl;
	}

	public static void main(String args[]) {
		TTFE game = new TTFE();

		tgm = new TGM("2048_TGM", 4 * 4, 4, game.getControl(), TRAINING_SESSIONS);
		new Renderer(game);
	}
}
