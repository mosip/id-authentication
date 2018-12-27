package io.mosip.registration.processor.message.sender.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.PacketFiles;
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
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.message.sender.utility.Util;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.FieldNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.InstantanceCreationException;
import io.mosip.registration.processor.packet.storage.exception.MappingJsonException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;

/**
 * ServiceImpl class for sending notification.
 * 
 * @author 	Alok
 * 			Shuchita
 * 			Ayush
 * 		
 * @since 1.0.0
 *
 */
@Service
public class MessageNotificationServiceImpl implements MessageNotificationService<ResponseEntity<SmsResponseDto>,CompletableFuture<ResponseEntity<ResponseDto>>,MultipartFile[]> {

	private JSONObject demographicIdentity = null;
	private static final String LANGUAGE = "language";
	private static final String LABEL = "label";
	private static final String VALUE = "value";
	
	private static final String UIN = "UIN";

	@Value("${registration.processor.primary.language.code}")
	private String langCode;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageNotificationServiceImpl.class);

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	@Autowired
	private TemplateGenerator templateGenerator;

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	@Autowired
	private Util utility;

	@Autowired
	private RegistrationProcessorNotificationTemplate regProcessorTemplateJson;

	private SmsRequestDto smsDto = new SmsRequestDto();

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@SuppressWarnings("unchecked")
	public ResponseEntity<SmsResponseDto> sendSmsNotification(String templateTypeCode, String id, String idType,
			Map<String, Object> attributes) {
		
		ResponseEntity<SmsResponseDto> response = null;
		try {
			
			NotificationTemplate templatejson = getTemplateJson(id, idType, attributes);
			
			if(langCode == null) {
				throw new ConfigurationNotFoundException(PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode());
			}
			
			String artifact = templateGenerator.templateGenerator(templateTypeCode, attributes, langCode);

			smsDto.setMessage(artifact);
			smsDto.setNumber(templatejson.getPhoneNumber()[0].getValue());

			response = (ResponseEntity<SmsResponseDto>) restClientService.postApi(ApiName.SMSNOTIFIER, "", "", smsDto,
					ResponseEntity.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	@SuppressWarnings("unchecked")
	public CompletableFuture<ResponseEntity<ResponseDto>> sendEmailNotification(String templateTypeCode, String id,
			String idType, Map<String, Object> attributes, String[] mailCc, String subject,
			MultipartFile[] attachment) {

		CompletableFuture<ResponseEntity<ResponseDto>> response = null;
		
		try {
			NotificationTemplate template = getTemplateJson(id, idType, attributes);
			
			if(langCode == null) {
				throw new ConfigurationNotFoundException(PlatformErrorMessages.RPR_TEM_CONFIGURATION_NOT_FOUND.getCode());
			}
			
			String artifact = templateGenerator.templateGenerator(templateTypeCode, attributes, langCode);
			
			String[] mailTo = new String[template.getEmailID().length];
			for (int i = 0; i < mailTo.length; i++) {
				mailTo[i] = template.getEmailID()[i].getValue();
			}
			
			
			String queryparam = "mailTo,mailCc,mailSubject,mailContent,attachments";
			String queryParamValue = "alokranjan1106@gmail.com,alokranjan1106@gmail.com,Hello,Alok,null";

			response = (CompletableFuture<ResponseEntity<ResponseDto>>) restClientService.postApi(ApiName.EMAILNOTIFIER,
					queryparam, queryParamValue, "", ResponseEntity.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}
	
	private NotificationTemplate getTemplateJson(String id, String idType, Map<String, Object> attributes)
			throws IOException {
		InputStream demographicInfoStream;
		if (idType.equalsIgnoreCase( UIN )) {
			//get registration id using UIN
			id = packetInfoManager.getRegIdByUIN(id).get(0);
		}
		
		demographicInfoStream = adapter.getFile(id, PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
		
		String demographicInfo = new String(IOUtils.toByteArray(demographicInfoStream));

		NotificationTemplate templatejson = getKeysandValues(demographicInfo);

		attributes.put("FirstName", templatejson.getFirstName()[0].getValue());
		
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
	
}
