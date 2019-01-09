package io.mosip.registration.processor.message.sender.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

@Data
@Component
public class MessageSenderUtil {
	
	/** The config server file storage URL. */
	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;
	
	/** The notification types. */
	@Value("${registration.processor.globalconfigjson}")
	private String getGlobalConfigJson;

	@Value("${registration.processor.templatejson}")
	private String getRegProcessorTemplateJson;

	@Value("${registration.processor.demographic.identity}")
	private String getRegProcessorDemographicIdentity;
	

	public static String getJson(String configServerFileStorageURL,String uri) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configServerFileStorageURL+uri, String.class);
	}
}

