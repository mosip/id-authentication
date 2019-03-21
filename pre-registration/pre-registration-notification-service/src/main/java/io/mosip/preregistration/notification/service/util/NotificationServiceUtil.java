package io.mosip.preregistration.notification.service.util;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
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
		return LocalDateTime.now(ZoneId.of("UTC")).toString();
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
		log.info("sessionId", "idType", "id", " URL in notification service util of configRestCall" + uriBuilder);
		return restTemplate.getForObject(uriBuilder.toString(), String.class);

	}

	public void getConfigParams(Properties prop, Map<String, String> configParamMap, List<String> reqParams) {
		for (Entry<Object, Object> e : prop.entrySet()) {
			if (reqParams.contains(String.valueOf(e.getKey()))) {
				configParamMap.put(String.valueOf(e.getKey()), e.getValue().toString());
			}

		}
	}

	public MainRequestDTO<NotificationDTO> createNotificationDetails(String jsonString) throws JsonParseException,
			JsonMappingException, io.mosip.kernel.core.exception.IOException, JSONException, ParseException {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(utcDateTimePattern);
		log.info("sessionId", "idType", "id", "In createUploadDto method of document service util");
		MainRequestDTO<NotificationDTO> notificationReqDto = new MainRequestDTO<>();
		JSONObject notificationData = new JSONObject(jsonString);
		JSONObject notificationDtoData = (JSONObject) notificationData.get("request");
		NotificationDTO notififcationDTO = (NotificationDTO) JsonUtils.jsonStringToJavaObject(NotificationDTO.class,
				notificationDtoData.toString());
		LocalDateTime localDateTime = DateUtils.parseToLocalDateTime(notificationData.get("requesttime").toString());
		notificationReqDto.setId(notificationData.get("id").toString());
		notificationReqDto.setVersion(notificationData.get("version").toString());
		notificationReqDto.setRequesttime(localDateTime);
		notificationReqDto.setRequest(notififcationDTO);
		return notificationReqDto;

	}

}
