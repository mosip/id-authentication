package io.mosip.registration.processor.stages.demodedupe;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthSecureDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.MatchInfo;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicDedupeDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

/**
 * The Class DemoDedupeAuthentication.
 *
 * @author M1048358 Alok Ranjan
 */

@Component
public class DemoDedupeAuthentication {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant BIOMETRIC_APPLICANT. */
	public static final String BIOMETRIC_APPLICANT = PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

	/** The adapter. */
	@Autowired
	FilesystemCephAdapterImpl adapter;

	/** The rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> restClientService;

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

	/** The auth secure DTO. */
	AuthSecureDTO authSecureDTO = new AuthSecureDTO();

	/** The match info. */
	MatchInfo matchinfo = new MatchInfo();

	/** The pin info info. */
	PinInfo pininfo = new PinInfo();

	/** The packet info manager. */
	@Autowired
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private PacketInfoDao packetInfoDao;

	/**
	 * Authenticate duplicates.
	 *
	 * @param regId
	 *            the reg id
	 * @param duplicateUins
	 *            the duplicate ids
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean authenticateDuplicates(String regId, List<String> duplicateUins)
			throws ApisResourceAccessException, IOException {

		List<String> applicantfingerprintImageNames = packetInfoManager.getApplicantFingerPrintImageNameById(regId);
		List<String> applicantIrisImageNames = packetInfoManager.getApplicantIrisImageNameById(regId);
		boolean isDuplicate = false;

		for (String duplicateUin : duplicateUins) {
			if ((authenticateBiometric(applicantfingerprintImageNames, PacketFiles.FINGER.name(), duplicateUin, regId)
					|| authenticateBiometric(applicantIrisImageNames, PacketFiles.IRIS.name(), duplicateUin, regId))) {
				isDuplicate = true;
				break;
			}
		}

		return isDuplicate;

	}

	/**
	 * Authenticate biometric.
	 *
	 * @param biometriclist
	 *            the biometriclist
	 * @param type
	 *            the type
	 * @param duplicateUin
	 *            the duplicate id
	 * @param regId
	 *            the reg id
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean authenticateBiometric(List<String> biometriclist, String type, String duplicateUin, String regId)
			throws ApisResourceAccessException, IOException {
		boolean isDuplicate = false;
		for (String biometricName : biometriclist) {
			String biometric = BIOMETRIC_APPLICANT + biometricName.toUpperCase();

			if (adapter.checkFileExistence(regId, biometric)) {
				InputStream biometricFileName = adapter.getFile(regId, biometric);
				byte[] biometricByte = IOUtils.toByteArray(biometricFileName);

				if (validateBiometric(biometricName, biometricByte, type, duplicateUin)) {
					isDuplicate = true;
					break;
				}
			}
		}

		return isDuplicate;
	}

	/**
	 * Validate biometric.
	 *
	 * @param biometricName
	 *            the biometric name
	 * @param biometricByte
	 *            the biometric byte
	 * @param type
	 *            the type
	 * @param duplicateUin
	 *            the duplicate id
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean validateBiometric(String biometricName, byte[] biometricByte, String type, String duplicateUin)
			throws ApisResourceAccessException {

		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());

		authRequestDTO.setId("mosip.internal.auth");
		authRequestDTO.setIdvId(duplicateUin);
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

		identityInfoDTO.setValue(new String(biometricByte));
		List<IdentityInfoDTO> biometricData = new ArrayList<>();
		biometricData.add(identityInfoDTO);

		setBiometric(biometricName, biometricData, type);

		request.setIdentity(identityDTO);
		authRequestDTO.setRequest(request);

		// sending request to get authentication response
		AuthResponseDTO authResponseDTO = (AuthResponseDTO) restClientService.postApi(ApiName.AUTHINTERNAL, "", "",
				authRequestDTO, AuthResponseDTO.class);

		return "y".equalsIgnoreCase(authResponseDTO.getStatus()) ? true : false;
	}

	/**
	 * Sets the biometric.
	 *
	 * @param biometricName
	 *            the biometric name
	 * @param biometricData
	 *            the biometric data
	 * @param type
	 *            the type
	 */
	private void setBiometric(String biometricName, List<IdentityInfoDTO> biometricData, String type) {
		if (type.equalsIgnoreCase(PacketFiles.IRIS.name())) {
			authTypeDTO.setIris(true);
			setIrisBiometric(biometricName, biometricData);
		} else if (type.equalsIgnoreCase(PacketFiles.FINGER.name())) {
			authTypeDTO.setFingerPrint(true);
			setFingerBiometric(biometricName, biometricData);
		}
	}

	/**
	 * Sets the iris biometric.
	 *
	 * @param biometricName
	 *            the biometric name
	 * @param biometricData
	 *            the biometric data
	 */
	private void setIrisBiometric(String biometricName, List<IdentityInfoDTO> biometricData) {
		if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setLeftEye(biometricData);

		} else if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setRightEye(biometricData);
		}
	}

	/**
	 * Sets the finger biometric.
	 *
	 * @param biometricName
	 *            the biometric name
	 * @param biometricData
	 *            the biometric data
	 */
	private void setFingerBiometric(String biometricName, List<IdentityInfoDTO> biometricData) {
		if (PacketFiles.LEFTTHUMB.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setLeftThumb(biometricData);
		} else if (PacketFiles.LEFTINDEX.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setLeftIndex(biometricData);
		} else if (PacketFiles.LEFTMIDDLE.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setLeftMiddle(biometricData);
		} else if (PacketFiles.LEFTLITTLE.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setLeftLittle(biometricData);
		} else if (PacketFiles.LEFTRING.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setLeftRing(biometricData);
		} else if (PacketFiles.RIGHTTHUMB.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setRightThumb(biometricData);
		} else if (PacketFiles.RIGHTINDEX.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setRightIndex(biometricData);
		} else if (PacketFiles.RIGHTMIDDLE.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setRightMiddle(biometricData);
		} else if (PacketFiles.RIGHTLITTLE.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setRightLittle(biometricData);
		} else if (PacketFiles.RIGHTRING.name().equalsIgnoreCase(biometricName)) {
			identityDTO.setRightRing(biometricData);
		}
	}

	public Set<DemographicDedupeDto> performDedupe(String refId) {
		List<DemographicDedupeDto> idWithOutUin = packetInfoDao.findDemoById(refId);// Record with out uin and need to
																					// perform dedupe
		Set<DemographicDedupeDto> duplicateRegIds = new HashSet<>();
		List<DemographicDedupeDto> uinDtos;
		for (DemographicDedupeDto demoDto : idWithOutUin) {
			uinDtos = packetInfoDao.getAllDemoWithUIN(demoDto.getPhoneticName(), demoDto.getGenderCode(),
					demoDto.getDob());
			if (uinDtos != null && !uinDtos.isEmpty()) {
				duplicateRegIds.addAll(uinDtos);
				break;

			}

		}
		return duplicateRegIds;

	}
}
