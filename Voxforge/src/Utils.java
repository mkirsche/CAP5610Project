import java.util.*;
import java.io.*;
public class Utils
{
	public static final int MAX_SIZE = 300;
	
	public static ArrayList<Data> readAll(FastScanner in) throws Exception
	{
		ArrayList<Data> ret = new ArrayList<Data>();
		
		while(in.hasNext())
		{
			int index = in.nextInt();
			String fileName = in.nextLine().trim();
			
			ret.add(readFile(index, fileName));
		}
		
		return ret;
	}
	
	public static Data readFile(int index, String fileName) throws Exception
	{
		double[][] coeff = new double[MAX_SIZE][13];
		
		Utils.FastScanner cep = new Utils.FastScanner(new File(fileName));
		
		ArrayList<double[]> data = new ArrayList<double[]>();
		outer:
		while(cep.hasNext())
		{
			double[] current = new double[13];
			for(int i = 0; i < 13; i++)
			{
				String token = cep.next();
				while(cep.hasNext() && (token.charAt(0) == '[' || token.charAt(0) == ']'))
				{
					token = cep.next();
				}
				
				if(token.charAt(0) == ']')
				{
					break outer;
				}
				
				token = token.replace(']', ' ').trim();
				
				current[i] = Double.parseDouble(token);
			}
			
			data.add(current);
		}
		
		if(data.size() > MAX_SIZE)
		{
			return null;
		}
		
		for(int i = 0; i < MAX_SIZE; i++)
		{
			coeff[i] = data.get(i + (data.size() - MAX_SIZE) / 2);
		}
		
		return new Data(coeff, index);
	}
	
	static class Data
	{
		double[][] coeff;
		int index;
		
		public Data(double[][] c, int i)
		{
			coeff = c;
			index = i;
		}
	}
	
	static class Results
	{
		double overallPercent;
		double[] percent;
		int[] tp;
		int[] fp;
		int[] fn;
		double macroF1;
		
		public Results(double op, double[] p, int[] tp, int[] fp, int[] fn)
		{
			overallPercent = op;
			percent = p;
			this.tp = tp;
			this.fp = fp;
			this.fn = fn;
			
			calcF1();
		}
		
		public void calcF1()
		{
			double[] f1 = new double[percent.length];
			for(int i = 0; i < f1.length; i++)
			{
				double p = (double)tp[i] / (tp[i] + fp[i]);
				double r = (double)tp[i] / (tp[i] + fn[i]);
				f1[i] = (2 * p * r) / (r + p);
			}
			
			macroF1 = 0;
			for(int i = 0; i < f1.length; i++)
			{
				macroF1 += f1[i];
			}
			macroF1 /= percent.length;
		}
		
		public String toString()
		{
			return overallPercent + "\n" + Arrays.toString(percent) + "\n" + macroF1;
		}
	}
	
	static class FastScanner
	{
		BufferedReader br;
		StringTokenizer st;
		
		public FastScanner(File file) throws IOException
		{
			br = new BufferedReader(new FileReader(file));
			st = new StringTokenizer("");
		}
		
		public boolean hasNext() throws IOException
		{
			if(st.hasMoreTokens())
			{
				return true;
			}
			else
			{
				String line = br.readLine();
				if(line == null)
				{
					return false;
				}
				
				st = new StringTokenizer(line);
				return hasNext();
			}
		}
		
		public String nextLine() throws IOException
		{
			if(!st.hasMoreTokens())
			{
				return br.readLine();
			}
			else
			{
				StringBuilder sb = new StringBuilder();
				while(st.hasMoreTokens())
				{
					sb.append(st.nextToken());
				}
				
				return sb.toString();
			}
		}
		
		public String next() throws IOException
		{
			if(st.hasMoreTokens())
			{
				return st.nextToken();
			}
			else
			{
				st = new StringTokenizer(br.readLine());
				return next();
			}
		}
		
		public int nextInt() throws IOException
		{
			return Integer.parseInt(next());
		}
		
		public void close() throws IOException
		{
			br.close();
		}
	}
}
