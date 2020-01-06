package io.mosip.registration.processor.quality.check.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * The Class QCUsersClient.
 */
@Component
public class QCUsersClient {	
	
	/** The qcusers manager client. */
	private  String qcusersManagerClient=
			"http://localhost:9093/v0.1/registration-processor/qc-users//qcUsersList";
	
	/**
	 * Gets the all qcuser ids.
	 *
	 * @return the all qcuser ids
	 */
	public List<String> getAllQcuserIds() {
	
		ResponseEntity<String[]> responseEntity = new RestTemplate().getForEntity(
				qcusersManagerClient,String[].class);
		
		return Arrays.asList(responseEntity.getBody());
	}
}
