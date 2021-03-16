package versus;

import org.apache.log4j.Logger;

import comparison.ComparisonTable;

public class VersusTest {

	private static Logger logger = Logger.getLogger(VersusTest.class);
	
	public static void main(String[] args) {
		logger.info(Versus.NAME + " " + Versus.VERSION);
		Versus.resetLastTime();
		ComparisonTable ct = new ComparisonTable();
		// Wikidata
		//ct.versus("Q7259").versus("Q7251").generate(0.01); // Lovelace vs Turing
		ct.versus("Q7259").versus("Q7251").versus("Q11641").generate(0.01); // Lovelace vs Turing vs Hopper
		//ct.versus("Q215819").versus("Q185524").versus("Q850").versus("Q192490").generate(0.01); // SQLServer vs Oracle vs MySQL vs PostgreSQL
		//ct.versus("Q1406").versus("Q388").generate(0.01); // Windows vs Linux
		//ct.versus("Q9191").versus("Q1290").generate(0.01); // Descartes vs Pascal
		//ct.versus("Q9351").versus("Q844940").generate(0.01); // Pikachu vs Charizard
		//ct.versus("Q3052772").versus("Q22686").versus("Q567").generate(0.01); // Macron vs Trump vs Merkel
		//ct.versus("Q142").versus("Q30").versus("Q183").generate(0.01); // France vs USA vs Germany 
		//ct.versus("Q90").versus("Q60").generate(0.01); // Paris vs NY
		//ct.versus("Q487789").versus("Q110354").generate(0.01); // Grande vadrouille vs The great escape
		//ct.versus("Q2737").versus("Q159347").generate(0.01); // Louis de Funes vs Steve Mc Queen
		//ct.versus("Q75459").versus("Q121773").generate(0.001); 
		
		// DBpedia
		//ct.versus("http://dbpedia.org/resource/Ada_Lovelace").versus("http://dbpedia.org/resource/Alan_Turing").generate(0.0001);
		//ct.versus("http://dbpedia.org/resource/Paris").versus("http://dbpedia.org/resource/London").generate(0.001);
		
		// CHEMBL
		//ct.versus("http://identifiers.org/biomodels.db/BIOMD0000000001#_000003").versus("http://identifiers.org/biomodels.db/BIOMD0000000001#_000006").generate(0.01);
		//ct.versus("http://identifiers.org/reactome/R-BTA-170660.1").versus("http://identifiers.org/reactome/R-BTA-2534343.1").generate(0.01);
				
		ct.show();
		logger.info(Versus.getQueryNumber() + " " + Versus.getTime());
	}

}
