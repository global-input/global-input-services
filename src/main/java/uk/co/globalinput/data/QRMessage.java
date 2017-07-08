package uk.co.globalinput.data;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.co.globalinput.util.GlobalInputUtil;

public class QRMessage {
  private String se;
  private String cl;
  private String dt;
  
public String getDt() {
	return dt;
}
public void setDt(String dt) {
	this.dt = dt;
}
public String getSe() {
	return se;
}
public void setSe(String se) {
	this.se = se;
}
public String getCl() {
	return cl;
}
public void setCl(String cl) {
	this.cl = cl;
}
public String toJsonString() throws JsonProcessingException{
	com.fasterxml.jackson.databind.ObjectMapper objectMapper=GlobalInputUtil.createObjectMapper();				
    String content=objectMapper.writeValueAsString(this);		
	return content;
}
}
