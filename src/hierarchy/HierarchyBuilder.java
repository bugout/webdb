package hierarchy;

import java.util.List;

public class HierarchyBuilder {
	
	// mannually build the hierarchy
	// TODO: read hierarchy from an xml file
	public static Category buildHierarchy(String rulefile) {
		Category root = new Category("Root", null);
		Category computer = new Category("Computers", root);
		Category health = new Category("Health", root);
		Category sports = new Category("Sports", root);
		root.addSubcategory(computer);
		root.addSubcategory(health);
		root.addSubcategory(sports);
		
		Category hardware = new Category("Hardware", computer);
		Category programming = new Category("Programming", computer);
		computer.addSubcategory(hardware);
		computer.addSubcategory(programming);
		
		Category fitness = new Category("Fitness", health);
		Category diseases = new Category("Diseases", health);
		health.addSubcategory(fitness);
		health.addSubcategory(diseases);
		
		Category basketball = new Category("Basketball", sports);
		Category soccer = new Category("Soccer", sports);
		sports.addSubcategory(basketball);
		sports.addSubcategory(soccer);
		

		RuleGenerator rulegen = new RuleGenerator(rulefile, root);
		List<Rule> rules = rulegen.generateRules(); 
		for (Rule rule : rules) {
			rule.getCategory().getParent().addRule(rule);
		}
		
		return root;
	}
}
