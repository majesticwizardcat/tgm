package loukoum.tgm.parts;

import static loukoum.tgm.tools.Tools.random;
import static loukoum.tgm.tools.Tools.saveObjectFile;

import java.io.Serializable;
import java.util.ArrayList;

public class Brain implements Serializable {

	public static final int MAX_LAYER_DIFFERENCE = 250;
	public static final int TRAIN_POPULATION = 1000;

	public static final float CHOOSE_LAX = 0.20f;
	
	private class BrainCell implements Serializable {
		
		private NeuralNetwork network;
		private float fitness;
		private int games;
		private int curGen;
		
		public BrainCell(int layers[]) {
			this.network = new NeuralNetwork(layers);
			this.fitness = 0;
			this.games = 0;
			this.curGen = 0;
		}
		
		public BrainCell(NeuralNetwork p0, NeuralNetwork p1) { 
			this.network = new NeuralNetwork(p0, p1);
			this.fitness = 0;
			this.games = 0;
			this.curGen = 0;
		}
		
		public void evaluate(float fitness, int sessionsPerGen) {
			games++;

			this.fitness =
					this.fitness * ((float)((float)sessionsPerGen - 1) / (float)sessionsPerGen) 
					+ fitness * ((float)(1.0f / (float)sessionsPerGen));
		}	
		
		public float getAVG() {
			return fitness;
		}
	}

	private String name;

	private int visionSize;
	private int moves;

	private BrainCell population[];
	
	private int generation;
	private int gamesTrained;
	private int totalGames;
	private int trainingSessions;
	private float score;
	private float bestScore;
	private float bestFitness;
	private float chooseLax;
	private float fitness;
	private float fitnessInterpolation;

	private boolean isTraining;
	private boolean doneTraining;
	private boolean isPlaying;

	public Brain(String name, int visionSize, int moves, int sessions) {
		this.name = name;
		this.visionSize = visionSize;
		this.moves = moves;
		this.trainingSessions = sessions;
		this.chooseLax = CHOOSE_LAX;
		
		this.fitnessInterpolation = (float) (((float) (1.0f / 
				((float) TRAIN_POPULATION * sessions))));

		isTraining = false;
		doneTraining = true;
		isPlaying = false;

		createPopulation();
	}

	private void createPopulation() {
		population = new BrainCell[TRAIN_POPULATION];

		int midLayers[] = createMidLayers(visionSize, moves);
		int layers[] = new int[midLayers.length + 2];

		layers[0] = visionSize;
		for (int i = 0; i < midLayers.length; ++i) {
			layers[i + 1] = midLayers[i];
		}
		layers[layers.length - 1] = moves;

		System.out.print("Created networks with: [  ");
		for (int l : layers) {
			System.out.print(l + "  ");
		}
		System.out.print("] layers");

		System.out.println();

		for (int i = 0; i < population.length; ++i) {
			population[i] = new BrainCell(layers);
		}
	}

	private int[] createMidLayers(int visionSize, int moves) {
		ArrayList<Integer> midlayers = new ArrayList<Integer>();
		int smaller = visionSize > moves ? moves : visionSize;
		int larger = visionSize < moves ? moves : visionSize;

		do {
			midlayers.add((larger / smaller) + smaller);
			larger = midlayers.get(midlayers.size() - 1);

		} while(Math.abs(midlayers.get(midlayers.size() - 1) - smaller) > MAX_LAYER_DIFFERENCE);

		int returnArray[] = new int[midlayers.size()];

		int midindex = visionSize > moves ? 0 : midlayers.size() - 1;
		int midindexincr = visionSize > moves ? 1 : -1;

		for (int i = 0; i < midlayers.size(); ++i) {
			returnArray[i] = midlayers.get(midindex);
			midindex += midindexincr;
		}

		return returnArray;
	}

