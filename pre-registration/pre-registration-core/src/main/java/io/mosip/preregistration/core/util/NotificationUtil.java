package io.mosip.preregistration.core.util;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.code.RequestCodes;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
import io.mosip.preregistration.core.common.dto.NotificationResponseDTO;
import io.mosip.preregistration.core.common.dto.RequestWrapper;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.common.dto.SMSRequestDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;

/**
 * @author Sanober Noor
 *@since 1.0.0
 */
@Component
public class NotificationUtil {
	
	private Logger log = LoggerConfiguration.logConfig(NotificationUtil.class);
 
	@Value("${emailResourse.url}")
	private String emailResourseUrl;

	@Value("${smsResourse.url}")
	private String smsResourseUrl;
	
	@Value("${email.acknowledgement.template}")
	private String emailAcknowledgement;
	
	@Value("${email.acknowledgement.subject.template}")
	private String emailAcknowledgementSubject;
	
	@Value("${sms.acknowledgement.template}")
	private String smsAcknowledgement;
	
	@Value("${cancel.appoinment.template}")
	private String cancelAppoinment;
	
	@Autowired
	private TemplateUtil templateUtil;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${mosip.utc-datetime-pattern}")
	private String dateTimeFormat;
	
	public MainResponseDTO<NotificationResponseDTO> notify(String notificationType,NotificationDTO acknowledgementDTO,
			String langCode, MultipartFile file) throws IOException  {
		
		log.info("sessionId", "idType", "id", "In notify method of NotificationUtil service");
		
		MainResponseDTO<NotificationResponseDTO> response=new MainResponseDTO<>();
		if(notificationType.equals(RequestCodes.SMS))  {
			response=smsNotification(acknowledgementDTO, langCode);
		}
		if(notificationType.equals(RequestCodes.EMAIL)) {
			response=emailNotification(acknowledgementDTO, langCode, file);
		}
		
		
		return response;
	}
	
	
	
	/**
	 * This method will send the email notification to the user
	 * 
	 * @param acknowledgementDTO
	 * @param langCode
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public MainResponseDTO<NotificationResponseDTO> emailNotification(NotificationDTO acknowledgementDTO,
			String langCode, MultipartFile file) throws IOException {
		log.info("sessionId", "idType", "id", "In emailNotification method of NotificationUtil service");
		HttpEntity<byte[]> doc=null;
		String fileText=null;
		if(file!=null) {
		 LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
		    pdfHeaderMap.add("Content-disposition", "form-data; name=attachments; filename=" + file.getOriginalFilename());
		    pdfHeaderMap.add("Content-type", "text/plain");
		    doc = new HttpEntity<>(file.getBytes(), pdfHeaderMap); 
		}

		ResponseEntity<ResponseWrapper<NotificationResponseDTO>> resp = null;
		MainResponseDTO<NotificationResponseDTO> response = new MainResponseDTO<>();
		String merseTemplate = null;
		if(acknowledgementDTO.isBatch()) {
			 fileText = templateUtil.getTemplate(langCode, cancelAppoinment);
		}
		else { 
			
			fileText = templateUtil.getTemplate(langCode, emailAcknowledgement);
		
		}
		
			merseTemplate =templateUtil.templateMerge(fileText, acknowledgementDTO);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
			emailMap.add("attachments", doc);
			emailMap.add("mailContent", merseTemplate);
			emailMap.add("mailSubject", getEmailSubject(acknowledgementDTO, langCode));
			emailMap.add("mailTo", acknowledgementDTO.getEmailID());
			HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);
			log.info("sessionId", "idType", "id", "In emailNotification method of NotificationUtil service emailResourseUrl: "+emailResourseUrl);
			resp = restTemplate.exchange(emailResourseUrl, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ResponseWrapper<NotificationResponseDTO>>() {});
			
			NotificationResponseDTO notifierResponse = new NotificationResponseDTO();
			notifierResponse.setMessage(resp.getBody().getResponse().getMessage());
			notifierResponse.setStatus(resp.getBody().getResponse().getStatus());
			response.setResponse(notifierResponse);
			response.setResponsetime(getCurrentResponseTime());
		
		return response;
	}

	/**
	 * This method will give the email subject
	 * 
	 * @param acknowledgementDTO
	 * @param langCode
	 * @return
	 * @throws IOException 
	 */
	public String getEmailSubject(NotificationDTO acknowledgementDTO, String langCode) throws IOException {
		log.info("sessionId", "idType", "id", "In getEmailSubject method of NotificationUtil service");
		return  templateUtil.templateMerge(templateUtil.getTemplate(langCode, emailAcknowledgementSubject), acknowledgementDTO);
	}
	/**
	 * This method will send the sms notification to the user
	 * 
	 * @param acknowledgementDTO
	 * @param langCode
	 * @return
	 * @throws IOException 
	 */
	public MainResponseDTO<NotificationResponseDTO> smsNotification(NotificationDTO acknowledgementDTO,
			String langCode) throws IOException {
		log.info("sessionId", "idType", "id", "In smsNotification method of NotificationUtil service");
		MainResponseDTO<NotificationResponseDTO> response = new MainResponseDTO<>();
		ResponseEntity<ResponseWrapper<NotificationResponseDTO>> resp = null;
		String mergeTemplate =null;
		if(acknowledgementDTO.isBatch()) {
			mergeTemplate = templateUtil.templateMerge(templateUtil.getTemplate(langCode, cancelAppoinment),
					acknowledgementDTO);
		}
		else { 
			mergeTemplate = templateUtil.templateMerge(templateUtil.getTemplate(langCode, smsAcknowledgement),
					acknowledgementDTO);
		}
			SMSRequestDTO smsRequestDTO = new SMSRequestDTO();
			smsRequestDTO.setMessage(mergeTemplate);
			smsRequestDTO.setNumber(acknowledgementDTO.getMobNum());
			RequestWrapper<SMSRequestDTO> req=new RequestWrapper<>();
			req.setRequest(smsRequestDTO);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<RequestWrapper<SMSRequestDTO>> httpEntity = new HttpEntity<>(req, headers);
			log.info("sessionId", "idType", "id", "In smsNotification method of NotificationUtil service smsResourseUrl: "+smsResourseUrl);
			resp = restTemplate.exchange(smsResourseUrl, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<ResponseWrapper<NotificationResponseDTO>>() {});

			NotificationResponseDTO notifierResponse = new NotificationResponseDTO();
			notifierResponse.setMessage(resp.getBody().getResponse().getMessage());
			notifierResponse.setStatus(resp.getBody().getResponse().getStatus());
			response.setResponse(notifierResponse);
			response.setResponsetime(getCurrentResponseTime());
		
		return response;

	}
	 
		public String getCurrentResponseTime() {
			log.info("sessionId", "idType", "id", "In getCurrentResponseTime method of NotificationUtil service");
			return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
		}
}
