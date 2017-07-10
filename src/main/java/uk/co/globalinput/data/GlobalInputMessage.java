package uk.co.globalinput.data;


import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;


import uk.co.globalinput.util.GlobalInputUtil;


public class GlobalInputMessage{
	private String session;
	private String client;
	private Map<String, Object> data;
	
	public GlobalInputMessage(String session, String client, Map<String, Object> data) {
		super();
		this.session = session;
		this.client = client;
		this.data = data;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public String toJsonString() throws JsonProcessingException{
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GlobalInputUtil.createObjectMapper();				
	    String content=objectMapper.writeValueAsString(this);		
		return content;
	}
}	
	
	
	

