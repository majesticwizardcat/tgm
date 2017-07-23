package loukoum.balljump;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.event.KeyEvent;

public class Renderer implements Runnable {

	public static final int WIDTH = 500;
	public static final int HEIGHT = 600;

	private JFrame frame;
	private Canvas canvas;
	
	private World world;

	public Renderer(World world) {
		this.world = world;

		frame = new JFrame();
		canvas = new Canvas();

		frame.setTitle("Ball jump");
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setSize(WIDTH, HEIGHT);
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		canvas.setSize(WIDTH, HEIGHT);
		canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		frame.addKeyListener(new Input());

		frame.add(canvas);
		canvas.createBufferStrategy(2);

		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		long old = System.currentTimeMillis();
		long now;
		float delta = 0;
		float step = 1f / 60f;

		while (true) {
			now = System.currentTimeMillis();
			delta += ((float) (now - old) / 1000.0f);
			old = now;

			if (BallJump.tgm.getSpeedMult() != 1) {
				update(step);
				delta = 0;
				continue;
			}
			
			if (delta >= step) {			
				update(step);
				delta -= step;
			}

			render();
		}
	}

	private void update(float delta) {
		if (Input.isKeyDown(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}

		world.update(delta);
	}

	private void render() {
		BufferStrategy bs = canvas.getBufferStrategy();

		Graphics g = bs.getDrawGraphics();

		// Clear screen
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		world.render(g);

		bs.show();
	}

}
