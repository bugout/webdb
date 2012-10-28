package database;

import java.util.Arrays;

import cache.ProbeCache;

import query.ProbeResult;
import query.QueryResultParser;

import searcher.SearchProvider;
import util.Logger;
import util.Logger.MsgType;

public class SearchDatabase extends Database {

	private static String cachepath = "test.bin"; 
	
	private SearchProvider provider = null;
	private QueryResultParser parser = null;
	
	private ProbeCache resultCaches = null;
	//private HashMap<String[], ProbeResult> resultCaches = null;
	
	public SearchDatabase(String host, SearchProvider provider) {
		super(host);
		this.provider = provider;
		parser = new QueryResultParser();
		resultCaches = new ProbeCache(host);
	}

	@Override
	public ProbeResult probe(String[] keywords) {
		// return from cache
		ProbeResult r;
		
		if ((r = resultCaches.get(keywords)) != null) {			
			Logger.getInstance().write("Querying " + Arrays.toString(keywords) + " hits cache", MsgType.LOG);
			return r;
		}		
		try {
			String searchResult = provider.search(keywords, host);
			ProbeResult result = parser.parseQueryResult(searchResult);
			resultCaches.put(keywords, result);
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		resultCaches.close();
	}
}
