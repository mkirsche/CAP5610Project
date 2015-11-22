import java.util.*;
public class Neuron
{
	double[] w;
	double activation;
	double output;
	
	// Create a new neuron with n inputs (not including bias)
	public Neuron(int n)
	{
		w = new double[n + 1];
		
		Random rand = new Random();
		for(int i = 0; i < w.length; i++)
		{
			w[i] = rand.nextGaussian() * 0.35;
//			w[i] = rand.nextDouble()*1.4 - .7;
		}
	}
	
	// Fire neuron using inputs (not including bias)
	public double fire(double[] input)
	{
		activation = w[0];
		for(int i = 1; i < w.length; i++)
		{
			activation += w[i] * input[i - 1];
		}
		
		return output = activationFunction(activation);
	}
	
	public static double activationFunction(double x)
	{
		return 1.0 / (1 + Math.exp(-x));
	}
	
	public static double fPrime(double x)
	{
		double fx = activationFunction(x);
		return fx * (1 - fx);
	}
}
