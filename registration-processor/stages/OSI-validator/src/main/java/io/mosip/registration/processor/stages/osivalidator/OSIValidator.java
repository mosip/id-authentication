package io.mosip.registration.processor.stages.osivalidator;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.code.ApplicantType;
import io.mosip.registration.processor.status.code.IntroducerType;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.service.TransactionService;

/**
 * The Class OSIValidator.
 */
@Service
public class OSIValidator {

	/** The packet info manager. */
	@Autowired
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";
	/** The Constant BIOMETRIC_INTRODUCER. */
	public static final String BIOMETRIC_INTRODUCER = PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR
			+ PacketFiles.INTRODUCER.name() + FILE_SEPARATOR;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The adapter. */
	@Autowired
	FilesystemCephAdapterImpl adapter;

	/** The rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> restClientService;

	/** The transcation status service. */
	@Autowired
	private TransactionService<TransactionDto> transcationStatusService;

	/** The message. */
	private String message = null;

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

		boolean isValidOsi = false;
		RegOsiDto regOsi = packetInfoManager.getOsi(registrationId);

		if ((isValidOperator(regOsi, registrationId)) && (isValidSupervisor(regOsi, registrationId))
				&& (isValidIntroducer(regOsi, registrationId)))
			isValidOsi = true;

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

		String uin = regOsi.getOfficerId();
		if (uin == null)
			return true;
		else {

			String fingerPrint = regOsi.getOfficerFingerpImageName();
			String fingerPrintType = regOsi.getOfficerfingerType();
			String iris = regOsi.getOfficerIrisImageName();
			String irisType = regOsi.getOfficerIrisType();
			String face = regOsi.getOfficerPhotoName();
			String pin = regOsi.getOfficerHashedPin();

			if ((fingerPrint == null) && (iris == null) && (face == null) && (pin == null)) {
				registrationStatusDto.setStatusComment(StatusMessage.VALIDATION_DETAILS);
				return false;
			} else if ((validateUIN(uin)) && (validateFingerprint(uin, fingerPrint, fingerPrintType, registrationId))
					&& (validateIris(uin, iris, irisType, registrationId)) && (validateFace(uin, face, registrationId))
					&& (validatePin(uin, pin))) {
				return true;
			}

		}

		registrationStatusDto.setStatusComment(StatusMessage.OPERATOR + message);
		return false;
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
		String uin = regOsi.getSupervisorId();
		if (uin == null)
			return false;
		else {

			String fingerPrint = regOsi.getSupervisorFingerpImageName();
			String fingerPrintType = regOsi.getSupervisorFingerType();
			String iris = regOsi.getSupervisorIrisImageName();
			String irisType = regOsi.getSupervisorIrisType();
			String face = regOsi.getSupervisorPhotoName();
			String pin = regOsi.getSupervisorHashedPin();
			if ((fingerPrint == null) && (iris == null) && (face == null) && (pin == null)) {
				registrationStatusDto.setStatusComment(StatusMessage.VALIDATION_DETAILS);
				return false;
			} else if ((validateUIN(uin)) && (validateFingerprint(uin, fingerPrint, fingerPrintType, registrationId))
					&& (validateIris(uin, iris, irisType, registrationId)) && (validateFace(uin, face, registrationId))
					&& (validatePin(uin, pin))) {
				return true;
			}

		}

		registrationStatusDto.setStatusComment(StatusMessage.SUPERVISOR + message);
		return false;
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

