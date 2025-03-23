package comparison;

import options.ProgOpts;
import org.apache.jena.query.QuerySolution;
import org.apache.log4j.Logger;

import tools.SparqlQuerier;
import versus.Versus;

public class Selector {

    private static Logger logger = Logger.getLogger(Selector.class);

    protected ComplexProperty complexProperty;
    protected String object;
    protected int count = 0;
    protected int contextualCount = 0;

    public Selector(ComplexProperty property, String object) {
        this.complexProperty = property;
        this.object = object;
    }

    public String getFilter() {
        return complexProperty.getFilter(object);
    }

    public void computeCount() throws InterruptedException {
        try {
            new SparqlQuerier("select (count(*) as ?c) where {" + getFilter() + "}", Versus.ENDPOINT) {

                @Override
                public boolean fact(QuerySolution qs) throws InterruptedException {
                    if (qs != null && qs.get("c") != null) {
                        count = qs.getLiteral("c").getInt();
                        return true;
                    }
                    return false;
                }

                @Override
                public void end() {
                }

                @Override
                public void begin() {
                }
            }.execute();
        } catch (InterruptedException e) {
            logger.error(e, e);
            if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                throw e;
            }
        }
    }

    public ComplexProperty getComplexProperty() {
        return complexProperty;
    }

    public String getObject() {
        return object;
    }

    public int getCount() {
        return count;
    }

    public int getContextualCount(Context context) throws InterruptedException {
        try {
            new SparqlQuerier("select (count(*) as ?c) where { " + getFilter() + " ." + context.getFilter() + "}", Versus.ENDPOINT) {

                @Override
                public boolean fact(QuerySolution qs) throws InterruptedException {
                    if (qs != null && qs.get("c") != null) {
                        contextualCount = qs.getLiteral("c").getInt();
                        return true;
                    }
                    return false;
                }

                @Override
                public void end() {
                }

                @Override
                public void begin() {
                }
            }.execute();
        } catch (InterruptedException e) {
            logger.error(e, e);
            if (ProgOpts.get(ProgOpts.OptKeys.STOP_ON_ERRORS).equals("1")) {
                throw e;
            }
        }
        return contextualCount;
    }

    @Override
    public String toString() {
        return "Selector [property=" + complexProperty + ", object=" + object + "]";
    }

}
