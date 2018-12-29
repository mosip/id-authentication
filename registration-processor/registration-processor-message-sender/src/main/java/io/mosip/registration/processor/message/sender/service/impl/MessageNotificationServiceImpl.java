package io.mosip.registration.processor.message.sender.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsRequestDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.notification.template.mapping.NotificationTemplate;
import io.mosip.registration.processor.core.notification.template.mapping.RegistrationProcessorNotificationTemplate;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.ConfigurationNotFoundException;
import io.mosip.registration.processor.message.sender.exception.PhoneNumberNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.exception.TemplateNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.message.sender.utility.Util;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.FieldNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.InstantanceCreationException;
import io.mosip.registration.processor.packet.storage.exception.MappingJsonException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;
import io.mosip.registration.processor.rest.client.utils.RestApiClient;

/**
 * ServiceImpl class for sending notification.
 * 
 * @author Alok Shuchita Ayush
 * 
 * @since 1.0.0
 *
 */
@Service
public class MessageNotificationServiceImpl
		implements MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> {

	private JSONObject demographicIdentity = null;
	private static final String LANGUAGE = "language";
	private static final String LABEL = "label";
	private static final String VALUE = "value";

	private static final String UIN = "UIN";
	private static final String PRIMARY_LANGUAGE = "primary.language";
	public static final String FILE_SEPARATOR = "\\";
	private String langCode;
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageNotificationServiceImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	@Autowired
	private TemplateGenerator templateGenerator;

	@Autowired
	private Util utility;

	@Autowired
	private RegistrationProcessorNotificationTemplate regProcessorTemplateJson;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	private RestApiClient resclient;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	private SmsRequestDto smsDto = new SmsRequestDto();

	public SmsResponseDto sendSmsNotification(String templateTypeCode, String id, String idType,
			Map<String, Object> attributes) {
		SmsResponseDto response = null;

		try {
			langCode = getPrimaryLanguage();
			NotificationTemplate templatejson = getTemplateJson(id, idType, attributes);

			String artifact = templateGenerator.templateGenerator(templateTypeCode, attributes, langCode);

			if (templatejson.getPhoneNumber()[0].getValue().isEmpty()) {
				throw new PhoneNumberNotFoundException(PlatformErrorMessages.RPR_SMS_PHONE_NUMBER_NOT_FOUND.getCode());
			}
			smsDto.setNumber(templatejson.getPhoneNumber()[0].getValue());
			smsDto.setMessage(artifact);

			response = (SmsResponseDto) restClientService.postApi(ApiName.SMSNOTIFIER, "", "", smsDto,
					SmsResponseDto.class);

		} catch (TemplateNotFoundException | TemplateProcessingFailureException e) {
			LOGGER.error("Template was not found for this templateTypeCode and langCode");
			throw new TemplateGenerationFailedException(
					PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.getCode(), e);
		} catch (ApisResourceAccessException e) {
			LOGGER.error("Sms can not be sent due to some technical error", e);
		} catch (Exception e) {
			LOGGER.error("Sms can not be sent due to some internal error", e);
		}

		return response;
	}

	public ResponseDto sendEmailNotification(String templateTypeCode, String id, String idType,
			Map<String, Object> attributes, String[] mailCc, String subject, MultipartFile[] attachment) {
		ResponseDto response = null;

		try {
			langCode = getPrimaryLanguage();
			NotificationTemplate template = getTemplateJson(id, idType, attributes);

			String artifact = templateGenerator.templateGenerator(templateTypeCode, attributes, langCode);

			String[] mailTo = new String[template.getEmailID().length];
			for (int i = 0; i < mailTo.length; i++) {
				if (langCode.contains(template.getEmailID()[i].getLanguage()))
					mailTo[i] = template.getEmailID()[i].getValue();
			}

			response = sendEmail(mailTo, mailCc, subject, artifact, attachment);

		} catch (TemplateNotFoundException | TemplateProcessingFailureException e) {
			LOGGER.error("Template was not found for this templateTypeCode and langCode");
			throw new TemplateGenerationFailedException(
					PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.getCode(), e);
		} catch (Exception e) {
			LOGGER.error("Sms can not be sent due to some internal error", e);
		}

		return response;
	}

	private ResponseDto sendEmail(String[] mailTo, String[] mailCc, String subject, String artifact,
			MultipartFile[] attachment) {

		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

		String apiHost = env.getProperty(ApiName.EMAILNOTIFIER.name());
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiHost);

		for (String item : mailTo) {
			builder.queryParam("mailTo", item);
		}
		for (String item : mailCc) {
			builder.queryParam("mailCc", item);
		}

		builder.queryParam("mailSubject", subject);
		builder.queryParam("mailContent", artifact);

		params.add("attachments", attachment);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);

		Object response = resclient.postApi(builder.build().toUriString(), requestEntity, ResponseDto.class);

		return (ResponseDto) response;
	}

	private NotificationTemplate getTemplateJson(String id, String idType, Map<String, Object> attributes)
			throws IOException {
		InputStream demographicInfoStream;
		if (idType.equalsIgnoreCase(UIN)) {
			// get registration id using UIN
			id = packetInfoManager.getRegIdByUIN(id).get(0);
		}

		demographicInfoStream = adapter.getFile(id,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());

		String demographicInfo = new String(IOUtils.toByteArray(demographicInfoStream));

		NotificationTemplate templatejson = getKeysandValues(demographicInfo);

		String firstName = null;
		for (int count = 0; count < templatejson.getFirstName().length; count++) {
			String lang = templatejson.getFirstName()[count].getLanguage();
			if (langCode.contains(lang)) {
				firstName = templatejson.getFirstName()[count].getValue();
				break;
			}
		}
		attributes.put("FirstName", firstName);

		return templatejson;
	}

	private NotificationTemplate getKeysandValues(String demographicJsonString) {
		NotificationTemplate template = new NotificationTemplate();

		try {
			// Get Identity Json from config server and map keys to Java Object
			String templateJsonString = Util.getJson(utility.getConfigServerFileStorageURL(),
					utility.getGetRegProcessorTemplateJson());

			ObjectMapper mapTemplateJsonStringToObject = new ObjectMapper();

			regProcessorTemplateJson = mapTemplateJsonStringToObject.readValue(templateJsonString,
					RegistrationProcessorNotificationTemplate.class);

			JSONParser parser = new JSONParser();
			JSONObject demographicJson = (JSONObject) parser.parse(demographicJsonString);

			demographicIdentity = (JSONObject) demographicJson.get(utility.getGetRegProcessorDemographicIdentity());
			if (demographicIdentity == null)
				throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

			template.setFirstName(getJsonValues(regProcessorTemplateJson.getFirstName()));
			template.setEmailID(getJsonValues(regProcessorTemplateJson.getEmailID()));
			template.setPhoneNumber(getJsonValues(regProcessorTemplateJson.getPhoneNumber()));

		} catch (IOException e) {
			LOGGER.error("Error while mapping Identity Json ", e);
			throw new MappingJsonException(PlatformErrorMessages.RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION.getMessage(),
					e);
		} catch (ParseException e) {
			LOGGER.error("Error while parsing Json file", e);
			throw new ParsingException(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage(), e);
		}

		return template;
	}

	private JsonValue[] getJsonValues(Object identityKey) {
		JSONArray demographicJsonNode = null;
		if (demographicIdentity != null)
			demographicJsonNode = (JSONArray) demographicIdentity.get(identityKey);

		return (demographicJsonNode != null)
				? (JsonValue[]) mapJsonNodeToJavaObject(JsonValue.class, demographicJsonNode)
				: null;
	}

	@SuppressWarnings("unchecked")
	private <T> T[] mapJsonNodeToJavaObject(Class<? extends Object> genericType, JSONArray demographicJsonNode) {
		String language;
		String label;
		String value;
		T[] javaObject = (T[]) Array.newInstance(genericType, demographicJsonNode.size());
		try {
			for (int i = 0; i < demographicJsonNode.size(); i++) {

				T jsonNodeElement = (T) genericType.newInstance();

				JSONObject objects = (JSONObject) demographicJsonNode.get(i);
				language = (String) objects.get(LANGUAGE);
				label = (String) objects.get(LABEL);
				value = (String) objects.get(VALUE);

				Field labelField = jsonNodeElement.getClass().getDeclaredField(LABEL);
				labelField.setAccessible(true);
				labelField.set(jsonNodeElement, label);

				Field languageField = jsonNodeElement.getClass().getDeclaredField(LANGUAGE);
				languageField.setAccessible(true);
				languageField.set(jsonNodeElement, language);

				Field valueField = jsonNodeElement.getClass().getDeclaredField(VALUE);
				valueField.setAccessible(true);
				valueField.set(jsonNodeElement, value);

				javaObject[i] = jsonNodeElement;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.error("Error while Creating Instance of generic type", e);
			throw new InstantanceCreationException(PlatformErrorMessages.RPR_SYS_INSTANTIATION_EXCEPTION.getMessage(),
					e);
		} catch (NoSuchFieldException | SecurityException e) {
			LOGGER.error("no such field exception", e);
			throw new FieldNotFoundException(PlatformErrorMessages.RPR_SYS_NO_SUCH_FIELD_EXCEPTION.getMessage(), e);
		}

		return javaObject;
	}

	private String getPrimaryLanguage() {
		langCode = env.getProperty(PRIMARY_LANGUAGE);
		if (langCode.isEmpty()) {
			throw new ConfigurationNotFoundException(PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode());
		}
		return langCode;
	}

}