	public void train(Control control) {
		isTraining = true;
		doneTraining = false;

		while(isTraining) {
			play(population[gamesTrained], control);
			
			if (population[gamesTrained].games >=
					(population[gamesTrained].curGen + 1) * trainingSessions) {
				gamesTrained++;
			}

			if (gamesTrained >= population.length) {
				evolvePopulation(population);
				generation++;
				
				gamesTrained = 0;

				if (generation % 100 == 0) {
					save();
				}
			}
		}

		doneTraining = true;

		synchronized(this) {
			try {
				notifyAll();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		save();
	}

	public void stop() {
		isTraining = false;

		synchronized(this) {
			while (isPlaying || !doneTraining) {
				try {
					wait();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void shufflePopulation(BrainCell population[]) {
		for (int i = 0; i < population.length; ++i) {
			int r0 = (int) random(population.length);
			int r1 = -1;
			
			do {
				r1 = (int) random(population.length);
			} while (r1 == r0);
			
			BrainCell temp = population[r0];
			population[r0] = population[r1];
			population[r1] = temp;
		}
	}

	private void evolvePopulation(BrainCell population[]) {
		shufflePopulation(population);

		for (BrainCell bc : population) {
			bc.curGen++;
		}

		BrainCell newPopulation[] = new BrainCell[population.length];

		float highestFitness = population[0].getAVG();
		float avgSum = 0;

		for (int i = 0; i < population.length; ++i) {
			if (population[i].getAVG() > highestFitness) {
				highestFitness = population[i].getAVG();
			}
			
			avgSum += population[i].getAVG();
		}
		
		float avg = avgSum / population.length;
		
		System.out.println("Best AVG: " + highestFitness);

		int npindex = 0;

		for (int i = npindex; i < population.length; ++i) {
			if (random() <= population[i].getAVG() / highestFitness) {
				newPopulation[npindex] = population[i];
				npindex++;
			}
		}
		
		System.out.println("Kept: " + npindex);

		NeuralNetwork p0;
		NeuralNetwork p1;
		for (int i = npindex; i < population.length; ++i) {
			p0 = null;
			p1 = null;

			while (p0 == null) {
				int p = (int) random(population.length);
				if (random() <= population[p].getAVG() / highestFitness) {
					p0 = population[p].network;
				}
			}

			while (p1 == null) {
				int p = (int) random(population.length);
				if (p0 != population[p].network &&
					random() <= population[p].getAVG() / highestFitness + chooseLax) {
					p1 = population[p].network;
				}
			}

			newPopulation[i] = new BrainCell(p0, p1);
		}

		this.population = newPopulation;
	}

	private BrainCell findBest(BrainCell population[]) {
		BrainCell best = population[0];
		float bestFitness = best.getAVG();

		for (int i = 1;i < population.length; ++i) {
			if (population[i].getAVG() > bestFitness) {
				best = population[i];
				bestFitness = population[i].getAVG();
			}
		}

		return best;
	}

	public void evaluate(BrainCell cell, float score, float fitness) {
		cell.evaluate(fitness, trainingSessions);

		if (score > bestScore) {
			bestScore = score;
		}

		if (fitness > bestFitness) {
			bestFitness = fitness;
		}

		this.score = this.score * (1.0f - fitnessInterpolation)
				+ score * fitnessInterpolation;
		this.fitness = this.fitness * (1.0f - fitnessInterpolation)
				+ fitness * fitnessInterpolation;
		totalGames++;
	}

	public void play(Control control) {
		BrainCell b = findBest(population);
		play(b, control);

		synchronized(this) {
			try {
				notifyAll();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {
		saveObjectFile(this, name);
	}

	private float play(BrainCell cell, Control control) {
		isPlaying = true;
		control.playGame(cell.network);

		float fitness = fitness(control.getScore(), control.getDiversityFactor());
		evaluate(cell, control.getScore(), fitness);

		isPlaying = false;
		return fitness;
	}
	
	public void reset() {
		isTraining = false;
		isPlaying = false;
		doneTraining = true;
	}

	private float fitness(float score, float divFactor) {
		return  (2 * score * (1 + divFactor)) / 3;
	}

	public String getName() {
		return name;
	}

	public int getGeneration() {
		return generation;
	}

	public float getGenerationTrainPer() {
		return (float) (gamesTrained) / (float) population.length;
	}

	public float getBestScore() {
		return bestScore;
	}

	public float getAVGScore() {
		return score;
	}

	public float getBestFitness() {
		return bestFitness;
	}

	public float getAVGFitness() {
		return fitness;
	}
	
	public int getTotalGames() {
		return totalGames;
	}

}
