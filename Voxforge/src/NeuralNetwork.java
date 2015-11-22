import java.util.*;
public class NeuralNetwork
{
	static final double TRAIN_RATIO = 0.8;
	static final double INIT_LEARNING_RATE = .3;
	static final double RATE_UPDATE = .1;
	static final int MAX_EPOCHS = 10000;
	static final double MOMENTUM = .95;
	
	int[] sizes;
	Layer[] layers;
	
	// Create a neural network. n = number of layers (including input and output layers)
	// sizes = number of neurons in each layer (including input and output layers)
	public NeuralNetwork(int n, int[] sizes)
	{
		this.sizes = sizes;
		layers = new Layer[n];
		for(int i = 1; i < layers.length; i++)
		{
			layers[i] = new Layer(sizes[i], sizes[i - 1]);
		}
		
//		layers[1].neurons[0].w = new double[] {-6.06, -11.62, 10.99};
//		layers[1].neurons[1].w = new double[] {-7.19, 12.88, -13.13};
//		layers[2].neurons[0].w = new double[] {-6.56, 13.34, 13.13};
	}
	
	// Train the neural network. The number of elements in features and classes should be the same
	// and corresponds to the number of test cases. The number of elements in each feature vector should
	// be the same as the number of neurons in the input layer.
	public void train(double[][] features, int[] classes)
	{
		shuffle(features, classes);
		
		// Split data into training set and validation set
		int trainCount = (int)Math.ceil(features.length * TRAIN_RATIO);
		double[][] trainFeatures = new double[trainCount][features[0].length];
		int[] trainClasses = new int[trainCount];
		double[][] validateFeatures = new double[features.length - trainCount][features[0].length];
		int[] validateClasses = new int[features.length - trainCount];
		for(int i = 0; i < features.length; i++)
		{
			if(i < trainCount)
			{
				trainFeatures[i] = features[i];
				trainClasses[i] = classes[i];
			}
			else
			{
				validateFeatures[i - trainCount] = features[i];
				validateClasses[i - trainCount] = classes[i];
			}
		}
		
		double learningRate = INIT_LEARNING_RATE;
		int stoppingCriteria = 0;
		double validationError = 1.0 - validate(validateFeatures, validateClasses);
		double[][][] prevGradient = null;
		Random rand = new Random();
		
		// Run stochastic gradient ascent by sorting the training examples and using each one for a number of epochs
		for(int i = 0; i < MAX_EPOCHS && stoppingCriteria < 4; i++)
		{
			if(i % 100 == 0)
			{
				System.out.println("EPOCH "  + i);
			}
		
			shuffle(trainFeatures, trainClasses);
			
			for(int j = 0; j < trainFeatures.length; j++)
			{
				predict(trainFeatures[j]);
				
				double[] target = new double[sizes[layers.length - 1]];
				target[trainClasses[j]] = 1;
				
				double[][] delta = new double[layers.length][];
				double[][][] gradient = new double[layers.length][][];
				
				// Calculate gradient for output layer
				delta[layers.length - 1] = new double[sizes[layers.length - 1]];
				gradient[layers.length - 1] = new double[sizes[layers.length - 1]][sizes[layers.length - 2] + 1];
				for(int k = 0; k < sizes[layers.length - 1]; k++)
				{
					delta[layers.length - 1][k] = (layers[layers.length - 1].neurons[k].output - target[k]) * Neuron.fPrime(layers[layers.length - 1].neurons[k].activation);
					gradient[layers.length - 1][k][0] = delta[layers.length - 1][k];
					for(int l = 0; l < sizes[layers.length - 2]; l++)
					{
						gradient[layers.length - 1][k][l + 1] = delta[layers.length - 1][k] * layers[layers.length - 2].neurons[l].output;
					}
				}
				
				// Calculate gradient for other layers
				for(int k = layers.length - 2; k >= 1; k--)
				{
					delta[k] = new double[sizes[k]];
					gradient[k] = new double[sizes[k]][sizes[k - 1] + 1];
					
					for(int l = 0; l < sizes[k]; l++)
					{
						for(int m = 0; m < sizes[k + 1]; m++)
						{
							delta[k][l] += delta[k + 1][m] * layers[k + 1].neurons[m].w[l + 1];
						}
						
						delta[k][l] *= Neuron.fPrime(layers[k].neurons[l].activation);
						
						gradient[k][l][0] = delta[k][l];
						for(int m = 0; m < sizes[k - 1]; m++)
						{
							gradient[k][l][m + 1] = delta[k][l] * (k == 1 ? trainFeatures[j][m] : layers[k - 1].neurons[m].output);
						}
					}
				}
				
				// Update weights and calculate dot product with previous gradient
				double dot = 0;
				for(int k = 1; k < layers.length; k++)
				{
					for(int l = 0; l < sizes[k]; l++)
					{
						for(int m = 0; m < sizes[k - 1] + 1; m++)
						{
							if(prevGradient == null)
							{
								layers[k].neurons[l].w[m] -= learningRate * gradient[k][l][m];
							}
							else
							{
								layers[k].neurons[l].w[m] -= learningRate * (gradient[k][l][m] + MOMENTUM * prevGradient[k][l][m]);
								dot += gradient[k][l][m] * prevGradient[k][l][m];
							}
						}
					}
				}
				
				// Update learning rate based on how different the new gradient was from the previous gradient
				learningRate += RATE_UPDATE * dot;
				
				prevGradient = gradient;
			}
			
			// Periodically validate the network with cases that weren't used for testing.
			// When the error gets worse 4 times in a row, stop training to avoid overfitting
			if((i + 1) % 4 == 0)
			{
				double newError = 1.0 - validate(validateFeatures, validateClasses);
				if(newError > validationError)
				{
					stoppingCriteria++;
				}
				else
				{
					stoppingCriteria = 0;
				}
				
				validationError = newError;
			}
			
//			for(int k = 1; k < layers.length; k++)
//			{
//				for(int l = 0; l < sizes[k]; l++)
//				{
//					System.out.println("Layer " + k + "; Neuron " + (l + 1) + "; Weights = " + Arrays.toString(layers[k].neurons[l].w));
//				}
//			}
		}
	}
	
	// Predict the class using a trained neural network. The number of elements in features
	// should be the same as the number of neurons in the input layer.
	public int predict(double[] features)
	{
		double[] input = Arrays.copyOf(features, features.length);
		for(int i = 1; i < layers.length; i++)
		{
			input = layers[i].evaluate(input);
		}
		
		int prediction = 0;
		for(int i = 0; i < input.length; i++)
		{
			if(input[i] > input[prediction])
			{
				prediction = i;
			}
		}
		
		return prediction;
	}
	
	// Validate the neural network on a validation set
	public double validate(double[][] features, int[] classes)
	{
		if(features.length == 0)
		{
			return 0.0;
		}
		
		int correctCount = 0;
		for(int i = 0; i < features.length; i++)
		{
			if(predict(features[i]) == classes[i])
			{
				correctCount++;
			}
		}
		
		return (double)correctCount / features.length;
	}
	
	public void shuffle(double[][] features, int[] classes)
	{
		Random rand = new Random();
		for(int i = 0; i < features.length; i++)
		{
			int j = rand.nextInt(i + 1);
			
			double[] temp = features[i];
			features[i] = features[j];
			features[j] = temp;
			
			int tmp = classes[i];
			classes[i] = classes[j];
			classes[j] = tmp;
		}
	}
}
