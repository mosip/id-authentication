package io.mosip.registration.processor.quality.check.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class QCUsersClient {
	@Value("${qc-users-manger}")
	private String qcUsersManagerHost;
	
	/**
	 * Function to get all qcuserids
	 * 
	 * 
	 */
	public List<String> getAllQcuserIds( ){
		
		ResponseEntity<String[]> responseEntity = new RestTemplate().
				getForEntity(qcUsersManagerHost, String[].class);
		
		return Arrays.asList(responseEntity.getBody());
		
		
	} 
}
