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
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.LoggerFileConstant;
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

/**
 * 
 * @author Girish Yarru
 *
 */
@Component
public class NotificationService {
	private static final Logger logger = LoggerConfiguration.logConfig(NotificationService.class);
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

	@Autowired
	private Utilitiy utility;

	private static final String LINE_SEPARATOR = "" + '\n' + '\n' + '\n';
	private static final String BOTH = "both";
	private static final String EMAIL = "_EMAIL";
	private static final String SMS = "_SMS";

	public NotificationResponseDTO sendNotification(NotificationRequestDto dto) throws ResidentServiceCheckedException {
		logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), dto.getId(), "NotificationService::sendNotification()::entry");
		String subject = "";
		boolean smsStatus = false;
		boolean emailStatus = false;
		// try {
		Map<String, Object> notificationAttributes = utility.getMailingAttributes(dto.getId(), dto.getIdType());
		if (dto.getAdditionalAttributes() != null && dto.getAdditionalAttributes().size() > 0) {
			notificationAttributes.putAll(dto.getAdditionalAttributes());
		}
		switch (dto.getTemplateTypeCode().name()) {
		case "RS_DOW_UIN_Status":
			subject = "request for download e-card is sucessfull";
			break;
		case "RS_UIN_RPR_Status":
			subject = "Request for re-print UIN successfull";
			break;
		case "RS_AUTH_HIST_Status":
			subject = "Request for Auth History status is successfull";
			break;
		case "RS_LOCK_AUTH_Status":
			subject = "Request for locking AuthTypes is successfull ";
			break;
		case "RS_INV_DATA_NOT":
			subject = "Data entered is invalid";
			break;
		case "RS_INV_RID_NOT":
			subject = "Invalid RID";
			break;
		case "RS_INV_UIN-VID_NOT":
			subject = "UIN/VID entered is invalid";
			break;
		case "RS_LOST_RID_Status":
			subject = "Request for lost RID is successful";
			break;
		case "RS_NO_MOB-MAIL-ID":
			subject = "Registered mobile number/email not found";
			break;
		case "RS_UIN_GEN_Status":
			subject = "UIN status for requestewd RID";
			break;
		case "RS_UIN_UPD_REQ":
			subject = "Request for UIN update is successfull";
			break;
		case "RS_UIN_UPD_Status":
			subject = "UIN update status for requested RID";
			break;
		case "RS_UIN_UPD_VAL":
			subject = "Uploaded document validation failed";
			break;
		case "RS_UNLOCK_AUTH_Status":
			subject = "Request for unlocking Auth(s) is successfull";
			break;
		case "RS_VIN_GEN_Status":
			subject = "VID generated for the requested RID";
			break;
		case "RS_VIN_REV_Status":
			subject = "VID revoked successfully";
			break;
		}

		smsStatus = sendSMSNotification(notificationAttributes, dto.getTemplateTypeCode());
		emailStatus = sendEmailNotification(notificationAttributes, dto.getTemplateTypeCode(), null, subject);
		NotificationResponseDTO notificationResponse = new NotificationResponseDTO();
		if (!(smsStatus && emailStatus))
			throw new ResidentServiceException(ResidentErrorCode.NOTIFICATION_FAILURE.getErrorCode(),
					ResidentErrorCode.NOTIFICATION_FAILURE.getErrorMessage());
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
						ResidentErrorCode.TEMPLATE_EXCEPTION.getErrorMessage()
								+ (resp != null ? resp.getErrors().get(0) : ""));
			}
			TemplateResponseDto templateResponse = JsonUtil.objectMapperReadValue(
					JsonUtil.objectMapperObjectToJson(resp.getResponse()), TemplateResponseDto.class);
			List<TemplateDto> response = templateResponse.getTemplates();

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

	private String templateMerge(String fileText, Map<String, Object> mailingAttributes)
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
		String primaryLanguageMergeTemplate = templateMerge(getTemplate(primaryLang, notificationTemplate + SMS),
				mailingAttributes);

		if (languageType.equalsIgnoreCase(BOTH)) {
			String secondaryLanguageMergeTemplate = templateMerge(
					getTemplate(secondaryLang, notificationTemplate + SMS), mailingAttributes);
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
			NotificationResponseDTO notifierResponse = JsonUtil.objectMapperReadValue(
					JsonUtil.objectMapperObjectToJson(resp.getResponse()), NotificationResponseDTO.class);
			if (notifierResponse.getStatus().equalsIgnoreCase("success"))
				return true;
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

		} catch (IOException e) {
			throw new ResidentServiceCheckedException(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode(),
					ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorMessage(), e);
		}

		return false;

	}

	private boolean sendEmailNotification(Map<String, Object> mailingAttributes,
			NotificationTemplateCode notificationTemplate, MultipartFile[] attachment, String subject)
			throws ResidentServiceCheckedException {
		String primaryLanguageMergeTemplate = templateMerge(getTemplate(primaryLang, notificationTemplate + EMAIL),
				mailingAttributes);
		if (languageType.equalsIgnoreCase(BOTH)) {
			String secondaryLanguageMergeTemplate = templateMerge(
					getTemplate(secondaryLang, notificationTemplate + EMAIL), mailingAttributes);
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
		try {
			builder.queryParam("mailSubject", subject);
			builder.queryParam("mailContent", primaryLanguageMergeTemplate);
			params.add("attachments", attachment);
			ResponseWrapper<NotificationResponseDTO> response;

			response = restClient.postApi(builder.build().toUriString(), MediaType.MULTIPART_FORM_DATA, params,
					ResponseWrapper.class, tokenGenerator.getToken());
			// ObjectMapper mapper = new ObjectMapper();
			NotificationResponseDTO notifierResponse = JsonUtil.objectMapperReadValue(
					JsonUtil.objectMapperObjectToJson(response.getResponse()), NotificationResponseDTO.class);

			if (notifierResponse.getStatus().equals("success")) {
				return true;
			}
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

		} catch (IOException e) {
			throw new ResidentServiceCheckedException(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode(),
					ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorMessage(), e);
		}

		return false;

	}

}
