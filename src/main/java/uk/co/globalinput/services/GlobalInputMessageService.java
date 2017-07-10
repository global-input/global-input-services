package uk.co.globalinput.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import uk.co.globalinput.data.GlobalInputMessage;




@Component
public class GlobalInputMessageService {
	private static Logger logger=Logger.getLogger(GlobalInputMessageService.class);
	
	@Value("${websocket.url}")
	private String websocketURL;
	
	@Value("${websocket.namespace}")
	private String websocketNamewspace;
	
	
	
	private Socket socket=null;
	
	List<GlobalInputMessage> messagesToSend = new ArrayList<GlobalInputMessage>();
	
	private void addMessageToQueue(GlobalInputMessage message){
		synchronized (messagesToSend) {
			messagesToSend.add(message);				
	    }
	}
	
	private void addMessageToQueue(String session, String client, Map<String, Object> data){
		if(session==null| client==null || data==null){
			return;
		}
		session=session.trim();
		client=client.trim();		
		if(session.length()==0 || client.length()==0|| data.size()==0){
			return;
		}		
		addMessageToQueue(new GlobalInputMessage(session,client,data));				
	}
	
	private void sendMessage(GlobalInputMessage message){
				
		String content=null;
		try{
			content=message.toJsonString();
		}
		catch(Exception e){
			logger.error(e+" while converting the message to json:"+message+":"+content);
			return;
		}
		logger.info("Sending the message to: client "+message.getClient()+" message: "+content);
		socket.emit("sendToSession", content);
	}
	private void sendMessageInQueue(){
			try{	
					synchronized(messagesToSend){
						if(messagesToSend.size()==0){
							return;
						}
						for(GlobalInputMessage message:messagesToSend){	
							sendMessage(message);
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
	
	private Socket tsocket=null;
	public void sendMessage(final String session, String client,final Map<String, Object> data){	
		
		addMessageToQueue(session,client, data);
		
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
    			
    			if(websocketNamewspace!=null && websocketNamewspace.trim().length()>0){
    				Manager manager = new Manager(new URI(websocketURL));
    				tsocket = manager.socket(websocketNamewspace);    				
    			}
    			else{
    				tsocket=IO.socket(websocketURL);
    			}
    			
    			
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
