package loukoum.tgm;

import loukoum.tgm.parts.Brain;
import loukoum.tgm.parts.Control;
import loukoum.tgm.windows.MonitorWindow; 

import static loukoum.tgm.tools.Tools.loadObjectFile;

public class TGM {

	public static final String VERSION = "0.0.6";

	private Brain brain;
	private Control control;

	private int speedMult;

	public TGM(String name, int visionSize, int moves, Control control, int sessions) {

		this.brain = loadBrain(name);
		this.control = control;
		speedMult = 1;

		if (brain == null) {
			brain = new Brain(name, visionSize, moves, sessions);
			brain.save();
		}
		
		brain.reset();

		new MonitorWindow(brain, this);
	}

	private Brain loadBrain(String name) {
		return (Brain) loadObjectFile(name);
	}

	public void train() {
		Thread t = new Thread() {

			public void run() {
				brain.stop();
				brain.train(control);
			}
		};
		t.start();
	}

	public void stopTraining() {
		Thread t = new Thread() {

			public void run() {
				brain.stop();
			}
		};
		t.start();
	}

	public void play() {
		Thread t = new Thread() {

			public void run() {
				brain.stop();
				brain.play(control);
				brain.save();
			}
		};
		t.start();
	}
	
	public int getSpeedMult() {
		return speedMult;
	}
	
	public void changeMultSpeed(int speed) {
		speedMult = speed;
	}
		
}
