package io.mosip.registration.processor.stages.osivalidator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.crypto.SecretKey;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.processor.core.auth.dto.AuthRequestDTO;
import io.mosip.registration.processor.core.auth.dto.AuthTypeDTO;
import io.mosip.registration.processor.core.auth.dto.BioInfo;
import io.mosip.registration.processor.core.auth.dto.DataInfoDTO;
import io.mosip.registration.processor.core.auth.dto.IdentityDTO;
import io.mosip.registration.processor.core.auth.dto.IdentityInfoDTO;
import io.mosip.registration.processor.core.auth.dto.PinInfo;
import io.mosip.registration.processor.core.auth.dto.PublicKeyResponseDto;
import io.mosip.registration.processor.core.auth.dto.RequestDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PacketStructure;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.ErrorDTO;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RIDResponseDto;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.ServerError;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.masterdata.UserResponseDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.stages.osivalidator.utils.BioSubType;
import io.mosip.registration.processor.stages.osivalidator.utils.BioSubTypeMapperUtil;
import io.mosip.registration.processor.stages.osivalidator.utils.BioType;
import io.mosip.registration.processor.stages.osivalidator.utils.BioTypeMapperUtil;
import io.mosip.registration.processor.stages.osivalidator.utils.OSIUtils;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class OSIValidator.
 */
@Service
public class OSIValidator {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(OSIValidatorStage.class);

	/** The packet info manager. */
	@Autowired
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant BIOMETRIC_INTRODUCER. */
	public static final String BIOMETRIC = PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR;
	/** The registration status service. */
	/** the application Id */
	private static final String APP_ID = "registrationprocessor";
	private static final String UIN = "UIN";
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The adapter. */
	@Autowired
	private FileSystemAdapter adapter;

	/** The rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> restClientService;

	/** The env. */
	@Autowired
	private Environment env;

	/** The osi utils. */
	@Autowired
	private OSIUtils osiUtils;

	@Autowired
	ABISHandlerUtil abisHandlerUtil;

	private JSONObject demographicIdentity;

	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The message. */
	private String message = null;

	/** The Constant TRUE. */
	private static final String TRUE = "true";

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto;

	/** The auth request DTO. */
	AuthRequestDTO authRequestDTO = new AuthRequestDTO();

	/** The auth type DTO. */
	AuthTypeDTO authTypeDTO = new AuthTypeDTO();

	/** The identity DTO. */
	IdentityDTO identityDTO = new IdentityDTO();

	/** The identity info DTO. */
	IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();

	/** The pin info. */
	PinInfo pinInfo = new PinInfo();

	/** The identity iterator util. */
	IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();

	@Value("${mosip.kernel.applicant.type.age.limit}")
	private String ageLimit;

	@Value("${registration.processor.applicant.dob.format}")
	private String dobFormat;

	private static final String VALUE = "value";

	@Autowired
	private Utilities utility;

	RegistrationExceptionMapperUtil registrationExceptionMapperUtil = new RegistrationExceptionMapperUtil();

	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

	/** The encryptor. */
	@Autowired
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/** The registration processor rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	private ObjectMapper mapper = new ObjectMapper();

	/** The Constant APPLICATION_ID. */
	public static final String IDA_APP_ID = "IDA";

	/** The Constant RSA. */
	public static final String RSA = "RSA";

	/** The Constant RSA. */
	public static final String PARTNER_ID = "PARTNER";

	BioTypeMapperUtil bioTypeMapperUtil = new BioTypeMapperUtil();

	BioSubTypeMapperUtil bioSubTypeMapperUtil = new BioSubTypeMapperUtil();

