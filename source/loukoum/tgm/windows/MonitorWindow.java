package loukoum.tgm.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import loukoum.tgm.TGM;
import loukoum.tgm.parts.Brain;

public class MonitorWindow extends JFrame {

	public static final int WIDTH = 400;
	public static final int HEIGHT = 300;

	public static final int PADDING = 10;
	public static final int SCORE_X = PADDING;
	public static final int SCORE_Y = PADDING;
	public static final int SCORE_W = WIDTH - 2 * PADDING;
	public static final int SCORE_H = 30;
	public static final int FITNESS_X = PADDING;
	public static final int FITNESS_Y = SCORE_Y + SCORE_H + PADDING;
	public static final int FITNESS_W = WIDTH - 2 * PADDING;
	public static final int FITNESS_H = 30;
	public static final int COMPLETION_X = PADDING;
	public static final int COMPLETION_Y = FITNESS_Y + FITNESS_H + PADDING;
	public static final int COMPLETION_W = WIDTH - 2 * PADDING;
	public static final int COMPLETION_H = 30;
	public static final int SPEED_MUL_X = PADDING;
	public static final int SPEED_MUL_Y = COMPLETION_Y + COMPLETION_H + PADDING;
	public static final int SPEED_MUL_W = WIDTH / 3 - 2 * PADDING;
	public static final int SPEED_MUL_H = 30;
	public static final int SPEED_MUL_FIELD_X = SPEED_MUL_X + SPEED_MUL_W + PADDING;
	public static final int SPEED_MUL_FIELD_Y = SPEED_MUL_Y;
	public static final int SPEED_MUL_FIELD_W = WIDTH / 3 - 2 * PADDING;
	public static final int SPEED_MUL_FIELD_H = 30;
	public static final int SPEED_MUL_BUTTON_X = PADDING + SPEED_MUL_FIELD_W + SPEED_MUL_FIELD_X;
	public static final int SPEED_MUL_BUTTON_Y = SPEED_MUL_Y;
	public static final int SPEED_MUL_BUTTON_W = WIDTH / 3 - 2 * PADDING;
	public static final int SPEED_MUL_BUTTON_H = 30;
	public static final int CONTROLS_X = PADDING;
	public static final int CONTROLS_Y = SPEED_MUL_Y + SPEED_MUL_H + PADDING;
	public static final int BUTTON_W = 80;
	public static final int BUTTON_H = 30;
	public static final int PLAY_X = CONTROLS_X;
	public static final int PLAY_Y = CONTROLS_Y;
	public static final int TRAIN_X = PLAY_X + BUTTON_W + PADDING;
	public static final int TRAIN_Y = CONTROLS_Y;
	public static final int STOP_X = TRAIN_X + BUTTON_W + PADDING;
	public static final int STOP_Y = CONTROLS_Y;

	private Brain brain;
	private TGM tgm;

	private JLabel score;
	private JLabel fitness;
	private JLabel completion;
	private JLabel speedMult;
	
	private JTextField speedMultField;

	public MonitorWindow(Brain brain, TGM tgm) {
		this.brain = brain;
		this.tgm = tgm;

		createWindow();
	}

	private void createWindow() {
		setTitle(brain.getName() + " " + TGM.VERSION);
		setLayout(new BorderLayout());
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setSize(WIDTH, HEIGHT);
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		score = new JLabel();
		score.setLocation(SCORE_X, SCORE_Y);
		score.setSize(SCORE_W, SCORE_H);

		fitness = new JLabel();
		fitness.setLocation(FITNESS_X, FITNESS_Y);
		fitness.setSize(FITNESS_W, FITNESS_H);

		completion = new JLabel();
		completion.setLocation(COMPLETION_X, COMPLETION_Y);
		completion.setSize(COMPLETION_W, COMPLETION_H);

		JButton play = new JButton("Play");
		play.setLocation(PLAY_X, PLAY_Y);
		play.setSize(BUTTON_W, BUTTON_H);

		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tgm.play();
			}
		});

		JButton train = new JButton("Train");
		train.setLocation(TRAIN_X, TRAIN_Y);
		train.setSize(BUTTON_W, BUTTON_H);

		train.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tgm.train();
			}
		});

		JButton stop = new JButton("Stop");
		stop.setLocation(STOP_X, STOP_Y);
		stop.setSize(BUTTON_W, BUTTON_H);

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tgm.stopTraining();
			}
		});
		
		speedMult = new JLabel();
		speedMult.setLocation(SPEED_MUL_X, SPEED_MUL_Y);
		speedMult.setSize(SPEED_MUL_W, SPEED_MUL_H);
		
		speedMultField = new JTextField();
		speedMultField.setLocation(SPEED_MUL_FIELD_X, SPEED_MUL_Y);
		speedMultField.setSize(SPEED_MUL_FIELD_W, SPEED_MUL_H);

		JButton setSpeed = new JButton("Set");
		setSpeed.setLocation(SPEED_MUL_BUTTON_X, SPEED_MUL_BUTTON_Y);
		setSpeed.setSize(SPEED_MUL_BUTTON_W, SPEED_MUL_BUTTON_H);

		setSpeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tgm.changeMultSpeed(Integer.parseInt(speedMultField.getText()));
			}
		});

		panel.add(stop);
		panel.add(score);
		panel.add(fitness);
		panel.add(completion);
		panel.add(train);
		panel.add(play);
		panel.add(speedMult);
		panel.add(speedMultField);
		panel.add(setSpeed);

		add(panel);

		Thread t = new Thread() {
			public void run() {
				while (true) {
					updateWindow();
					try {
						Thread.sleep(1000);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		};

		t.start();

	}

	private void updateWindow() {
		score.setText("Generation: " + brain.getGeneration() + " Best Score: " + brain.getBestScore() +
				" AVG: " + brain.getAVGScore());
		fitness.setText("Best Fitness: " + brain.getBestFitness() + " AVG: " + brain.getAVGFitness());
		completion.setText("Generation train: " + brain.getGenerationTrainPer());
		speedMult.setText("Speed Multiplier: " + tgm.getSpeedMult());

		revalidate();
		repaint();
	}

}
