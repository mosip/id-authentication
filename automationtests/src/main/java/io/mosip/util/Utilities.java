package io.mosip.util;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import io.mosip.service.ApplicationLibrary;
import io.restassured.response.Response;
import lombok.Data;

/**
 * 
 * @author Girish Yarru
 *
 */
@Component
@Data
public class Utilities {

	/** The config server file storage URL. */
	//@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL = "http://104.211.212.28:51000/registration-processor-packet-info-storage-service/qa/0.11.0";

	//@Value("${registration.processor.identityjson}")
	//private String getRegProcessorIdentityJson;
	String getRegProcessorIdentityJson="/RegistrationProcessorIdentity.json";

	//@Value("${registration.processor.demographic.identity}")
	private String getRegProcessorDemographicIdentity = "identity";
	
	

	public String getGetRegProcessorIdentityJson() {
		return getRegProcessorIdentityJson;
	}



	public void setGetRegProcessorIdentityJson(String getRegProcessorIdentityJson) {
		this.getRegProcessorIdentityJson = getRegProcessorIdentityJson;
	}



	public static String getJson(String configServerFileStorageURL, String uri) {
		Response actualResponse = null;
		ApplicationLibrary applicationLibrary = new ApplicationLibrary();
		HashMap<String, String> request = null;
		//actualResponse = applicationLibrary.getRequestDev(configServerFileStorageURL + uri);
		return actualResponse.asString();
	}



	public String getConfigServerFileStorageURL() {
		return configServerFileStorageURL;
	}



	public void setConfigServerFileStorageURL(String configServerFileStorageURL) {
		this.configServerFileStorageURL = configServerFileStorageURL;
	}



	public String getGetRegProcessorDemographicIdentity() {
		return getRegProcessorDemographicIdentity;
	}



	public void setGetRegProcessorDemographicIdentity(String getRegProcessorDemographicIdentity) {
		this.getRegProcessorDemographicIdentity = getRegProcessorDemographicIdentity;
	}
}
