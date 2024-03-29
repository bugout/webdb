package hierarchy;

import java.util.Arrays;

/*
 * Data class to captures the rules associated with each category
 */

public final class Rule {
	private final String[] keywords;
	private final Category category;
	
	public Rule(String[] keywords, Category category) {
		this.keywords = keywords;
		this.category = category;
	}
	
	public String[] getKeywords() {
		return keywords;
	}
	
	public Category getCategory() {
		return category;				
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ Category: " + category.getName());
		sb.append(", Query: " + Arrays.toString(keywords));
		sb.append(" ]");
		
		return sb.toString();
	}
}
