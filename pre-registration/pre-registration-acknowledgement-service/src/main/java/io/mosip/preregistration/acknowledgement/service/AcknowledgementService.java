package io.mosip.preregistration.acknowledgement.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.preregistration.acknowledgement.dto.AcknowledgementDTO;
import io.mosip.preregistration.acknowledgement.dto.EmailResponseDTO;
import io.mosip.preregistration.acknowledgement.dto.ResponseDTO;
import io.mosip.preregistration.acknowledgement.dto.SMSRequestDTO;
import io.mosip.preregistration.acknowledgement.dto.TemplateResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;


/**
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@Service
public class AcknowledgementService {

	/**
	 * Reference for ${resource.url} from property file
	 */
	@Value("${resource.url}")
	private String resourceUrl;

	@Value("${emailResourse.url}")
	private String emailResourseUrl;

	@Value("${smsResourse.url}")
	private String smsResourseUrl;

	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private TemplateManager templateManager;

	/**
	 * @param jsonStirng
	 * @param langCode
	 * @param file
	 * @return
	 */
	public String acknowledgementNotifier(String jsonStirng, String langCode, MultipartFile file) {

		try {
			AcknowledgementDTO acknowledgementDTO = new AcknowledgementDTO();
			MainRequestDTO<AcknowledgementDTO> mainDTO = new MainRequestDTO<>();
			JSONObject acknowledgementData= new JSONObject(jsonStirng);

			
			JSONObject acknowledgementDTOData = (JSONObject) acknowledgementData.get("request");
			acknowledgementDTO = (AcknowledgementDTO) JsonUtils.jsonStringToJavaObject(AcknowledgementDTO.class,
					acknowledgementDTOData.toString());
			mainDTO.setId(acknowledgementData.get("id").toString());
			mainDTO.setVer(acknowledgementData.get("ver").toString());
			mainDTO.setReqTime(new SimpleDateFormat(dateTimeFormat).parse(acknowledgementData.get("reqTime").toString()));
			mainDTO.setRequest(acknowledgementDTO);
			String smsTemplate = smsNotification(acknowledgementDTO, langCode);
			String emailTemplate=emailNotification(acknowledgementDTO, langCode, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @param acknowledgementDTO
	 * @param langCode
	 * @param file
	 * @return
	 */
	public String emailNotification(AcknowledgementDTO acknowledgementDTO, String langCode, MultipartFile file) {
		List<TemplateResponseDTO> response = null;
		ResponseEntity<EmailResponseDTO> resp=null;
		String merseTemplate = null;
		 try {
		File convFile = new File( file.getOriginalFilename());
		
		 convFile.createNewFile(); 
		    FileOutputStream fos = new FileOutputStream(convFile); 
		    fos.write(file.getBytes());
		   
		FileSystemResource value = new FileSystemResource(convFile);
		String url = resourceUrl + "/" + langCode + "/" + "Email-Acknowledgement";

		ResponseEntity<ResponseDTO> respEntity = restTemplate.getForEntity(url, ResponseDTO.class);

		response = respEntity.getBody().getTemplates();

		String fileText = response.get(0).getFileText();
		merseTemplate = templateMerge(fileText, acknowledgementDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
		emailMap.add("attachments", value);
		emailMap.add("mailContent", merseTemplate);
		emailMap.add("mailSubject", getEmailSubject(acknowledgementDTO, langCode));
		emailMap.add("mailTo", acknowledgementDTO.getEmailID());
		HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);

		 resp = restTemplate.exchange(emailResourseUrl, HttpMethod.POST, httpEntity,
				EmailResponseDTO.class);
		 fos.close(); 
		}catch (IOException e) {
			e.printStackTrace();
		}
		return resp.getBody().getStatus();
	}
	
	/**
	 * @param acknowledgementDTO
	 * @param langCode
	 * @return
	 */
	public String getEmailSubject(AcknowledgementDTO acknowledgementDTO,String langCode) {
		
		String url = resourceUrl + "/" + langCode + "/" + "Acknowledgement-email-subject";

		ResponseEntity<ResponseDTO> respEntity = restTemplate.getForEntity(url, ResponseDTO.class);

		List<TemplateResponseDTO>	response = respEntity.getBody().getTemplates();

		String fileText = response.get(0).getFileText();
		String merseEmailSubject = templateMerge(fileText, acknowledgementDTO);
		return merseEmailSubject;
	}

	public Map<String, Object> mapSetting(AcknowledgementDTO acknowledgementDTO) {
		Map<String, Object> responseMap = new HashMap<>();

		DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("yyyy/MM/dd");

		LocalDateTime now = LocalDateTime.now();
		LocalTime localTime = LocalTime.now(ZoneId.of("UTC"));
		
		
		responseMap.put("name", acknowledgementDTO.getName());
		responseMap.put("PRID", acknowledgementDTO.getPreId());
		responseMap.put("Date", dateFormate.format(now));
		responseMap.put("Time",  localTime);
		responseMap.put("Appointmentdate", acknowledgementDTO.getAppointmentDate());
		responseMap.put("Appointmenttime", acknowledgementDTO.getAppointmentTime());
		return responseMap;
	}

	/**
	 * @param acknowledgementDTO
	 * @param langCode
	 * @return
	 */
	public String smsNotification(AcknowledgementDTO acknowledgementDTO, String langCode) {
		List<TemplateResponseDTO> response = null;
		ResponseEntity<EmailResponseDTO> resp = null;
		String url = resourceUrl + "/" + langCode + "/" + "SMS-Acknowledgement";

		ResponseEntity<ResponseDTO> respEntity = restTemplate.getForEntity(url, ResponseDTO.class);
		response = respEntity.getBody().getTemplates();
		String fileText = response.get(0).getFileText();
		String merseTemplate = templateMerge(fileText, acknowledgementDTO);
		SMSRequestDTO smsRequestDTO = new SMSRequestDTO();
		smsRequestDTO.setMessage(merseTemplate);
		smsRequestDTO.setNumber(acknowledgementDTO.getMobNum());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<SMSRequestDTO> httpEntity = new HttpEntity<>(smsRequestDTO, headers);
		try {
			resp = restTemplate.exchange(smsResourseUrl, HttpMethod.POST, httpEntity, EmailResponseDTO.class);
		} catch (HttpClientErrorException e) {
			System.out.println(e.getResponseBodyAsString());
		}
		return resp.getBody().getStatus();

	}

	/**
	 * This method merging the template
	 * 
	 * @param fileText
	 * @param acknowledgementDTO
	 * @return
	 */
	public String templateMerge(String fileText, AcknowledgementDTO acknowledgementDTO) {

		String merseTemplate = null;
		Map<String, Object> map = mapSetting(acknowledgementDTO);
		try {
			InputStream templateInputStream = new ByteArrayInputStream(fileText.getBytes(Charset.forName("UTF-8")));

			InputStream resultedTemplate = templateManager.merge(templateInputStream, map);

			merseTemplate = IOUtils.toString(resultedTemplate, StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return merseTemplate;
	}
}
