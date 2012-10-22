package classifier;

import hierarchy.Category;
import hierarchy.HierarchyBuilder;
import hierarchy.Rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import searcher.BingSearchProvider;
import searcher.SearchProvider;
import database.Database;
import database.SearchDatabase;

public class DatabaseClassifier {
	private static String rulefile = "rules.txt";
	private static String apiKey;
	
	private Category root = null;
	
	public DatabaseClassifier(Category hierarchy) {
		root = hierarchy;
	}
	
	public static void main(String[] args) {
		/* read arguments */		
		if (args.length < 4) {
			System.err.println("Usage: DatabaseClassifier <ApiKey> <min_speciality> <min_coverage> <hostname>");
			System.exit(1);
		}
		apiKey = args[0];
		
		double mincoverage = 1, minspeciality = 1;
		try {
			minspeciality = Double.parseDouble(args[1]);
			mincoverage = Double.parseDouble(args[2]);
		}
		catch (NumberFormatException e) {
			System.err.println("Please input a valid argument");
			System.exit(1);			
		}
		String host = args[3];
		
		
		// initialize classifier
		
		Category root = HierarchyBuilder.buildHierarchy(rulefile);
		DatabaseClassifier classifier = new DatabaseClassifier(root);
		
		// classify
		Set<Category> categories = classifier.classify(host, minspeciality, mincoverage);
		
		// output result
		for (Category c : categories)
			System.out.println(c);
		
	}
	
	/**
	 * Implements the algorithm in Fig. 4
	 * @param parent
	 * @param d
	 * @param minspeciality
	 * @param mincoverage
	 * @param parentSpeciality
	 * @return
	 */
	private Set<Category> doClassify(Category parent, Database d, double minspeciality, double mincoverage, double parentSpeciality) {
		
		Set<Category> result = new HashSet<Category>();
		
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
			int matches = d.probe(rule.getKeywords());
			coverages.put(rule.getCategory(), coverages.get(rule.getCategory()) + matches);
		}
		
		// calculate ESpeciality
		int totalCoverage = 0;
		for (Integer c : coverages.values()) {
			totalCoverage += c;
		}		
		Map<Category, Double> specialities = new HashMap<Category, Double>();
		for (Category sub : parent.getChildren()) {
			specialities.put(sub, calculateSpeciality(coverages.get(sub),parentSpeciality, totalCoverage));
		}
		
		// recursive push down
		for (Category sub : parent.getChildren()) {
			if (specialities.get(sub) > minspeciality && coverages.get(sub) > mincoverage) {
				result.addAll(doClassify(sub, d, minspeciality, mincoverage, specialities.get(sub)));				
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

	public Set<Category> classify(String host, double speciality, double coverage) {
		// construct database;
		SearchProvider provider = new BingSearchProvider(apiKey, 10); // topK doesn't matter
		Database d = new SearchDatabase(host, provider);
					
		return doClassify(root, d, speciality, coverage, 1);
	}	
	
}
