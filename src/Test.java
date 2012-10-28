import hierarchy.Category;
import hierarchy.HierarchyBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
		
		System.out.println("Classification in progress (run.log captures the execution)...");
		
		// classify
		SearchProvider provider = new BingSearchProvider(apiKey, 10); // topK doesn't matter
		Database db = new SearchDatabase(host, provider);
		Set<Category> categories = classifier.classify(host, db, minspeciality, mincoverage);
		
		// output result
		System.out.println("Result: ");
		for (Category c : categories)
			System.out.println("    " + c);
		
		db.close();
		
		System.out.println("Content Summary Classification in progress (run.log captures the execution)...");
		
		// content summary
		ContentSummarizer summarizer = new ContentSummarizer(host);
		
		//content summaries need to be evaluated for all the categories determined for the host
		/*
		 * this algorithm works for our case, for the given category tree structure we have
		 * It can be modified to make it generic
		 * 
		 */
		Map<String, Set<String>> categorySamples = new HashMap<String, Set<String>>();
		
		for (Category c : categories) {
			
			if (c.isLeaf()) {
				c = c.getParent();
				if (categorySamples.containsKey(c.getName())) 
					c = null;
			}
										
			
			Set<String> tempSamples = new HashSet<String>();
			while (c != null)
			{
				//Logger.getInstance().write(c + " " + classifier.getSampleUrls(c).size(), MsgType.LOG);
				
				Set<String> samples = new HashSet<String>();
				samples.addAll(tempSamples);
				samples.addAll( classifier.getSampleUrls(c) );
				
				if (categorySamples.get(c.getName()) == null)
				{	
					categorySamples.put(c.getName(), samples);
					tempSamples.addAll(samples);
				}
				else {
					samples.addAll(categorySamples.get(c.getName()));
					categorySamples.put(c.getName(), samples);
				}
				c = c.getParent();
			}
		}
		
		//for (String s : categorySamples.keySet())
		//{
		//	Set<String> theSamples = categorySamples.get(s);
		//	Logger.getInstance().write(s + " " + theSamples.size(), MsgType.LOG);
		//}
		//now go through categorySamples and generate the metadata summary for each category 
		for (String s : categorySamples.keySet())
		{
			Category theCategory = root.getCategoryByName(s);
			Set<String> theSamples = categorySamples.get(s);
			summarizer.summarize(theCategory, host, theSamples);
			myLogger.write("urls for " + theCategory.getName() + ": " + theSamples.size() + " pages", MsgType.LOG);
			for (String url : theSamples) {
				myLogger.write(url, MsgType.LOG);
			}
			myLogger.write("----------", MsgType.LOG);
			
		}
		
		summarizer.close();
		
		System.out.println("run.log contains full pogram run summary");
	}

}
