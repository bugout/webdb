import hierarchy.Category;
import hierarchy.HierarchyBuilder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import query.ProbeResult;

import classifier.DatabaseClassifier;
import database.Database;
import database.SearchDatabase;
import searcher.BingSearchProvider;
import searcher.SearchProvider;
import summarizer.ContentSummarizer;


public class Test {
	private static String rulefile = "rules.txt";
	
	public static void main(String[] args) {
		// read arguments		
		if (args.length < 4) {
			System.err.println("Usage: DatabaseClassifier <ApiKey> <min_speciality> <min_coverage> <hostname>");
			System.exit(1);
		}
		String apiKey = args[0];
		
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
				
		root.printTree(0);
		
		DatabaseClassifier classifier = new DatabaseClassifier(apiKey, root);
		
		
		// classify
		SearchProvider provider = new BingSearchProvider(apiKey, 10); // topK doesn't matter
		Database db = new SearchDatabase(host, provider);
		Set<Category> categories = classifier.classify(host, db, minspeciality, mincoverage);
		
		// output result
		System.out.println("Result: ");
		for (Category c : categories)
			System.out.println(c);
		
		db.close();
		
		
		// content summary
		ContentSummarizer summarizer = new ContentSummarizer(host);
		for (Category c : categories) {
			// no query associated with leaf category, starting from its parent
			if (c.isLeaf())
				c = c.getParent();
			Set<String> samples = new HashSet<String>();
			do {
				samples.addAll(classifier.getSampleUrls(c));
				summarizer.summarize(c, host, samples);
				System.out.println("urls for " + c.getName() + " " + samples.size() + " pages");
				for (String url : samples) {
					System.out.println(url);
				}
				System.out.println("----------");
				c = c.getParent();
			} while (c != null);
		}
		summarizer.close();
	}

}
