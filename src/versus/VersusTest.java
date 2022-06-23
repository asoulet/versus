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

        // if items are given as arguments, compare these, else the examples below
        if (args.length > 0) {
            versusAndShow(args, threshold);
        } else {
            // Wikidata examples
            versusAndShow(new String[]{"Q7259", "Q7251", "Q11641"}, threshold); // Lovelace vs Turing vs Hopper
            versusAndShow(new String[]{"Q1406", "Q388"}, threshold); // Windows vs Linux
            versusAndShow(new String[]{"Q34236", "Q34264"}, threshold); // FreeBSD vs. BSD
            versusAndShow(new String[]{"Q1406", "Q388", "Q34236"}, threshold); // Windows vs Linux vs FreeBSD
            versusAndShow(new String[]{"Q16766305", "Q19841877"}, threshold); // Atom editor cs VSCode
            versusAndShow(new String[]{"Q16766305", "Q111967621"}, threshold); // Atom editor cs VSCodium
            versusAndShow(new String[]{"Q19841877", "Q111967621"}, threshold);// VSCode cs VSCodium
            versusAndShow(new String[]{"Q16766305", "Q19841877", "Q111967621"}, threshold); // Atom editor vs. VSCode vs. VCodium
            //versusAndShow(new String[]{"Q7259", "Q7251"}, threshold); // Lovelace vs Turing – runs long time
            //versusAndShow(new String[]{"Q7259", "Q7251", "Q11641"}, threshold); // Lovelace vs Turing vs Hopper
            //versusAndShow(new String[]{"Q215819", "Q185524", "Q850", "Q192490"}, threshold); // SQLServer vs Oracle vs MySQL vs PostgreSQL
            versusAndShow(new String[]{"Q9191", "Q1290"}, threshold); // Descartes vs Pascal
            //versusAndShow(new String[]{"Q9351", "Q844940"}, threshold); // Pikachu vs Charizard
            versusAndShow(new String[]{"Q3052772", "Q22686", "Q567"}, threshold); // Macron vs Trump vs Merkel
            versusAndShow(new String[]{"Q3052772", "Q22686", "Q567", "Q61053"}, threshold); // Macron vs Trump vs Merkel vs Scholz
            versusAndShow(new String[]{"Q142", "Q30", "Q183"}, threshold); // France vs USA vs Germany
            versusAndShow(new String[]{"Q90", "Q60"}, threshold); // Paris vs NY
            //versusAndShow(new String[]{"Q487789", "Q110354"}, threshold); // Grande vadrouille vs The great escape
            versusAndShow(new String[]{"Q2737", "Q159347"}, threshold); // Louis de Funes vs Steve Mc Queen

            // DBpedia
//            Versus.ENDPOINT = "http://dbpedia.org/resource/";
////            versusAndShow(new String[]{"http://dbpedia.org/resource/Ada_Lovelace", "http://dbpedia.org/resource/Alan_Turing"}, threshold);
//            versusAndShow(new String[]{"Ada_Lovelace", "Alan_Turing"}, threshold);
////            versusAndShow(new String[]{"http://dbpedia.org/resource/Paris", "http://dbpedia.org/resource/London").generate(0.001);
//            versusAndShow(new String[]{"Paris", "London").generate(0.001);
//
//            // CHEMBL
//            Versus.ENDPOINT = "http://identifiers.org/biomodels.db/BIOMD0000000001";
////            versusAndShow(new String[]{"http://identifiers.org/biomodels.db/BIOMD0000000001#_000003", "http://identifiers.org/biomodels.db/BIOMD0000000001#_000006"}, threshold);
//            versusAndShow(new String[]{"#_000003", "#_000006"}, threshold);
//            Versus.ENDPOINT = "http://identifiers.org/reactome/";
////            versusAndShow(new String[]{"http://identifiers.org/reactome/R-BTA-170660.1", "http://identifiers.org/reactome/R-BTA-2534343.1"}, threshold);
//            versusAndShow(new String[]{"R-BTA-170660.1", "R-BTA-2534343.1"}, threshold);
        }

    }

    private static void versusAndShow(String[] args, double threshold) {
        ComparisonTable ct = new ComparisonTable();
        Versus.resetErrorNumber();
        Versus.resetLastTime();
        logger.info("new versus: " + String.join(", ", args));
        try {
            for (String arg : args) {
                ct = ct.versus(arg);
            }
            ct.generate(threshold);
            ct.show();
        } catch (InterruptedException ex) {
            logger.error("!!! " + ex + " for versus " + String.join(", ", args));
        }
    }
}
