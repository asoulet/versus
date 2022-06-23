package comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class Property {

    private static Logger logger = Logger.getLogger(Property.class);

    private ComplexProperty complexProperty;
    private HashMap<String, ArrayList<String>> objects = new HashMap<String, ArrayList<String>>();

    private int MAX_OBJECTS = Integer.MAX_VALUE;

    public Property(ComplexProperty complexProperty) {
        this.complexProperty = complexProperty;
    }

    public Property(String property, boolean inverse) {
        this.complexProperty = new ComplexProperty(property, inverse);
    }

    public void addObject(String object, String entity) {
        if (objects.size() < MAX_OBJECTS) {
            ArrayList<String> entities = objects.get(object);
            if (entities == null) {
                entities = new ArrayList<String>();
            }
            entities.add(entity);
            objects.put(object, entities);
        }
    }

    public ArrayList<Context> getContexts(int n) throws InterruptedException {
        ArrayList<Context> contexts = new ArrayList<Context>();
        for (Entry<String, ArrayList<String>> object : objects.entrySet()) {
            if (object.getValue().size() == n && !complexProperty.isInverse()) {
                logger.trace("new context: " + complexProperty + " " + object.getKey());
                contexts.add(new Context(complexProperty, object.getKey()));
            }
        }
        return contexts;
    }

    public FeatureComparison getFeatureComparisons(int n) {
        ArrayList<String> features = new ArrayList<String>();
        for (Entry<String, ArrayList<String>> object : objects.entrySet()) {
            if (object.getValue().size() <= n) { // < n for discriminant comparison table (i.e., with no similar values for compared entities
                features.add(object.getKey());
            }
        }
        if (features.size() > 0) {// && entities.size() == n) {
            logger.trace("add feature: " + complexProperty + " " + features);
            FeatureComparison fc = new FeatureComparison(complexProperty);
            for (String feature : features) {
                fc.addObject(feature, objects.get(feature));
            }
            return fc;
        } else {
            return null;
        }
    }

}
