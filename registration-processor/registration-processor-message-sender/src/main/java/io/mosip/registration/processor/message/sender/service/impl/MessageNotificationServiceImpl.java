package io.mosip.registration.processor.message.sender.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;
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
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.idjson.IdentityJson;
import io.mosip.registration.processor.core.packet.dto.idjson.ValuesDTO;
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
import io.mosip.registration.processor.message.sender.utility.TemplateConstant;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
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

	/** The idjson. */
	private IdentityJson idjson = new IdentityJson();

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

			setAttributes(id, idType, attributes);

			String artifact = templateGenerator.getTemplate(templateTypeCode, attributes, langCode);

			String phoneNumber = idjson.getIdentity().getPhone();
			if (phoneNumber == null || phoneNumber.isEmpty()) {
				throw new PhoneNumberNotFoundException(PlatformErrorMessages.RPR_SMS_PHONE_NUMBER_NOT_FOUND.getCode());
			}

			smsDto.setNumber(phoneNumber);
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

			setAttributes(id, idType, attributes);

			String artifact = templateGenerator.getTemplate(templateTypeCode, attributes, langCode);

			String email = idjson.getIdentity().getEmail();
			if (email == null || email.isEmpty()) {
				throw new EmailIdNotFoundException(PlatformErrorMessages.RPR_EML_EMAILID_NOT_FOUND.getCode());
			}

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
	 * Sets the attributes from id json.
	 *
	 * @param id
	 *            the id
	 * @param idType
	 *            the id type
	 * @param attributes
	 *            the attributes
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void setAttributes(String id, IdType idType, Map<String, Object> attributes) throws IOException {
		InputStream demographicInfoStream;

		if (idType.toString().equalsIgnoreCase(TemplateConstant.UIN)) {
			attributes.put(TemplateConstant.UIN, id);
			id = packetInfoManager.getRegIdByUIN(id).get(0);
			attributes.put(TemplateConstant.RID, id);
		} else {
			attributes.put(TemplateConstant.RID, id);
		}

		demographicInfoStream = adapter.getFile(id,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());

		String demographicInfo = new String(IOUtils.toByteArray(demographicInfoStream));

		setAttributes(demographicInfo, attributes);
	}

	/**
	 * Sets the attributes.
	 *
	 * @param demographicInfo
	 *            the demographic info
	 * @param attributes
	 *            the attributes
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void setAttributes(String demographicInfo, Map<String, Object> attributes) throws IOException {
		try {
			// Get Identity Json from config server and map keys to Java Object

			idjson = (IdentityJson) JsonUtils.jsonStringToJavaObject(IdentityJson.class, demographicInfo);

			attributes.put(TemplateConstant.FULLNAME, getParameter(idjson.getIdentity().getFullName()));
			attributes.put(TemplateConstant.DATEOFBIRTH, idjson.getIdentity().getDateOfBirth());

			attributes.put(TemplateConstant.AGE, idjson.getIdentity().getAge());
			attributes.put(TemplateConstant.ADDRESSLINE1, getParameter(idjson.getIdentity().getAddressLine1()));
			attributes.put(TemplateConstant.ADDRESSLINE2, getParameter(idjson.getIdentity().getAddressLine2()));
			attributes.put(TemplateConstant.ADDRESSLINE3, getParameter(idjson.getIdentity().getAddressLine3()));
			attributes.put(TemplateConstant.REGION, getParameter(idjson.getIdentity().getRegion()));
			attributes.put(TemplateConstant.PROVINCE, getParameter(idjson.getIdentity().getProvince()));
			attributes.put(TemplateConstant.CITY, getParameter(idjson.getIdentity().getCity()));
			attributes.put(TemplateConstant.POSTALCODE, idjson.getIdentity().getPostalCode());
			attributes.put(TemplateConstant.PARENTORGUARDIANNAME,
					getParameter(idjson.getIdentity().getParentOrGuardianName()));
			attributes.put(TemplateConstant.PARENTORGUARDIANRIDORUIN,
					idjson.getIdentity().getParentOrGuardianRIDOrUIN());
			attributes.put(TemplateConstant.PROOFOFADDRESS, idjson.getIdentity().getProofOfAddress().getValue());
			attributes.put(TemplateConstant.PROOFOFIDENTITY, idjson.getIdentity().getProofOfIdentity().getValue());
			attributes.put(TemplateConstant.PROOFOFRELATIONSHIP,
					idjson.getIdentity().getProofOfRelationship().getValue());
			attributes.put(TemplateConstant.PROOFOFDATEOFBIRTH,
					idjson.getIdentity().getProofOfDateOfBirth().getValue());
			attributes.put(TemplateConstant.INDIVIDUALBIOMETRICS,
					idjson.getIdentity().getIndividualBiometrics().getValue());
			attributes.put(TemplateConstant.LOCALADMINISTRATIVEAUTHORITY,
					getParameter(idjson.getIdentity().getLocalAdministrativeAuthority()));
			attributes.put(TemplateConstant.IDSCHEMAVERSION, idjson.getIdentity().getIdSchemaVersion());
			attributes.put(TemplateConstant.CNIENUMBER, idjson.getIdentity().getCnieNumber());

		} catch (JsonParseException | JsonMappingException | io.mosip.kernel.core.exception.IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					null, "Error while parsing Json file" + ExceptionUtils.getStackTrace(e));
			throw new ParsingException(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage(), e);
		}

	}

	/**
	 * Gets the parameter.
	 *
	 * @param values
	 *            the values
	 * @return the parameter
	 */
	private String getParameter(List<ValuesDTO> values) {
		String parameter = null;
		if (values != null) {
			for (int count = 0; count < values.size(); count++) {
				String lang = values.get(count).getLanguage();
				if (langCode.contains(lang)) {
					parameter = values.get(count).getValue();
					break;
				}
			}
		}

		return parameter;
	}

}
