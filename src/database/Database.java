package database;

public abstract class Database {
	private String host;
	
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
	public abstract int probe(String[] keywords);
}
