package query;

import java.util.Vector;


/**
 *  Parse the string returned from bing
 *  and summarize the result in ProbeResult
 *  including both matches and a vector of topK result
 */
public class ProbeResult {
	private int matches;
	private Vector<QueryRecord> records;

	public ProbeResult(int matches, Vector<QueryRecord> records) {
		this.matches =  matches;
		this.records = records;
	}
	
	public int getMatch() {
		return matches;		
	}
	
	public Vector<QueryRecord> getRecords() {
		return records;
	}
}
