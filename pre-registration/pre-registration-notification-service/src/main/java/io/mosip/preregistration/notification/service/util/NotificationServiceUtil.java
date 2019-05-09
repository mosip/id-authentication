package io.mosip.preregistration.notification.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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



	private Logger log = LoggerConfiguration.logConfig(NotificationServiceUtil.class);

	/**
	 * Autowired reference for {@link #RestTemplateBuilder}
	 */
	//@Autowired
	//private RestTemplateBuilder restTemplateBuilder;
	


	/**
	 * Method to generate currentresponsetime.
	 * 
	 * @return the string.
	 */
	public String getCurrentResponseTime() {
		return LocalDateTime.now(ZoneId.of("UTC")).toString();
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
		notificationReqDto.setRequesttime(new SimpleDateFormat(utcDateTimePattern).parse(notificationData.get("requesttime").toString()) );
		notificationReqDto.setRequest(notififcationDTO);
		return notificationReqDto;

	}

	public Map<String, String> prepareRequestMap(MainRequestDTO<?> requestDto) {
		log.info("sessionId", "idType", "id", "In prepareRequestMap method of Login Service Util");
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("id", requestDto.getId());
		requestMap.put("version", requestDto.getVersion());
		LocalDate date = requestDto.getRequesttime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		requestMap.put("requesttime",date.toString());
		requestMap.put("request", requestDto.getRequest().toString());
		return requestMap;
	}
}
