import java.util.*;
import java.io.*;
public class GenerateSets {
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File("all.txt")));
		Scanner map = new Scanner(new File("map.txt"));
		
		
		int N = map.nextInt();
		int[] counts = new int[N];
		while(map.hasNext()){
			String name = map.next();
			int index = map.nextInt();
			counts[index] = map.nextInt();
		}
		

		int min = 50;
		int cap = 200;
		double percent = .8;
		
		
		int[] max = new int[N];
		boolean[] bad = new boolean[N];
		for(int i = 0;i<N;i++){
			max[i] = (int)(counts[i]*percent);
			max[i] = Math.min(max[i], cap);
			if(max[i] < min){
				bad[i] = true;
			}
		}
		
		
		int[] trainCount = new int[N];
		
		ArrayList<String> files = new ArrayList<String>();
		
		while(true){
			String line = br.readLine();
			if(line == null) break;
			files.add(line);
		}
		
		Collections.shuffle(files);
		
		
		
		ArrayList<String> train = new ArrayList<String>();
		ArrayList<String> test = new ArrayList<String>();
		for(String s : files){
			StringTokenizer st = new StringTokenizer(s, " ");
			int index = Integer.parseInt(st.nextToken());
			if(!bad[index]){
				if(trainCount[index] < max[index]){
					trainCount[index]++;
					train.add(s);
				}
				else{
					test.add(s);
				}
			}
			
		}
		
		PrintWriter trainOut = new PrintWriter(new File("train.txt"));
		PrintWriter testOut = new PrintWriter(new File("test.txt"));
		
		for(String s : train){
			trainOut.println(s);
		}
		
		for(String s : test){
			testOut.println(s);
		}
		trainOut.close();
		testOut.close();
		
	}
}
