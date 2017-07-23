package loukoum.tgm.parts;

import static loukoum.tgm.tools.Tools.random;

import java.io.Serializable;

public class NeuralNetwork implements Serializable {

	public static final int BIAS_NEURONS = 0;
	public static final float STARTING_WEIGHTS = 1f;
	public static final float BIAS_VALUE = 1.0f;
	public static final float MUTATION_CHANCE = 0.1f;

	private class Neuron implements Serializable {
		public float data;
		public float weights[];

		public Neuron(int weights) {
			this.weights = new float[weights];
			
			for (int i = 0; i < weights; ++i) {
				this.weights[i] = random(-STARTING_WEIGHTS, STARTING_WEIGHTS);
			}
		}

		public void setWeights(float weight) {
			for (int i = 0; i < weights.length; ++i) {
				weights[i] = weight;
			}
		}

		public void feed(Layer prevLayer, int index) {
			float sum = 0.0f;

			for (int i = 0; i < prevLayer.size; ++i) {
				sum += prevLayer.neurons[i].data * prevLayer.neurons[i].weights[index];
			}

			data = activation(sum);
		}

		public String toString() {
			String toReturn = "\n\t\tData: " + data + "\n\t\t";

			for (int i = 0; i < weights.length; ++i) {
				toReturn += "Weight of " + i + ": " + weights[i] + "\n\t\t";
			}

			return toReturn;
		}
	}

	private class Layer implements Serializable {
		public int size;
		public int biases;
		public Neuron neurons[];
		
		public Layer(int neurons, int nextLayerSize, int biases) {
			this.biases = biases;
			this.size = neurons + biases;
			this.neurons = new Neuron[size];
		
			for (int i = 0; i < size; ++i) {
				this.neurons[i] = new Neuron(nextLayerSize);
			}
		}

		public Layer(int neurons) {
			this(neurons, 0, 0);
		}

		public void setNeuronWeights(float weight) {
			for (Neuron n : neurons) {
				n.setWeights(weight);
			}
		}

		public void latch(float inputs[]) {
			// Latch neurons
			for (int i = 0; i < size - biases; ++i) {
				neurons[i].data = inputs[i];
			}

			// Set Bias values
			for (int i = size - biases; i < size; ++i) {
				neurons[i].data = BIAS_VALUE;
			}
		}

		public void feed(Layer prevLayer) {
			// Feed neuron
			for (int i = 0; i < size - biases; ++i) {
				neurons[i].feed(prevLayer, i);
			}

			// Set Bias values
			for (int i = size - biases; i < size; ++i) {
				neurons[i].data = BIAS_VALUE;
			}
		}

		public String toString() {
			String toReturn = "\n\t";

			for (int i = 0; i < neurons.length; ++i) {
				toReturn += "Neuron " + i + ": " + neurons[i].toString() + "\n\t";
			}

			return toReturn;
		}
	}

	private int size;
	private int layerSizes[];
	private Layer layers[];
	private float outputs[];

	public NeuralNetwork(int layers[]) {
		this.size = layers.length;
		this.layerSizes = layers;
		this.layers = new Layer[size];
		
		// Create all layers except the last one
		for (int i = 0; i < size - 1; ++i) {
			if (i == size - 2) {
				this.layers[i] = new Layer(layers[i], layers[i + 1], BIAS_NEURONS);
				continue;
			}

			this.layers[i] = new Layer(layers[i], layers[i + 1] + BIAS_NEURONS, BIAS_NEURONS);
		}
		
		// Create the output layer
		this.layers[size - 1] = new Layer(layers[size - 1]);

		this.outputs = new float[getOutputs()];
	}
	
	public NeuralNetwork(int layers[], float weightsValue) {
		this(layers);

		for (Layer l : this.layers) {
			l.setNeuronWeights(weightsValue);
		}
	}

	public NeuralNetwork(NeuralNetwork p0, NeuralNetwork p1) {
		this(p0.layerSizes);

		float p;
		for (int i = 0; i < layers.length; ++i) {
			for (int j = 0; j < layers[i].neurons.length; ++j) {
				for (int k = 0; k < layers[i].neurons[j].weights.length; ++k) {
					
					p = random();
					
					if (p > 0.5f) {
						layers[i].neurons[j].weights[k] = 
								p0.layers[i].neurons[j].weights[k];
					}
					
					else {
						layers[i].neurons[j].weights[k] = 
								p1.layers[i].neurons[j].weights[k];
					}

					p = random();

					if (p >= 1.0 - MUTATION_CHANCE) {
						layers[i].neurons[j].weights[k] = 
							mutation(layers[i].neurons[j].weights[k]);
					}
				}
			}
		}
	}

	private float mutation(float value) {
		return random(-STARTING_WEIGHTS, STARTING_WEIGHTS);
	}

	public float[] feed(float inputs[]) {
		// Latch inputs
		inputLayer().latch(inputs);

		// Feed forward
		for (int i = 1; i < size; ++i) {
			layers[i].feed(layers[i - 1]);
		}

		// Latch outputs 
		for (int i = 0; i < getOutputs(); ++i) {
			outputs[i] = outputLayer().neurons[i].data;
		}

		return outputs;
	}

	// Using the sigmoid function as activation function
	private static float activation(float x) {
		return (float) (1 / (1 + Math.exp(-x)));
	}

	public Layer inputLayer() {
		return layers[0];
	}

	public Layer outputLayer() {
		return layers[size - 1];
	}

	public int getInputs() {
		return inputLayer().size - BIAS_NEURONS;
	}

	public int getOutputs() {
		return outputLayer().size;
	}

	public int getSize() {
		return size;
	}

	public String toString() {
		String toReturn = "Inputs: " + getInputs() + " Outputs: " + getOutputs() + "\n";
		for (int i = 0; i < layers.length; ++i) {
			toReturn += "Layer " + i + ": " + layers[i].toString() + "\n";
		}

		return toReturn;
	}
}
