package cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import query.ProbeResult;

public class ProbeCache extends Cache<String[], ProbeResult> {

	private static String basicDir = "cache/probes/";
	private static String cacheFileName = "probes.bin";
	private String cachepath = null;
	private HashMap<String[], ProbeResult> resultCaches = null;
	
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
			} catch (IOException | ClassNotFoundException e) {
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
