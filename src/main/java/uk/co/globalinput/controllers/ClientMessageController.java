package uk.co.globalinput.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;




import uk.co.globalinput.barcode.QRCodeGenerator;
import uk.co.globalinput.services.ClientMessageSender;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * Controller for barcode
 * 
 * @author Dilshat Hewzulla
 *
 */

class ResponseMessage{
	private String clientId;
	private String status;	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ResponseMessage(String clientId, String status) {
		super();
		this.clientId = clientId;
		this.status = status;
	}
	
}
@RestController
public class ClientMessageController {
    private static Logger logger=Logger.getLogger(ClientMessageController.class);
    
    @Autowired
    private ClientMessageSender clientMessageSender; 
    
    private String createNewClient(){
    	UUID id=UUID.randomUUID();    	
    	return id.toString();
    }
    
  /**
   * 
   * @param dataId
   * @param request
   * @return
   * @throws IOException
   * @throws WriterException
   */
    @CrossOrigin(origins="*")
	@RequestMapping(value="/global-input/clients/{clientId}/qr-code", method = RequestMethod.GET, produces = "image/png")
	public byte[] showBarCode(@PathVariable String clientId, HttpServletRequest request) throws IOException, WriterException{
		int size=125; 
		String sizeValue = request.getParameter("size");
		if(sizeValue!=null){
			try{
				size=Integer.parseInt(sizeValue);
			}
			catch(Exception e){
				logger.error(e+" while getting the size parmeter:"+size,e);
			}
			if(size<40){
				size=125;
			}
			if(size>=2000){
				size=2000;
			}			
		}
		
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		QRCodeGenerator.createQRImage(out, clientId, size, "png");
		return out.toByteArray();
	}
    
     
    
    @CrossOrigin(origins="*")
	@RequestMapping(value="/global-input/clients/{clientId}/messages", method = RequestMethod.POST, produces = "application/json")
    
    public ResponseMessage sendMessage(@PathVariable String clientId,@RequestBody Map<String, Object> messageObject,  HttpServletRequest request) throws IOException, URISyntaxException{
    	logger.info("Received the posted content:"+messageObject);
    	clientMessageSender.sendMessage(clientId,messageObject);
    	return new ResponseMessage(clientId, "success");    	
    }
    
    @CrossOrigin(origins="*")
	@RequestMapping(value="/global-input/clients", method = RequestMethod.POST, produces = "application/json")
    public ResponseMessage createClient(@RequestBody Map<String, Object> options){
    	logger.info("Received the posted create client:"+options);    	
    	return new ResponseMessage(createNewClient(), "success"); 
    	
    }
    
	
}
