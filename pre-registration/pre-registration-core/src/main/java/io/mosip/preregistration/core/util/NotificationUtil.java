package io.mosip.preregistration.core.util;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.NotificationDTO;
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
	
	public MainListResponseDTO<NotificationResponseDTO> notify(String notificationType,NotificationDTO acknowledgementDTO,
			String langCode, MultipartFile file) throws IOException  {
		
		MainListResponseDTO<NotificationResponseDTO> response=new MainListResponseDTO<>();
		if(notificationType=="sms")  {
			response=smsNotification(acknowledgementDTO, langCode);
		}
		if(notificationType=="email") {
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
	public MainListResponseDTO<NotificationResponseDTO> emailNotification(NotificationDTO acknowledgementDTO,
			String langCode, MultipartFile file) throws IOException {
		
		 LinkedMultiValueMap<String, String> pdfHeaderMap = new LinkedMultiValueMap<>();
		    pdfHeaderMap.add("Content-disposition", "form-data; name=attachments; filename=" + file.getOriginalFilename());
		    pdfHeaderMap.add("Content-type", "text/plain");
		    HttpEntity<byte[]> doc = new HttpEntity<>(file.getBytes(), pdfHeaderMap); 


		ResponseEntity<NotificationResponseDTO> resp = null;
		MainListResponseDTO<NotificationResponseDTO> response = new MainListResponseDTO<>();
		String merseTemplate = null;
			String fileText = templateUtil.getTemplate(langCode, "Email-Acknowledgement");
			merseTemplate =templateUtil.templateMerge(fileText, acknowledgementDTO);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<Object, Object> emailMap = new LinkedMultiValueMap<>();
			emailMap.add("attachments", doc);
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
	 * @throws IOException 
	 */
	public String getEmailSubject(NotificationDTO acknowledgementDTO, String langCode) throws IOException {

		return  templateUtil.templateMerge(templateUtil.getTemplate(langCode, "Acknowledgement-email-subject"), acknowledgementDTO);
	}
	/**
	 * This method will send the sms notification to the user
	 * 
	 * @param acknowledgementDTO
	 * @param langCode
	 * @return
	 * @throws IOException 
	 */
	public MainListResponseDTO<NotificationResponseDTO> smsNotification(NotificationDTO acknowledgementDTO,
			String langCode) throws IOException {
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