	/**
	 * Checks if is valid OSI.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return true, if is valid OSI
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public boolean isValidOSI(String registrationId) throws IOException, ApisResourceAccessException,
			InvalidKeySpecException, NoSuchAlgorithmException, ParserConfigurationException, SAXException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "OSIValidator::isValidOSI()::entry");
		boolean isValidOsi = false;
		demographicIdentity = getDemoIdentity(registrationId);
		regProcessorIdentityJson = getIdentity();
		Identity identity = osiUtils.getIdentity(registrationId);
		/** Getting data from packet MetadataInfo */
		RegOsiDto regOsi = osiUtils.getOSIDetailsFromMetaInfo(registrationId, identity);
		String officerId = regOsi.getOfficerId();
		String supervisorId = regOsi.getSupervisorId();
		if ((officerId == null || officerId.isEmpty()) && (supervisorId == null || supervisorId.isEmpty())) {
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.SUPERVISORID_AND_OFFICERID_NOT_PRESENT_IN_PACKET));
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(StatusMessage.SUPERVISORID_AND_OFFICERID_NOT_PRESENT_IN_PACKET);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Both Officer and Supervisor ID are not present in Packet");
			return false;
		}
		String creationDate = osiUtils.getMetaDataValue(JsonConstant.CREATIONDATE, identity);
		if (creationDate != null && !creationDate.isEmpty()) {
			if (!wereOperatorsActiveDuringPCT(officerId, creationDate, supervisorId)) {
				registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.SUPERVISOR_OR_OFFICER_WAS_INACTIVE));
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				return false;
			}
		} else {
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.PACKET_CREATION_DATE_NOT_PRESENT_IN_PACKET));
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_CREATION_DATE_NOT_PRESENT_IN_PACKET);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "packet creationDate is null");

			return false;
		}
		if (((isValidOperator(regOsi, registrationId)) && (isValidSupervisor(regOsi, registrationId)))
				&& (isValidIntroducer(registrationId)))
			isValidOsi = true;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "OSIValidator::isValidOSI()::exit");
		return isValidOsi;
	}

	private boolean wereOperatorsActiveDuringPCT(String officerId, String creationDate, String supervisorId)
			throws ApisResourceAccessException {
		boolean wasOfficerActiveDuringPCT = false;
		boolean wasSupervisorActiveDuringPCT = false;
		String statusMessage = "";
		if (officerId != null && !officerId.isEmpty()) {
			UserResponseDto officerResponse = wasOperatorActiveDuringPCT(officerId, creationDate);
			if (officerResponse.getErrors() == null) {
				wasOfficerActiveDuringPCT = officerResponse.getResponse().getUserResponseDto().get(0).isActive();
				if (!wasOfficerActiveDuringPCT) {
					statusMessage = statusMessage + " " + StatusMessage.OFFICER_NOT_ACTIVE;
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "", StatusMessage.OFFICER_NOT_ACTIVE);
				}
			} else {
				List<ServerError> errors = officerResponse.getErrors();
				statusMessage = statusMessage + " " + "Officer : " + errors.get(0).getMessage();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", errors.get(0).getMessage());
			}

		}

		if (supervisorId != null && !supervisorId.isEmpty()) {
			UserResponseDto supervisorResponse = wasOperatorActiveDuringPCT(supervisorId, creationDate);
			if (supervisorResponse.getErrors() == null) {
				wasSupervisorActiveDuringPCT = supervisorResponse.getResponse().getUserResponseDto().get(0).isActive();
				if (!wasSupervisorActiveDuringPCT) {
					statusMessage = statusMessage + " " + StatusMessage.SUPERVISOR_NOT_ACTIVE;
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "", StatusMessage.SUPERVISOR_NOT_ACTIVE);
				}
			} else {
				List<ServerError> errors = supervisorResponse.getErrors();
				statusMessage = statusMessage + " " + "Supervisor : " + errors.get(0).getMessage();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", errors.get(0).getMessage());
			}
		}
		this.registrationStatusDto.setStatusComment(statusMessage);
		return wasSupervisorActiveDuringPCT || wasOfficerActiveDuringPCT;
	}

	private UserResponseDto wasOperatorActiveDuringPCT(String operatorId, String creationDate)
			throws ApisResourceAccessException {
		UserResponseDto userResponse;
		List<String> pathSegments = new ArrayList<String>();
		pathSegments.add(operatorId);
		pathSegments.add(creationDate);
		try {
			userResponse = (UserResponseDto) restClientService.getApi(ApiName.USERDETAILS, pathSegments, "", "",
					UserResponseDto.class);

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				String result = httpClientException.getResponseBodyAsString();
				this.registrationStatusDto.setStatusComment(result);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", result);
				throw new ApisResourceAccessException(httpClientException.getResponseBodyAsString(),
						httpClientException);
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				String result = httpServerException.getResponseBodyAsString();
				this.registrationStatusDto.setStatusComment(result);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", result);
				throw new ApisResourceAccessException(httpServerException.getResponseBodyAsString(),
						httpServerException);
			} else {
				throw e;
			}

		}
		return userResponse;
	}

	/**
	 * Checks if is valid operator.
	 *
	 * @param regOsi
	 *            the reg osi
	 * @param registrationId
	 *            the registration id
	 * @return true, if is valid operator
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isValidOperator(RegOsiDto regOsi, String registrationId)
			throws IOException, ApisResourceAccessException {

		String officerId = regOsi.getOfficerId();
		if (officerId != null) {
			// officer password and otp check
			String officerPassword = regOsi.getOfficerHashedPwd();
			String officerOTPAuthentication = regOsi.getOfficerOTPAuthentication();
			String officerRegistrationId = getOperatorRid(officerId);
			String officerUin = getOperatorUin(officerRegistrationId);
			String fingerPrint = null;// regOsi.getOfficerFingerpImageName();
			String fingerPrintType = null;// regOsi.getOfficerfingerType();
			String iris = null;// regOsi.getOfficerIrisImageName();
			String irisType = null;// regOsi.getOfficerIrisType();
			String face = null;// regOsi.getOfficerPhotoName();
			String pin = null;// regOsi.getOfficerHashedPin();

			if (checkBiometricNull(fingerPrint, iris, face, pin)) {
				boolean flag = validateOtpAndPwd(officerPassword, officerOTPAuthentication);
				if (flag) {
					registrationStatusDto
							.setStatusComment(StatusMessage.VALIDATION_DETAILS_SUCCESS + StatusMessage.OPERATOR);
				} else {
					registrationStatusDto
							.setStatusComment(StatusMessage.VALIDATION_DETAILS_FAILURE + StatusMessage.OPERATOR);

				}
				return flag;
			} else if (validateOtpAndPwd(officerPassword, officerOTPAuthentication)) {
				return true;
			} else {
				registrationStatusDto.setStatusComment(StatusMessage.OPERATOR + message);
				return false;
			}
		}
		return true;
	}

	/**
	 * Check biometric null.
	 *
	 * @param fingerPrint
	 *            the finger print
	 * @param iris
	 *            the iris
	 * @param face
	 *            the face
	 * @param pin
	 *            the pin
	 * @return true, if successful
	 */
	boolean checkBiometricNull(String fingerPrint, String iris, String face, String pin) {
		return (fingerPrint == null) && (iris == null) && (face == null) && (pin == null);
	}

	/**
	 * Checks if is valid supervisor.
	 *
	 * @param regOsi
	 *            the reg osi
	 * @param registrationId
	 *            the registration id
	 * @return true, if is valid supervisor
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean isValidSupervisor(RegOsiDto regOsi, String registrationId)
			throws IOException, ApisResourceAccessException {
		String supervisorId = regOsi.getSupervisorId();
		if (supervisorId != null) {
			// superVisior otp and password
			String supervisiorPassword = regOsi.getSupervisorHashedPwd();
			String supervisorOTPAuthentication = regOsi.getSupervisorOTPAuthentication();
			String supervisorRegistrationId = getOperatorRid(supervisorId);
			String supervisorUin = getOperatorUin(supervisorRegistrationId);
			String fingerPrint = null;// regOsi.getSupervisorBiometricFileName();
			String fingerPrintType = null;// regOsi.getSupervisorFingerType();
			String iris = null;// regOsi.getSupervisorIrisImageName();
			String irisType = null;// regOsi.getSupervisorIrisType();
			String face = null;// regOsi.getSupervisorPhotoName();
			String pin = null;// regOsi.getSupervisorHashedPin();

			if (checkBiometricNull(fingerPrint, iris, face, pin)) {
				boolean flag = validateOtpAndPwd(supervisiorPassword, supervisorOTPAuthentication);
				if (flag) {
					registrationStatusDto
							.setStatusComment(StatusMessage.VALIDATION_DETAILS_SUCCESS + StatusMessage.SUPERVISOR);
				} else {
					registrationStatusDto
							.setStatusComment(StatusMessage.VALIDATION_DETAILS_FAILURE + StatusMessage.SUPERVISOR);
				}
				return flag;
			} else if (validateOtpAndPwd(supervisiorPassword, supervisorOTPAuthentication)) {
				return true;
			} else {
				registrationStatusDto.setStatusComment(StatusMessage.SUPERVISOR + message);
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if is valid introducer.
	 *
	 * @param regOsi
	 *            the reg osi
	 * @param registrationId
	 *            the registration id
	 * @return true, if is valid introducer
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private boolean isValidIntroducer(String registrationId) throws IOException, ApisResourceAccessException,
			InvalidKeySpecException, NoSuchAlgorithmException, ParserConfigurationException, SAXException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "OSIValidator::isValidIntroducer()::entry");

		if (registrationStatusDto.getRegistrationType().equalsIgnoreCase(SyncTypeDto.NEW.name())
				|| (registrationStatusDto.getRegistrationType().equalsIgnoreCase(SyncTypeDto.UPDATE.name()))) {
			int age = utility.getApplicantAge(registrationId);
			int ageThreshold = Integer.parseInt(ageLimit);
			if (age < ageThreshold) {
				String introducerUinLabel = regProcessorIdentityJson.getIdentity().getParentOrGuardianUIN().getValue();
				String introducerRidLabel = regProcessorIdentityJson.getIdentity().getParentOrGuardianRID().getValue();
				Number introducerUinNumber = JsonUtil.getJSONValue(demographicIdentity, introducerUinLabel);
				Number introducerRidNumber = JsonUtil.getJSONValue(demographicIdentity, introducerRidLabel);
				String introducerBiometricsLabel = regProcessorIdentityJson.getIdentity()
						.getParentOrGuardianBiometrics().getValue();
				String introducerBiometricsFileName = null;
				Object object = JsonUtil.getJSONValue(demographicIdentity, introducerBiometricsLabel);
				if (object instanceof LinkedHashMap) {
					JSONObject json = JsonUtil.getJSONObject(demographicIdentity, introducerBiometricsLabel);
					introducerBiometricsFileName = (String) json.get(VALUE);
				}
				String introducerUIN = numberToString(introducerUinNumber);
				String introducerRID = numberToString(introducerRidNumber);
				if (introducerUIN == null && introducerRID == null) {
					registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
							.getStatusCode(RegistrationExceptionTypeCode.PARENT_UIN_AND_RID_NOT_IN_PACKET));
					registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
					registrationStatusDto
							.setStatusComment(StatusMessage.PARENT_UIN_AND_RID_NOT_IN_PACKET + registrationId);
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
							StatusMessage.PARENT_UIN_AND_RID_NOT_IN_PACKET);
					return false;
				}

				if (introducerUIN == null && validateIntroducerRid(introducerRID, registrationId)) {

					introducerUinNumber = abisHandlerUtil.getUinFromIDRepo(introducerRID);
					introducerUIN=numberToString(introducerUinNumber);
					if (introducerUIN == null) {
						registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
								.getStatusCode(RegistrationExceptionTypeCode.PARENT_UIN_NOT_AVAIALBLE));
						registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
						registrationStatusDto.setStatusComment(StatusMessage.PARENT_UIN_NOT_AVAIALBLE + registrationId);
						regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
								StatusMessage.PARENT_UIN_NOT_AVAIALBLE);
						return false;
					}

				}
				if (introducerUIN != null) {
					return validateIntroducer(registrationId, introducerUIN, introducerBiometricsFileName);
				} else {
					return false;
				}
			}

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "OSIValidator::isValidIntroducer()::exit");

		return true;
	}

	private String numberToString(Number number) {
		return number != null ? number.toString() : null;
	}

	private RegistrationProcessorIdentity getIdentity() throws JsonParseException, JsonMappingException, IOException {
		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorIdentityJson());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		RegistrationProcessorIdentity regProcessorIdentityJson = mapIdentityJsonStringToObject
				.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);
		return regProcessorIdentityJson;
	}

	private JSONObject getDemoIdentity(String registrationId) throws IOException {
		InputStream documentInfoStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());

		byte[] bytes = IOUtils.toByteArray(documentInfoStream);
		String demographicJsonString = new String(bytes);
		JSONObject demographicJson = (JSONObject) JsonUtil.objectMapperReadValue(demographicJsonString,
				JSONObject.class);
		JSONObject demographicIdentity = JsonUtil.getJSONObject(demographicJson,
				utility.getGetRegProcessorDemographicIdentity());
		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		return demographicIdentity;
	}

	/**
	 * Sets the finger biometric dto.
	 *
	 * @param obj
	 *            the obj
	 * @param fieldName
	 *            the field name
	 * @param value
	 *            the value
	 */
	private void setFingerBiometricDto(IdentityDTO obj, String fieldName, Object value) {
		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor(fieldName, obj.getClass());
			pd.getWriteMethod().invoke(obj, value);
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"OSIValidator", e.getMessage());
		}
	}

	/**
	 * Sets the finger biometric.
	 *
	 * @param biometricData
	 *            the biometric data
	 * @param type
	 *            the type
	 */
	void setFingerBiometric(List<IdentityInfoDTO> biometricData, String type) {
		String finger = null;
		String[] fingerType = env.getProperty("registration.processor.fingerType").split(",");
		List<String> list = new ArrayList<>(Arrays.asList(fingerType));
		finger = type;
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String ftype = it.next();
			if (ftype.equalsIgnoreCase(type)) {
				finger = ftype;
				break;
			}
		}
		this.setFingerBiometricDto(identityDTO, finger, biometricData);
	}

	/**
	 * Validate otp and pwd.
	 *
	 * @param password
	 *            the password
	 * @param otp
	 *            the otp
	 * @return true, if successful
	 */
	boolean validateOtpAndPwd(String password, String otp) {
		if (password != null && password.equals(TRUE) || otp != null && otp.equals(TRUE)) {
			return true;
		} else {
			message = StatusMessage.VALIDATE_OTP_PASSWORD;
			return false;

		}

	}

	/**
	 * Validate introducer.
	 *
	 * @param regOsi
	 *            the reg osi
	 * @param registrationId
	 *            the registration id
	 * @param introducerUin
	 *            the introducer uin
	 * @return true, if successful
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */

	private boolean validateIntroducer(String registrationId, String introducerUin, String introducerBiometricsFile)
			throws ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException, IOException,
			ParserConfigurationException, SAXException {
		if (introducerBiometricsFile != null && (!introducerBiometricsFile.trim().isEmpty())) {
			InputStream packetMetaInfoStream = adapter.getFile(registrationId,
					PacketStructure.BIOMETRIC + introducerBiometricsFile.toUpperCase());
			byte[] introducerbiometric = IOUtils.toByteArray(packetMetaInfoStream);
			// TODO change parameter and fix once finalized
			return authByIdAuthentication(null, introducerbiometric);

		} else {
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.PARENT_BIOMETRIC_NOT_IN_PACKET));
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(StatusMessage.PARENT_BIOMETRIC_NOT_IN_PACKET + registrationId);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, StatusMessage.PARENT_BIOMETRIC_NOT_IN_PACKET);
			return false;
		}

	}

	/**
	 * Validate introducer rid.
	 *
	 * @param introducerRid
	 *            the introducer rid
	 * @param registrationId
	 *            the registration id
	 * @return true, if successful
	 */
	private boolean validateIntroducerRid(String introducerRid, String registrationId) {
		InternalRegistrationStatusDto introducerRegistrationStatusDto = registrationStatusService
				.getRegistrationStatus(introducerRid);
		if (introducerRegistrationStatusDto != null) {
			if (introducerRegistrationStatusDto.getStatusCode().equals(RegistrationStatusCode.PROCESSING.toString())) {

				registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.OSI_FAILED_ON_HOLD_PARENT_PACKET));

				registrationStatusDto.setStatusComment(StatusMessage.PACKET_IS_ON_HOLD);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, StatusMessage.PACKET_IS_ON_HOLD);
				return false;

			} else if (introducerRegistrationStatusDto.getStatusCode()
					.equals(RegistrationStatusCode.REJECTED.toString())
					|| introducerRegistrationStatusDto.getStatusCode()
							.equals(RegistrationStatusCode.FAILED.toString())) {

				registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.OSI_FAILED_REJECTED_PARENT));

				registrationStatusDto.setStatusComment(StatusMessage.OSI_FAILED_REJECTED_PARENT);
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						StatusMessage.OSI_FAILED_REJECTED_PARENT);

				return false;
			} else {
				return true;
			}

		} else {
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.OSI_FAILED_ON_HOLD_PARENT_PACKET));

			registrationStatusDto.setStatusComment(StatusMessage.PACKET_IS_ON_HOLD);
			registrationStatusDto.setStatusCode(RegistrationStatusCode.PROCESSING.toString());
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, StatusMessage.PACKET_IS_ON_HOLD);
			return false;
		}

	}

	public boolean authByIdAuthentication(Long uin, byte[] biometricFile) throws ApisResourceAccessException,
			InvalidKeySpecException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		RequestDTO req = new RequestDTO();
		List<BioInfo> biometrics = new ArrayList<>();
		// BioInfo bioInfo = new BioInfo();
		// DataInfoDTO dataInfo = new DataInfoDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authRequestDTO.setId("mosip.identity.auth");
		authRequestDTO.setIndividualId(uin.toString());
		authRequestDTO.setIndividualIdType("UIN");
		authRequestDTO.setRequestTime(DateUtils.getUTCCurrentDateTimeString());

		/*
		 * dataInfo.setBioType("FMR"); dataInfo.setBioSubType("RIGHT_INDEX");
		 * dataInfo.setBioValue(
		 * "Rk1SACAyMAAAAAGSAAABPAFiAMUAxQEAAAAoPkCTANfnZEBzAMnSSUCGAPZcXUBzAKnAV0ClAPprZEBZANPKSYCXAI3gV0DLAPZxZEDfANhRZEBHAKoyIUCFAHCRZEDmAPBgNUCEASr9XUCwASz7XUDAAS95XUCFAFOEZEDkAF/cZECaAVF8UEC/AVIAPIBOADwHXUDoACZcSYCPAK7SV0CcAKneUEDAANlgZIClAJpeUIC0AJtcV0BcAN5KUEB+AI+6V0CuAIVkXUB5AIGnZECUASB2V0DBARh9XUCSASt9XYBQARBfXUBjASlvZIC0ATv8UEBQATF5Q0CFAVT8Q0B4AD+DZEBaAU92UEDtADzbV0CcAA1zSUCyAMVWV0CGAKjIV0BoAL87UIC9AOdrZIC3APdvZEBsAJ65XUDNAOthZIDEAI3RZEBQAJ2zZEBuARllZEDkAKDPZEC6AHFjZEA/AJW5Q0A3AQDRQ0BGAHGpXUCBAUx7Q0DAAU9/PIBJAFGVXUDYAVeAKEDLABxkZAAA"
		 * ); dataInfo.setDeviceProviderID("cogent"); dataInfo.setDeviceCode("cogent");
		 * dataInfo.setTransactionID("1234567890");
		 * dataInfo.setTimestamp(DateUtils.getUTCCurrentDateTimeString());
		 * 
		 * bioInfo.setData(dataInfo); biometrics.add(bioInfo);
		 */

		biometrics = getBioInfoListDto(biometricFile);

		req.setBiometrics(biometrics);
		req.setTimestamp(DateUtils.getUTCCurrentDateTimeString());

		authType.setBio(Boolean.TRUE);
		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setVersion("1.0");

		String identityBlock = mapper.writeValueAsString(req);

		final SecretKey secretKey = keyGenerator.getSymmetricKey();

		byte[] encryptedIdentityBlock = encryptor.symmetricEncrypt(secretKey, identityBlock.getBytes());
		authRequestDTO.setRequest(Base64.encodeBase64URLSafeString(encryptedIdentityBlock));

		byte[] encryptedSessionKeyByte = encryptRSA(secretKey.getEncoded(), PARTNER_ID,
				DateUtils.getUTCCurrentDateTimeString());
		authRequestDTO.setRequestSessionKey(Base64.encodeBase64URLSafeString(encryptedSessionKeyByte));

		byte[] byteArr = encryptor.symmetricEncrypt(secretKey,
				HMACUtils.digestAsPlainText(HMACUtils.generateHash(identityBlock.getBytes())).getBytes());
		authRequestDTO.setRequestHMAC(Base64.encodeBase64String(byteArr));

		/*
		 * AuthResponseDTO str = (AuthResponseDTO)
		 * registrationProcessorRestClientService.postApi(ApiName.IDAINTERNALAUTH, null,
		 * null, authRequestDTO, AuthResponseDTO.class, MediaType.APPLICATION_JSON);
		 */
		return true;
	}

	private byte[] encryptRSA(final byte[] sessionKey, String refId, String creationTime)
			throws ApisResourceAccessException, InvalidKeySpecException, java.security.NoSuchAlgorithmException,
			IOException {

		// encrypt AES Session Key using RSA public key
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(IDA_APP_ID);
		ResponseWrapper<?> responseWrapper;
		PublicKeyResponseDto publicKeyResponsedto = null;

		responseWrapper = (ResponseWrapper<?>) registrationProcessorRestClientService.getApi(ApiName.ENCRYPTIONSERVICE,
				pathsegments, "timeStamp,referenceId", creationTime + ',' + refId, ResponseWrapper.class);
		publicKeyResponsedto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
				PublicKeyResponseDto.class);

		PublicKey publicKey = KeyFactory.getInstance(RSA)
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKeyResponsedto.getPublicKey())));

		return encryptor.asymmetricPublicEncrypt(publicKey, sessionKey);

	}

	public List<BioInfo> getBioInfoListDto(byte[] cbefByteFile)
			throws ParserConfigurationException, SAXException, IOException {

		List<BioInfo> biometrics = new ArrayList<>();

		String byteFileStr = new String(cbefByteFile);
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(byteFileStr));
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();
		if (doc != null) {
			NodeList bdbInfo = doc.getElementsByTagName("BDBInfo");
			for (int bi = 0; bi < bdbInfo.getLength(); bi++) {
				BioInfo bioInfo = new BioInfo();
				DataInfoDTO dataInfoDTO = new DataInfoDTO();
				Node bdbInfoList = bdbInfo.item(bi);
				if (bdbInfoList.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) bdbInfoList;
					String bioType = eElement.getElementsByTagName("Type").item(0).getTextContent();
					getBioType(dataInfoDTO, bioType);

					String bioSubType = eElement.getElementsByTagName("Subtype").item(0).getTextContent();
					getBioSubType(dataInfoDTO, bioSubType);
					NodeList bdb = doc.getElementsByTagName("BDB");
					String value = bdb.item(0).getTextContent();
					// dataInfoDTO.setBioValue(value);
					dataInfoDTO.setDeviceProviderID("cogent");
					dataInfoDTO.setTimestamp(DateUtils.getUTCCurrentDateTimeString());
					dataInfoDTO.setTransactionID("1234567890");
				}
				bioInfo.setData(dataInfoDTO);
				biometrics.add(bioInfo);
			}
		}
		return biometrics;
	}

	private String getOperatorRid(String operatorId) throws ApisResourceAccessException {
		List<String> pathSegments = new ArrayList<String>();
		pathSegments.add(APP_ID);
		pathSegments.add(operatorId);
		try {
			RIDResponseDto ridDto = (RIDResponseDto) restClientService.getApi(ApiName.GETRIDFROMUSERID, pathSegments,
					"", "", RIDResponseDto.class);
			if (ridDto.getErrors() == null) {
				return ridDto.getResponse().getRid();
			} else {
				List<ServerError> errors = ridDto.getErrors();
				this.registrationStatusDto.setStatusComment(errors.get(0).getMessage());
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", errors.get(0).getMessage());
			}

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				String result = httpClientException.getResponseBodyAsString();
				this.registrationStatusDto.setStatusComment(result);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", result);
				throw new ApisResourceAccessException(httpClientException.getResponseBodyAsString(),
						httpClientException);
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				String result = httpServerException.getResponseBodyAsString();
				this.registrationStatusDto.setStatusComment(result);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", result);
				throw new ApisResourceAccessException(httpServerException.getResponseBodyAsString(),
						httpServerException);
			} else {
				throw e;
			}

		}
		return null;
	}

	private String getOperatorUin(String operatorRegistrationId) throws ApisResourceAccessException, IOException {
		IdResponseDTO response;
		String operatorUin = null;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(operatorRegistrationId);
		try {
			response = (IdResponseDTO) restClientService.getApi(ApiName.RETRIEVEIDENTITYFROMRID, pathsegments, "", "",
					IdResponseDTO.class);
			if (response.getError() == null) {
				ObjectMapper mapper = new ObjectMapper();
				String identityjson = mapper.writeValueAsString(response.getResponse().getIdentity());
				JSONObject identity = JsonUtil.objectMapperReadValue(identityjson, JSONObject.class);
				operatorUin = (String) identity.get(UIN);
			} else {
				List<ErrorDTO> errors = response.getError();
				this.registrationStatusDto.setStatusComment(errors.get(0).getMessage());
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", errors.get(0).getMessage());
			}
		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				String result = httpClientException.getResponseBodyAsString();
				this.registrationStatusDto.setStatusComment(result);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", result);
				throw new ApisResourceAccessException(httpClientException.getResponseBodyAsString(),
						httpClientException);
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				String result = httpServerException.getResponseBodyAsString();
				this.registrationStatusDto.setStatusComment(result);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", result);
				throw new ApisResourceAccessException(httpServerException.getResponseBodyAsString(),
						httpServerException);
			} else {
				throw e;
			}
		}
		return operatorUin;

	}

	private DataInfoDTO getBioType(DataInfoDTO dataInfoDTO, String bioType) {
		if (bioType.equalsIgnoreCase(BioType.FINGER.toString())) {
			dataInfoDTO.setBioType(bioTypeMapperUtil.getStatusCode(BioType.FINGER));
		} else if (bioType.equalsIgnoreCase(BioType.FACE.toString())) {
			dataInfoDTO.setBioType(bioTypeMapperUtil.getStatusCode(BioType.FACE));
		} else if (bioType.equalsIgnoreCase(BioType.IRIS.toString())) {
			dataInfoDTO.setBioType(bioTypeMapperUtil.getStatusCode(BioType.IRIS));
		}
		return dataInfoDTO;
	}

	private DataInfoDTO getBioSubType(DataInfoDTO dataInfoDTO, String bioSubType) {
		if (bioSubType.equalsIgnoreCase(BioSubType.LEFT_INDEX_FINGER.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.LEFT_INDEX_FINGER));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.LEFT_LITTLE_FINGER.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.LEFT_LITTLE_FINGER));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.LEFT_MIDDLE_FINGER.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.LEFT_MIDDLE_FINGER));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.LEFT_RING_FINGER.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.LEFT_RING_FINGER));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.RIGHT_INDEX_FINGER.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.RIGHT_INDEX_FINGER));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.RIGHT_LITTLE_FINGER.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.RIGHT_LITTLE_FINGER));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.RIGHT_MIDDLE_FINGER.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.RIGHT_MIDDLE_FINGER));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.RIGHT_RING_FINGER.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.RIGHT_RING_FINGER));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.LEFT_THUMB.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.LEFT_THUMB));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.RIGHT_THUMB.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.RIGHT_THUMB));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.IRIS_LEFT.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.IRIS_LEFT));
		} else if (bioSubType.equalsIgnoreCase(BioSubType.IRIS_RIGHT.getBioType())) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.IRIS_RIGHT));
		} else if (bioSubType.equalsIgnoreCase("")) {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.FACE));
		} else {
			dataInfoDTO.setBioSubType(bioSubTypeMapperUtil.getStatusCode(BioSubType.FACE));
		}
		return dataInfoDTO;
	}

}