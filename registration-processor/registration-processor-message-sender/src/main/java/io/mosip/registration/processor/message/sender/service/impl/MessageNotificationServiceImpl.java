package io.mosip.registration.processor.message.sender.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.notification.spi.EmailNotification;
import io.mosip.kernel.core.notification.spi.SmsNotification;
import io.mosip.kernel.smsnotification.dto.SmsRequestDto;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.notification.template.generator.TemplateGenerator;
import io.mosip.registration.processor.core.notification.template.generator.TemplateResponseDto;
import io.mosip.registration.processor.core.notification.template.mapping.NotificationTemplate;
import io.mosip.registration.processor.core.notification.template.mapping.RegistrationProcessorNotificationTemplate;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.dto.MessageNotificationResponse;
import io.mosip.registration.processor.message.sender.dto.ResponseDto;
import io.mosip.registration.processor.message.sender.service.MessageNotificationService;
import io.mosip.registration.processor.message.sender.utility.Utilities;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.FieldNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.InstantanceCreationException;
import io.mosip.registration.processor.packet.storage.exception.MappingJsonException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;

public class MessageNotificationServiceImpl implements MessageNotificationService {

	private JSONObject demographicIdentity = null;
	private static final String LANGUAGE = "language";
	private static final String LABEL = "label";
	private static final String VALUE = "value";
	
	@Value("${registration.processor.primary.language.code}")
	private String langCode;

	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	@Autowired
	private TemplateGenerator templateGenerator;

	@Autowired
	private EmailNotification<MultipartFile[], CompletableFuture<ResponseDto>> emailNotificationService;

	@Autowired
	private SmsNotification<?> smsNotifierService;

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	@Autowired
	private Utilities utility;

	@Autowired
	private RegistrationProcessorNotificationTemplate regProcessorTemplateJson;
	
	@Autowired
	private SmsRequestDto smsDto;
	
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	public MessageNotificationResponse sendSmsNotification(String templateTypeCode, String id, String idType,
			Map<String, Object> attributes) {
		InputStream demographicInfoStream = null;
		try {
			if (idType.equalsIgnoreCase("RID")) {
				demographicInfoStream = adapter.getFile(id,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
			} else {
				String rid = packetInfoManager.getRegIdByUIN(id).get(0);
				demographicInfoStream = adapter.getFile(rid,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
			}

			String demographicInfo = new String(IOUtils.toByteArray(demographicInfoStream));

			NotificationTemplate template = getKeysandValues(demographicInfo);

			attributes.put("FirstName", template.getFirstName());

			String langCode = "eng";

			String artifact = templateGenerator.templateGenerator(templateTypeCode, attributes, langCode);
			
			smsDto.setMessage(artifact);
			smsDto.setNumber(template.getPhoneNumber()[0].getValue());
			
			ResponseEntity response = (ResponseEntity) restClientService.(ApiName.MASTER_DATA,
					pathSegments, "", "", TemplateResponseDto.class);

			//smsNotifierService.sendSmsNotification(template.getPhoneNumber().toString(), artifact);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public MessageNotificationResponse sendEmailNotification(String templateTypeCode, String id, String idType,
			Map<String, Object> attributes, String[] CCEmailList, String subject, MultipartFile[] attachment) {
		
		InputStream demographicInfoStream = null;
		try {
			if (idType.equalsIgnoreCase("RID")) {
				demographicInfoStream = adapter.getFile(id,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
			} else {
				String rid = packetInfoManager.getRegIdByUIN(id).get(0);
				demographicInfoStream = adapter.getFile(rid,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
			}

			String demographicInfo = new String(IOUtils.toByteArray(demographicInfoStream));

			NotificationTemplate template = getKeysandValues(demographicInfo);

			attributes.put("FirstName", template.getFirstName());

			String artifact = templateGenerator.templateGenerator(templateTypeCode, attributes, langCode);
			
			String[] mailTo = new String[template.getEmailID().length];
			for(int i=0; i<mailTo.length;i++) {
				mailTo[i] = template.getEmailID()[i].getValue();
			}
			
			
			emailNotificationService.sendEmail(mailTo, CCEmailList,
					subject, artifact,attachment);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	private NotificationTemplate getKeysandValues(String demographicJsonString) {
		NotificationTemplate template = new NotificationTemplate();

		try {
			// Get Identity Json from config server and map keys to Java Object
			String templateJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
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
			// LOGGER.error("Error while mapping Identity Json ", e);
			throw new MappingJsonException(PlatformErrorMessages.RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION.getMessage(),
					e);

		} catch (ParseException e) {
			// LOGGER.error("Error while parsing Json file", e);
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
			// LOGGER.error("Error while Creating Instance of generic type", e);
			throw new InstantanceCreationException(PlatformErrorMessages.RPR_SYS_INSTANTIATION_EXCEPTION.getMessage(),
					e);

		} catch (NoSuchFieldException | SecurityException e) {
			// LOGGER.error("no such field exception", e);
			throw new FieldNotFoundException(PlatformErrorMessages.RPR_SYS_NO_SUCH_FIELD_EXCEPTION.getMessage(), e);

		}

		return javaObject;

	}

}
