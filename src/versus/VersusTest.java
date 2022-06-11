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

        logger.info("endpoint: '" + ProgOpts.get(OptKeys.ENDPOINT) + "'");
        logger.info("threshold: " + threshold);

        Versus.resetLastTime();
        ComparisonTable ct = new ComparisonTable();

        // if items are given as arguments, compare these, else the examples below
        if (args.length > 0) {
            for (String arg : args) {
                ct = ct.versus(arg);
            }
            ct.generate(threshold);
        } else {
            // Wikidata
            ct.versus("Q16766305").versus("Q19841877").versus("Q111967621").generate(threshold); // Atom editor vs. VSCode vs. VCodium
            ct.versus("Q16766305").versus("Q19841877").generate(threshold);
            ct.versus("Q16766305").versus("Q111967621").generate(threshold);
            ct.versus("Q19841877").versus("Q111967621").generate(threshold);
            //ct.versus("Q7259").versus("Q7251").generate(threshold); // Lovelace vs Turing
            //ct.versus("Q7259").versus("Q7251").versus("Q11641").generate(threshold); // Lovelace vs Turing vs Hopper
            //ct.versus("Q215819").versus("Q185524").versus("Q850").versus("Q192490").generate(threshold); // SQLServer vs Oracle vs MySQL vs PostgreSQL
            ct.versus("Q1406").versus("Q388").generate(threshold); // Windows vs Linux
            ct.versus("Q34236").versus("Q34264").generate(threshold); // FreeBSD vs. BSD
            ct.versus("Q1406").versus("Q388").versus("Q34264").generate(threshold); // Windows vs Linux vs BSD

            ct.versus("Q9191").versus("Q1290").generate(threshold); // Descartes vs Pascal
            //ct.versus("Q9351").versus("Q844940").generate(threshold); // Pikachu vs Charizard
            ct.versus("Q3052772").versus("Q22686").versus("Q567").generate(threshold); // Macron vs Trump vs Merkel
            ct.versus("Q142").versus("Q30").versus("Q183").generate(threshold); // France vs USA vs Germany
            ct.versus("Q90").versus("Q60").generate(threshold); // Paris vs NY
            //ct.versus("Q487789").versus("Q110354").generate(threshold); // Grande vadrouille vs The great escape
            ct.versus("Q2737").versus("Q159347").generate(threshold); // Louis de Funes vs Steve Mc Queen

            // DBpedia
            //ct.versus("http://dbpedia.org/resource/Ada_Lovelace").versus("http://dbpedia.org/resource/Alan_Turing").generate(threshold);
            //ct.versus("http://dbpedia.org/resource/Paris").versus("http://dbpedia.org/resource/London").generate(0.001);
            // CHEMBL
            //ct.versus("http://identifiers.org/biomodels.db/BIOMD0000000001#_000003").versus("http://identifiers.org/biomodels.db/BIOMD0000000001#_000006").generate(threshold);
            //ct.versus("http://identifiers.org/reactome/R-BTA-170660.1").versus("http://identifiers.org/reactome/R-BTA-2534343.1").generate(threshold);
        }

        ct.show();
        logger.info(Versus.getQueryNumber() + " " + Versus.getTime());
    }

}
