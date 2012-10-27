import hierarchy.Category;
import hierarchy.HierarchyBuilder;

import java.util.HashSet;
import java.util.Set;

import classifier.DatabaseClassifier;
import database.Database;
import database.SearchDatabase;
import searcher.BingSearchProvider;
import searcher.SearchProvider;
import summarizer.ContentSummarizer;
import util.Logger;
import util.Logger.MsgType;


public class Test {
	private static String rulefile = "rules.txt";
	
	public static void main(String[] args) {
		// read arguments
		
		Logger myLogger = Logger.getInstance();
		
		if (args.length < 4) {
			System.err.println("Usage: DatabaseClassifier <ApiKey> <min_speciality> <min_coverage> <hostname>");
			System.exit(1);
		}
		String apiKey = args[0];
		
		double mincoverage = 1, minspeciality = 1;
		
		try {
			minspeciality = Double.parseDouble(args[1]);
			mincoverage = Double.parseDouble(args[2]);
			
			if (minspeciality < 0 || minspeciality > 1) {
				System.err.println("Please input a minspeciality between [0,1]");
				System.exit(1);	
			}
			
			if (mincoverage < 1) {
				System.err.println("Mincoverage value can't be less than 1");
				System.exit(1);	
			}
			
		}
		catch (NumberFormatException e) {
			System.err.println("Please input a valid argument");
			System.exit(1);			
		}
		String host = args[3];
		
		// initialize classifier
		Category root = HierarchyBuilder.buildHierarchy(rulefile);
		
		//tree is printed in the debug file
		root.printTree(0);
		
		DatabaseClassifier classifier = new DatabaseClassifier(apiKey, root);
		
		System.out.println("Classifying...");
		
		// classify
		SearchProvider provider = new BingSearchProvider(apiKey, 10); // topK doesn't matter
		Database db = new SearchDatabase(host, provider);
		Set<Category> categories = classifier.classify(host, db, minspeciality, mincoverage);
		
		// output result
		System.out.println("Result: ");
		for (Category c : categories)
			System.out.println(c);
		
		db.close();
		
		System.out.println("Preparing Content Summary...");
		
		// content summary
		ContentSummarizer summarizer = new ContentSummarizer(host);
		
		//content summaries need to be evaluated for all the categories determined for the host
		for (Category c : categories) {
			// no query associated with leaf category, starting from its parent
			if (c.isLeaf())
				c = c.getParent();
			Set<String> samples = new HashSet<String>();
			myLogger.write("Preparing Summary for: " + c.getName(), MsgType.LOG);
			do {
				samples.addAll(classifier.getSampleUrls(c));
				summarizer.summarize(c, host, samples);
				myLogger.write("urls for " + c.getName() + ": " + samples.size() + " pages", MsgType.LOG);
				for (String url : samples) {
					myLogger.write(url, MsgType.LOG);
				}
				myLogger.write("----------", MsgType.LOG);
				c = c.getParent();
			} while (c != null);
		}
		summarizer.close();
	}

}
