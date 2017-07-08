package uk.co.globalinput.data;


import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;


import uk.co.globalinput.util.GlobalInputUtil;


public class GlobalInputMessage{
	private String session;
	private String client;
	private Map<String, Object> message;
	
	public GlobalInputMessage(String session, String client, Map<String, Object> message) {
		super();
		this.session = session;
		this.client = client;
		this.message = message;
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
	public Map<String, Object> getMessage() {
		return message;
	}
	public void setMessage(Map<String, Object> message) {
		this.message = message;
	}
	public String toJsonString() throws JsonProcessingException{
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=GlobalInputUtil.createObjectMapper();				
	    String content=objectMapper.writeValueAsString(this);		
		return content;
	}
}	
	
	
	

