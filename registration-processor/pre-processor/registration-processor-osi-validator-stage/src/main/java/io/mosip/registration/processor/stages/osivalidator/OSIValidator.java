package io.mosip.registration.processor.stages.osivalidator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.xml.sax.SAXException;

import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.registration.processor.core.auth.dto.AuthRequestDTO;
import io.mosip.registration.processor.core.auth.dto.AuthResponseDTO;
import io.mosip.registration.processor.core.auth.dto.AuthTypeDTO;
import io.mosip.registration.processor.core.auth.dto.IdentityDTO;
import io.mosip.registration.processor.core.auth.dto.IdentityInfoDTO;
import io.mosip.registration.processor.core.auth.dto.PinInfo;
import io.mosip.registration.processor.core.auth.util.BioSubTypeMapperUtil;
import io.mosip.registration.processor.core.auth.util.BioTypeMapperUtil;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.RegistrationExceptionTypeCode;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.BioTypeException;
import io.mosip.registration.processor.core.exception.util.PacketStructure;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.ServerError;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.masterdata.UserResponseDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.manager.idreposervice.IdRepoService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.stages.osivalidator.utils.AuthUtil;
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

	@Autowired
	private IdRepoService idRepoService;

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

	@Autowired
	private AuthUtil authUtil;

	/** The registration processor rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	/** The Constant APPLICATION_ID. */
	public static final String IDA_APP_ID = "IDA";

	/** The Constant RSA. */
	public static final String RSA = "RSA";

	/** The Constant RSA. */
	public static final String PARTNER_ID = "PARTNER";

	public static final String INDIVIDUAL_TYPE_UIN = "UIN";

	private static final String INDIVIDUAL_TYPE_USERID = "USERID";

	BioTypeMapperUtil bioTypeMapperUtil = new BioTypeMapperUtil();

	BioSubTypeMapperUtil bioSubTypeMapperUtil = new BioSubTypeMapperUtil();

	IBioApi bioAPi = new BioApiImpl();

	CbeffUtil cbeffUtil = new CbeffImpl();

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
	 * @throws BioTypeException
	 * @throws BiometricException
	 * @throws NumberFormatException
	 */
	public boolean isValidOSI(String registrationId)
			throws IOException, ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException,
			ParserConfigurationException, SAXException, NumberFormatException, BiometricException, BioTypeException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "OSIValidator::isValidOSI()::entry");
		boolean isValidOsi = false;
		demographicIdentity = utility.getDemographicIdentityJSONObject(registrationId);
		regProcessorIdentityJson = utility.getRegistrationProcessorIdentityJson();
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
		} else {
			boolean isActive = isActiveUserId(registrationId, regOsi, identity);
			if (!isActive) {
				registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.SUPERVISOR_OR_OFFICER_WAS_INACTIVE));
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				registrationStatusDto.setStatusComment(StatusMessage.SUPERVISOR_OR_OFFICER_WAS_INACTIVE);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						StatusMessage.SUPERVISOR_OR_OFFICER_WAS_INACTIVE);
				return false;
			}
			if (((isValidOperator(regOsi, registrationId)) && (isValidSupervisor(regOsi, registrationId)))
					&& (isValidIntroducer(registrationId)))
				isValidOsi = true;
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "OSIValidator::isValidOSI()::exit");
		}
		return isValidOsi;
	}

	private boolean isActiveUser(String officerId, String creationDate, String supervisorId)
			throws ApisResourceAccessException {
		boolean wasOfficerActiveDuringPCT = false;
		boolean wasSupervisorActiveDuringPCT = false;
		String statusMessage = "";
		if (officerId != null && !officerId.isEmpty()) {
			UserResponseDto officerResponse = isUserActive(officerId, creationDate);
			if (officerResponse.getErrors() == null) {
				wasOfficerActiveDuringPCT = officerResponse.getResponse().getUserResponseDto().get(0).getIsActive();
				if (!wasOfficerActiveDuringPCT) {
					statusMessage = statusMessage + " " + StatusMessage.OFFICER_NOT_ACTIVE;
					this.registrationStatusDto.setStatusComment(statusMessage);
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "", StatusMessage.OFFICER_NOT_ACTIVE);
				}
			} else {
				List<ServerError> errors = officerResponse.getErrors();
				statusMessage = statusMessage + " " + "Officer : " + errors.get(0).getMessage();
				this.registrationStatusDto.setStatusComment(statusMessage);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", errors.get(0).getMessage());
			}

		}

		if (supervisorId != null && !supervisorId.isEmpty()) {
			UserResponseDto supervisorResponse = isUserActive(supervisorId, creationDate);
			if (supervisorResponse.getErrors() == null) {
				wasSupervisorActiveDuringPCT = supervisorResponse.getResponse().getUserResponseDto().get(0)
						.getIsActive();
				if (!wasSupervisorActiveDuringPCT) {
					statusMessage = statusMessage + " " + StatusMessage.SUPERVISOR_NOT_ACTIVE;
					this.registrationStatusDto.setStatusComment(statusMessage);
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), "", StatusMessage.SUPERVISOR_NOT_ACTIVE);
				}
			} else {
				List<ServerError> errors = supervisorResponse.getErrors();
				statusMessage = statusMessage + " " + "Supervisor : " + errors.get(0).getMessage();
				this.registrationStatusDto.setStatusComment(statusMessage);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "", errors.get(0).getMessage());
			}
		}
		return wasSupervisorActiveDuringPCT || wasOfficerActiveDuringPCT;
	}

	private UserResponseDto isUserActive(String operatorId, String creationDate) throws ApisResourceAccessException {
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
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws BioTypeException
	 * @throws BiometricException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws ApisResourceAccessException
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	private boolean isValidOperator(RegOsiDto regOsi, String registrationId)
			throws IOException, ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException,
			BiometricException, BioTypeException, ParserConfigurationException, SAXException {
		boolean isValid = false;
		String officerId = regOsi.getOfficerId();
		if (officerId != null) {
			// officer password and otp check
			String officerPassword = regOsi.getOfficerHashedPwd();
			String officerOTPAuthentication = regOsi.getOfficerOTPAuthentication();

			String officerBiometricFileName = regOsi.getOfficerBiometricFileName();

			if (StringUtils.isEmpty(officerBiometricFileName) || officerBiometricFileName == null) {
				isValid = validateOtpAndPwd(officerPassword, officerOTPAuthentication);
				if (!isValid) {
					registrationStatusDto.setStatusComment(StatusMessage.PASSWORD_OTP_FAILURE + StatusMessage.OPERATOR);
					registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
							.getStatusCode(RegistrationExceptionTypeCode.PASSWORD_OTP_FAILURE));
					registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
							StatusMessage.PASSWORD_OTP_FAILURE);
				}
			} else {
				isValid = validateUserBiometric(registrationId, officerBiometricFileName, regOsi.getOfficerId());
				if (!isValid) {
					registrationStatusDto.setStatusComment(StatusMessage.IDA_AUTHENTICATION_FAILURE);
					registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
							.getStatusCode(RegistrationExceptionTypeCode.IDA_AUTHENTICATION_FAILURE));
					registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				}
			}

		} else
			isValid = true; // either officer or supervisor information is mandatory. Officer id can be null

		return isValid;
	}

	private boolean validateUserBiometric(String registrationId, String fileName, String userId)
			throws IOException, ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException,
			BiometricException, BioTypeException, ParserConfigurationException, SAXException {
		InputStream biometricStream = adapter.getFile(registrationId, fileName.toUpperCase());
		byte[] officerbiometric = IOUtils.toByteArray(biometricStream);
		AuthResponseDTO response = authUtil.authByIdAuthentication(userId, INDIVIDUAL_TYPE_USERID, officerbiometric);
		if (!response.getResponse().isAuthStatus()) {
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, StatusMessage.IDA_AUTHENTICATION_FAILURE + " " + response.getErrors().get(0));
		}

		return response.getResponse().isAuthStatus();

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
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws BioTypeException
	 * @throws BiometricException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws ApisResourceAccessException
	 * @throws Exception
	 */
	private boolean isValidSupervisor(RegOsiDto regOsi, String registrationId)
			throws IOException, ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException,
			BiometricException, BioTypeException, ParserConfigurationException, SAXException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "OSIValidator::isValidSupervisor()::entry");
		String supervisorId = regOsi.getSupervisorId();
		boolean isValid = false;
		if (supervisorId != null) {

			// officer password and otp check
			String supervisiorPassword = regOsi.getSupervisorHashedPwd();
			String supervisorOTP = regOsi.getSupervisorOTPAuthentication();

			String supervisorBiometricFileName = regOsi.getSupervisorBiometricFileName();

			if (StringUtils.isEmpty(supervisorBiometricFileName) || supervisorBiometricFileName == null) {
				isValid = validateOtpAndPwd(supervisiorPassword, supervisorOTP);
				if (!isValid) {
					registrationStatusDto
							.setStatusComment(StatusMessage.PASSWORD_OTP_FAILURE + StatusMessage.SUPERVISOR);
					registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
							.getStatusCode(RegistrationExceptionTypeCode.PASSWORD_OTP_FAILURE));
					registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
					regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
							LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
							StatusMessage.PASSWORD_OTP_FAILURE);
				}
			} else {
				isValid = validateUserBiometric(registrationId, supervisorBiometricFileName, regOsi.getSupervisorId());
				if (!isValid) {
					registrationStatusDto.setStatusComment(StatusMessage.IDA_AUTHENTICATION_FAILURE);
					registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
							.getStatusCode(RegistrationExceptionTypeCode.IDA_AUTHENTICATION_FAILURE));
					registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				}
			}

		} else
			isValid = true; // either officer or supervisor information is mandatory. Supervisor id can be
							// null
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "OSIValidator::isValidSupervisor()::exit");
		return isValid;
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
	 * @throws BioTypeException
	 * @throws BiometricException
	 */
	private boolean isValidIntroducer(String registrationId)
			throws IOException, ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException,
			ParserConfigurationException, SAXException, BiometricException, BioTypeException {
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

					introducerUinNumber = idRepoService.getUinByRid(introducerRID,
							utility.getGetRegProcessorDemographicIdentity());
					introducerUIN = numberToString(introducerUinNumber);
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
	 * @throws BioTypeException
	 * @throws BiometricException
	 */

	private boolean validateIntroducer(String registrationId, String introducerUin, String introducerBiometricsFile)
			throws ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException, IOException,
			ParserConfigurationException, SAXException, BiometricException, BioTypeException {
		if (introducerBiometricsFile != null && (!introducerBiometricsFile.trim().isEmpty())) {
			InputStream packetMetaInfoStream = adapter.getFile(registrationId,
					PacketStructure.BIOMETRIC + introducerBiometricsFile.toUpperCase());
			byte[] introducerbiometric = IOUtils.toByteArray(packetMetaInfoStream);
			AuthResponseDTO authResponseDTO = authUtil.authByIdAuthentication(introducerUin, INDIVIDUAL_TYPE_UIN,
					introducerbiometric);
			if (authResponseDTO.getErrors() == null || authResponseDTO.getErrors().isEmpty()) {
				if (authResponseDTO.getResponse().isAuthStatus()) {
					return true;
				} else {
					registrationStatusDto.setLatestTransactionStatusCode(
							registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.AUTH_FAILED));
					registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
					registrationStatusDto.setStatusComment(StatusMessage.AUTHENTICATION_FAILED);
					return false;
				}

			} else {
				List<io.mosip.registration.processor.core.auth.dto.ErrorDTO> errors = authResponseDTO.getErrors();
				registrationStatusDto.setLatestTransactionStatusCode(
						registrationExceptionMapperUtil.getStatusCode(RegistrationExceptionTypeCode.AUTH_ERROR));
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
				registrationStatusDto.setStatusComment(errors.get(0).getErrorMessage());
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId, errors.get(0).getErrorMessage());
				return false;
			}
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

	private boolean isActiveUserId(String registrationId, RegOsiDto regOsi, Identity identity)
			throws UnsupportedEncodingException, ApisResourceAccessException {
		boolean isValid = false;
		String creationDate = osiUtils.getMetaDataValue(JsonConstant.CREATIONDATE, identity);
		if (creationDate == null && StringUtils.isEmpty(creationDate)) {
			registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
					.getStatusCode(RegistrationExceptionTypeCode.PACKET_CREATION_DATE_NOT_PRESENT_IN_PACKET));
			registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_CREATION_DATE_NOT_PRESENT_IN_PACKET);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "packet creationDate is null");

		} else {
			isValid = isActiveUser(regOsi.getOfficerId(), creationDate, regOsi.getSupervisorId());
			if (!isValid) {
				registrationStatusDto.setLatestTransactionStatusCode(registrationExceptionMapperUtil
						.getStatusCode(RegistrationExceptionTypeCode.SUPERVISOR_OR_OFFICER_WAS_INACTIVE));
				registrationStatusDto.setStatusCode(RegistrationStatusCode.FAILED.toString());
			}
		}
		return isValid;
	}

}