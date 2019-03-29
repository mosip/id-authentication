package io.mosip.kernel.syncdata.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SigningUtil {

	@Autowired
	RestTemplate restTemplate;
	
	public String signDataWithPrivate(String hashedData) {
		
		return hashedData;
		
	}
}
