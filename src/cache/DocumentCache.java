package cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.HashMap;

public class DocumentCache extends Cache<String, String> {

	private static String basicDir = "cache/documents/";
	private static String cacheFileName = "documents.bin";
	private String cachepath = null;
	private HashMap<String, String> cache = null;
	
	/*
	 * This class allows caching of the document associated with the URL
	 * Read and initialize cache hashmap if there is any previous data available 
	 * for the specified host
	 * If previous data is not available, create a cache file that will be populated
	 * when the close method is called for the DocumentCache 
	 */
	public DocumentCache(String host) {
		cachepath = basicDir + host + "/" + cacheFileName;				
		File cachefile = new File(cachepath);
		if (!cachefile.exists()) {
			cache = new HashMap<String, String>();
			File cachedir = new File(basicDir + host);
			cachedir.mkdirs();
		}
		else {
			try {
				FileInputStream filein = new FileInputStream(cachepath);
				ObjectInputStream in = new ObjectInputStream(filein);
				cache = (HashMap<String, String>)in.readObject();

				filein.close();
				in.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}			
		}
	}
	
	public void close() {
		try {
			FileOutputStream fileOut = new FileOutputStream(cachepath);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(cache);
			fileOut.close();
			objOut.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String get(String url) {
		return cache.get(url);
	}

	@Override
	public void put(String key, String value) {
		cache.put(key, value);
	}
	
}
