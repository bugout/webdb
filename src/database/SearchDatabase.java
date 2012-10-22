package database;
import query.ProbeResult;
import query.QueryResultParser;
import searcher.BingSearchProvider;
import searcher.SearchProvider;

public class SearchDatabase extends Database {

	private SearchProvider provider = null;
	private QueryResultParser parser = null;
	
	public SearchDatabase(String host, SearchProvider provider) {
		super(host);
		this.provider = provider;
		parser = new QueryResultParser();
	}

	@Override
	public int probe(String[] keywords) {
		try {
			String searchResult = provider.search(keywords);
			ProbeResult result = parser.parseQueryResult(searchResult);
			return result.getMatch();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
