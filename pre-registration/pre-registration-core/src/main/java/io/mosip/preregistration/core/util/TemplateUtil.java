package io.mosip.preregistration.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.common.dto.TemplateResponseDTO;
import io.mosip.preregistration.core.common.dto.TemplateResponseListDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;








/**
 * @author Sanober Noor
 *@since 1.0.0 
 */
@Component
public class TemplateUtil {
	
	private Logger log = LoggerConfiguration.logConfig(TemplateUtil.class);
	/**
	 * Reference for ${resource.template.url} from property file
	 */

	@Value("${resource.template.url}")
	private String resourceUrl;
	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private TemplateManager templateManager;

	/**
	 * This method is used for getting template
	 * @param langCode
	 * @param templatetypecode
	 * @return
	 */
	public String getTemplate(String langCode,String templatetypecode)  {
		String url = resourceUrl + "/" + langCode + "/" + templatetypecode;
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<RequestWrapper<TemplateResponseListDTO>> httpEntity = new HttpEntity<>(headers);
		log.info("sessionId", "idType", "id", "In getTemplate method of TemplateUtil service url: "+url);
		ResponseEntity<ResponseWrapper<TemplateResponseListDTO>> respEntity = restTemplate.exchange(url,HttpMethod.GET,httpEntity,new ParameterizedTypeReference<ResponseWrapper<TemplateResponseListDTO>>() {
		});

		List<TemplateResponseDTO> response = respEntity.getBody().getResponse().getTemplates();

		return response.get(0).getFileText().replaceAll("^\"|\"$", "");
		
	}
	
	/**
	 * This method merging the template
	 * 
	 * @param fileText
	 * @param acknowledgementDTO
	 * @return
	 * @throws IOException 
	 */
	public String templateMerge(String fileText, NotificationDTO acknowledgementDTO) throws IOException {
		log.info("sessionId", "idType", "id", "In templateMerge method of TemplateUtil service ");
		String mergeTemplate = null;
		Map<String, Object> map = mapSetting(acknowledgementDTO);
			InputStream templateInputStream = new ByteArrayInputStream(fileText.getBytes(Charset.forName("UTF-8")));

			InputStream resultedTemplate = templateManager.merge(templateInputStream, map);

			mergeTemplate = IOUtils.toString(resultedTemplate, StandardCharsets.UTF_8.name());
		
		return mergeTemplate;
	}
	
	/**
	 * This method will set the user detail for the template merger
	 * 
	 * @param acknowledgementDTO
	 * @return
	 */
	public Map<String, Object> mapSetting(NotificationDTO acknowledgementDTO) {
		Map<String, Object> responseMap = new HashMap<>();
		log.info("sessionId", "idType", "id", "In mapSetting method of TemplateUtil service ");
		DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("dd MMM yyyy");
		DateTimeFormatter timeFormate = DateTimeFormatter.ofPattern("HH:mm");

		LocalDateTime now = LocalDateTime.now();
		LocalTime localTime = LocalTime.now();
		

		responseMap.put("name", acknowledgementDTO.getName());
		responseMap.put("PRID", acknowledgementDTO.getPreRegistrationId());
		responseMap.put("Date", dateFormate.format(now));
		responseMap.put("Time", timeFormate.format(localTime));
		responseMap.put("Appointmentdate", acknowledgementDTO.getAppointmentDate());
		responseMap.put("Appointmenttime", acknowledgementDTO.getAppointmentTime());
		return responseMap;
	}

}

