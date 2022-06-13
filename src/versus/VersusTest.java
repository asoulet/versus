package versus;

import org.apache.log4j.Logger;

import comparison.ComparisonTable;
import options.ProgOpts;
import options.ProgOpts.OptKeys;

public class VersusTest {

    private static final Logger logger = Logger.getLogger(VersusTest.class);

    public static void main(String[] args) {
        logger.info(Versus.NAME + " " + Versus.VERSION);

        // parse program options
        args = ProgOpts.commandLineOptions(args);
        double threshold = Double.parseDouble(ProgOpts.get(OptKeys.THRESHOLD));
        Versus.ENDPOINT = ProgOpts.get(OptKeys.ENDPOINT);
        logger.info("endpoint: '" + ProgOpts.get(OptKeys.ENDPOINT) + "'");
        logger.info("threshold: " + threshold);

//        Versus.resetLastTime();
        ComparisonTable ct = new ComparisonTable();

        // if items are given as arguments, compare these, else the examples below
        if (args.length > 0) {
            for (String arg : args) {
                ct = ct.versus(arg);
            }
            ct.generate(threshold);
        } else {
            // Wikidata examples

            ct.versus("Q1406").versus("Q388").generate(threshold); // Windows vs Linux
            ct.show();

            ct = new ComparisonTable();
            Versus.resetLastTime();
            ct.versus("Q34236").versus("Q34264").generate(threshold); // FreeBSD vs. BSD
            ct.show();

            ct = new ComparisonTable();
            ct.versus("Q1406").versus("Q388").versus("Q34236").generate(threshold); // Windows vs Linux vs FreeBSD
            ct.show();

            ct = new ComparisonTable();
            ct.versus("Q16766305").versus("Q19841877").generate(threshold); // Atom editor cs VSCode
            ct.show();

            ct = new ComparisonTable();
            ct.versus("Q16766305").versus("Q111967621").generate(threshold); // Atom editor cs VSCodium
            ct.show();

            ct = new ComparisonTable();
            ct.versus("Q19841877").versus("Q111967621").generate(threshold);// VSCode cs VSCodium
            ct.show();

            ct = new ComparisonTable();
            ct.versus("Q16766305").versus("Q19841877").versus("Q111967621").generate(threshold); // Atom editor vs. VSCode vs. VCodium
            ct.show();

            //ct = new ComparisonTable();
            //ct.versus("Q7259").versus("Q7251").generate(threshold); // Lovelace vs Turing – runs long time
            //ct.show();
            //ct = new ComparisonTable();
            //ct.versus("Q7259").versus("Q7251").versus("Q11641").generate(threshold); // Lovelace vs Turing vs Hopper
            //ct.show();
            //ct = new ComparisonTable();
            //ct.versus("Q215819").versus("Q185524").versus("Q850").versus("Q192490").generate(threshold); // SQLServer vs Oracle vs MySQL vs PostgreSQL
            //ct.show();
            ct = new ComparisonTable();
            ct.versus("Q9191").versus("Q1290").generate(threshold); // Descartes vs Pascal
            ct.show();

            //ct = new ComparisonTable();
            //ct.versus("Q9351").versus("Q844940").generate(threshold); // Pikachu vs Charizard
            //ct.show();
            ct = new ComparisonTable();
            ct.versus("Q3052772").versus("Q22686").versus("Q567").generate(threshold); // Macron vs Trump vs Merkel
            ct.show();

            ct = new ComparisonTable();
            ct.versus("Q142").versus("Q30").versus("Q183").generate(threshold); // France vs USA vs Germany
            ct.show();

            ct = new ComparisonTable();
            ct.versus("Q90").versus("Q60").generate(threshold); // Paris vs NY
            ct.show();

            //ct = new ComparisonTable();
            //ct.versus("Q487789").versus("Q110354").generate(threshold); // Grande vadrouille vs The great escape
            //ct.show();
            ct = new ComparisonTable();
            ct.versus("Q2737").versus("Q159347").generate(threshold); // Louis de Funes vs Steve Mc Queen
            ct.show();

            // DBpedia
            //ct = new ComparisonTable();
            //ct.versus("http://dbpedia.org/resource/Ada_Lovelace").versus("http://dbpedia.org/resource/Alan_Turing").generate(threshold);
            //ct.show();
            //ct = new ComparisonTable();
            //ct.versus("http://dbpedia.org/resource/Paris").versus("http://dbpedia.org/resource/London").generate(0.001);
            //ct.show();
            // CHEMBL
            //ct = new ComparisonTable();
            //ct.versus("http://identifiers.org/biomodels.db/BIOMD0000000001#_000003").versus("http://identifiers.org/biomodels.db/BIOMD0000000001#_000006").generate(threshold);
            //ct.show();
            //ct = new ComparisonTable();
            //ct.versus("http://identifiers.org/reactome/R-BTA-170660.1").versus("http://identifiers.org/reactome/R-BTA-2534343.1").generate(threshold);
            //ct.show();
        }

//        logger.info(Versus.getQueryNumber() + " " + Versus.getTime());
    }

}
