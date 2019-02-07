package io.mosip.registration.processor.message.sender.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

/**
 * Instantiates a new message sender util.
 */
@Data
@Component
public class MessageSenderUtil {

	/** The config server file storage URL. */
	@Value("${config.server.file.storage.uri}")
	private String configServerFileStorageURL;

	/** The notification types. */
	@Value("${registration.processor.globalconfigjson}")
	private String getGlobalConfigJson;

	@Value("${registration.processor.identityjson}")
	private String getRegProcessorIdentityJson;

	/** The get reg processor demographic identity. */
	@Value("${registration.processor.demographic.identity}")
	private String getRegProcessorDemographicIdentity;

	/**
	 * Gets the json.
	 *
	 * @param configServerFileStorageURL
	 *            the config server file storage URL
	 * @param uri
	 *            the uri
	 * @return the json
	 */
	public static String getJson(String configServerFileStorageURL, String uri) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configServerFileStorageURL + uri, String.class);
	}
}
