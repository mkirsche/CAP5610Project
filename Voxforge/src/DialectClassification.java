import java.util.*;
import java.io.*;
public class DialectClassification
{
	public static final int NUM_FILTERS = 13;
	public static final int FRAME_SIZE = 100;
	public static final int NUM_HIDDEN_LAYERS = 1;
	public static final int HIDDEN_LAYER_SIZE = 100;
	
	public static int numDialects;
	
	public static void main(String[] args) throws Exception
	{
		Utils.FastScanner train = new Utils.FastScanner(new File("train.txt"));
		Utils.FastScanner test = new Utils.FastScanner(new File("test.txt"));
		Utils.FastScanner map = new Utils.FastScanner(new File("map.txt"));
		
		System.out.println("Reading Training Data");
		ArrayList<Utils.Data> trainData = Utils.readAll(train);
		
		System.out.println("Reading Test Data");
		ArrayList<Utils.Data> testData = Utils.readAll(test);
		
		numDialects = map.nextInt();
		
		double[][] trainFeatures = getFeatures1(trainData);
		double[][] testFeatures = getFeatures1(testData);
		
		System.out.println("Testing");
		
		Utils.Results results = test(trainData, testData, trainFeatures, testFeatures);
		
		System.out.println(results);
		
		train.close();
		test.close();
		map.close();
	}
	
	public static Utils.Results test(ArrayList<Utils.Data> trainData, ArrayList<Utils.Data> testData, double[][] trainFeatures, double[][] testFeatures)
	{
		int[] trainClasses = new int[trainData.size()];
		for(int i = 0; i < trainData.size(); i++)
		{
			trainClasses[i] = trainData.get(i).index;
		}
		
		int[] sizes = new int[2 + NUM_HIDDEN_LAYERS];
		sizes[0] = NUM_FILTERS;
		sizes[sizes.length - 1] = numDialects;
		for(int i = 1; i <= NUM_HIDDEN_LAYERS; i++)
		{
			sizes[i] = HIDDEN_LAYER_SIZE;
		}
		
		NeuralNetwork nn = new NeuralNetwork(2 + NUM_HIDDEN_LAYERS, sizes);
		
		nn.train(trainFeatures, trainClasses);
		
		int[] testClasses = new int[testData.size()];
		for(int i = 0; i < testData.size(); i++)
		{
			testClasses[i] = testData.get(i).index;
		}
		
		int totalCorrect = 0;
		int[] dialectCounts = new int[numDialects];
		int[] tp = new int[numDialects];
		int[] fp = new int[numDialects];
		int[] fn = new int[numDialects];
		for(int i = 0; i < testData.size(); i++)
		{
			if(i % 1000 == 0)
			{
				System.out.println("TEST " + i);
			}
		
			int prediction = nn.predict(testFeatures[i]);
			
			if(prediction == testData.get(i).index)
			{
				totalCorrect++;
				tp[testData.get(i).index]++;
			}
			else
			{
				fp[prediction]++;
				fn[testData.get(i).index]++;
			}
			
			dialectCounts[testData.get(i).index]++;
		}
		
		double[] percent = new double[numDialects];
		for(int i = 0; i < percent.length; i++)
		{
			percent[i] = (double)tp[i] / dialectCounts[i];
		}
		
		Utils.Results results = new Utils.Results((double)totalCorrect / testData.size(), percent, tp, fp, fn);
		
		return results;
	}
	
	public static double[][] getFeatures1(ArrayList<Utils.Data> data)
	{
		double[][] features = new double[data.size()][NUM_FILTERS];
		
		for(int i = 0; i < data.size(); i++)
		{
			features[i] = average(data.get(i), 150 - FRAME_SIZE / 2, 150 + FRAME_SIZE / 2);
		}
		
		return features;
	}
	
	// Calculate average coefficients in range from left (inclusive) to right (exclusive)
	public static double[] average(Utils.Data data, int left, int right)
	{
		double[] ret = new double[data.coeff[0].length];
		for(int i = 0; i < ret.length; i++)
		{
			for(int j = left; j < right; j++)
			{
				ret[i] += data.coeff[j][i];
			}
			
			ret[i] /= (double)(right - left);
		}
		
		return ret;
	}
}
