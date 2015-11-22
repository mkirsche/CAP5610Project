import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class parser {

	// The string used to determine if a dialect is unknown or unusable.
	static String unusableDialect = "unknown";
	
	// Names of dialects that will be used as the replacement dialect.
	static String[] dialectSubstitute = {
			unusableDialect, 		"american_english", 	"asian_accent", 
			"canadian_english", 	"french_accect", 		"new_york_city", 
			"australian_english"};
	
	// All the dialects in this array will be replaced with the dialect in the dialectSubstitute array.
	static String[][] dialectReplacements = {
			// unknown - All of these languages will be thrown out
			{"please_select", "other_nonnative_speaker", "other", "nonnative_speaker", "null", "", "american_english_nonnative_speaker"},
			
			// american_english
			{"general_american_english", "general_american", "america_english"},
			
			// asian_accent
			{"other_asian_accent"}, 
			
			// canadian english
			{"canadaian_english", "westerncentral_canadian_english", "westcentral_canadian", "maritimes"},
			
			// french accect
			{"france_accent", "european_english_french_accent"},
			
			// new york city
			{"new_york_city_and_northern_nj", "new_york_city_and_northern_new_jersey"},
			
			// australian english
			{"australian_english_adelaide"}
	};
	
	public static void main(String[] args) throws Exception {
		// Use first argument as the directory or if no arguments given,
		// use the expected voxforge directory on the external hard drive.
		String voxforgePath = null;
		if(args.length > 0)
			voxforgePath = args[0];
		else
			voxforgePath = "/run/media/sroyal/JOOOOOOOOOO/Voxforge uncompressed/";

		// Directory to the voxforge files.
		File voxforgeDirectory = new File(voxforgePath);
		
		// Printwriter to create the all.txt file.
		PrintWriter allWavFiles = new PrintWriter(new File(voxforgePath+"all.txt"));
		
		// Arraylist to store all the unique dialects.
		ArrayList<String> dialects = new ArrayList<String>();
		// Map a given dialect string to the dialect class.
		HashMap<String, Dialect> dialectMap = new HashMap<String, Dialect>();
		
		// Map a given dialect to the index to the replacement dialect.
		HashMap<String, Integer> dialectReplacementMappings = new HashMap<String, Integer>();
		for(int repl = 0; repl < dialectSubstitute.length; ++repl)
			for(String s : dialectReplacements[repl])
				dialectReplacementMappings.put(s, repl);
		
		// Go through each file and find the dialect, saving the paths.
		for(File languageDirectory : voxforgeDirectory.listFiles()) {
			// Not a language directory, ignore file.
			if(!languageDirectory.isDirectory()) continue;
			
			// Get the language for the current directory.
			String language = languageDirectory.getName().replaceAll("\\s+", "_");
			System.out.println(language);
			
			// Loop through each set of files in the language directory.
			for(File file : languageDirectory.listFiles()) {
				if(!file.isDirectory()) continue;
				
				// Parse the dialect out of the readme file
				String dialect = parseFile(file.getPath() + "/etc/README").replaceAll("\\s+", "_");
				
				// Check replacement dialect names.
				Integer replInd = dialectReplacementMappings.get(dialect);
				if(replInd != null)
					dialect = dialectSubstitute[replInd];
				
				// Check if dialect should be ignored.
				if(dialect.equals(unusableDialect)) continue;
				
				// Add language to the given dialect.
				dialect = language +"." + dialect;
				
				// Get the dialect object for the current dialect.
				Dialect dia = dialectMap.get(dialect);
				if(dia == null) {
					dia = new Dialect(dialect, dialects.size());
					dialects.add(dialect);
				}
				dialectMap.put(dialect, dia);
				
				// Add all the .wav files to the dialect object. 
				// Print the .wav file path to all.txt.
				//System.out.println(file.getPath());
				File wavDirectory = new File(file.getPath() +"/wav/");
				if(wavDirectory == null || wavDirectory.listFiles() == null) continue;
				for(File wavFile : wavDirectory.listFiles()) {
					if(!wavFile.getName().endsWith(".wav") || wavFile.getName().startsWith("."))
						continue; 
					
					// Read the coef data and check to see if it 
					// contains at least the minimum required.
					Utils.Data data = Utils.readFile(dia.index, wavFile.getPath()+".txt");
					if(data == null) 
						continue;
					
					// Calculate deltas for the given coefs.
					CalcDeltas.calcDeltas(data, wavFile.getPath()+".txt");
					
					// Add file to all.txt and its dialect.
					allWavFiles.println(dia.index +" " +wavFile.getPath()+".txt");
					dia.add(wavFile.getPath());
				}
			}
		}
		allWavFiles.close();
		
		// Sort all dialects based on the count of .wav files.
		TreeSet<Dialect> sortedDialect = new TreeSet<Dialect>(dialectMap.values());
		
		// Print all dialects to map.txt in decreasing order of files per dialect.
		PrintWriter mapFileOut = new PrintWriter(new File(voxforgePath+"map.txt"));
		mapFileOut.println(sortedDialect.size());
		for(Dialect dialect : sortedDialect) {
			// if(dialect.files.size() == 0) break;
			mapFileOut.println(dialect.dialect +" " + dialect.index + " " +dialect.files.size());
			System.out.printf("Count: %4d, Dialect: %s, index: %d%n", dialect.files.size(), dialect.dialect, dialect.index);
		}
		mapFileOut.close();
	}

	
	// Parse a given readme file and return the specified dialect.
	static boolean first = true;
	static String parseFile(String filename) throws Exception {
		File file = new File(filename);
		if(!file.exists()) 
			return "null";
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String dialect = unusableDialect;
		String prefix = "Pronunciation dialect:";
		while(br.ready()){
			String line = br.readLine().trim();
			if(line.startsWith(prefix)) {
				dialect = line.substring(prefix.length()).toLowerCase().trim().replaceAll("[^a-z ]", "");
				break;
			}
		}
		br.close();
		PrintWriter out = new PrintWriter(new File(file.getParent() +"/Dialect"));
		out.println(dialect);
		out.close();
		return dialect;
	}

	// Class to hold information about a dialect.
	// The class holds the dialect name, the index of
	// the dialect, and all the file paths of the 
	// cepstral coefficients files.
	static class Dialect implements Comparable<Dialect>{
		String dialect;
		ArrayList<String> files;
		int index;
		
		Dialect(String d, int i) {
			dialect = d;
			index = i;
			files = new ArrayList<String>();
		}
		void add(String file) {
			files.add(file);
		}
		
		public int compareTo(Dialect o) {
			int ret = o.files.size() - files.size();
			if(ret == 0)
				ret = dialect.compareTo(o.dialect);
			return ret;
		}
		
		public String toString() {
			return dialect +": "+files.size();
		}
	}
}
