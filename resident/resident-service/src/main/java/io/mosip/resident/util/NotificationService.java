package io.mosip.resident.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.NotificationTemplateCode;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.dto.NotificationRequestDto;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.SMSRequestDTO;
import io.mosip.resident.dto.TemplateDto;
import io.mosip.resident.dto.TemplateResponseDto;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.exception.ResidentServiceException;

@Component
public class NotificationService {
	@Autowired
	private TemplateManager templateManager;

	@Value("${mosip.primary-language}")
	private String primaryLang;

	@Value("${mosip.secondary-language}")
	private String secondaryLang;

	@Value("${mosip.notification.language-type}")
	private String languageType;

	@Value("${resident.notification.emails}")
	private String notificationEmails;

	@Value("${resident.notification.message}")
	private String notificationMessage;

	@Autowired
	private Environment env;

	@Autowired
	private ResidentServiceRestClient restClient;

	@Autowired
	private TokenGenerator tokenGenerator;

	public static final String LINE_SEPARATOR = "" + '\n' + '\n' + '\n';

	@Autowired
	private Utilitiy utility;

	public NotificationResponseDTO sendNotification(NotificationRequestDto dto) throws ResidentServiceCheckedException {
		String subject = "";
		boolean smsStatus = false;
		boolean emailStatus = false;
		// try {
		Map<String, Object> notificationAttributes = utility.getMailingAttributes(dto.getId(), dto.getIdType());
		if (dto.getAdditionalAttributes() != null && dto.getAdditionalAttributes().size() > 0) {
			notificationAttributes.putAll(dto.getAdditionalAttributes());
		}
		// added only few cases
		switch (dto.getTemplateTypeCode().name()) {
		case "RS_DOW_UIN_Status":
			subject = "Download e-card request sucessful";
			break;
		case "RS_UIN_RPR_Status_EMAIL":
			subject = "Request for re-print UIN successfull";
			break;
		case "RS_AUTH_HIST_Status":
			subject = "Request for Auth History is successfull";
			break;
		case "RS_LOCK_AUTH_Status":
			subject = "Request for locking AuthTypes";
			break;

		}

		smsStatus = sendSMSNotification(notificationAttributes, dto.getTemplateTypeCode());
		emailStatus = sendEmailNotification(notificationAttributes, dto.getTemplateTypeCode(), null, subject);
		NotificationResponseDTO notificationResponse = new NotificationResponseDTO();
		if (!(smsStatus && emailStatus))
			throw new NullPointerExceptiozxcn();
		notificationResponse.setMessage(notificationMessage);
		return notificationResponse;
	}

	@SuppressWarnings("unchecked")
	private String getTemplate(String langCode, String templatetypecode) throws ResidentServiceCheckedException {
		List<String> pathSegments = new ArrayList<String>();
		pathSegments.add(langCode);
		pathSegments.add(templatetypecode);
		try {
			ResponseWrapper<TemplateResponseDto> resp = (ResponseWrapper<TemplateResponseDto>) restClient.getApi(
					ApiName.TEMPLATES, pathSegments, null, null, ResponseWrapper.class, tokenGenerator.getToken());
			if (resp == null || resp.getErrors() != null && !resp.getErrors().isEmpty()) {
				throw new ResidentServiceException(ResidentErrorCode.TEMPLATE_EXCEPTION.getErrorCode(),
						ResidentErrorCode.TEMPLATE_EXCEPTION.getErrorMessage() + resp.getErrors().get(0));
			}
			List<TemplateDto> response = resp.getResponse().getTemplates();

			return response.get(0).getFileText().replaceAll("^\"|\"$", "");
		} catch (IOException e) {
			throw new ResidentServiceCheckedException(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode(),
					ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorMessage(), e);
		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				throw new ResidentServiceCheckedException(
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
						httpClientException.getResponseBodyAsString());

			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				throw new ResidentServiceCheckedException(
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
						httpServerException.getResponseBodyAsString());
			} else {
				throw new ResidentServiceCheckedException(
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage() + e.getMessage(), e);
			}
		}

	}

	public String templateMerge(String fileText, Map<String, Object> mailingAttributes)
			throws ResidentServiceCheckedException {
		try {
			String mergeTemplate = null;
			InputStream templateInputStream = new ByteArrayInputStream(fileText.getBytes(Charset.forName("UTF-8")));

			InputStream resultedTemplate = templateManager.merge(templateInputStream, mailingAttributes);

			mergeTemplate = IOUtils.toString(resultedTemplate, StandardCharsets.UTF_8.name());

			return mergeTemplate;
		} catch (IOException e) {
			throw new ResidentServiceCheckedException(ResidentErrorCode.IO_EXCEPTION.getErrorCode(),
					ResidentErrorCode.IO_EXCEPTION.getErrorMessage(), e);
		}
	}

	private boolean sendSMSNotification(Map<String, Object> mailingAttributes,
			NotificationTemplateCode notificationTemplate) throws ResidentServiceCheckedException {
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
		ResponseWrapper<NotificationResponseDTO> resp;
		try {
			resp = restClient.postApi(env.getProperty(ApiName.SMSNOTIFIER.name()), MediaType.APPLICATION_JSON, req,
					ResponseWrapper.class, tokenGenerator.getToken());
			NotificationResponseDTO notifierResponse = new NotificationResponseDTO();
			notifierResponse.setMessage(resp.getResponse().getMessage());
			notifierResponse.setStatus(resp.getResponse().getStatus());
			if (resp.getResponse().getStatus().equalsIgnoreCase("success"))
				return true;
			return false;
		} catch (Exception e) {
			throw new NullArgumentException();
		}
	}

	private boolean sendEmailNotification(Map<String, Object> mailingAttributes,
			NotificationTemplateCode notificationTemplate, MultipartFile[] attachment, String subject)
			throws Exception {
		String primaryLanguageMergeTemplate = templateMerge(getTemplate(primaryLang, notificationTemplate + "_EMAIL"),
				mailingAttributes);
		if (languageType.equalsIgnoreCase("both")) {
			String secondaryLanguageMergeTemplate = templateMerge(
					getTemplate(secondaryLang, notificationTemplate + "_EMAIL"), mailingAttributes);
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
				MediaType.MULTIPART_FORM_DATA, params, ResponseWrapper.class, tokenGenerator.getToken());

		if (response.getResponse().getStatus().equals("success")) {
			return true;
		}
		return false;

	}

}
