package hierarchy;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private Category parent = null;
	private List<Category> children = new ArrayList<Category>();
	private List<Rule> rules = new ArrayList<Rule>();
	private String name = null;
	
	public Category(String name, Category parent) {
		this.name = name;
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return "Category [name=" + name + "]";
	}

	public void addSubcategory(Category sub) {
		children.add(sub);		
	}
	
	public void addRule(Rule rule) {
		rules.add(rule);
	}
	
	public List<Rule> getRules() {
		return rules;
	}

	public String getName() {
		return name;
	}


	public Category getParent() {
		return parent;
	}

	public List<Category> getChildren() {
		return children;
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

	public Category getCategoryByName(String name) {
		Category c;
		if (getName().equals(name))
			return this;
		else {
			for (Category sub : getChildren()) {
				if ((c = sub.getCategoryByName(name)) != null)
					return c;
			}
		}
		return null;
	}
	
}
