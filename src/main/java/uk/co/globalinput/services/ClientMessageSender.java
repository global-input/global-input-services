package uk.co.globalinput.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;


import io.socket.client.IO;
import io.socket.client.IO.Options;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

class ClientMessage{
	String clientId;
	Map<String, Object> message;
	
	
	public ClientMessage(String queueId, final Map<String, Object> message){
		this.clientId=queueId;
		this.message=message;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Map<String, Object> getMessage() {
		return message;
	}

	public void setMessage(Map<String, Object> message) {
		this.message = message;
	}
	
	
}



@Component
public class ClientMessageSender {
	private static Logger logger=Logger.getLogger(ClientMessageSender.class);
	
	@Value("${websocket.url}")
	private String websocketURL;
	
	
	private Socket socket=null;
	
	List<ClientMessage> messagesToSend = new ArrayList<ClientMessage>();
	
	private  static com.fasterxml.jackson.databind.ObjectMapper createObjectMapper(){
        com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);       
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
	}
	private void addMessageToQueue( String clientId, final Map<String, Object> messageObject){
		if(clientId==null){
			return;
		}
		clientId=clientId.trim();
		if(clientId.length()==0){
			return;
		}
		if(messageObject==null|| messageObject.size()==0){
			return;
		}
		synchronized (messagesToSend) {
				messagesToSend.add(new ClientMessage(clientId,messageObject));				
		}
				
	}
	
	private void sendMessageToClient(ClientMessage wrappedMessage){
		logger.info("Sending the message:"+wrappedMessage.message+" to :"+wrappedMessage.clientId);
		com.fasterxml.jackson.databind.ObjectMapper objectMapper=createObjectMapper();
		String content=null;
		try{
			content=objectMapper.writeValueAsString(wrappedMessage);
		}
		catch(Exception e){
			logger.error(e+" while converting the message to json:"+wrappedMessage);
			return;
		}
		socket.emit("sendToClient", content);
	}
	private void sendMessageInQueue(){
			try{	
					synchronized(messagesToSend){
						if(messagesToSend.size()==0){
							return;
						}
						for(ClientMessage message:messagesToSend){	
							sendMessageToClient(message);
						}	
						messagesToSend.clear();
					}
			  }
			catch(Exception e){
				logger.error("error sending messages:"+e,e);
				try{
					socket.disconnect();
				}
				catch(Exception e1){
					logger.error("failed to disconnect");
				}
				socket=null;
			}
				
	}
	
	public void sendMessage(final String clientId, final Map<String, Object> messageObject){	
		
		addMessageToQueue(clientId, messageObject);
		
    	if(socket==null){
    		try{
    			logger.info("----connecting to:"+this.websocketURL);
    			String websocketURL=this.websocketURL;
    			// default settings for all sockets
    			//SSLContext mySSLContext=MYSSLContext.getSSLContext();
    			//IO.setDefaultSSLContext(mySSLContext);
    			//HostnameVerifier myHostnameVerifier=MYSSLContext.createHostnameVerifier();
    			
    			//IO.setDefaultHostnameVerifier(myHostnameVerifier);

    			// set as an option
    			//Options opts = new IO.Options();
    			//opts.sslContext = mySSLContext;
    			//opts.hostnameVerifier = myHostnameVerifier;
    			//Socket tsocket = IO.socket(websocketURL, opts);
    			
    			Socket tsocket=IO.socket(websocketURL);    
    			
    			tsocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {    				
    					@Override
    					public void call(Object... args) {    		    
    						
    						logger.info("websocket is connected:"+websocketURL);
    						socket=tsocket;
    						sendMessageInQueue();      						
    					}
    			});
    			tsocket.connect();
    		}
    		catch(Exception e){
    			logger.error(e+" failed to connect to the websocket, so failed to send the message",e);
    		}
    	}
    	else{
    			sendMessageInQueue();
    	}
    	    	
	}
	 
}
