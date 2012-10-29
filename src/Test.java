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
		
		//to log the program execution
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
		
		// populate the category tree
		Category root = HierarchyBuilder.buildHierarchy(rulefile);
		
		//tree is printed in the debug file
		root.printTree(0);
		
		//create a classifier object
		DatabaseClassifier classifier = new DatabaseClassifier(apiKey, root);
		
		System.out.println("Classification in progress (run.log captures the execution)...");
		
		//BingSearchProvider uses Bing API to query the host
		SearchProvider provider = new BingSearchProvider(apiKey, 10); // topK doesn't matter
		//the database provides the query result to the classifier, either by retrieving them 
		//from the cache or by running the search query.  
		//in case the data is fetched from the host, it is added to the cache for the next run
		Database db = new SearchDatabase(host, provider);
		
		//returns a set of categories that the host is classified to
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
		 * It will need to be modified to make it generic and to work for a larger category set
		 * 
		 * We will need to populate the list of sample URLs associated with all the categories 
		 * under which the host database is classified.  We will also need to create this list for 
		 * all the parents of such categories.
		 * Example - if the database is classified under 'Health' and 'Sports', we will create 
		 * three content summaries (associated with health, sports and root).  For root, the sample 
		 * set will contain the samples collected for queries associated with root plus samples associated 
		 * with health plus samples associated with sports.  All duplicates are eliminated (property of set 
		 * and maps)
		 */
		Map<String, Set<String>> categorySamples = new HashMap<String, Set<String>>();
		
		for (Category c : categories) {
			
			//if c is a leaf node, there are no queries associated with it, thus no sample data
			if (c.isLeaf()) {
				c = c.getParent();
				//We shouldn't start from the same node twice 
				if (categorySamples.containsKey(c.getName())) 
					c = null;
			}
			
			//tempSamples is used to calculate the sample set for the parent category nodes
			Set<String> tempSamples = new HashSet<String>();
			while (c != null)
			{
				//Logger.getInstance().write(c + " " + classifier.getSampleUrls(c).size(), MsgType.LOG);
				
				Set<String> samples = new HashSet<String>();
				//for first iteration, tempSamples will be empty
				//for second iteration, it will contain samples for the child node
				samples.addAll(tempSamples);
				samples.addAll( classifier.getSampleUrls(c) );
				
				if (categorySamples.get(c.getName()) == null)
				{	//addd any samples associated with the node plus any samples associated
					//with the child node
					categorySamples.put(c.getName(), samples);
					tempSamples.addAll(samples);
				}
				else {
					//this is needed to calculate the sample set for the parent 
					//if we have a host classified under multiple categories to account
					//for multiple children nodes
					samples.addAll(categorySamples.get(c.getName()));
					categorySamples.put(c.getName(), samples);
				}
				c = c.getParent();
			}
		}
		
		//go through categorySamples and generate the metadata summary for each category 
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
