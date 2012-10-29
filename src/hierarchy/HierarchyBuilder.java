package hierarchy;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.util.List;

public class HierarchyBuilder {

	/**
	 *  Build the category hierarchy by reading an XML file
	 *  Associate rules by reading the rule file
	 * @param the hierarchy in xml format
	 * @param the rules in a file
	 * @return the root element of the hierarchy
	 */
	public static Category buildHierarchy(String hierarchyfile, String rulefile) {
		// build hierarchy
		Category root = buildFromXml(hierarchyfile, rulefile);
		
		// generate rules
		buildRules(rulefile, root);
		
		return root;
	}
	
	private static void buildRules(String rulefile, Category root) {
		RuleGenerator rulegen = new RuleGenerator(rulefile, root);
		List<Rule> rules = rulegen.generateRules();
		for (Rule rule : rules) {
			rule.getCategory().getParent().addRule(rule);
		}
	}
	
	private static Category buildFromXml(String xmlfile, String rulefile) {
		Category root = null;

		try {
			File fXmlFile = new File(xmlfile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			root = buildHierarchy(doc.getDocumentElement(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return root;
	}

	private static Category buildHierarchy(Node node, Category parent) {
		Category current;
		current = new Category(node.getNodeName(), parent);

		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node nNode = children.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				current.addSubcategory(buildHierarchy(nNode, current));
			}
		}
		return current;
	}

	/**
	 * Naive solution: hard coding the hierarchy
	 */
	private static Category manualBuildHierarchy(String rulefile) {
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

		return root;
	}
}
