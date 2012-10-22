package hierarchy;



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
	
}
