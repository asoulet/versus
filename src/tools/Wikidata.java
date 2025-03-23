package tools;

import options.ProgOpts;
import org.apache.jena.query.QuerySolution;
import org.apache.log4j.Logger;

public class Wikidata {

    private static final Logger logger = Logger.getLogger(Wikidata.class);

    public final static String endpoint = "https://query.wikidata.org/sparql";

    public static WikidataItem getWikipediaItem(String uri, String lang) throws InterruptedException {
        return new SparqlQuerier("select ?wp ?title where {?wp <http://schema.org/about> <" + uri + "> . ?wp <http://schema.org/isPartOf> <https://" + lang + ".wikipedia.org/>. ?wp <http://schema.org/name> ?title}", Wikidata.endpoint) {

            private WikidataItem item = null;

            @Override
            public void begin() {
            }

            @Override
            public void end() {
            }

            @Override
            public boolean fact(QuerySolution qs) throws InterruptedException {
                String wp = qs.getResource("wp").toString();
                String title = qs.getLiteral("title").getString();
                item = new WikidataItem(wp, title);
                return true;
            }

            public WikidataItem getWikidataItem() throws InterruptedException {
                try {
                    execute();
                } catch (InterruptedException e) {
                    logger.error(e, e);
                    if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                        throw e;
                    }
                }
                return item;
            }

        }.getWikidataItem();
    }

}
