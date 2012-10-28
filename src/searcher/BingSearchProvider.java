package searcher;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

import util.Logger;
import util.Logger.MsgType;

public class BingSearchProvider extends SearchProvider {	
	private static final String bingUrl = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=";
	private static final String bingCompositeUrl = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Composite?Query=";
	
	public BingSearchProvider(String apikey, int topk) { super(apikey, topk); }
	
	@SuppressWarnings("deprecation")
	private String constructQueryUrl(String[] query) {
		String queryUrl = bingUrl;
		queryUrl = queryUrl + URLEncoder.encode("'");
		for (int i = 0; i < query.length; i++) {
			queryUrl = queryUrl + URLEncoder.encode(query[i]);
			if (i != query.length - 1) {			
				 queryUrl += URLEncoder.encode(" ");
			}
		}
		queryUrl = queryUrl + URLEncoder.encode("'");
		queryUrl += String.format("&$top=%d&$format=Atom", topK);
		return queryUrl;
	}
	
	private String constructHostQueryUrl(String[] query, String host) {
		String queryUrl = bingCompositeUrl;
		queryUrl = queryUrl + URLEncoder.encode("'");
		
		queryUrl = queryUrl + "site" + URLEncoder.encode(":") + host + URLEncoder.encode(" ");
		
		for (int i = 0; i < query.length; i++) {
			queryUrl = queryUrl + URLEncoder.encode(query[i]);
			if (i != query.length - 1) {			
				 queryUrl += URLEncoder.encode(" ");
			}
		}
		queryUrl = queryUrl + URLEncoder.encode("'");
		queryUrl += String.format("&$top=%d&$format=Atom", topK);
		return queryUrl;
	}
	
	// return an xml file of the query result
	public String search(String[] query) throws IOException{
		byte[] accountKeyBytes = Base64.encodeBase64((apiKey + ":" + apiKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		URL url = new URL(constructQueryUrl(query));		
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
					
		InputStream inputStream = (InputStream) urlConnection.getContent();		
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		inputStream.read(contentRaw);
		String result = new String(contentRaw);				
		
		return result;
	}
	
	
	// return an xml file of the query result
	public String search(String[] query, String host) throws IOException{
		
		Logger.getInstance().write("Querying: " + Arrays.toString(query), MsgType.LOG);
		
		byte[] accountKeyBytes = Base64.encodeBase64((apiKey + ":" + apiKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		URL url = new URL(constructHostQueryUrl(query, host));		
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
					
		InputStream inputStream = (InputStream) urlConnection.getContent();		
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		inputStream.read(contentRaw);
		String result = new String(contentRaw);				
		
		return result;
	}
}
