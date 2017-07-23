package loukoum.tgm.parts;

import java.util.HashMap;

public abstract class Control {

	private NeuralNetwork net;
	private boolean playing;

	private HashMap<Integer, Integer> moves;
	private float diversityFactor;

	public void playGame(NeuralNetwork net) {
		moves = new HashMap<Integer, Integer>();
		this.net = net;
		playing = true;
		startGame();

		synchronized(this) {
			while(playing) {
				try {
					this.wait();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		calculateDiversityFactor();
	}

	public int getMove(float[] vision) {
		float[] out = net.feed(vision);
		
		float highest = out[0];
		int hp = 0;
		for (int i = 1; i < out.length; ++i) {
			if (out[i] > highest) {
				highest = out[i];
				hp = i;
			}
		}

		if (moves == null) {
			moves = new HashMap<Integer, Integer>();
		}
		
		if (moves.containsKey(hp)) {
			moves.put(hp, moves.get(hp) + 1);
		}
		
		else {
			moves.put(hp, 1);
		}

		return hp;
	}

	private void calculateDiversityFactor() {
		float per[] = new float[moves.size()];
		int sum = 0;

		for (int m : moves.values()) {
			sum += m;
		}

		int index = 0;
		for (int m : moves.values()) {
			per[index] = (float) ((float) m / (float) sum);
		}

		diversityFactor = diversion(per);
	}

	private float diversion(float movePers[]) {
		if (movePers.length == 0) {
			return 1.0f;
		}

		float div = 1.0f * movePers.length;

		for (float p : movePers) {
			div -= Math.abs((float) (1.0f / (float) movePers.length) - p);
		}

		return div;
	}

	public void stopPlaying() {
		playing = false;

		synchronized(this) {
			notifyAll();
		}
	}

	public boolean isOn() {
		return playing;
	}

	public float getDiversityFactor() {
		return diversityFactor;
	}

	protected abstract void startGame();
	public abstract float getScore();
}
