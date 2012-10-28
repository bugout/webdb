package query;

import java.io.Serializable;

public class QueryRecord implements Serializable {
	
	private String title;
	private String url;
	private String displayUrl;
	private String description;
	private boolean relevant;
	
	public QueryRecord(String title, String url, String displayUrl, String description)
	{
		this.title= title;
		this.url = url;
		this.displayUrl = displayUrl;
		this.description = description;
		relevant = false;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String displayUrl() {
		return displayUrl;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isRelevant() {
		return relevant;
	}
	
	public void setFeedback(boolean relevant) {
		this.relevant = relevant;
	}
	
	
	public String toString() {
		
		StringBuffer sb = new StringBuffer();	
		sb.append("Title: " + title);
		return sb.toString();
	}

}
