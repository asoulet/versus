package comparison;

import java.util.ArrayList;

import org.apache.jena.query.QuerySolution;
import org.apache.log4j.Logger;

import tools.SparqlQuerier;
import versus.Versus;

public class FeatureEntity {
	
	private static Logger logger = Logger.getLogger(FeatureEntity.class);

	private String entity;
	private ComplexProperty complexProperty;
	private ArrayList<String> objects = new ArrayList<String>();
	protected int count = 0;
	protected int contextualCount = 0;

	public FeatureEntity(String entity, ComplexProperty complexProperty) {
		this.entity = entity;
		this.complexProperty = complexProperty;
	}
	
	public void addObject(String object) {
		this.objects.add(object);
	}
	
	public void computeCount() {
		count = 1;
		String queryStr = "select (count(DISTINCT ?s) as ?c) where { ?s <" + complexProperty.getUri() + "> ?o . <" + entity + "> <" + complexProperty.getUri() + "> ?o}";
		if (complexProperty.isInverse())
			queryStr = "select (count(DISTINCT ?s) as ?c) where { ?o <" + complexProperty.getUri() + "> ?s . ?o <" + complexProperty.getUri() + "> <" + entity + ">}";
		try {
			new SparqlQuerier(queryStr, Versus.ENDPOINT) {
				
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
			logger.error(e,e);
		}
	}

	public int getContextualCount(Context context) {
		contextualCount = 1;
		String queryStr = "select (count(DISTINCT ?s) as ?c) where { ?s <" + complexProperty.getUri() + "> ?o . " + context.getFilter()+ " . <" + entity + "> <" + complexProperty.getUri() + "> ?o}";
		if (complexProperty.isInverse())
			queryStr = "select (count(DISTINCT ?s) as ?c) where { ?o <" + complexProperty.getUri() + "> ?s . " + context.getFilter()+ " . ?o <" + complexProperty.getUri() + "> <" + entity + ">}";
		try {
			new SparqlQuerier(queryStr, Versus.ENDPOINT) {
				
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
			logger.error(e,e);
		}
		return contextualCount;
	}

	public int getCount() {
		return count;
	}

	public String getEntity() {
		return entity;
	}

}
