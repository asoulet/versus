package versus;

import tools.Wikidata;

public class Versus {
	
	public static String NAME = "versus";
	public static String VERSION = "AndThem";
	public static String CONFIGURATION = "properties/versus.properties";
	public static final boolean FILTER = true; // implemented for Wikidata and DBpedia
	public static String ENDPOINT = Wikidata.endpoint; // Wikidata
	//public static String ENDPOINT = "https://dbpedia.org/sparql"; // DBpedia
	//public static String ENDPOINT = "https://www.ebi.ac.uk/rdf/services/sparql"; // CHEMBL
	
	private static int queryNumber = 0;
	private static int errorNumber = 0;
	private static long lastTime = System.currentTimeMillis();
	
	public static int getQueryNumber() {
		return queryNumber;
	}
	public static void resetQueryNumber() {
		Versus.queryNumber = 0;
	}
	
	public static void incrementQueryNumber() {
		Versus.queryNumber++;
	}
	
	public static int getErrorNumber() {
		return errorNumber;
	}
	public static void resetErrorNumber() {
		Versus.errorNumber = 0;
	}
	
	public static void incrementErrorNumber() {
		Versus.errorNumber++;
	}
	
	public static void resetLastTime() {
		lastTime = System.currentTimeMillis();
	}
	
	public static long getTime() {
		return System.currentTimeMillis() - lastTime;
	}
	
	

}
