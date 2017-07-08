package uk.co.globalinput.data;

public class GlobaInputResponseMessage{
	private String session;
	private String client;
	private String status;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public GlobaInputResponseMessage(String session, String client, String status) {		
		this.session = session;
		this.client = client;
		this.status = status;
	}	
	
	
}
