package comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import options.ProgOpts;

import org.apache.log4j.Logger;

import tools.SparqlQuerier;
import versus.Versus;

public class FeatureComparison {

    private static Logger logger = Logger.getLogger(FeatureComparison.class);

    public static int CHAR_NUMBER = 45;

    private ComplexProperty complexProperty;
    private double probability = 0;
    private HashMap<String, ArrayList<String>> entityObjects = new HashMap<String, ArrayList<String>>();
    private HashMap<String, FeatureEntity> featureEntities = new HashMap<String, FeatureEntity>();

    public FeatureComparison(ComplexProperty property) {
        this.complexProperty = property;
    }

    public void addObject(String object, ArrayList<String> entities) {
        for (String entity : entities) {
            addEntityObject(entity, object);
        }
    }

    private void addEntityObject(String entity, String object) {
        ArrayList<String> objects = entityObjects.get(entity);
        if (objects == null) {
            objects = new ArrayList<String>();
        }
        objects.add(object);
        entityObjects.put(entity, objects);

        FeatureEntity fe = featureEntities.get(entity);
        if (fe == null) {
            fe = new FeatureEntity(entity, complexProperty);
        }
        fe.addObject(object);
        featureEntities.put(entity, fe);
    }

    public void computeProbabilityAdvancedPruning(ArrayList<Context> contexts, double threshold) throws InterruptedException {
        probability = 1;
        double optimistic = 1;
        ArrayList<FeatureEntity> fes = new ArrayList<FeatureEntity>();
        for (Entry<String, FeatureEntity> kv : featureEntities.entrySet()) {
            FeatureEntity fe = kv.getValue();
            fe.computeCount();
            if (fe.getCount() - 1 > 0) {
                for (Context context : contexts) {
                    if (!context.getComplexProperty().getUri().equals(complexProperty.getUri())) {
                        optimistic *= (1 - ((double) fe.getCount() - 1) / Integer.max(context.getCount() - 1, fe.getCount() - 1));
                    }
                }
                fes.add(fe);
            }
        }
        Collections.sort(fes, new Comparator<FeatureEntity>() {
            @Override
            public int compare(FeatureEntity o1, FeatureEntity o2) {
                if (o1.getCount() > o2.getCount()) {
                    return -1;
                } else if (o2.getCount() > o1.getCount()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        for (FeatureEntity fe : fes) {
            fe.computeCount();
            if (fe.getCount() - 1 > 0) {
                for (Context context : contexts) {
                    if (!context.getComplexProperty().getUri().equals(complexProperty.getUri())) {
                        int count = context.getCount() - 1;
                        int contextualCount = fe.getContextualCount(context) - 1;
                        probability *= (1 - ((double) contextualCount) / count);
                        logger.trace("# " + complexProperty + " " + fe.getEntity() + " " + " " + context.getComplexProperty() + " " + (1 - ((double) contextualCount) / count) + " " + probability);
                        optimistic = optimistic / (1 - ((double) fe.getCount() - 1) / Integer.max(context.getCount() - 1, fe.getCount() - 1));
                        if (1 - optimistic * probability < threshold) {
                            logger.trace("reject " + complexProperty + " " + probability + " " + optimistic);
                            probability = 1 - optimistic * probability;
                            return;
                        }
                        if (1 - probability > threshold) {
                            probability = 1 - probability;
                            logger.trace("select " + complexProperty + " " + probability);
                            return;
                        }
                    }
                }
            }
        }
        probability = 1 - probability;
        logger.trace(complexProperty + " " + probability);
    }

    public double getProbability() {
        return probability;
    }

    @Override
    public String toString() {
        return "FeatureComparison [property=" + complexProperty + ", probability=" + probability
                + "]";
    }

    public void show(ArrayList<String> entities) throws InterruptedException {
        int max = 1;
        for (String entity : entities) {
            ArrayList<String> objects = entityObjects.get(entity);
            if (objects != null) {
                max = Integer.max(max, objects.size());
            }
        }
        boolean numerical = false;
        if (max > 10) {
            numerical = true;
            max = 1;
        }
        for (int i = 0; i < max; i++) {
            System.out.println(row(entities, i, numerical) + (i == 0 ? " -> " + probability : ""));
        }
    }

    public String row(ArrayList<String> entities, int r, boolean numerical) throws InterruptedException {
        String str = "| ";
        if (r == 0) {
            if (ProgOpts.get(ProgOpts.OptKeys.MARKDOWN).equals("1")) {
                str += right((numerical ? "#" : "") + getLabel(complexProperty.getUri().replace("prop/direct", "entity")) + " ([" + getId(complexProperty.getUri()) + "]" + "(" + complexProperty.getUri() + "))", CHAR_NUMBER);
            } else {
                str += right((numerical ? "#" : "") + getLabel(complexProperty.getUri().replace("prop/direct", "entity")) + " (" + getId(complexProperty.getUri()) + ")", CHAR_NUMBER);
            }
        } else {
            str += space("", CHAR_NUMBER);
        }
        for (String entity : entities) {
            str += " | ";
            ArrayList<String> objects = entityObjects.get(entity);
            if (objects != null && r < objects.size()) {
                if (!numerical) {
                    if (ProgOpts.get(ProgOpts.OptKeys.MARKDOWN).equals("1")) {
                        // include link for markdown
                        str += left(getLabel(objects.get(r)) + " ([" + getId(objects.get(r)) + "]" + "(" + objects.get(r) + "))", CHAR_NUMBER);
                    } else {
                        str += left(getLabel(objects.get(r)) + " (" + getId(objects.get(r)) + ")", CHAR_NUMBER);
                    }
                } else {
                    str += left(Integer.toString(objects.size()), CHAR_NUMBER);
                }
            } else {
                str += space("", CHAR_NUMBER);
            }
        }
        str += " |";
        return str;
    }

    public static String getLabel(String uri) throws InterruptedException {
        String label = SparqlQuerier.getLabel(uri, Versus.ENDPOINT);
        if (label == null) {
            return "";
        } else {
            return label;
        }
    }

    public static String getId(String uri) {
        return uri.substring(uri.lastIndexOf("/") + 1);
    }

    public static String space(String text, int n) {
        String space = "";
        for (int i = text.length(); i < n; i++) {
            space += " ";
        }
        return space;
    }

    public static String line(int n) {
        String str = "";
        for (int i = 0; i < n; i++) {
            str += "-";
        }
        return str;
    }

    public static String right(String text, int n) {
        if (ProgOpts.get(ProgOpts.OptKeys.MARKDOWN).equals("1")) {
            return text;
        }
        if (text.length() > n + 1) {
            return text.substring(0, n);
        } else {
            return space(text, n) + text;
        }
    }

    public static String left(String text, int n) {
        if (ProgOpts.get(ProgOpts.OptKeys.MARKDOWN).equals("1")) {
            return text;
        }
        if (text.length() > n + 1) {
            return text.substring(0, n);
        } else {
            return text + space(text, n);
        }
    }

}
