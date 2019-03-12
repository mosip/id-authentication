package io.mosip.registration.processor.print.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.pdfgenerator.itext.constant.PDFGeneratorExceptionCodeConstant;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.UinCardType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.notification.template.mapping.NotificationTemplate;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.print.service.PrintService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.spi.uincardgenerator.UinCardGenerator;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.message.sender.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.FieldNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.InstantanceCreationException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.service.exception.UINNotFoundInDatabase;
import io.mosip.registration.processor.print.service.kernel.dto.IdResponseDTO;
import io.mosip.registration.processor.print.service.utility.UINCardConstant;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class PrintServiceImpl.
 * 
 * @author M1048358 Alok
 */
@Service
public class PrintServiceImpl implements PrintService<byte[]> {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant LANGUAGE. */
	private static final String LANGUAGE = "language";

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	/** The Constant ENG. */
	private static final String ENG = "eng";

	/** The Constant ARA. */
	private static final String ARA = "ara";

	/** The Constant UIN_CARD_TEMPLATE. */
	private static final String UIN_CARD_TEMPLATE = "RPR_UIN_CARD_TEMPLATE";

	/** The Constant UIN. */
	private static final String UIN = "UIN";

	/** The Constant RID. */
	private static final String RID = "RID";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PrintServiceImpl.class);

	/** The primary language. */
	@Value("${primary.language}")
	private String langCode;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The utility. */
	@Autowired
	private Utilities utility;

	/** The reg processor identity json. */
	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The demographic identity. */
	private JSONObject demographicIdentity = null;

	/** The attributes. */
	private Map<String, Object> attributes = new HashMap<>();

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The template generator. */
	@Autowired
	private TemplateGenerator templateGenerator;

	/** The uin card generator. */
	@Autowired
	private UinCardGenerator<ByteArrayOutputStream> uinCardGenerator;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The qr code generator. */
	@Autowired
	private QrCodeGenerator<QrVersion> qrCodeGenerator;

	/** The qr string. */
	private StringBuilder qrString = new StringBuilder();

	/** The is transactional. */
	private boolean isTransactionSuccessful = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.print.service.PrintService#getPdf(
	 * java.lang.String)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public byte[] getPdf(IdType idType, String idValue) {
		byte[] pdfBytes = null;
		String uin = null;
		String description = null;

		try {
			if (idType.toString().equalsIgnoreCase(UIN)) {
				uin = idValue;
			} else if (idType.toString().equalsIgnoreCase(RID)) {
				uin = packetInfoManager.getUINByRid(idValue).get(0);
				if (uin == null) {
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), uin,
							PlatformErrorMessages.RPR_PRT_UIN_NOT_FOUND_IN_DATABASE.name());
					throw new UINNotFoundInDatabase(PlatformErrorMessages.RPR_PRT_UIN_NOT_FOUND_IN_DATABASE.getCode());
				}
			}

			List<String> pathsegments = new ArrayList<>();
			pathsegments.add(uin);
			IdResponseDTO response = (IdResponseDTO) restClientService.getApi(ApiName.IDREPOSITORY, pathsegments, "",
					"", IdResponseDTO.class);

			String jsonString = new JSONObject((Map) response.getResponse().getIdentity()).toString();

			getArtifacts(jsonString);
			attributes.put(UINCardConstant.UIN, uin);

			// generating qrcode to be attached in uin card
			byte[] qrCodeBytes = qrCodeGenerator.generateQrCode(qrString.toString(), QrVersion.V30);
			File qrCode = new File("QrCode.png");
			FileUtils.writeByteArrayToFile(qrCode, qrCodeBytes);

			// generating template
			InputStream uinArtifact = templateGenerator.getTemplate(UIN_CARD_TEMPLATE, attributes, langCode);

			// generating pdf
			ByteArrayOutputStream pdf = uinCardGenerator.generateUinCard(uinArtifact, UinCardType.PDF);

			qrCode.delete();

			pdfBytes = pdf.toByteArray();

			isTransactionSuccessful = true;

		} catch (QrcodeGenerationException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					uin, PlatformErrorMessages.RPR_PRT_QRCODE_NOT_GENERATED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					e.getMessage() + ExceptionUtils.getStackTrace(e));

		} catch (UINNotFoundInDatabase e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					uin, PlatformErrorMessages.RPR_PRT_UIN_NOT_FOUND_IN_DATABASE.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					e.getMessage() + ExceptionUtils.getStackTrace(e));

		} catch (TemplateProcessingFailureException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					uin, PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw new TemplateProcessingFailureException(PlatformErrorMessages.RPR_TEM_PROCESSING_FAILURE.getCode());

		} catch (PDFGeneratorException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					uin, PlatformErrorMessages.RPR_PRT_PDF_NOT_GENERATED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					e.getMessage() + ExceptionUtils.getStackTrace(e));

		} catch (ApisResourceAccessException | IOException | ParseException
				| io.mosip.kernel.core.exception.IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					uin, PlatformErrorMessages.RPR_PRT_PDF_GENERATION_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					e.getMessage() + ExceptionUtils.getStackTrace(e));

		} finally {
			String eventId = "";
			String eventName = "";
			String eventType = "";
			if (isTransactionSuccessful) {
				description = "Pdf generated and sent to print stage";
				eventId = EventId.RPR_402.toString();
				eventName = EventName.UPDATE.toString();
				eventType = EventType.BUSINESS.toString();
			} else {

				description = "Pdf was not generated for uin card template";
				eventId = EventId.RPR_405.toString();
				eventName = EventName.EXCEPTION.toString();
				eventType = EventType.SYSTEM.toString();
			}
			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, uin,
					ApiName.AUDIT);
		}

		return pdfBytes;
	}

	/**
	 * Gets the artifacts.
	 *
	 * @param idJsonString
	 *            the id json string
	 * @return the artifacts
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 */
	private void getArtifacts(String idJsonString) throws IOException, ParseException {
		NotificationTemplate template = new NotificationTemplate();
		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorIdentityJson());

		regProcessorIdentityJson = (new ObjectMapper()).readValue(getIdentityJsonString,
				RegistrationProcessorIdentity.class);
		demographicIdentity = (JSONObject) JsonUtil.objectMapperReadValue(idJsonString, JSONObject.class);

		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

		template.setFirstName(getJsonValues(regProcessorIdentityJson.getIdentity().getName().getValue()));
		template.setGender(getJsonValues(regProcessorIdentityJson.getIdentity().getGender().getValue()));
		template.setEmailID(
				(String) JsonUtil.getJSONValue(demographicIdentity,regProcessorIdentityJson.getIdentity().getEmail().getValue()));
		template.setPhoneNumber(
				(String) JsonUtil.getJSONValue(demographicIdentity,regProcessorIdentityJson.getIdentity().getPhone().getValue()));
		template.setDateOfBirth(
				(String) JsonUtil.getJSONValue(demographicIdentity,regProcessorIdentityJson.getIdentity().getDob().getValue()));
		template.setAddressLine1(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine1().getValue()));
		template.setAddressLine2(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine2().getValue()));
		template.setAddressLine3(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine3().getValue()));
		template.setRegion(getJsonValues(regProcessorIdentityJson.getIdentity().getRegion().getValue()));
		template.setProvince(getJsonValues(regProcessorIdentityJson.getIdentity().getProvince().getValue()));
		template.setCity(getJsonValues(regProcessorIdentityJson.getIdentity().getCity().getValue()));
		template.setPostalCode(
				(String) JsonUtil.getJSONValue(demographicIdentity,regProcessorIdentityJson.getIdentity().getPostalCode().getValue()));

		setAtrributes(template);
	}

	/**
	 * Sets the atrributes.
	 *
	 * @param template
	 *            the new atrributes
	 */
	private void setAtrributes(NotificationTemplate template) {
		attributes.put(UINCardConstant.NAME_ENG, getParameter(template.getFirstName(), ENG));
		attributes.put(UINCardConstant.NAME_ARA, getParameter(template.getFirstName(), ARA));
		attributes.put(UINCardConstant.GENDER_ENG, getParameter(template.getGender(), ENG));
		attributes.put(UINCardConstant.GENDER_ARA, getParameter(template.getGender(), ARA));
		attributes.put(UINCardConstant.DATEOFBIRTH, template.getDateOfBirth());
		attributes.put(UINCardConstant.ADDRESSLINE1_ENG, getParameter(template.getAddressLine1(), ENG));
		attributes.put(UINCardConstant.ADDRESSLINE1_ARA, getParameter(template.getAddressLine1(), ARA));
		attributes.put(UINCardConstant.ADDRESSLINE2_ENG, getParameter(template.getAddressLine2(), ENG));
		attributes.put(UINCardConstant.ADDRESSLINE2_ARA, getParameter(template.getAddressLine2(), ARA));
		attributes.put(UINCardConstant.ADDRESSLINE3_ENG, getParameter(template.getAddressLine3(), ENG));
		attributes.put(UINCardConstant.ADDRESSLINE3_ARA, getParameter(template.getAddressLine3(), ARA));
		attributes.put(UINCardConstant.REGION_ENG, getParameter(template.getRegion(), ENG));
		attributes.put(UINCardConstant.REGION_ARA, getParameter(template.getRegion(), ARA));
		attributes.put(UINCardConstant.PROVINCE_ENG, getParameter(template.getProvince(), ENG));
		attributes.put(UINCardConstant.PROVINCE_ARA, getParameter(template.getProvince(), ARA));
		attributes.put(UINCardConstant.CITY_ENG, getParameter(template.getCity(), ENG));
		attributes.put(UINCardConstant.CITY_ARA, getParameter(template.getCity(), ARA));
		attributes.put(UINCardConstant.POSTALCODE, template.getPostalCode());
		attributes.put(UINCardConstant.PHONENUMBER, template.getPhoneNumber());
		attributes.put(UINCardConstant.EMAILID, template.getEmailID());

		for (Object param : attributes.values()) {
			qrString.append(param);
		}
	}

	/**
	 * Gets the parameter.
	 *
	 * @param jsonValues
	 *            the json values
	 * @param langCode
	 *            the lang code
	 * @return the parameter
	 */
	private String getParameter(JsonValue[] jsonValues, String langCode) {
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
	 * Gets the json values.
	 *
	 * @param identityKey
	 *            the identity key
	 * @return the json values
	 */
	private JsonValue[] getJsonValues(Object identityKey) {
		JSONArray demographicJsonNode = null;
		if (demographicIdentity != null)
			demographicJsonNode = JsonUtil.getJSONArray(demographicIdentity, identityKey);

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

				JSONObject objects = JsonUtil.getJSONObjectFromArray(demographicJsonNode, i);
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
