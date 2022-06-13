package options;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
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
        FILE_OUTPUT, ENDPOINT, THRESHOLD, MARKDOWN, DIRECTORY
    }

    public static String get(OptKeys optKey) {
        String key = optKey.toString();
        return opts.get(key);
    }

    public static void set(OptKeys optKey, String optValue) {
        String key = optKey.toString();
        logger.debug("setting option '" + key + "' to '" + optValue + "'");
        opts.put(key, optValue);
    }

    public static String[] commandLineOptions(String[] args) {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(versus.Versus.CONFIGURATION));
        } catch (FileNotFoundException e) {
            logger.warn(e);
        } catch (IOException e) {
            logger.error(e, e);
        }

        logger.info("setting options from properties file (or defaulting if not set there)");
        set(OptKeys.ENDPOINT, properties.getProperty("endpoint", Wikidata.endpoint));
        set(OptKeys.DIRECTORY, properties.getProperty("directory", "./"));
        set(OptKeys.FILE_OUTPUT, properties.getProperty("file", "0"));
        set(OptKeys.MARKDOWN, properties.getProperty("markdown", "0"));
        set(OptKeys.THRESHOLD, properties.getProperty("threshold", "0.01"));

        logger.info("setting options from command line");
        CommandLineParser parser = new DefaultParser();
        cmdOptions.addOption("d", "directory", true, "directory to use for file output, relative to execution dir (default: '../')");
        cmdOptions.addOption("e", "endpoint", true, "endpoint to use (default: " + Wikidata.endpoint + ")");
        cmdOptions.addOption("f", "file", false, "write result to file, instead of stdout");
        cmdOptions.addOption("h", "help", false, "this help message");
        cmdOptions.addOption("m", "markdown", false, "use markdown for comparison table instead of plain text table");
        cmdOptions.addOption("t", "threshold", true, "threshold to use (default: 0.01)");

        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine line = parser.parse(cmdOptions, args);
            if (line.hasOption("help")) {
                formatter.printHelp("versus", cmdOptions);
                System.out.println("give items to be compared as further arguments");
                System.exit(0);
            }
            if (line.hasOption("directory")) {
                set(OptKeys.DIRECTORY, line.getOptionValue("directory"));
            }
            if (line.hasOption("endpoint")) {
                set(OptKeys.ENDPOINT, line.getOptionValue("endpoint"));
            }
            if (line.hasOption("file")) {
                set(OptKeys.FILE_OUTPUT, "1");
            }
            if (line.hasOption("markdown")) {
                set(OptKeys.MARKDOWN, "1");
            }
            if (line.hasOption("threshold")) {
                set(OptKeys.THRESHOLD, line.getOptionValue("threshold"));
            }
            return line.getArgs();
        } catch (ParseException ex) {
            logger.warn(ex, ex);
            System.exit(1);
            return new String[0];   // stop IDE nagging about no return statement
        }

    }

}
