package classifier;

import hierarchy.Category;
import hierarchy.HierarchyBuilder;
import hierarchy.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import query.ProbeResult;
import query.QueryRecord;

import searcher.BingSearchProvider;
import searcher.SearchProvider;
import util.Logger;
import util.Logger.MsgType;
import database.Database;
import database.SearchDatabase;

public class DatabaseClassifier {

	private String apiKey;	
	private Category root = null;
	
	private Map<Category, Set<String>> sampleUrls = null;
	
	private Logger myLogger = Logger.getInstance();
	
	public DatabaseClassifier(String apiKey, Category hierarchy) {
		root = hierarchy;
		this.apiKey = apiKey;
		sampleUrls = new HashMap<Category, Set<String>>();
	}
	
	/**
	 * Implements the algorithm in Fig. 4
	 * @param parent
	 * @param db
	 * @param minspeciality
	 * @param mincoverage
	 * @param parentSpeciality
	 * @return
	 */
	private Set<Category> doClassify(Category parent, Database db, double minspeciality, double mincoverage, double parentSpeciality) {
		
		myLogger.write("Analyzing Node: " + parent.getName(), MsgType.LOG);
		
		for ( Category sub : parent.getChildren() ) 
			myLogger.write("  SubCategory: " + sub.getName(), MsgType.LOG);
		
		Set<Category> result = new HashSet<Category>();
		
		//if parent category is a leaf node, there are no subcategories to analyze
		if (parent.isLeaf()) {
			result.add(parent);
			return result;
		}
		
		// calculate ECoverage
		Map<Category, Integer> coverages = new HashMap<Category, Integer>();
		for (Category sub : parent.getChildren()) {
			coverages.put(sub, 0);
		}		
		for (Rule rule : parent.getRules()) {
			ProbeResult pr = db.probe(rule.getKeywords());
			
			//log error and move on to the next query
			if (pr == null)
				myLogger.write("Probing failed for " + rule.toString(), MsgType.DEBUG);
			else {
				int matches = pr.getMatch();
				coverages.put(rule.getCategory(), coverages.get(rule.getCategory()) + matches);
			}
			
		}
		
		collectSamples(db, parent);
		
		// calculate ESpeciality
		int totalCoverage = 0;
		for (Integer c : coverages.values()) {
			totalCoverage += c;
		}
		
		myLogger.write("Total Coverage: " + totalCoverage, MsgType.LOG);
		
		Map<Category, Double> specialities = new HashMap<Category, Double>();
		for (Category sub : parent.getChildren()) {
			specialities.put(sub, calculateSpeciality(coverages.get(sub),parentSpeciality, totalCoverage));
		}
		
		// recursive push down
		for (Category sub : parent.getChildren()) {
			if (specialities.get(sub) > minspeciality && coverages.get(sub) > mincoverage) {							
				result.addAll(doClassify(sub, db, minspeciality, mincoverage, specialities.get(sub)));	
			}
		}
		
		
		if (result.isEmpty()) {
			result.add(parent);
			return result;
		}
		else
			return result;
	}
	
	private double calculateSpeciality(Integer myCoverage,
			double parentSpeciality, int totalCoverage) {
		return parentSpeciality * myCoverage / totalCoverage;
	}

	public Set<Category> classify(String host, Database db, double speciality, double coverage) {
		return doClassify(root, db, speciality, coverage, 1);
	}	
	
	private void collectSamples(Database db, Category c) {
		if (sampleUrls.containsKey(c))
			return;
		
		Set<String> urls = new HashSet<String>();
		
		for (Rule rule : c.getRules()) {
			ProbeResult pr = db.probe(rule.getKeywords());
			Vector<QueryRecord> records = pr.getRecords();
			
			int sampleSize = Math.min(4, records.size());
			for (int i = 0; i < sampleSize; i++) {
				urls.add(records.get(i).getUrl());
			}
		}
		sampleUrls.put(c, urls);
	}
	
	public Set<String> getSampleUrls(Category c) {
		// copy constructor
//		Set<String> urls = new HashSet<String>(sampleUrls.get(c));
//		while (!c.isLeaf()) {
//			 c.getChildren();
//			urls.addAll(sampleUrls.get(c));
//		}		
		return sampleUrls.get(c);
	}
	
}
