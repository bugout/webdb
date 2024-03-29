package cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;

import query.ProbeResult;

public class ProbeCache extends Cache<String[], ProbeResult> {

	private static String basicDir = "cached/probes/";
	private static String cacheFileName = "probes.bin";
	private String cachepath = null;
	private HashMap<String[], ProbeResult> resultCaches = null;
	
	/*
	 * This class allows caching of the ProbeResult associated with the query
	 * Read and initialize resultCaches hashmap if there is any previous data available 
	 * for the specified host
	 * If previous data is not available, create a cache file that will be populated
	 * when the close method is called for the DocumentCache 
	 */	
	public ProbeCache(String host) {
		cachepath = basicDir + host + "/" + cacheFileName;				
		File cachefile = new File(cachepath);
		if (!cachefile.exists()) {
			resultCaches = new HashMap<String[], ProbeResult>();
			File cachedir = new File(basicDir + host);
			cachedir.mkdirs();
		}
		else {
			try {
				FileInputStream filein = new FileInputStream(cachepath);
				ObjectInputStream in = new ObjectInputStream(filein);
				resultCaches = (HashMap<String[], ProbeResult>)in.readObject();
				
				filein.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void close() {
		try {
			FileOutputStream fileOut = new FileOutputStream(cachepath);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			objOut.writeObject(resultCaches);
			fileOut.close();
			objOut.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ProbeResult get(String[] keywords) {
		for (String[] keys : resultCaches.keySet())
			if (Arrays.equals(keys, keywords)) {
				return resultCaches.get(keys);
			}
		return null;
	}

	@Override
	public void put(String[] key, ProbeResult value) {
		resultCaches.put(key, value);
	}
	
}
