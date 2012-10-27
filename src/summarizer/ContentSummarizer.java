package summarizer;

import hierarchy.Category;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class ContentSummarizer {
	private HtmlParser parser = null;
	
	public ContentSummarizer(String host) {
		parser = new HtmlParser(host);
	}
	
	// given a collection of urls
	// parse each web page and build summarization
	public void summarize(Category c, String host, Set<String> urls) {
		TreeMap<String, Integer> summary = new TreeMap<String, Integer>();
		Vector<Set<String>> allwords = new Vector<Set<String>>();
		// Retrieve each page
		// Parse each page and get words
		
		for (String url : urls) {
			allwords.add(parser.parseWords(url));
		}
		
		for (Set<String> words : allwords) {
			for (String word : words) {
				if (!summary.containsKey(word)) {
					summary.put(word, 1);
				}
				else
					summary.put(word, summary.get(word) + 1);
			}
		}
		
		// Output summarization		
		outputSummary(c, host, summary);
	}
	
	private void outputSummary(Category c, String host, TreeMap<String, Integer> summary) {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(buildFileName(c, host)));
			for (Map.Entry<String, Integer> wordFreq : summary.entrySet()) {
				output.write(wordFreq.getKey() + "#" + String.format("%.1f", 1.0 * wordFreq.getValue()));
				output.newLine();
			}
			output.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String buildFileName(Category category, String host) {
		return category.getName() + "-" + host + ".txt";
	}
	
	public void close() {
		parser.close();
	}
	
}
