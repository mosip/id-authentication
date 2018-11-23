package io.mosip.registration.processor.stages.osivalidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

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
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.stages.osivalidator.utils.StatusMessage;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

public class OSIValidator {

	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";
	/** The Constant BIOMETRIC_INTRODUCER. */
	public static final String BIOMETRIC_INTRODUCER = PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR
			+ PacketFiles.INTRODUCER.name() + FILE_SEPARATOR;

	private FileSystemAdapter<InputStream, Boolean> adapter;

	private RegistrationProcessorRestClientService<Object> restClientService;
	private String message = null;

	InternalRegistrationStatusDto registrationStatusDto;

	AuthRequestDTO authRequestDTO = new AuthRequestDTO();
	AuthTypeDTO authTypeDTO = new AuthTypeDTO();
	IdentityDTO identityDTO = new IdentityDTO();
	IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
	RequestDTO request = new RequestDTO();
	PinInfo pinInfo = new PinInfo();

	public OSIValidator(FileSystemAdapter<InputStream, Boolean> adapter,
			RegistrationProcessorRestClientService<Object> restClientService,
			PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager) {
		this.packetInfoManager = packetInfoManager;
		this.adapter = adapter;
		this.restClientService = restClientService;
	}

	public boolean isValidOSI(String registrationId) throws IOException, ApisResourceAccessException {

		boolean isValidOsi = false;
		RegOsiDto regOsi = packetInfoManager.getOsi(registrationId);

		if ((isValidOperator(regOsi, registrationId)) && (isValidSupervisor(regOsi, registrationId))
				&& (isValidIntroducer(regOsi, registrationId)))
			isValidOsi = true;

		return isValidOsi;

	}

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

		registrationStatusDto.setStatusComment(StatusMessage.OPERATOR + message);
		return false;
	}

	private boolean isValidIntroducer(RegOsiDto regOsi, String registrationId)
			throws IOException, ApisResourceAccessException {
		String uin = regOsi.getIntroducerUin();
		if (uin == null)
			return true;

		if ((regOsi.getIntroducerFingerpImageName() == null) && (regOsi.getIntroducerIrisImageName() == null)
				&& (regOsi.getIntroducerFingerpImageName() == null)) {
			registrationStatusDto.setStatusComment(StatusMessage.VALIDATION_DETAILS);
			return false;
		} else {
			String fingerPrint = BIOMETRIC_INTRODUCER + regOsi.getIntroducerFingerpImageName();
			String fingerPrintType = regOsi.getIntroducerFingerpType();
			String iris = BIOMETRIC_INTRODUCER + regOsi.getIntroducerIrisImageName();
			String irisType = regOsi.getIntroducerIrisType();
			String face = BIOMETRIC_INTRODUCER + regOsi.getIntroducerFingerpImageName();

			if ((validateUIN(uin)) && (validateFingerprint(uin, fingerPrint, fingerPrintType, registrationId))
					&& (validateIris(uin, iris, irisType, registrationId)
							&& (validateFace(uin, face, registrationId)))) {
				return true;
			}

		}

		registrationStatusDto.setStatusComment(StatusMessage.INTRODUCER + message);
		return false;
	}

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

	private boolean validateUIN(String uin) {
		// todo To call IAM rest API for UNI validation
		return true;

	}

	boolean validatePin(String uin, String pin) throws ApisResourceAccessException {
		if (pin == null)
			return true;

		Boolean isValidPin = false;
		authTypeDTO.setAddress(false);
		authTypeDTO.setBio(false);
		authTypeDTO.setFace(false);
		authTypeDTO.setFingerprint(false);
		authTypeDTO.setFullAddress(false);
		authTypeDTO.setIris(false);
		authTypeDTO.setOtp(false);
		authTypeDTO.setPersonalIdentity(false);
		authTypeDTO.setPin(true);

		authRequestDTO.setIdvId(uin);

		pinInfo.setValue(pin);

		List<PinInfo> pinList = new ArrayList<>();
		pinList.add(pinInfo);
		authRequestDTO.setPinInfo(pinList);

		AuthResponseDTO authResponseDTO = (AuthResponseDTO) restClientService.postApi(ApiName.AUTHINTERNAL, "", "",
				authRequestDTO, AuthResponseDTO.class);

		if (authResponseDTO.isStatus())
			isValidPin = true;

		return isValidPin;
	}

	boolean validateBiometric(String uin, String biometricType, String identity, byte[] biometricFileHashByte)
			throws ApisResourceAccessException {

		Boolean isValidBiometric = false;

		authRequestDTO.setIdvId(uin);
		authTypeDTO.setAddress(false);
		authTypeDTO.setBio(false);
		authTypeDTO.setFace(false);
		authTypeDTO.setFingerprint(false);
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
			authTypeDTO.setFingerprint(true);
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
		if (authResponseDTO.isStatus())
			isValidBiometric = true;

		return isValidBiometric;
	}

}
