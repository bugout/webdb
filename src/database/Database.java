package database;

import query.ProbeResult;

public abstract class Database {
	protected String host;
	
	public Database(String host) {
		this.host = host;
	}
	
	public String getHost() {
		return host;
	}
	
	/**
	 * Probe the database with keywords
	 * @param keywords
	 * @return number of matches of the probe
	 */
	public abstract ProbeResult probe(String[] keywords);
	
	public abstract void close();
}
