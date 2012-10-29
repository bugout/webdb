package hierarchy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
 * Read the rules.txt and populate the Category nodes in the Category tree with 
 * the rules associated with the cateogory
 */

public class RuleGenerator {
	private String rulefile = null;
	private Category hierarchy = null;
	
	// generate from file
	public RuleGenerator(String rulefile, Category hierarchy) {
		this.rulefile = rulefile;
		this.hierarchy = hierarchy;
	}
	
	private Rule parseRule(String line) {
		String[] tokens = line.split(" ");
		String categoryName = tokens[0];
		Category c = hierarchy.getCategoryByName(categoryName);
		if (c == null) {
			throw new RuntimeException("Category " + categoryName + " does not exists!");
		}
		String[] keywords = Arrays.copyOfRange(tokens, 1, tokens.length);
		return new Rule(keywords, c);
	}
	
	public List<Rule> generateRules() {
		List<Rule> rules = new ArrayList<Rule>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(rulefile));
			String line;
			while ((line = br.readLine()) != null) {
				Rule rule = parseRule(line);
				rules.add(rule);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return rules;
	}
}
