package io.mosip.resident.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.NotificationTemplate;
import io.mosip.resident.dto.NotificationRequestDto;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.SMSRequestDTO;
import io.mosip.resident.dto.TemplateDto;
import io.mosip.resident.dto.TemplateResponseDto;

@Component
public class NotificationService {
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private TemplateManager templateManager;

	private static final String regType = "RES_UPDATE";

	@Value("${mosip.primary-language}")
	private String primaryLang;

	@Value("${mosip.secondary-language}")
	private String secondaryLang;

	@Value("${mosip.notification.language-type}")
	private String languageType;

	@Value("${registration.processor.notification.emails}")
	private String notificationEmails;

	@Autowired
	private Environment env;

	@Autowired
	private ResidentServiceRestClient restClient;

	@Autowired
	private TokenGenerator tokenGenerator;

	public static final String LINE_SEPARATOR = "" + '\n' + '\n' + '\n';

	// idType
	// subject

	// ---------dto---
	// ---need to find email and phone number
	// regId
	// uin
	// vid

	@Autowired
	private Utilitiy utility;

	public void sendNotification(NotificationRequestDto dto) {
		String subject = "";

		try {
			Map<String, Object> mailingAttributes = utility.getMailingAttributes(dto.getId(), dto.getIdType());
			if (dto.getAdditionalAttributes() != null && dto.getAdditionalAttributes().size() > 0) {
				mailingAttributes.putAll(dto.getAdditionalAttributes());
			}
			switch (dto.getTemplateType().name()) {
			case "RS_DOW_UIN_Status":
				subject = "Download e-card";
				break;
			case "RS_UIN_RPR_Status_EMAIL":
				subject = "Request for re-print UIN";
			}

			sendSMSNotification(mailingAttributes, dto.getTemplateType());
			sendEmailNotification(mailingAttributes, dto.getTemplateType(), null, subject);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getTemplate(String langCode, String templatetypecode) {

		String url = ApiName.TEMPLATES + "/" + langCode + "/" + templatetypecode;
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<RequestWrapper<TemplateResponseDto>> httpEntity = new HttpEntity<>(headers);
		ResponseEntity<ResponseWrapper<TemplateResponseDto>> respEntity = restTemplate.exchange(url, HttpMethod.GET,
				httpEntity, new ParameterizedTypeReference<ResponseWrapper<TemplateResponseDto>>() {
				});

		List<TemplateDto> response = respEntity.getBody().getResponse().getTemplates();

		return response.get(0).getFileText().replaceAll("^\"|\"$", "");

	}

	public String templateMerge(String fileText, Map<String, Object> mailingAttributes) throws IOException {
		String mergeTemplate = null;
		InputStream templateInputStream = new ByteArrayInputStream(fileText.getBytes(Charset.forName("UTF-8")));

		InputStream resultedTemplate = templateManager.merge(templateInputStream, mailingAttributes);

		mergeTemplate = IOUtils.toString(resultedTemplate, StandardCharsets.UTF_8.name());

		return mergeTemplate;
	}

	private boolean sendSMSNotification(Map<String, Object> mailingAttributes,
			NotificationTemplate notificationTemplate) throws IOException {
		// ResponseWrapper<NotificationResponseDTO> response = new ResponseWrapper<>();
		ResponseEntity<ResponseWrapper<NotificationResponseDTO>> resp = null;

		String primaryLanguageMergeTemplate = templateMerge(getTemplate(primaryLang, notificationTemplate + "_SMS"),
				mailingAttributes);

		if (languageType.equalsIgnoreCase("both")) {
			String secondaryLanguageMergeTemplate = templateMerge(
					getTemplate(secondaryLang, notificationTemplate + "_SMS"), mailingAttributes);
			primaryLanguageMergeTemplate = primaryLanguageMergeTemplate + LINE_SEPARATOR
					+ secondaryLanguageMergeTemplate;
		}

		SMSRequestDTO smsRequestDTO = new SMSRequestDTO();
		smsRequestDTO.setMessage(primaryLanguageMergeTemplate);
		smsRequestDTO.setNumber((String) mailingAttributes.get("phone"));
		RequestWrapper<SMSRequestDTO> req = new RequestWrapper<>();
		req.setRequest(smsRequestDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<RequestWrapper<SMSRequestDTO>> httpEntity = new HttpEntity<>(req, headers);
		resp = restTemplate.exchange(ApiName.SMSNOTIFIER.name(), HttpMethod.POST, httpEntity,
				new ParameterizedTypeReference<ResponseWrapper<NotificationResponseDTO>>() {
				});

		NotificationResponseDTO notifierResponse = new NotificationResponseDTO();
		notifierResponse.setMessage(resp.getBody().getResponse().getMessage());
		notifierResponse.setStatus(resp.getBody().getResponse().getStatus());
//		response.setResponse(notifierResponse);
//		response.setResponsetime(DateUtils.formatDate(new Date(System.currentTimeMillis()), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		if (resp.getBody().getResponse().getStatus().equalsIgnoreCase("success"))
			return true;
		return false;
	}

	private boolean sendEmailNotification(Map<String, Object> mailingAttributes,
			NotificationTemplate notificationTemplate, MultipartFile[] attachment, String subject) throws Exception {
		// ResponseEntity<ResponseWrapper<NotificationResponseDTO>> resp = null;

		String primaryLanguageMergeTemplate = templateMerge(getTemplate(primaryLang, notificationTemplate + "_SMS"),
				mailingAttributes);

		if (languageType.equalsIgnoreCase("both")) {
			String secondaryLanguageMergeTemplate = templateMerge(
					getTemplate(secondaryLang, notificationTemplate + "_SMS"), mailingAttributes);
			primaryLanguageMergeTemplate = primaryLanguageMergeTemplate + LINE_SEPARATOR
					+ secondaryLanguageMergeTemplate;
		}
		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		String[] mailTo = { mailingAttributes.get("email").toString() };
		String[] mailCc = notificationEmails.split("\\|");
		String apiHost = env.getProperty(ApiName.EMAILNOTIFIER.name());
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiHost);
		for (String item : mailTo) {
			builder.queryParam("mailTo", item);
		}

		if (mailCc != null) {
			for (String item : mailCc) {
				builder.queryParam("mailCc", item);
			}
		}

		builder.queryParam("mailSubject", subject);
		builder.queryParam("mailContent", primaryLanguageMergeTemplate);
		params.add("attachments", attachment);
		ResponseWrapper<NotificationResponseDTO> response = restClient.postApi(builder.build().toUriString(),
				MediaType.MULTIPART_FORM_DATA, params,
				new ParameterizedTypeReference<ResponseWrapper<NotificationResponseDTO>>() {
				}.getClass(), tokenGenerator.getToken());

		if (response.getResponse().getStatus().equals("success")) {
			return true;
		}

		return false;

	}

}
