package io.mosip.preregistration.notification.service.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.config.LoggerConfiguration;

/**
 * The util class.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@Component
public class NotificationServiceUtil {
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;

	/**
	 * Environment instance
	 */
	@Autowired
	private Environment env;
	
	private Logger log = LoggerConfiguration.logConfig(NotificationServiceUtil.class);

	/**
	 * Autowired reference for {@link #RestTemplateBuilder}
	 */
	//@Autowired
	//private RestTemplateBuilder restTemplateBuilder;
	
	@Autowired
	private RestTemplate restTemplate;


	/**
	 * Method to generate currentresponsetime.
	 * 
	 * @return the string.
	 */
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
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
		uriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
				.append(configLabel + "/").append(filname);
		log.info("sessionId", "idType", "id",
				" URL in notification service util of configRestCall"+uriBuilder);
		return restTemplate.getForObject(uriBuilder.toString(), String.class);
		
	}
	
	public void getConfigParams(Properties prop,Map<String, String> configParamMap,List<String> reqParams){
		for (Entry<Object, Object> e : prop.entrySet()) {
			if (reqParams.contains(String.valueOf(e.getKey()))) {
				configParamMap.put(String.valueOf(e.getKey()), e.getValue().toString());
			}

		}
	}

}
