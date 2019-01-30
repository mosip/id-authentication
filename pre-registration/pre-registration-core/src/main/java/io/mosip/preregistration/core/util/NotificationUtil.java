package io.mosip.preregistration.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.AcknowledgementDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationResponseDTO;
import io.mosip.preregistration.core.common.dto.SMSRequestDTO;

@Component
public class NotificationUtil {
 
	@Value("${emailResourse.url}")
	private String emailResourseUrl;

	@Value("${smsResourse.url}")
	private String smsResourseUrl;
	
	@Autowired
	private TemplateUtil templateUtil;
	
	@Autowired
	RestTemplate restTemplate;
	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public String notify(String notificationType,AcknowledgementDTO acknowledgementDTO,
			String langCode, File file) throws Exception {
		if(notificationType=="sms")  {
			smsNotification(acknowledgementDTO, langCode);
		}
		if(notificationType=="email") {
			emailNotification(acknowledgementDTO, langCode, file);
		}
		
		
		return null;
	}
	
	
	
	/**
	 * This method will send the email notification to the user
	 * 
	 * @param acknowledgementDTO
	 * @param langCode
	 * @param file
	 * @return
	 */
	public MainListResponseDTO<NotificationResponseDTO> emailNotification(AcknowledgementDTO acknowledgementDTO,
			String langCode, File file) throws Exception{
		ResponseEntity<NotificationResponseDTO> resp = null;
		MainListResponseDTO<NotificationResponseDTO> response = new MainListResponseDTO<>();
		String merseTemplate = null;

			//FileSystemResource value = new FileSystemResource(file);

			String fileText = templateUtil.getTemplate(langCode, "Email-Acknowledgement");
			merseTemplate =templateUtil.templateMerge(fileText, acknowledgementDTO);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
			emailMap.add("attachments", file);
			emailMap.add("mailContent", merseTemplate);
			emailMap.add("mailSubject", getEmailSubject(acknowledgementDTO, langCode));
			emailMap.add("mailTo", acknowledgementDTO.getEmailID());
			HttpEntity<MultiValueMap<Object, Object>> httpEntity = new HttpEntity<>(emailMap, headers);

			resp = restTemplate.exchange(emailResourseUrl, HttpMethod.POST, httpEntity, NotificationResponseDTO.class);
			List<NotificationResponseDTO> list = new ArrayList<>();
			NotificationResponseDTO notifierResponse = new NotificationResponseDTO();
			notifierResponse.setMessage(resp.getBody().getMessage());
			notifierResponse.setStatus(resp.getBody().getStatus());
			list.add(notifierResponse);
			response.setResponse(list);
			response.setResTime(getCurrentResponseTime());
			response.setStatus(Boolean.TRUE);
		
		return response;
	}

	/**
	 * This method will give the email subject
	 * 
	 * @param acknowledgementDTO
	 * @param langCode
	 * @return
	 */
	public String getEmailSubject(AcknowledgementDTO acknowledgementDTO, String langCode) {

		return  templateUtil.templateMerge(templateUtil.getTemplate(langCode, "Acknowledgement-email-subject"), acknowledgementDTO);
	}
	/**
	 * This method will send the sms notification to the user
	 * 
	 * @param acknowledgementDTO
	 * @param langCode
	 * @return
	 */
	public MainListResponseDTO<NotificationResponseDTO> smsNotification(AcknowledgementDTO acknowledgementDTO,
			String langCode) throws Exception{
		MainListResponseDTO<NotificationResponseDTO> response = new MainListResponseDTO<>();
		ResponseEntity<NotificationResponseDTO> resp = null;

			String mergeTemplate = templateUtil.templateMerge(templateUtil.getTemplate(langCode, "SMS-Acknowledgement"),
					acknowledgementDTO);
			SMSRequestDTO smsRequestDTO = new SMSRequestDTO();
			smsRequestDTO.setMessage(mergeTemplate);
			smsRequestDTO.setNumber(acknowledgementDTO.getMobNum());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<SMSRequestDTO> httpEntity = new HttpEntity<>(smsRequestDTO, headers);

			resp = restTemplate.exchange(smsResourseUrl, HttpMethod.POST, httpEntity, NotificationResponseDTO.class);

			List<NotificationResponseDTO> list = new ArrayList<>();
			NotificationResponseDTO notifierResponse = new NotificationResponseDTO();
			notifierResponse.setMessage(resp.getBody().getMessage());
			notifierResponse.setStatus(resp.getBody().getStatus());
			list.add(notifierResponse);
			response.setResponse(list);
			response.setResTime(getCurrentResponseTime());
			response.setStatus(Boolean.TRUE);
		
		return response;

	}
	 
		public String getCurrentResponseTime() {
			return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
		}
}
