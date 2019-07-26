package io.mosip.registration.processor.transaction.api.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.status.dto.RegistrationTransactionDto;
@Component
public class Utilities {
	@Value("${registration.processor.transactionStatusJson}")
	private String getTransactionStatusJson;
	
	public static String getJson(String uri) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(uri, String.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistrationTransactionDto> getTransactionsInPreferedLanguage(List<RegistrationTransactionDto> dtoList
			,String langCode) throws JsonParseException, JsonMappingException, IOException{

		ObjectMapper mapper=new ObjectMapper();
		JSONObject statusJson=mapper.readValue(getJson(getTransactionStatusJson), JSONObject.class);
		for(RegistrationTransactionDto traDTO:dtoList) {
			if( traDTO.getStatusCode() !=null && !traDTO.getStatusCode().isEmpty()) {
			HashMap<String,String> statusObject=   (HashMap<String, String>) statusJson.get(traDTO.getStatusCode());
			statusObject.entrySet().forEach(entry->{
				String lang= entry.getKey();
			    if(lang.matches(langCode))  {
			    	traDTO.setStatusCode(entry.getValue());
			    }
			 });
			}
		}
		return dtoList;
		
	}
}
