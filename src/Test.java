import query.ProbeResult;
import query.QueryResultParser;
import searcher.BingSearchProvider;
import searcher.SearchProvider;


public class Test {
	public static void main(String args[]) throws Exception {
		String apiKey = "troBf72dETWWVPyZCL5wDCZbiOQgss+cMMPEE3Yf3Ho=";
		SearchProvider provider = new BingSearchProvider(apiKey, 10); // topK doesn't matter
		QueryResultParser parser = new QueryResultParser();
		
		String[] query = {"bill", "gates"};
		String host = "microsoft.com";
		
		String result = provider.search(query, host);
		ProbeResult r = parser.parseQueryResult(result);
		System.out.println(result);
		System.out.println(r.getMatch());
		System.out.println(r.getRecords());
	}
}
