package uk.co.globalinput.util;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;



public class GlobalInputUtil {

	public   static com.fasterxml.jackson.databind.ObjectMapper createObjectMapper(){
        com.fasterxml.jackson.databind.ObjectMapper objectMapper=new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);       
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
	}
	
	public  static String createNewUUID(){
	    	UUID id=UUID.randomUUID();    	
	    	return id.toString();
	}
	   
	
	
}
