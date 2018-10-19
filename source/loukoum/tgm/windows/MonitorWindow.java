package loukoum.tgm.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.text.NumberFormatter;

import loukoum.tgm.TGM;
import loukoum.tgm.parts.Brain;

public class MonitorWindow extends JFrame {
	private static final long serialVersionUID = 2006075407585592740L;
	private static final int FRAME_WIDTH = 420;
	private static final int FRAME_HEIGHT = 300;
	private static final int FONT_SIZE = 16;

	private Brain brain;
	private TGM tgm;
	private JLabel generationLabel;
	private JLabel bestScoreLabel;
	private JLabel AVGLabel;
	private JLabel bestFitnessLabel;
	private JLabel AVGFitnessLabel;
	private JLabel completionLabel;
	private JLabel speedMultLabel;
	private JFormattedTextField speedMultField;

	public MonitorWindow(Brain brain, TGM tgm) {
		this.brain = brain;
		this.tgm = tgm;
		setUpUIManager();
		createWindow();
		setFontToAllComponents(getContentPane());
		createRefreshTimer();
	}

	private void setFontToAllComponents(Container c) {
		for (Component comp : c.getComponents()) {
			if (comp instanceof JPanel)
				setFontToAllComponents((Container) comp);
			else
				comp.setFont(new Font("Tahoma", 0, FONT_SIZE));
		}
	}

	private void createRefreshTimer() {
		// javax.swing.Timer
		Timer t = new Timer(1000, e -> updateWindow());
		t.start();
	}

	private void createWindow() {
		setTitle(brain.getName() + " " + TGM.VERSION);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createMainPanel(), BorderLayout.PAGE_START);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setVisible(true);
	}

	private JPanel createMainPanel() {
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Some insets
		main.add(createFieldsPanel());
		completionLabel = new JLabel("Generation train: " + brain.getGenerationTrainPer());
		completionLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		main.add(completionLabel);
		main.add(createSpeedMultPanel());
		main.add(createActionsPanel());
		return main;
	}

	private JPanel createFieldsPanel() {
		JPanel p = new JPanel(new GridLayout(0, 2));
		// Panel with scores
		JPanel scorePanel = new JPanel();
		scorePanel.setBorder(BorderFactory.createTitledBorder("Score"));
		scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
		generationLabel = new JLabel("Generation: " + brain.getGeneration());
		bestScoreLabel = new JLabel("Best Score: " + brain.getBestScore());
		AVGLabel = new JLabel("AVG: " + brain.getAVGScore());
		scorePanel.add(generationLabel);
		scorePanel.add(bestScoreLabel);
		scorePanel.add(AVGLabel);
		p.add(scorePanel);

		// Panel with fitness
		JPanel fitnessPanel = new JPanel();
		fitnessPanel.setLayout(new BoxLayout(fitnessPanel, BoxLayout.Y_AXIS));
		fitnessPanel.setBorder(BorderFactory.createTitledBorder("Fitness"));
		bestFitnessLabel = new JLabel("Best Fitness: " + brain.getBestFitness());
		AVGFitnessLabel = new JLabel("AVG: " + brain.getAVGFitness());
		fitnessPanel.add(bestFitnessLabel);
		fitnessPanel.add(AVGFitnessLabel);
		p.add(fitnessPanel);
		return p;
	}

	private JPanel createSpeedMultPanel() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		speedMultLabel = new JLabel("Speed Multiplier: " + tgm.getSpeedMult());
		p.add(speedMultLabel);

		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		speedMultField = new JFormattedTextField(formatter);
		speedMultField.setColumns(10);
		speedMultField.setText("1");
		p.add(speedMultField);

		JButton setSpeedBtn = new JButton("Set");
		setSpeedBtn.addActionListener(e -> {
			tgm.changeMultSpeed(Integer.parseInt(speedMultField.getText()));
			speedMultLabel.setText("Speed Multiplier: " + tgm.getSpeedMult());
		});
		p.add(setSpeedBtn);
		return p;
	}

	private JPanel createActionsPanel() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JButton playBtn = new JButton("Play");
		playBtn.addActionListener(e -> tgm.play());
		p.add(playBtn);

		JButton trainBtn = new JButton("Train");
		trainBtn.addActionListener(e -> tgm.train());
		p.add(trainBtn);

		JButton stopBtn = new JButton("Stop");
		stopBtn.addActionListener(e -> tgm.stopTraining());
		p.add(stopBtn);
		return p;
	}

	private void updateWindow() {
		speedMultLabel.setText("Speed Multiplier: " + tgm.getSpeedMult());
		generationLabel.setText("Generation: " + brain.getGeneration());
		bestScoreLabel.setText("Best Score: " + brain.getBestScore());
		AVGLabel.setText("AVG: " + brain.getAVGScore());
		bestFitnessLabel.setText("Best Fitness: " + brain.getBestFitness());
		AVGFitnessLabel.setText("AVG: " + brain.getAVGFitness());
	}

	private void setUpUIManager() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
