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
        CONNECT_TIMEOUT,
        DIRECTORY,
        ENDPOINT,
        FAIR_USE_DELAY,
        MARKDOWN,
        QUERY_RETRIES_MAX,
        READ_TIMEOUT,
        STOP_ON_ERRORS,
        THRESHOLD,
        WRITE_FILE,
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

    public static String[] parseOptions(String[] args) {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(versus.Versus.CONFIGURATION));
        } catch (FileNotFoundException e) {
            logger.warn(e);
        } catch (IOException e) {
            logger.error(e, e);
        }

        logger.info("setting options from properties file (or defaulting if not set there)");
        set(OptKeys.CONNECT_TIMEOUT, properties.getProperty("connect_timeout", "60"));
        set(OptKeys.DIRECTORY, properties.getProperty("directory", "./"));
        set(OptKeys.ENDPOINT, properties.getProperty("endpoint", Wikidata.endpoint));
        set(OptKeys.FAIR_USE_DELAY, properties.getProperty("fair_use_delay", "50"));
        set(OptKeys.MARKDOWN, properties.getProperty("markdown", "0"));
        set(OptKeys.QUERY_RETRIES_MAX, properties.getProperty("query_retries_max", "12"));
        set(OptKeys.READ_TIMEOUT, properties.getProperty("read_timeout", "60"));
        set(OptKeys.STOP_ON_ERRORS, properties.getProperty("stop_on_errors", "0"));
        set(OptKeys.THRESHOLD, properties.getProperty("threshold", "0.01"));
        set(OptKeys.WRITE_FILE, properties.getProperty("write_file", "0"));

        logger.info("setting options from command line");
        CommandLineParser parser = new DefaultParser();
        cmdOptions.addOption("c", "connect_timeout", true, "seconds to timeout SPARQL connections (default: '60')");
        cmdOptions.addOption("d", "directory", true, "directory to use for file output, relative to execution dir (default: '../')");
        cmdOptions.addOption("e", "endpoint", true, "endpoint to use (default: " + Wikidata.endpoint + ")");
        cmdOptions.addOption("f", "fair_use_delay", true, "minimum milliseconds to delay between queries for fair use (default: '50')");
        cmdOptions.addOption("h", "help", false, "this help message");
        cmdOptions.addOption("m", "markdown", false, "use markdown for comparison table output instead of plain text table");
        cmdOptions.addOption("q", "query_retries_max", true, "maximum number of retries if queries fail on server side (default: 12))");
        cmdOptions.addOption("r", "read_timeout", true, "seconds to timeout SPARQL readings (default: '60')");
        cmdOptions.addOption("s", "stop_on_errors", false, "stop generating current versus if more errors occur, than 'query_retries_max' allows");
        cmdOptions.addOption("t", "threshold", true, "threshold to use (default: 0.01)");
        cmdOptions.addOption("w", "write_file", false, "write result to file, instead of stdout");

        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine line = parser.parse(cmdOptions, args);
            if (line.hasOption("help")) {
                formatter.printHelp("versus", cmdOptions);
                System.out.println("give items to be compared as further arguments");
                System.exit(0);
            }
            if (line.hasOption("connect_timeout")) {
                set(OptKeys.CONNECT_TIMEOUT, line.getOptionValue("connect_timeout"));
            }
            if (line.hasOption("directory")) {
                set(OptKeys.DIRECTORY, line.getOptionValue("directory"));
            }
            if (line.hasOption("endpoint")) {
                set(OptKeys.ENDPOINT, line.getOptionValue("endpoint"));
            }
            if (line.hasOption("fair_use_delay")) {
                set(OptKeys.FAIR_USE_DELAY, line.getOptionValue("fair_use_delay"));
            }
            if (line.hasOption("markdown")) {
                set(OptKeys.MARKDOWN, "1");
            }
            if (line.hasOption("query_retries_max")) {
                set(OptKeys.QUERY_RETRIES_MAX, line.getOptionValue("query_retries_max"));
            }
            if (line.hasOption("read_timeout")) {
                set(OptKeys.READ_TIMEOUT, line.getOptionValue("read_timeout"));
            }
            if (line.hasOption("stop_on_errors")) {
                set(OptKeys.STOP_ON_ERRORS, "1");
            }
            if (line.hasOption("threshold")) {
                set(OptKeys.THRESHOLD, line.getOptionValue("threshold"));
            }
            if (line.hasOption("write_file")) {
                set(OptKeys.WRITE_FILE, "1");
            }
            return line.getArgs();
        } catch (ParseException ex) {
            logger.warn(ex, ex);
            System.exit(1);
            return new String[0];   // stop IDE nagging about no return statement
        }

    }

}
