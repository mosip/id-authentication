package io.mosip.preregistration.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.common.dto.TemplateResponseDTO;
import io.mosip.preregistration.core.common.dto.TemplateResponseListDTO;








/**
 * @author Sanober Noor
 *@since 1.0.0 
 */
@Component
public class TemplateUtil {
	/**
	 * Reference for ${resource.url} from property file
	 */

	
	private String resourceUrl="https://integ.mosip.io/masterdata/v1.0/templates";
	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private TemplateManager templateManager;

	public String getTemplate(String langCode,String templatetypecode)  {
		
		
		String url = resourceUrl + "/" + langCode + "/" + templatetypecode;

		ResponseEntity<TemplateResponseListDTO> respEntity = restTemplate.getForEntity(url, TemplateResponseListDTO.class);

		List<TemplateResponseDTO> response = respEntity.getBody().getTemplates();

		return response.get(0).getFileText();
		
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

		DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("yyyy/MM/dd");

		LocalDateTime now = LocalDateTime.now();
		LocalTime localTime = LocalTime.now(ZoneId.of("UTC"));

		responseMap.put("name", acknowledgementDTO.getName());
		responseMap.put("PRID", acknowledgementDTO.getPreId());
		responseMap.put("Date", dateFormate.format(now));
		responseMap.put("Time", localTime);
		responseMap.put("Appointmentdate", acknowledgementDTO.getAppointmentDate());
		responseMap.put("Appointmenttime", acknowledgementDTO.getAppointmentTime());
		return responseMap;
	}

}
