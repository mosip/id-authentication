package io.mosip.preregistration.notification.service.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.util.DateUtils;

/**
 * The util class.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@Component
public class NotificationServiceUtil {

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	/**
	 * Environment instance
	 */
	@Autowired
	private Environment env;

	/**
	 * Autowired reference for {@link #RestTemplateBuilder}
	 */
	@Autowired
	private RestTemplateBuilder restTemplateBuilder;


	/**
	 * Method to generate currentresponsetime.
	 * 
	 * @return the string.
	 */
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}
	
	public Properties parsePropertiesString(String s) throws IOException {
		final Properties p = new Properties();
		p.load(new StringReader(s));
		return p;
	}
	
	public String configRestCall(String filname) {
		String configServerUri = env.getProperty("spring.cloud.config.uri");
		String configLabel = env.getProperty("spring.cloud.config.label");
		String configProfile = env.getProperty("spring.profiles.active");
		String configAppName = env.getProperty("spring.cloud.config.name");
		StringBuilder uriBuilder= new StringBuilder();
		RestTemplate restTemplate = restTemplateBuilder.build();
		uriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
				.append(configLabel + "/").append(filname);
		return restTemplate.getForObject(uriBuilder.toString(), String.class);
		
	}
	
	public void getConfigParams(Properties prop,Map<String, String> configParamMap,List<String> reqParams){
		for (Entry<Object, Object> e : prop.entrySet()) {
			if (reqParams.contains(String.valueOf(e.getKey()))) {
				System.out.println(String.valueOf(e.getKey()) + " ----value--- " + e.getValue());
				configParamMap.put(String.valueOf(e.getKey()), e.getValue().toString());
			}

		}
	}

}
