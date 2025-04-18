package tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import options.ProgOpts;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.log4j.Logger;

import versus.Versus;

public abstract class SparqlQuerier {

    private static final Logger logger = Logger.getLogger(SparqlQuerier.class);

    protected int limit = 10000;
    protected int offset = 0;
    protected String query;
    protected String triplestore;
    protected String timeout = null;
    protected SparqlStatus status = SparqlStatus.WAIT;
    protected QueryExecution qexec = null;

    protected static int readTimeout = 3 * 60 * 1000;
    protected static int connectTimeout = 1 * 60 * 1000;

    public abstract void begin();

    public abstract void end();

    public abstract boolean fact(QuerySolution qs) throws InterruptedException;

    private static long lastTime = 0;
    public static long fairUseDelay = 50;

    public enum SparqlStatus {
        WAIT,
        BEGIN,
        SEND,
        RECEIVE,
        END
    };

    public static void configure(String filename) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            logger.warn(e);
        } catch (IOException e) {
            logger.error(e, e);
        }
        readTimeout = Integer.parseInt(ProgOpts.get(ProgOpts.OptKeys.READ_TIMEOUT)) * 1000;
        connectTimeout = Integer.parseInt(ProgOpts.get(ProgOpts.OptKeys.CONNECT_TIMEOUT)) * 1000;
        fairUseDelay = Integer.parseInt(ProgOpts.get(ProgOpts.OptKeys.FAIR_USE_DELAY));
        logger.info("fairUseDelay " + fairUseDelay);
    }

    public static void setFairUseDelay(long d) {
        fairUseDelay = d;
    }

    public SparqlQuerier(String query, String triplestore) {
        this.query = query;
        this.triplestore = triplestore;
    }

    public synchronized static void checkTime() {
        long currentTime = System.currentTimeMillis();
        long currentDelay = currentTime - lastTime;
        if (currentDelay < fairUseDelay) {
            try {
                lastTime = lastTime + fairUseDelay;
                Thread.sleep(fairUseDelay - currentDelay);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        } else {
            lastTime = currentTime;
        }
    }

    public void execute() throws InterruptedException {
        execute(Integer.MAX_VALUE, 0);
    }

    public void execute(int pageLimit) throws InterruptedException {
        execute(pageLimit, 0);
    }

    public void execute(int pageLimit, int offset) throws InterruptedException {
        Versus.incrementQueryNumber();
        try {
            status = SparqlStatus.BEGIN;
            begin();
            int k = 0;
            int success = 0;
            int pageNb = 0;
            do {
                pageNb++;
                k = 0;
                success = 0;
                String queryStr = ""
                        + query
                        + " LIMIT " + limit
                        + " OFFSET " + (limit * offset);
                Query query = QueryFactory.create(queryStr);
                if (fairUseDelay > 0) {
                    checkTime();
                }
                int queryId = (int) (Math.random() * 1000);
                logger.debug("query " + triplestore + ": (" + queryId + ") " + queryStr);
                status = SparqlStatus.SEND;
                qexec = QueryExecutionFactory.sparqlService(triplestore, query);
                qexec.setTimeout(readTimeout, connectTimeout);
                if (timeout != null) {
                    ((QueryEngineHTTP) qexec).addParam("timeout", timeout);
                }
                if (qexec != null) {
                    long tempo = 10000;
                    long sumTempo = 0;
                    int tries = 0;
                    int query_retries_max = Integer.parseInt(ProgOpts.get(ProgOpts.OptKeys.QUERY_RETRIES_MAX));
                    ResultSet resultSet = null;
                    while (true) {
                        try {
                            resultSet = qexec.execSelect();
                            break;
                        } catch (Exception e) {
                            logger.warn(e + " => " + tempo + " (" + queryId + ") " + queryStr);
                            if (tries++ >= query_retries_max) {
                                logger.error("abort query" + " (" + queryId + ")");
                                logger.warn("reached maximum  tries: " + query_retries_max);
                                Versus.incrementErrorNumber();
                                Versus.error_queries.add("`" + queryStr + "`");
                                if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                                    throw new InterruptedException("Stopping on reaching 'query_retries_max'");
                                }
                                break;
                            }
                            logger.warn("abort query, start retry #" + tries + " (" + queryId + ")");
                            //done = true;
                            //throw e;
                        }
                    }
                    status = SparqlStatus.RECEIVE;
                    if (resultSet != null) {
                        while (resultSet.hasNext()) {
                            QuerySolution qs = resultSet.nextSolution();
                            if (qs != null) {
                                if (fact(qs)) {
                                    success++;
                                }
                                k++;
                            }
                        }
                    }
                }
                close();
                offset++;
            } while (k >= limit && pageNb < pageLimit);
            status = SparqlStatus.END;
            end();
            status = SparqlStatus.WAIT;
        } catch (Exception e) {
            close();
            logger.warn(triplestore + " " + e, e);
            if (e instanceof InterruptedException) {
                throw e;
            }
        }
    }

    public void close() {
        if (qexec != null && !qexec.isClosed()) {
            qexec.abort();
            qexec.close();
            qexec = null;
        }
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public SparqlStatus getStatus() {
        return status;
    }

    @Deprecated
    public void safeExecute(long millis) throws InterruptedException {
        // not used at the moment
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    execute();
                } catch (InterruptedException e) {
                    close();
                    logger.info(e);

                }
            }

        });
        t.start();
        t.join(millis);
        if (getStatus() != SparqlStatus.SEND) {
            t.join();
        } else {
            close();
            t.interrupt();
        }
    }

    public static int getInDegree(String uri, String triplestore) throws InterruptedException {
        return new SparqlQuerier("select (COUNT(*) AS ?number) where {?s ?p <" + uri + ">}", triplestore) {

            private int inDegree;

            @Override
            public void begin() {
            }

            @Override
            public void end() {
            }

            @Override
            public boolean fact(QuerySolution qs) throws InterruptedException {
                inDegree = qs.getLiteral("number").getInt();
                return true;
            }

            public int getInDegree() throws InterruptedException {
                try {
                    execute();
                } catch (InterruptedException e) {
                    logger.error(e, e);
                    if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                        throw e;
                    }
                }
                return inDegree;
            }

        }.getInDegree();
    }

    public static String getLabel(String uri, String triplestore) throws InterruptedException {
        return new SparqlQuerier("select ?label where {<" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?label. FILTER(LANG(?label) = \"en\")}", triplestore) {

            private String label;

            @Override
            public void begin() {
            }

            @Override
            public void end() {
            }

            @Override
            public boolean fact(QuerySolution qs) throws InterruptedException {
                label = qs.getLiteral("label").getString();
                return true;
            }

            public String getLabel() throws InterruptedException {
                try {
                    execute();
                } catch (InterruptedException e) {
                    logger.error(e, e);
                    if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                        throw e;
                    }
                }
                return label;
            }

        }.getLabel();
    }

    public static String getInverseLabel(String uri, String triplestore) throws InterruptedException {
        return new SparqlQuerier("select ?label where {<" + uri + "> <http://www.wikidata.org/prop/direct/P7087> ?o . ?o <http://www.w3.org/2000/01/rdf-schema#label> ?label. FILTER(LANG(?label) = \"en\")}", triplestore) {

            private String label;

            @Override
            public void begin() {
            }

            @Override
            public void end() {
            }

            @Override
            public boolean fact(QuerySolution qs) throws InterruptedException {
                label = qs.getLiteral("label").getString();
                return true;
            }

            public String getLabel() throws InterruptedException {
                try {
                    execute();
                } catch (InterruptedException e) {
                    logger.error(e, e);
                    if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                        throw e;
                    }
                }
                return label;
            }

        }.getLabel();
    }

    public static String getDescription(String uri, String triplestore) throws InterruptedException {
        return new SparqlQuerier("select ?description where {<" + uri + "> <http://schema.org/description> ?description. FILTER(LANG(?description) = \"en\")}", triplestore) {

            private String description;

            @Override
            public void begin() {
            }

            @Override
            public void end() {
            }

            @Override
            public boolean fact(QuerySolution qs) throws InterruptedException {
                description = qs.getLiteral("description").getString();
                return true;
            }

            public String getDescription() throws InterruptedException {
                try {
                    execute();
                } catch (InterruptedException e) {
                    logger.error(e, e);
                    if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                        throw e;
                    }
                }
                return description;
            }

        }.getDescription();
    }

    public static String getDomain(String uri, String triplestore) throws InterruptedException {
        return new SparqlQuerier("select * where {select ?c (count(*) as ?nb) where {select ?s ?c where {?s <" + uri + "> ?o . ?s <http://www.wikidata.org/prop/direct/P31> ?c} limit 1000} group by ?c order by desc(?nb) limit 1}", triplestore) {

            private String domain;

            @Override
            public void begin() {
            }

            @Override
            public void end() {
            }

            @Override
            public boolean fact(QuerySolution qs) throws InterruptedException {
                domain = qs.getResource("c").toString();
                return true;
            }

            public String getDomain() throws InterruptedException {
                try {
                    execute();
                } catch (InterruptedException e) {
                    logger.error(e, e);
                    if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                        throw e;
                    }
                }
                return domain;
            }

        }.getDomain();
    }

    public static String getRange(String uri, String triplestore) throws InterruptedException {
        return new SparqlQuerier("select * where {select ?c (count(*) as ?nb) where {select ?s ?c where {?s <" + uri + "> ?o . ?o <http://www.wikidata.org/prop/direct/P31> ?c} limit 1000} group by ?c order by desc(?nb) limit 1}", triplestore) {

            private String range;

            @Override
            public void begin() {
            }

            @Override
            public void end() {
            }

            @Override
            public boolean fact(QuerySolution qs) throws InterruptedException {
                range = qs.getResource("c").toString();
                return true;
            }

            public String getRange() throws InterruptedException {
                try {
                    execute();
                } catch (InterruptedException e) {
                    logger.error(e, e);
                    if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                        throw e;
                    }
                }
                return range;
            }

        }.getRange();
    }

}
