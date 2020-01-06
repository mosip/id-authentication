package io.mosip.resident.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import io.mosip.resident.util.JsonUtil;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;
import io.mosip.resident.util.Utilitiy;
import io.mosip.resident.validator.RequestValidator;

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

	@Autowired
	private Environment env;

	@Autowired
	private ResidentServiceRestClient restClient;

	@Autowired
	private TokenGenerator tokenGenerator;

	@Autowired
	private Utilitiy utility;

	@Autowired
	private RequestValidator requestValidator;

	private static final String LINE_SEPARATOR = new  StringBuilder().append('\n').append('\n').append('\n').toString();
	private static final String BOTH = "both";
	private static final String EMAIL = "_EMAIL";
	private static final String SMS = "_SMS";
	private static final String SUBJECT = "_SUB";
	private static final String SMS_EMAIL_SUCCESS = "Notification has been sent to the provided contact detail(s)";
	private static final String SMS_SUCCESS = "Notification has been sent to the provided contact phone number";
	private static final String EMAIL_SUCCESS = "Notification has been sent to the provided email ";
	private static final String SMS_EMAIL_FAILED = "Invalid phone number and email";
	private static final String IS_SMS_NOTIFICATION_SUCCESS = "NotificationService::sendSMSNotification()::isSuccess?::";
	private static final String IS_EMAIL_NOTIFICATION_SUCCESS = "NotificationService::sendEmailNotification()::isSuccess?::";
	private static final String TEMPLATE_CODE = "Template Code";
	private static final String SUCCESS = "success";

	public NotificationResponseDTO sendNotification(NotificationRequestDto dto) throws ResidentServiceCheckedException {
		logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), dto.getId(),
				"NotificationService::sendNotification()::entry");
		boolean smsStatus;
		boolean emailStatus;
		Map<String, Object> notificationAttributes = utility.getMailingAttributes(dto.getId(), dto.getIdType());
		if (dto.getAdditionalAttributes() != null && dto.getAdditionalAttributes().size() > 0) {
			notificationAttributes.putAll(dto.getAdditionalAttributes());
		}
		smsStatus = sendSMSNotification(notificationAttributes, dto.getTemplateTypeCode());
		emailStatus = sendEmailNotification(notificationAttributes, dto.getTemplateTypeCode(), null);
		logger.info(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), dto.getId(),
				IS_SMS_NOTIFICATION_SUCCESS + smsStatus);
		logger.info(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), dto.getId(),
				IS_EMAIL_NOTIFICATION_SUCCESS + emailStatus);
		NotificationResponseDTO notificationResponse = new NotificationResponseDTO();
		if (smsStatus && emailStatus) {
			notificationResponse.setMessage(SMS_EMAIL_SUCCESS);
			notificationResponse.setStatus(SUCCESS);
		} else if (smsStatus) {
			notificationResponse.setMessage(SMS_SUCCESS);
		} else if (emailStatus) {
			notificationResponse.setMessage(EMAIL_SUCCESS);
		} else {
			notificationResponse.setMessage(SMS_EMAIL_FAILED);
			throw new ResidentServiceException(ResidentErrorCode.NOTIFICATION_FAILURE.getErrorCode(),
					ResidentErrorCode.NOTIFICATION_FAILURE.getErrorMessage() + SMS_EMAIL_FAILED);
		}

		logger.info(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), dto.getId(),
				"NotificationService::sendSMSNotification()::isSuccess?::" + notificationResponse.getMessage());
		logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), dto.getId(),
				"NotificationService::sendNotification()::exit");
		return notificationResponse;
	}

	@SuppressWarnings("unchecked")
	private String getTemplate(String langCode, String templatetypecode) throws ResidentServiceCheckedException {
		logger.debug(LoggerFileConstant.APPLICATIONID.toString(), TEMPLATE_CODE, templatetypecode,
				"NotificationService::getTemplate()::entry");
		List<String> pathSegments = new ArrayList<>();
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
			TemplateResponseDto templateResponse = JsonUtil.readValue(JsonUtil.writeValueAsString(resp.getResponse()),
					TemplateResponseDto.class);
			logger.info(LoggerFileConstant.APPLICATIONID.toString(), TEMPLATE_CODE, templatetypecode,
					"NotificationService::getTemplate()::getTemplateResponse::" + JsonUtil.writeValueAsString(resp));
			List<TemplateDto> response = templateResponse.getTemplates();
			logger.debug(LoggerFileConstant.APPLICATIONID.toString(), TEMPLATE_CODE, templatetypecode,
					"NotificationService::getTemplate()::exit");
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
		logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), "",
				"NotificationService::templateMerge()::entry");
		try {
			String mergeTemplate;
			InputStream templateInputStream = new ByteArrayInputStream(fileText.getBytes(Charset.forName("UTF-8")));

			InputStream resultedTemplate = templateManager.merge(templateInputStream, mailingAttributes);

			mergeTemplate = IOUtils.toString(resultedTemplate, StandardCharsets.UTF_8.name());
			logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), "",
					"NotificationService::templateMerge()::exit");
			return mergeTemplate;
		} catch (IOException e) {
			throw new ResidentServiceCheckedException(ResidentErrorCode.IO_EXCEPTION.getErrorCode(),
					ResidentErrorCode.IO_EXCEPTION.getErrorMessage(), e);
		}
	}

	private boolean sendSMSNotification(Map<String, Object> mailingAttributes,
			NotificationTemplateCode notificationTemplate) throws ResidentServiceCheckedException {
		logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
				"NotificationService::sendSMSNotification()::entry");
		String phone = (String) mailingAttributes.get("phone");
		if (nullValueCheck(phone) || !(requestValidator.phoneValidator(phone))) {
			logger.info(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
					"NotificationService::sendSMSNotification()::phoneValidatio::" + "false :: invalid phone number");
			return false;
		}

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
		smsRequestDTO.setNumber(phone);
		RequestWrapper<SMSRequestDTO> req = new RequestWrapper<>();
		req.setRequest(smsRequestDTO);
		ResponseWrapper<NotificationResponseDTO> resp;
		try {
			resp = restClient.postApi(env.getProperty(ApiName.SMSNOTIFIER.name()), MediaType.APPLICATION_JSON, req,
					ResponseWrapper.class, tokenGenerator.getToken());
			if (nullCheckForResponse(resp)) {
				throw new ResidentServiceException(ResidentErrorCode.IN_VALID_API_RESPONSE.getErrorCode(),
						ResidentErrorCode.IN_VALID_API_RESPONSE.getErrorMessage() + " SMSNOTIFIER API"
								+ (resp != null ? resp.getErrors().get(0) : ""));
			}
			NotificationResponseDTO notifierResponse = JsonUtil
					.readValue(JsonUtil.writeValueAsString(resp.getResponse()), NotificationResponseDTO.class);
			logger.info(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
					"NotificationService::sendSMSNotification()::response::"
							+ JsonUtil.writeValueAsString(notifierResponse));

			if (SUCCESS.equalsIgnoreCase(notifierResponse.getStatus())) {
				logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
						"NotificationService::sendSMSNotification()::exit");
				return true;
			}
		} catch (ApisResourceAccessException e) {

			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + httpClientException.getResponseBodyAsString());
				throw new ResidentServiceCheckedException(
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
						httpClientException.getResponseBodyAsString());

			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + httpServerException.getResponseBodyAsString());
				throw new ResidentServiceCheckedException(
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
						httpServerException.getResponseBodyAsString());
			} else {
				logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
						e.getMessage() + ExceptionUtils.getStackTrace(e));
				throw new ResidentServiceCheckedException(
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
						ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage() + e.getMessage(), e);
			}

		} catch (IOException e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
					e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new ResidentServiceCheckedException(ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorCode(),
					ResidentErrorCode.TOKEN_GENERATION_FAILED.getErrorMessage(), e);
		}
		logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
				"NotificationService::sendSMSNotification()::exit");

		return false;

	}

	private boolean sendEmailNotification(Map<String, Object> mailingAttributes,
			NotificationTemplateCode notificationTemplate, MultipartFile[] attachment)
			throws ResidentServiceCheckedException {
		logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
				"NotificationService::sendEmailNotification()::entry");
		String email = String.valueOf(mailingAttributes.get("email"));
		if (nullValueCheck(email) || !(requestValidator.emailValidator(email))) {
			logger.info(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
					"NotificationService::sendEmailNotification()::emailValidation::" + "false :: invalid email");
			return false;
		}
		String emailSubject = getTemplate(primaryLang, notificationTemplate + EMAIL + SUBJECT);
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

		UriComponentsBuilder builder = prepareBuilder(mailTo, mailCc);

		try {
			builder.queryParam("mailSubject", emailSubject);
			builder.queryParam("mailContent", primaryLanguageMergeTemplate);
			params.add("attachments", attachment);
			ResponseWrapper<NotificationResponseDTO> response;

			response = restClient.postApi(builder.build().toUriString(), MediaType.MULTIPART_FORM_DATA, params,
					ResponseWrapper.class, tokenGenerator.getToken());
			if (nullCheckForResponse(response)) {
				throw new ResidentServiceException(ResidentErrorCode.IN_VALID_API_RESPONSE.getErrorCode(),
						ResidentErrorCode.IN_VALID_API_RESPONSE.getErrorMessage() + " EMAILNOTIFIER API"
								+ (response != null ? response.getErrors().get(0) : ""));
			}
			NotificationResponseDTO notifierResponse = JsonUtil
					.readValue(JsonUtil.writeValueAsString(response.getResponse()), NotificationResponseDTO.class);
			logger.info(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
					"NotificationService::sendEmailNotification()::response::"
							+ JsonUtil.writeValueAsString(notifierResponse));

			if ("success".equals(notifierResponse.getStatus())) {
				logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
						"NotificationService::sendEmailNotification()::exit");
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
		logger.debug(LoggerFileConstant.APPLICATIONID.toString(), LoggerFileConstant.UIN.name(), " ",
				"NotificationService::sendEmailNotification()::exit");
		return false;

	}

	public boolean nullValueCheck(String value) {
		if (value == null || value.isEmpty())
			return true;
		return false;
	}

	public boolean nullCheckForResponse(ResponseWrapper<NotificationResponseDTO> response) {
		if (response == null || response.getResponse() == null
				|| response.getErrors() != null && !response.getErrors().isEmpty())
			return true;
		return false;

	}

	public UriComponentsBuilder prepareBuilder(String[] mailTo, String[] mailCc) {
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
		return builder;
	}
}
