package io.mosip.registration.processor.message.sender.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.notification.template.generator.dto.ResponseDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsRequestDto;
import io.mosip.registration.processor.core.notification.template.generator.dto.SmsResponseDto;
import io.mosip.registration.processor.core.notification.template.mapping.NotificationTemplate;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.message.sender.MessageNotificationService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.message.sender.exception.EmailIdNotFoundException;
import io.mosip.registration.processor.message.sender.exception.PhoneNumberNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateGenerationFailedException;
import io.mosip.registration.processor.message.sender.exception.TemplateNotFoundException;
import io.mosip.registration.processor.message.sender.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;
import io.mosip.registration.processor.message.sender.utility.TemplateConstant;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.FieldNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.InstantanceCreationException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;
import io.mosip.registration.processor.rest.client.utils.RestApiClient;

/**
 * ServiceImpl class for sending notification.
 * 
 * @author Alok Ranjan
 * 
 * @since 1.0.0
 *
 */
@Service
public class MessageNotificationServiceImpl
		implements MessageNotificationService<SmsResponseDto, ResponseDto, MultipartFile[]> {

	/** The demographic identity. */
	private JSONObject demographicIdentity = null;

	/** The Constant LANGUAGE. */
	private static final String LANGUAGE = "language";

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	/** The Constant UIN. */
	private static final String UIN = "UIN";

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(MessageNotificationServiceImpl.class);

	/** The primary language. */
	@Value("${primary.language}")
	private String langCode;

	/** The env. */
	@Autowired
	private Environment env;

	/** The adapter. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/** The template generator. */
	@Autowired
	private TemplateGenerator templateGenerator;

	/** The utility. */
	@Autowired
	private MessageSenderUtil utility;

	/** The reg processor template json. */
	@Autowired
	private RegistrationProcessorIdentity regProcessorTemplateJson;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The resclient. */
	@Autowired
	private RestApiClient resclient;

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The sms dto. */
	private SmsRequestDto smsDto = new SmsRequestDto();

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.message.sender.
	 * MessageNotificationService#sendSmsNotification(java.lang.String,
	 * java.lang.String, io.mosip.registration.processor.core.constant.IdType,
	 * java.util.Map)
	 */
	public SmsResponseDto sendSmsNotification(String templateTypeCode, String id, IdType idType,
			Map<String, Object> attributes) throws ApisResourceAccessException, IOException {
		SmsResponseDto response = null;

		try {

			NotificationTemplate templatejson = getTemplateJson(id, idType, attributes);

			String artifact = templateGenerator.getTemplate(templateTypeCode, attributes, langCode);

			if (templatejson.getPhoneNumber().isEmpty() || templatejson.getPhoneNumber() == null) {
				throw new PhoneNumberNotFoundException(PlatformErrorMessages.RPR_SMS_PHONE_NUMBER_NOT_FOUND.getCode());
			}
			smsDto.setNumber(templatejson.getPhoneNumber());
			smsDto.setMessage(artifact);

			response = (SmsResponseDto) restClientService.postApi(ApiName.SMSNOTIFIER, "", "", smsDto,
					SmsResponseDto.class);

		} catch (TemplateNotFoundException | TemplateProcessingFailureException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					id, PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw new TemplateGenerationFailedException(
					PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.getCode(), e);
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.core.spi.message.sender.
	 * MessageNotificationService#sendEmailNotification(java.lang.String,
	 * java.lang.String, io.mosip.registration.processor.core.constant.IdType,
	 * java.util.Map, java.lang.String[], java.lang.String, java.lang.Object)
	 */
	public ResponseDto sendEmailNotification(String templateTypeCode, String id, IdType idType,
			Map<String, Object> attributes, String[] mailCc, String subject, MultipartFile[] attachment)
			throws Exception {
		ResponseDto response = null;

		try {

			NotificationTemplate template = getTemplateJson(id, idType, attributes);

			String artifact = templateGenerator.getTemplate(templateTypeCode, attributes, langCode);

			if (template.getEmailID().isEmpty() || template.getEmailID() == null) {
				throw new EmailIdNotFoundException(PlatformErrorMessages.RPR_EML_EMAILID_NOT_FOUND.getCode());
			}

			String email = template.getEmailID();
			String[] mailTo = new String[1];
			mailTo[0] = email;

			response = sendEmail(mailTo, mailCc, subject, artifact, attachment);

		} catch (TemplateNotFoundException | TemplateProcessingFailureException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					id, PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw new TemplateGenerationFailedException(
					PlatformErrorMessages.RPR_SMS_TEMPLATE_GENERATION_FAILURE.getCode(), e);
		}

		return response;
	}

	/**
	 * Send email.
	 *
	 * @param mailTo
	 *            the mail to
	 * @param mailCc
	 *            the mail cc
	 * @param subject
	 *            the subject
	 * @param artifact
	 *            the artifact
	 * @param attachment
	 *            the attachment
	 * @return the response dto
	 * @throws Exception
	 *             the exception
	 */
	private ResponseDto sendEmail(String[] mailTo, String[] mailCc, String subject, String artifact,
			MultipartFile[] attachment) throws Exception {

		LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

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
		builder.queryParam("mailContent", artifact);

		params.add("attachments", attachment);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);

		Object response = resclient.postApi(builder.build().toUriString(), requestEntity, ResponseDto.class);

		return (ResponseDto) response;
	}

	/**
	 * Gets the template json.
	 *
	 * @param id
	 *            the id
	 * @param idType
	 *            the id type
	 * @param attributes
	 *            the attributes
	 * @return the template json
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private NotificationTemplate getTemplateJson(String id, IdType idType, Map<String, Object> attributes)
			throws IOException {
		InputStream demographicInfoStream;
		if (idType.toString().equalsIgnoreCase(UIN)) {
			attributes.put("UIN", id);
			// get registration id using UIN
			id = packetInfoManager.getRegIdByUIN(id).get(0);
			attributes.put("RID", id);
		} else {
			attributes.put("RID", id);
		}

		demographicInfoStream = adapter.getFile(id,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());

		String demographicInfo = new String(IOUtils.toByteArray(demographicInfoStream));

		NotificationTemplate templatejson = getKeysandValues(demographicInfo);

		attributes.put(TemplateConstant.FULLNAME, getParameter(templatejson.getFirstName()));
		attributes.put(TemplateConstant.DATEOFBIRTH, templatejson.getDateOfBirth());
		attributes.put(TemplateConstant.AGE, templatejson.getAge());
		attributes.put(TemplateConstant.ADDRESSLINE1, getParameter(templatejson.getAddressLine1()));
		attributes.put(TemplateConstant.ADDRESSLINE2, getParameter(templatejson.getAddressLine2()));
		attributes.put(TemplateConstant.ADDRESSLINE3, getParameter(templatejson.getAddressLine3()));
		attributes.put(TemplateConstant.REGION, getParameter(templatejson.getRegion()));
		attributes.put(TemplateConstant.PROVINCE, getParameter(templatejson.getProvince()));
		attributes.put(TemplateConstant.CITY, getParameter(templatejson.getCity()));
		attributes.put(TemplateConstant.POSTALCODE, templatejson.getPostalCode());
		attributes.put(TemplateConstant.PROOFOFADDRESS, templatejson.getProofOfAddress());
		attributes.put(TemplateConstant.PROOFOFIDENTITY, templatejson.getProofOfIdentity());
		attributes.put(TemplateConstant.PROOFOFRELATIONSHIP, templatejson.getProofOfRelationship());
		attributes.put(TemplateConstant.PROOFOFDATEOFBIRTH, templatejson.getProofOfDateOfBirth());
		attributes.put(TemplateConstant.INDIVIDUALBIOMETRICS, templatejson.getIndividualBiometrics());
		attributes.put(TemplateConstant.LOCALADMINISTRATIVEAUTHORITY, templatejson.getLocalAdministrativeAuthority());
		attributes.put(TemplateConstant.IDSCHEMAVERSION, templatejson.getIdSchemaVersion());
		attributes.put(TemplateConstant.CNIENUMBER, templatejson.getCnieNumber());
		attributes.put(TemplateConstant.GENDER, templatejson.getGender());
		attributes.put(TemplateConstant.PHONENUMBER, templatejson.getPhoneNumber());
		attributes.put(TemplateConstant.EMAILID, templatejson.getEmailID());

		return templatejson;
	}

	/**
	 * Gets the parameter.
	 *
	 * @param jsonValues
	 *            the json values
	 * @return the parameter
	 */
	private String getParameter(JsonValue[] jsonValues) {
		String parameter = null;
		if (jsonValues != null) {
			for (int count = 0; count < jsonValues.length; count++) {
				String lang = jsonValues[count].getLanguage();
				if (langCode.contains(lang)) {
					parameter = jsonValues[count].getValue();
					break;
				}
			}
		}
		return parameter;
	}

	/**
	 * Gets the keysand values.
	 *
	 * @param demographicJsonString
	 *            the demographic json string
	 * @return the keysand values
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private NotificationTemplate getKeysandValues(String demographicJsonString) throws IOException {
		NotificationTemplate template = new NotificationTemplate();

		try {
			// Get Identity Json from config server and map keys to Java Object
			String templateJsonString = MessageSenderUtil.getJson(utility.getConfigServerFileStorageURL(),
					utility.getGetRegProcessorIdentityJson());

			ObjectMapper mapTemplateJsonStringToObject = new ObjectMapper();

			regProcessorTemplateJson = mapTemplateJsonStringToObject.readValue(templateJsonString,
					RegistrationProcessorIdentity.class);

			JSONParser parser = new JSONParser();
			JSONObject demographicJson = (JSONObject) parser.parse(demographicJsonString);

			demographicIdentity = (JSONObject) demographicJson.get(utility.getGetRegProcessorDemographicIdentity());
			if (demographicIdentity == null)
				throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

			template.setFirstName(getJsonValues(regProcessorTemplateJson.getIdentity().getName().getValue()));
			template.setGender(getJsonValues(regProcessorTemplateJson.getIdentity().getGender().getValue()));

			template.setEmailID(
					(String) demographicIdentity.get(regProcessorTemplateJson.getIdentity().getEmail().getValue()));
			template.setPhoneNumber(
					(String) demographicIdentity.get(regProcessorTemplateJson.getIdentity().getPhone().getValue()));
			template.setDateOfBirth(
					(String) demographicIdentity.get(regProcessorTemplateJson.getIdentity().getDob().getValue()));
			template.setAge((Long) demographicIdentity.get(regProcessorTemplateJson.getIdentity().getAge().getValue()));
			template.setAddressLine1(
					getJsonValues(regProcessorTemplateJson.getIdentity().getAddressLine1().getValue()));
			template.setAddressLine2(
					getJsonValues(regProcessorTemplateJson.getIdentity().getAddressLine2().getValue()));
			template.setAddressLine3(
					getJsonValues(regProcessorTemplateJson.getIdentity().getAddressLine3().getValue()));
			template.setRegion(getJsonValues(regProcessorTemplateJson.getIdentity().getRegion().getValue()));
			template.setProvince(getJsonValues(regProcessorTemplateJson.getIdentity().getProvince().getValue()));
			template.setCity(getJsonValues(regProcessorTemplateJson.getIdentity().getCity().getValue()));
			template.setPostalCode((String) demographicIdentity
					.get(regProcessorTemplateJson.getIdentity().getPostalCode().getValue()));

			template.setProofOfRelationship((String) (regProcessorTemplateJson.getIdentity().getPor().getValue()));

			template.setProofOfAddress((String) regProcessorTemplateJson.getIdentity().getPoa().getValue());
			template.setProofOfIdentity((String) regProcessorTemplateJson.getIdentity().getPoi().getValue());
			template.setProofOfDateOfBirth((String) regProcessorTemplateJson.getIdentity().getPob().getValue());
			template.setIndividualBiometrics(
					(String) (regProcessorTemplateJson.getIdentity().getIndividualBiometrics().getValue()));
			template.setLocalAdministrativeAuthority(
					(String) regProcessorTemplateJson.getIdentity().getLocalAdministrativeAuthority().getValue());
			template.setIdSchemaVersion((Double) demographicIdentity
					.get(regProcessorTemplateJson.getIdentity().getIdschemaversion().getValue()));
			template.setCnieNumber(
					(Long) demographicIdentity.get(regProcessorTemplateJson.getIdentity().getCnienumber().getValue()));
			
		} catch (ParseException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					null, "Error while parsing Json file" + ExceptionUtils.getStackTrace(e));
			throw new ParsingException(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage(), e);
		}

		return template;
	}

	/**
	 * Gets the json values.
	 *
	 * @param identityKey
	 *            the identity key
	 * @return the json values
	 */
	private JsonValue[] getJsonValues(Object identityKey) {
		JSONArray demographicJsonNode = null;
		if (demographicIdentity != null)
			demographicJsonNode = (JSONArray) demographicIdentity.get(identityKey);

		return (demographicJsonNode != null) ? mapJsonNodeToJavaObject(JsonValue.class, demographicJsonNode) : null;
	}

	/**
	 * Map json node to java object.
	 *
	 * @param <T>
	 *            the generic type
	 * @param genericType
	 *            the generic type
	 * @param demographicJsonNode
	 *            the demographic json node
	 * @return the t[]
	 */
	@SuppressWarnings("unchecked")
	private <T> T[] mapJsonNodeToJavaObject(Class<? extends Object> genericType, JSONArray demographicJsonNode) {
		String language;
		String value;
		T[] javaObject = (T[]) Array.newInstance(genericType, demographicJsonNode.size());
		try {
			for (int i = 0; i < demographicJsonNode.size(); i++) {

				T jsonNodeElement = (T) genericType.newInstance();

				JSONObject objects = (JSONObject) demographicJsonNode.get(i);
				language = (String) objects.get(LANGUAGE);
				value = (String) objects.get(VALUE);

				Field languageField = jsonNodeElement.getClass().getDeclaredField(LANGUAGE);
				languageField.setAccessible(true);
				languageField.set(jsonNodeElement, language);

				Field valueField = jsonNodeElement.getClass().getDeclaredField(VALUE);
				valueField.setAccessible(true);
				valueField.set(jsonNodeElement, value);

				javaObject[i] = jsonNodeElement;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					null, "Error while Creating Instance of generic type" + ExceptionUtils.getStackTrace(e));
			throw new InstantanceCreationException(PlatformErrorMessages.RPR_SYS_INSTANTIATION_EXCEPTION.getMessage(),
					e);

		} catch (NoSuchFieldException | SecurityException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					null, "no such field exception" + ExceptionUtils.getStackTrace(e));
			throw new FieldNotFoundException(PlatformErrorMessages.RPR_SYS_NO_SUCH_FIELD_EXCEPTION.getMessage(), e);

		}

		return javaObject;

	}

}