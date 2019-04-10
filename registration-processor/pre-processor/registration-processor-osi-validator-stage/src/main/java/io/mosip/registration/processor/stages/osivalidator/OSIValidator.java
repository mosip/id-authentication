package io.mosip.registration.processor.stages.osivalidator;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.auth.dto.AuthRequestDTO;
import io.mosip.registration.processor.core.auth.dto.AuthTypeDTO;
import io.mosip.registration.processor.core.auth.dto.IdentityDTO;
import io.mosip.registration.processor.core.auth.dto.IdentityInfoDTO;
import io.mosip.registration.processor.core.auth.dto.PinInfo;
import io.mosip.registration.processor.core.auth.dto.RequestDTO;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.stages.osivalidator.utils.OSIUtils;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

// TODO: Auto-generated Javadoc
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

	/** The request. */
	RequestDTO request = new RequestDTO();

	/** The pin info. */
	PinInfo pinInfo = new PinInfo();

	/** The identity iterator util. */
	IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
	
	@Value("${mosip.kernel.applicant.type.age.limit}")
	private String ageLimit;
	
	@Value("${registration.processor.applicant.dob.format}")
	private String dobFormat;
	

	@Autowired
	private Utilities utility;

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
	 */
	public boolean isValidOSI(String registrationId) throws IOException, ApisResourceAccessException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "OSIValidator::isValidOSI()::entry");
		boolean isValidOsi = false;
		demographicIdentity = getDemoIdentity(registrationId);
		regProcessorIdentityJson = getIdentity();
		Identity identity = osiUtils.getIdentity(registrationId);
		/** Getting data from packet MetadataInfo*/
		RegOsiDto regOsi = osiUtils.getOSIDetailsFromMetaInfo(registrationId,identity);
		String officerId = regOsi.getOfficerId();
		String supervisorId = regOsi.getSupervisorId();
		if (officerId == null && supervisorId == null) {
			registrationStatusDto.setStatusComment(StatusMessage.OSI_VALIDATION_FAILURE + " Officer and Supervisor are null");
			return false;
		}
		if (((isValidOperator(regOsi, registrationId)) && (isValidSupervisor(regOsi, registrationId))) && (isValidIntroducer(regOsi, registrationId)))
			isValidOsi = true;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "OSIValidator::isValidOSI()::exit");
		return isValidOsi;
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
			
			String fingerPrint = null;//regOsi.getOfficerFingerpImageName();
			String fingerPrintType = null;//regOsi.getOfficerfingerType();
			String iris =null;// regOsi.getOfficerIrisImageName();
			String irisType =null;// regOsi.getOfficerIrisType();
			String face =null;// regOsi.getOfficerPhotoName();
			String pin = null;//regOsi.getOfficerHashedPin();
			
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
			} else if ((validateUIN(officerId))
					&& (validateFingerprint(officerId, fingerPrint, fingerPrintType, registrationId))
					&& (validateIris(officerId, iris, irisType, registrationId))
					&& (validateFace(officerId, face, registrationId)) && (validatePin(officerId, pin))
					&& validateOtpAndPwd(officerPassword, officerOTPAuthentication)) {
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
			String supervisorOTPAuthentication =regOsi.getSupervisorOTPAuthentication();
			
			String fingerPrint = null;//regOsi.getSupervisorBiometricFileName();
			String fingerPrintType = null;//regOsi.getSupervisorFingerType();
			String iris = null;//regOsi.getSupervisorIrisImageName();
			String irisType = null;//regOsi.getSupervisorIrisType();
			String face = null;//regOsi.getSupervisorPhotoName();
			String pin = null;//regOsi.getSupervisorHashedPin();
			
			if (checkBiometricNull(fingerPrint, iris, face, pin)) {
				boolean flag = validateOtpAndPwd(supervisiorPassword, supervisorOTPAuthentication);
				if (flag) {
					registrationStatusDto.setStatusComment(StatusMessage.VALIDATION_DETAILS_SUCCESS + StatusMessage.SUPERVISOR);
				} else {
					registrationStatusDto.setStatusComment(StatusMessage.VALIDATION_DETAILS_FAILURE + StatusMessage.SUPERVISOR);
				}
				return flag;
			} else if ((validateUIN(supervisorId))
					&& (validateFingerprint(supervisorId, fingerPrint, fingerPrintType, registrationId))
					&& (validateIris(supervisorId, iris, irisType, registrationId))
					&& (validateFace(supervisorId, face, registrationId)) && (validatePin(supervisorId, pin))
					&& validateOtpAndPwd(supervisiorPassword, supervisorOTPAuthentication)) {
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
	 */
	private boolean isValidIntroducer(RegOsiDto regOsi, String registrationId)
			throws IOException, ApisResourceAccessException {
			
		int childAgeLimit = Integer.parseInt(ageLimit);
		int applicantAge = getApplicantAge(registrationId);
		if (registrationStatusDto.getRegistrationType().equalsIgnoreCase(SyncTypeDto.NEW.name())
				&& applicantAge<= childAgeLimit && applicantAge>0) {
			String introducerUinLabel = regProcessorIdentityJson.getIdentity().getParentOrGuardianUIN().getValue();
			String introducerRidLabel = regProcessorIdentityJson.getIdentity().getParentOrGuardianRID().getValue();
			Number introducerUinNumber = JsonUtil.getJSONValue(demographicIdentity, introducerUinLabel);
			Number introducerRidNumber = JsonUtil.getJSONValue(demographicIdentity, introducerRidLabel);
			BigInteger introducerUIN =numberToBigInteger(introducerUinNumber);
			BigInteger introducerRID =numberToBigInteger(introducerRidNumber);
			if (introducerUIN == null && introducerRID == null) {
				registrationStatusDto.setStatusComment(StatusMessage.PARENT_UIN_AND_RID_NOT_IN_PACKET + registrationId);
				return false;
			}
			String introducerRidString = bigIntegerToString(introducerRID);
			String introducerUinString = bigIntegerToString(introducerUIN);
			if (introducerUinString == null && validateIntroducerRid(introducerRidString, registrationId)) {

				introducerUinString = getIntroducerUIN(introducerRidString);
				if (introducerUinString == null) {
					registrationStatusDto
							.setStatusComment(StatusMessage.PARENT_UIN_NOT_FOUND_IN_TABLE + registrationId);
					return false;
				}
			}
			if (introducerUinString != null) {
				return validateIntroducer(regOsi, registrationId, introducerUinString);
			} else {
				return false;
			}
		}
		return true;
	}

	private BigInteger numberToBigInteger(Number number ) {
		return number!=null? new BigInteger(String.valueOf(number)):null;
	}
	
	private String bigIntegerToString(BigInteger number ) {

		return String.valueOf(number);
	}

	private int getApplicantAge(String registrationId) throws IOException {
		String ageKey = regProcessorIdentityJson.getIdentity().getAge().getValue();
		String dobKey = regProcessorIdentityJson.getIdentity().getDob().getValue();
		
		String applicantDob = JsonUtil.getJSONValue(demographicIdentity, dobKey);
		try {
			if (applicantDob != null) {
				DateFormat sdf = new SimpleDateFormat(dobFormat);
				Date birthDate = sdf.parse(applicantDob);
				LocalDate ld = new java.sql.Date(birthDate.getTime()).toLocalDate();
				Period p = Period.between(ld, LocalDate.now());
				return p.getYears();
			} else {
				return JsonUtil.getJSONValue(demographicIdentity, ageKey);
			} 
		} catch (ParseException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, e.getMessage());
		}
		return 0;
	}
	private RegistrationProcessorIdentity getIdentity() throws JsonParseException, JsonMappingException, IOException {
		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),utility.getGetRegProcessorIdentityJson());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		RegistrationProcessorIdentity regProcessorIdentityJson = mapIdentityJsonStringToObject.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);
		return regProcessorIdentityJson;
	}
	private JSONObject getDemoIdentity(String registrationId) throws IOException{
		InputStream documentInfoStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());

		byte[] bytes = IOUtils.toByteArray(documentInfoStream);
		String demographicJsonString = new String(bytes);
		JSONObject demographicJson = (JSONObject) JsonUtil.objectMapperReadValue(demographicJsonString,
				JSONObject.class);
		JSONObject demographicIdentity = JsonUtil.getJSONObject(demographicJson,utility.getGetRegProcessorDemographicIdentity());
		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		return demographicIdentity;
	}
	

	/**
	 * Validate fingerprint.
	 *
	 * @param uin
	 *            the uin
	 * @param fingerprint
	 *            the fingerprint
	 * @param type
	 *            the type
	 * @param registrationId
	 *            the registration id
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean validateFingerprint(String uin, String fingerprint, String type, String registrationId)
			throws IOException, ApisResourceAccessException {
		if (fingerprint == null)
			return true;
		else {
			if (adapter.checkFileExistence(registrationId, fingerprint.toUpperCase())) {
				InputStream fingerPrintFileName = adapter.getFile(registrationId, fingerprint.toUpperCase());
				byte[] fingerPrintByte = IOUtils.toByteArray(fingerPrintFileName);
				setAuthDto();
				identityInfoDTO.setValue(new String(fingerPrintByte));
				List<IdentityInfoDTO> biometricData = new ArrayList<>();
				biometricData.add(identityInfoDTO);
				// authTypeDTO.setFingerPrint(true);
				setFingerBiometric(biometricData, type.toUpperCase());
				if (validateBiometric(uin))
					return true;
			}
		}
		message = StatusMessage.FINGER_PRINT;
		return false;
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
			e.printStackTrace();
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
	 * Validate iris.
	 *
	 * @param uin
	 *            the uin
	 * @param iris
	 *            the iris
	 * @param type
	 *            the type
	 * @param registrationId
	 *            the registration id
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean validateIris(String uin, String iris, String type, String registrationId)
			throws IOException, ApisResourceAccessException {
		if (iris == null)
			return true;
		else {
			if (adapter.checkFileExistence(registrationId, iris.toUpperCase())) {
				InputStream irisFileName = adapter.getFile(registrationId, iris.toUpperCase());
				byte[] irisByte = IOUtils.toByteArray(irisFileName);
				setAuthDto();
				identityInfoDTO.setValue(new String(irisByte));
				List<IdentityInfoDTO> biometricData = new ArrayList<>();
				biometricData.add(identityInfoDTO);
				// authTypeDTO.setIris(true);
				if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(type.toUpperCase())) {
					identityDTO.setLeftEye(biometricData);
				} else if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(type.toUpperCase())) {
					identityDTO.setRightEye(biometricData);
				}
				if (validateBiometric(uin))
					return true;
			}
		}
		message = StatusMessage.IRIS;
		return false;
	}

	/**
	 * Validate face.
	 *
	 * @param uin
	 *            the uin
	 * @param face
	 *            the face
	 * @param registrationId
	 *            the registration id
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean validateFace(String uin, String face, String registrationId)
			throws IOException, ApisResourceAccessException {
		if (face == null)
			return true;
		else {
			if (adapter.checkFileExistence(registrationId, face.toUpperCase())) {
				setAuthDto();
				InputStream faceFile = adapter.getFile(registrationId, face.toUpperCase());
				byte[] faceByte = IOUtils.toByteArray(faceFile);
				identityInfoDTO.setValue(new String(faceByte));
				List<IdentityInfoDTO> biometricData = new ArrayList<>();
				biometricData.add(identityInfoDTO);
				// authTypeDTO.setFace(true);
				identityDTO.setFace(biometricData);
				if (validateBiometric(uin))
					return true;
			}
		}
		message = StatusMessage.FACE;
		return false;
	}

	/**
	 * Validate UIN.
	 *
	 * @param uin
	 *            the uin
	 * @return true, if successful
	 */
	private boolean validateUIN(String uin) {
		// todo To call IAM rest API for UNI validation
		return true;
	}

	/**
	 * Validate pin.
	 *
	 * @param uin
	 *            the uin
	 * @param pin
	 *            the pin
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	boolean validatePin(String uin, String pin) throws ApisResourceAccessException {
		if (pin == null)
			return true;
		Boolean isValidPin = false;
		authTypeDTO.setPin(true);
		authRequestDTO.setIdvId(uin);
		pinInfo.setValue(pin);
		pinInfo.setType("SPIN");
		List<PinInfo> pinList = new ArrayList<>();
		pinList.add(pinInfo);
		authRequestDTO.setPinInfo(pinList);
		/*
		 * AuthResponseDTO authResponseDTO = (AuthResponseDTO)
		 * restClientService.postApi(ApiName.AUTHINTERNAL, "", "", authRequestDTO,
		 * AuthResponseDTO.class); if
		 * (authResponseDTO.getStatus().equalsIgnoreCase("y"))
		 */
		isValidPin = true;
		return isValidPin;
	}

	/**
	 * Validate otp and pwd.
	 *
	 * @param password the password
	 * @param otp the otp
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
	 * Validate biometric.
	 *
	 * @param uin
	 *            the uin
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	boolean validateBiometric(String uin) throws ApisResourceAccessException {

		authRequestDTO.setIdvId(uin);
		authRequestDTO.setAuthType(authTypeDTO);
		request.setIdentity(identityDTO);
		authRequestDTO.setRequest(request);
		/*
		 * AuthResponseDTO authResponseDTO = (AuthResponseDTO)
		 * restClientService.postApi(ApiName.AUTHINTERNAL, "", "", authRequestDTO,
		 * AuthResponseDTO.class); return authResponseDTO != null &&
		 * authResponseDTO.getStatus() != null &&
		 * authResponseDTO.getStatus().equalsIgnoreCase("y");
		 */
		return true;
	}

	/**
	 * Sets the auth dto.
	 */
	public void setAuthDto() {
		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		authRequestDTO.setReqTime(date);
		authRequestDTO.setId("mosip.internal.auth");
		authRequestDTO.setIdvIdType("D");
		// authRequestDTO.setVer("1.0");
		authTypeDTO.setAddress(false);
		authTypeDTO.setBio(false);
		authTypeDTO.setFullAddress(false);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPersonalIdentity(false);
		authTypeDTO.setPin(false);
		// authTypeDTO.setFace(false);
		// authTypeDTO.setFingerPrint(false);
		// authTypeDTO.setIris(false);
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
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	
	//TODO Now Introducer data will come in ID JSON Logic is going to change 
	private boolean validateIntroducer(RegOsiDto regOsi, String registrationId, String introducerUin)
			throws ApisResourceAccessException, IOException {
		
		/*if ((regOsi.getIntroducerFingerpImageName() == null) && (regOsi.getIntroducerIrisImageName() == null)
				&& (regOsi.getIntroducerPhotoName() == null)) {
			registrationStatusDto.setStatusComment(StatusMessage.VALIDATION_DETAILS);
			return false;
		}

		if (regOsi.getIntroducerFingerpImageName() != null) {
			String fingerPrint = BIOMETRIC
					+ getHashSequenceValue(registrationId, JsonConstant.INTRODUCERBIOMETRICSEQUENCE);
			String fingerPrintType = regOsi.getIntroducerFingerpType();
			if (!validateFingerprint(introducerUin, fingerPrint, fingerPrintType, registrationId)) {
				registrationStatusDto.setStatusComment(StatusMessage.INTRODUCER + message);
				return false;
			}
		}
		if (regOsi.getIntroducerIrisImageName() != null) {
			String iris = BIOMETRIC + regOsi.getIntroducerIrisImageName().toUpperCase();
			String irisType = regOsi.getIntroducerIrisType();
			if (!validateIris(introducerUin, iris, irisType, registrationId)) {
				registrationStatusDto.setStatusComment(StatusMessage.INTRODUCER + message);
				return false;
			}
		}
		if (regOsi.getIntroducerPhotoName() != null) {
			String face = BIOMETRIC + regOsi.getIntroducerPhotoName().toUpperCase();
			if (!validateFace(introducerUin, face, registrationId)) {
				registrationStatusDto.setStatusComment(StatusMessage.INTRODUCER + message);
				return false;
			}
		}*/
		return true;

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
			List<String> introducerUINList = packetInfoManager.getUINByRid(introducerRid);
			if(!introducerUINList.isEmpty()) {
						return true;
					}
			registrationStatusDto.setStatusComment(StatusMessage.PACKET_IS_ON_HOLD);
			return false;
		} else {
			registrationStatusDto.setStatusComment(StatusMessage.PARENT_RID_NOT_IN_REGISTRATION_TABLE + registrationId);
			return false;
		}
	}

	/**
	 * Gets the introducer UIN.
	 *
	 * @param intoducerRid
	 *            the intoducer rid
	 * @return the introducer UIN
	 */
	private String getIntroducerUIN(String intoducerRid) {
		List<DemographicInfoDto> demographicDedupeDtoList = packetInfoManager.findDemoById(intoducerRid);
		if (!demographicDedupeDtoList.isEmpty()) {
			return demographicDedupeDtoList.get(0).getUin();
		}
		return null;
	}

	/**
	 * Gets the hash sequence value.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param field
	 *            the field
	 * @return the hash sequence value
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	private String getHashSequenceValue(String registrationId, String field) throws UnsupportedEncodingException {

		Identity identity = getIdentity(registrationId);
		List<FieldValueArray> hashSequence = identity.getHashSequence1();
		List<String> hashList = identityIteratorUtil.getHashSequence(hashSequence, field);
		if (hashList != null)
			return hashList.get(0).toUpperCase();

		return null;

	}

	/**
	 * Gets the identity.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the identity
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	private Identity getIdentity(String registrationId) throws UnsupportedEncodingException {
		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKET_META_INFO.name());
		PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,
				PacketMetaInfo.class);
		return packetMetaInfo.getIdentity();

	}

}