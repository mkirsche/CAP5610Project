public class Layer
{
	Neuron[] neurons;
	
	// Create a layer in a neural network
	// n = number of neurons in this layer, m = number of connections coming into each neuron
	public Layer(int n, int m)
	{
		neurons = new Neuron[n];
		for(int i = 0; i < neurons.length; i++)
		{
			neurons[i] = new Neuron(m);
		}
	}
	
	// Calculate output of each neuron in this layer assuming that the inputs are the same for each one
	public double[] evaluate(double[] input)
	{
		double[] output = new double[neurons.length];
		for(int i = 0; i < neurons.length; i++)
		{
			output[i] = neurons[i].fire(input);
		}
		
		return output;
	}
}
