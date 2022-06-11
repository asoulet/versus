package options;

import java.util.HashMap;
import java.util.logging.Level;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.log4j.Logger;
import tools.Wikidata;

/**
 * @author jalsti
 */
public class ProgOpts {

    public static Options cmdOptions = new Options();
    private static final HashMap<String, String> opts = new HashMap<>();

    private static final Logger logger = Logger.getLogger(ProgOpts.class);

    public enum OptKeys {
        FILE_OUTPUT, ENDPOINT, THRESHOLD
    }

    public static String get(OptKeys optKey) {
        String key = optKey.toString();
        return opts.get(key);
    }

    public static void set(OptKeys optKey, String optValue) {
        String key = optKey.toString();
        logger.debug("setting " + key + " to '" + optValue + "'");
        opts.put(key, optValue);
    }

    public static String[] commandLineOptions(String[] args) {

        set(OptKeys.FILE_OUTPUT, "0");
        set(OptKeys.ENDPOINT, Wikidata.endpoint);
        set(OptKeys.THRESHOLD, "0.01");

        CommandLineParser parser = new DefaultParser();
        cmdOptions.addOption("e", "endpoint", true, "endpoint to use (default: " + Wikidata.endpoint + ")");
        cmdOptions.addOption("f", "file", false, "write result to file, instead of stdout");
        cmdOptions.addOption("h", "help", false, "this help message");
        cmdOptions.addOption("t", "threshold", true, "threshold to use (default: 0.01)");

        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine line = parser.parse(cmdOptions, args);
            if (line.hasOption("help")) {
                formatter.printHelp("versus", cmdOptions);
                System.out.println("give items to be compared as further arguments");
                System.exit(0);
            }
            if (line.hasOption("endpoint")) {
                set(OptKeys.ENDPOINT, line.getOptionValue("endpoint"));
            }
            if (line.hasOption("file")) {
                set(OptKeys.FILE_OUTPUT, "1");
                logger.info("Will write comparison output to file");
            }
            if (line.hasOption("threshold")) {
                set(OptKeys.THRESHOLD, line.getOptionValue("threshold"));
            }
            return line.getArgs();
        } catch (ParseException ex) {
            logger.warn(ex);
            return new String[0];
        }

    }

}
