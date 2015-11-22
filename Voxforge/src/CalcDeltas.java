import java.util.*;
import java.io.*;
public class CalcDeltas
{
	static final boolean FORCE_DELTAS = true;

	public static void main(String[] args) throws Exception
	{
		Utils.FastScanner in = new Utils.FastScanner(new File("all.txt"));
		
		while(in.hasNext())
		{
			int index = in.nextInt();
			String fileName = in.nextLine().trim();
			
			Utils.Data data = Utils.readFile(index, fileName);
			
			calcDeltas(data, fileName);
		}
		
		in.close();
	}
	
	public static void calcDeltas(Utils.Data data, String fileName) throws Exception
	{
		File deltaFile = new File(fileName + ".delta");
		File deltaDeltaFile = new File(fileName + ".deltadelta");
		
		if(!FORCE_DELTAS && deltaFile.exists() && deltaDeltaFile.exists())
		{
			return;
		}
	
		PrintWriter delta = new PrintWriter(deltaFile);
		PrintWriter deltaDelta = new PrintWriter(deltaDeltaFile);
		
		double[][] d = calc(data.coeff);
		double[][] d2 = calc(d);
		
		print(delta, d);
		print(deltaDelta, d2);
		
		delta.close();
		deltaDelta.close();
	}
	
	
	public static double[][] calc(double[][] coeff)
	{
		double[][] delta = new double[coeff.length][coeff[0].length];
		
		for(int i = 0; i < coeff[0].length; i++)
		{
			for(int j = 0; j < coeff.length; j++)
			{
				for(int k = 1; k <= 2; k++)
				{
					delta[j][i] += k * ((j + k < coeff.length ? coeff[j + k][i] : 0) - (j - k >= 0 ? coeff[j - k][i] : 0));
				}
				
				delta[j][i] /= 10.0;
			}
		}
		
		return delta;
	}
	
	public static void print(PrintWriter out, double[][] delta)
	{
		out.print('[');
		
		for(int i = 0; i < delta.length; i++)
		{
			out.print("[");
			for(int j = 0; j < delta[0].length; j++)
			{
				out.print(" " + delta[i][j]);
			}
			
			out.print(']');
			if(i < delta.length - 1)
			{
				out.println();
			}
		}
		
		out.println(']');
	}
}
