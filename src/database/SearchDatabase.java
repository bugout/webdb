package database;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cache.ProbeCache;

import query.ProbeResult;
import query.QueryResultParser;
import searcher.BingSearchProvider;
import searcher.SearchProvider;

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
			System.out.println("Querying " + Arrays.toString(keywords) + " hits cache");
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
