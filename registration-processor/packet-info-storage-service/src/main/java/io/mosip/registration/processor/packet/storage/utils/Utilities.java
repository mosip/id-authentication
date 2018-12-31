package io.mosip.registration.processor.packet.storage.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;

	@Value("${registration.processor.identityjson}")
	private String getRegProcessorIdentityJson;

	@Value("${registration.processor.demographic.identity}")
	private String getRegProcessorDemographicIdentity;

	public static String getJson(String configServerFileStorageURL,String uri) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configServerFileStorageURL+uri, String.class);
	}
}