		if (registrationStatusDto.getRegistrationType().equalsIgnoreCase(SyncTypeDto.NEW.name())
				&& registrationStatusDto.getApplicantType().equalsIgnoreCase(ApplicantType.CHILD.name())
				&& regOsi.getIntroducerTyp().equalsIgnoreCase(IntroducerType.PARENT.name())) {
			String introducerUin = regOsi.getIntroducerUin();
			String introducerRid = regOsi.getIntroducerRegId();
			if (introducerUin == null && introducerRid == null) {
				registrationStatusDto.setStatusComment(StatusMessage.PARENT_UIN_AND_RID_NOT_IN_PACKET + registrationId);
				return false;
			}
			if (introducerUin == null) {
				if (validateIntroducerRid(introducerRid, registrationId)) {
					// To do get parent UIN from UIN Master DB
					introducerUin = getUIN() + regOsi.getIntroducerRegId();
					return validateIntroducer(regOsi, registrationId, introducerUin);
				} else {
					return false;
				}

			} else {
				return validateIntroducer(regOsi, registrationId, introducerUin);
			}

		} else {
			return true;
		}

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
				if (validateBiometric(uin, PacketFiles.FINGER.name(), type, fingerPrintByte))
					return true;
			}
		}
		message = StatusMessage.FINGER_PRINT;
		return false;

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
				if (validateBiometric(uin, PacketFiles.IRIS.name(), type, irisByte))
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
				InputStream faceFile = adapter.getFile(registrationId, face.toUpperCase());
				byte[] faceByte = IOUtils.toByteArray(faceFile);
				if (validateBiometric(uin, PacketFiles.FACE.name(), null, faceByte))
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
		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String date = simpleDateFormat.format(new Date());
		Boolean isValidPin = false;
		authTypeDTO.setAddress(false);
		authTypeDTO.setBio(false);
		authTypeDTO.setFace(false);
		authTypeDTO.setFingerPrint(false);
		authTypeDTO.setFullAddress(false);
		authTypeDTO.setIris(false);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPersonalIdentity(false);
		authTypeDTO.setPin(true);
		authRequestDTO.setVer("1.0");
		authRequestDTO.setId("mosip.internal.auth");
		authRequestDTO.setIdvId(uin);

		pinInfo.setValue(pin);
		pinInfo.setType("SPIN");

		authRequestDTO.setReqTime(date);

		List<PinInfo> pinList = new ArrayList<>();
		pinList.add(pinInfo);
		authRequestDTO.setPinInfo(pinList);

		AuthResponseDTO authResponseDTO = (AuthResponseDTO) restClientService.postApi(ApiName.AUTHINTERNAL, "", "",
				authRequestDTO, AuthResponseDTO.class);

		if (authResponseDTO.getStatus().equalsIgnoreCase("y"))
			isValidPin = true;

		return isValidPin;
	}

	/**
	 * Validate biometric.
	 *
	 * @param uin
	 *            the uin
	 * @param biometricType
	 *            the biometric type
	 * @param identity
	 *            the identity
	 * @param biometricFileHashByte
	 *            the biometric file hash byte
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	boolean validateBiometric(String uin, String biometricType, String identity, byte[] biometricFileHashByte)
			throws ApisResourceAccessException {

		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String date = simpleDateFormat.format(new Date());
		Boolean isValidBiometric = false;

		authRequestDTO.setId("mosip.internal.auth");
		authRequestDTO.setIdvId(uin);
		authRequestDTO.setIdvIdType("D");
		authRequestDTO.setVer("1.0");
		authRequestDTO.setReqTime(date);

		authTypeDTO.setAddress(false);
		authTypeDTO.setBio(false);
		authTypeDTO.setFace(false);
		authTypeDTO.setFingerPrint(false);
		authTypeDTO.setFullAddress(false);
		authTypeDTO.setIris(false);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPersonalIdentity(false);
		authTypeDTO.setPin(false);
		authRequestDTO.setAuthType(authTypeDTO);
		identityInfoDTO.setValue(new String(biometricFileHashByte));
		List<IdentityInfoDTO> biometricData = new ArrayList<>();
		biometricData.add(identityInfoDTO);
		if (biometricType.equals(PacketFiles.FACE.name())) {
			authTypeDTO.setFace(true);
			identityDTO.setFace(biometricData);
		} else if (biometricType.equalsIgnoreCase(PacketFiles.IRIS.name())) {
			authTypeDTO.setIris(true);
			if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(identity)) {
				identityDTO.setLeftEye(biometricData);

			} else if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(identity)) {
				identityDTO.setRightEye(biometricData);
			}
		} else if (biometricType.equalsIgnoreCase(PacketFiles.FINGER.name())) {
			authTypeDTO.setFingerPrint(true);
			if (PacketFiles.LEFTTHUMB.name().equalsIgnoreCase(identity)) {

				identityDTO.setLeftThumb(biometricData);
			} else if (PacketFiles.LEFTINDEX.name().equalsIgnoreCase(identity)) {
				identityDTO.setLeftIndex(biometricData);
			} else if (PacketFiles.LEFTMIDDLE.name().equalsIgnoreCase(identity)) {
				identityDTO.setLeftThumb(biometricData);
			} else if (PacketFiles.LEFTLITTLE.name().equalsIgnoreCase(identity)) {
				identityDTO.setLeftLittle(biometricData);
			} else if (PacketFiles.LEFTRING.name().equalsIgnoreCase(identity)) {
				identityDTO.setLeftRing(biometricData);
			} else if (PacketFiles.RIGHTTHUMB.name().equalsIgnoreCase(identity)) {
				identityDTO.setRightThumb(biometricData);
			} else if (PacketFiles.RIGHTINDEX.name().equalsIgnoreCase(identity)) {
				identityDTO.setRightIndex(biometricData);
			} else if (PacketFiles.RIGHTMIDDLE.name().equalsIgnoreCase(identity)) {
				identityDTO.setRightThumb(biometricData);
			} else if (PacketFiles.RIGHTLITTLE.name().equalsIgnoreCase(identity)) {
				identityDTO.setRightLittle(biometricData);
			} else if (PacketFiles.RIGHTRING.name().equalsIgnoreCase(identity)) {
				identityDTO.setRightRing(biometricData);
			}
		}
		request.setIdentity(identityDTO);
		authRequestDTO.setRequest(request);

		AuthResponseDTO authResponseDTO = (AuthResponseDTO) restClientService.postApi(ApiName.AUTHINTERNAL, "", "",
				authRequestDTO, AuthResponseDTO.class);
		if (authResponseDTO.getStatus().equalsIgnoreCase("y"))
			isValidBiometric = true;

		return isValidBiometric;
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
	private boolean validateIntroducer(RegOsiDto regOsi, String registrationId, String introducerUin)
			throws ApisResourceAccessException, IOException {

		if ((regOsi.getIntroducerFingerpImageName() == null) && (regOsi.getIntroducerIrisImageName() == null)
				&& (regOsi.getIntroducerPhotoName() == null)) {
			registrationStatusDto.setStatusComment(StatusMessage.VALIDATION_DETAILS);
			return false;
		} else {
			String fingerPrint = BIOMETRIC_INTRODUCER + regOsi.getIntroducerFingerpImageName();
			String fingerPrintType = regOsi.getIntroducerFingerpType();
			String iris = BIOMETRIC_INTRODUCER + regOsi.getIntroducerIrisImageName();
			String irisType = regOsi.getIntroducerIrisType();
			String face = BIOMETRIC_INTRODUCER + regOsi.getIntroducerPhotoName();

			if ((validateUIN(introducerUin))
					&& (validateFingerprint(introducerUin, fingerPrint, fingerPrintType, registrationId))
					&& (validateIris(introducerUin, iris, irisType, registrationId)
							&& (validateFace(introducerUin, face, registrationId)))) {
				return true;
			} else {
				registrationStatusDto.setStatusComment(StatusMessage.INTRODUCER + message);
				return false;
			}

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
			TransactionDto transactionDto = transcationStatusService.getTransactionByRegIdAndStatusCode(introducerRid,
					RegistrationStatusCode.UIN_GENERATED.toString());
			if (transactionDto != null) {
				return true;
			} else {
				registrationStatusDto.setStatusComment(StatusMessage.PACKET_IS_ON_HOLD);
				return false;
			}

		} else {
			registrationStatusDto.setStatusComment(StatusMessage.PARENT_RID_NOT_IN_REGISTRATION_TABLE + registrationId);
			return false;
		}
	}

	/**
	 * Gets the uin.
	 *
	 * @return the uin
	 */
	private String getUIN() {
		// TO do handle Parent UIN not found in UIN Master DB
		return "";
	}

}
