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
import uk.co.globalinput.data.GlobaInputResponseMessage;
import uk.co.globalinput.data.QRMessage;

import uk.co.globalinput.services.GlobalInputMessageService;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.URISyntaxException;


import java.util.Map;


import javax.servlet.http.HttpServletRequest;

/**
 * 
 * Controller for barcode
 * 
 * @author Dilshat Hewzulla
 *
 */


@RestController
public class ClientMessageController {
    private static Logger logger=Logger.getLogger(ClientMessageController.class);
    
    @Autowired
    private GlobalInputMessageService clientMessageSender; 
    
    @Autowired
    QRCodeGenerator qrCodeGenerator;
    
    
    
    @CrossOrigin(origins="*")
	@RequestMapping(value="/global-input/qr-code/{session}/{client}/{data}", method = RequestMethod.GET, produces = "image/png")
	public byte[] showBarCode(@PathVariable String session,@PathVariable String client, @PathVariable String data, HttpServletRequest request) throws IOException, WriterException{
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
		
		String imageType = request.getParameter("imageType");
		if(imageType!=null){
			imageType=imageType.trim().toLowerCase();						
		}
		if(imageType==null || imageType.length()==0){
			imageType="png";
		}
		
		QRMessage  qrMessage=new QRMessage();
		qrMessage.setCl(client);
		qrMessage.setSe(session);	
		qrMessage.setDt(data);
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		qrCodeGenerator.createQRImage(out, qrMessage, size, imageType);		
		return out.toByteArray();
	}
    
     
    
    @CrossOrigin(origins="*")
	@RequestMapping(value="/global-input/messages/{session}/{client}", method = RequestMethod.POST, produces = "application/json")
    
    public GlobaInputResponseMessage sendMessage(@PathVariable String session, @PathVariable String client,@RequestBody Map<String, Object> data,  HttpServletRequest request) throws IOException, URISyntaxException{
    	logger.info("Received the posted client:"+client+":"+session+":"+data);
    	clientMessageSender.sendMessage(session,client,data);
    	return new GlobaInputResponseMessage(session,client, "success");    	
    }
	
}
