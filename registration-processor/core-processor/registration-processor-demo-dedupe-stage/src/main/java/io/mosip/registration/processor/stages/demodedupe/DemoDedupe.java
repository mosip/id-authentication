package io.mosip.registration.processor.stages.demodedupe;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.auth.dto.AuthRequestDTO;
import io.mosip.registration.processor.core.auth.dto.AuthTypeDTO;
import io.mosip.registration.processor.core.auth.dto.IdentityDTO;
import io.mosip.registration.processor.core.auth.dto.IdentityInfoDTO;
import io.mosip.registration.processor.core.auth.dto.RequestDTO;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

/**
 * The Class DemoDedupe.
 *
 * @author M1048358 Alok Ranjan
 * @author M1048860 Kiran Raj
 */
@Component
public class DemoDedupe {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(DemoDedupe.class);

	/** The Constant FILE_SEPARATOR. */
	private static final String FILE_SEPARATOR = "\\";

	/** The Constant BIOMETRIC_APPLICANT. */
	private static final String BIOMETRIC_APPLICANT = PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

	/** The adapter. */
	@Autowired
	private FileSystemAdapter adapter;

	/** The env. */
	@Autowired
	private Environment env;

	/** The auth request DTO. */
	private AuthRequestDTO authRequestDTO = new AuthRequestDTO();

	/** The auth type DTO. */
	private AuthTypeDTO authTypeDTO = new AuthTypeDTO();

	/** The identity DTO. */
	private IdentityDTO identityDTO = new IdentityDTO();

	/** The identity info DTO. */
	private IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();

	/** The request. */
	private RequestDTO request = new RequestDTO();

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The packet info dao. */
	@Autowired
	private PacketInfoDao packetInfoDao;

	@Autowired
	private BiometricValidation biometricValidation;

	/**
	 * Perform dedupe.
	 *
	 * @param refId
	 *            the ref id
	 * @return the list
	 */
	public List<DemographicInfoDto> performDedupe(String refId) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REFFERENCEID.toString(),
				refId, "DemoDedupe::performDedupe()::entry");

		List<DemographicInfoDto> applicantDemoDto = packetInfoDao.findDemoById(refId);
		List<DemographicInfoDto> demographicInfoDtos = new ArrayList<>();
		for (DemographicInfoDto demoDto : applicantDemoDto) {
			demographicInfoDtos.addAll(packetInfoDao.getAllDemographicInfoDtos(demoDto.getName(),
					demoDto.getGenderCode(), demoDto.getDob(), demoDto.getLangCode()));
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REFFERENCEID.toString(),
				refId, "DemoDedupe::performDedupe()::exit");
		return demographicInfoDtos;
	}

	/**
	 * Authenticate duplicates.
	 *
	 * @param regId
	 *            the reg id
	 * @param duplicateUins
	 *            the duplicate uins
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IntrospectionException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public boolean authenticateDuplicates(String regId, List<String> duplicateUins)
			throws ApisResourceAccessException, IOException,ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {

		//TODO Validating Mocked Biometric logic 
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "DemoDedupe::authenticateDuplicates()::entry");

		boolean isDuplicate = false;
		for (String duplicateUin : duplicateUins) {
			setAuthDto();
			isDuplicate = biometricValidation.validateBiometric(duplicateUin,regId);
			if(isDuplicate==true) {
				return isDuplicate;
			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "DemoDedupe::authenticateDuplicates()::exit");
		return isDuplicate;
	}

	/**
	 * Authenticate finger biometric.
	 *
	 * @param biometriclist
	 *            the biometriclist
	 * @param type
	 *            the type
	 * @param duplicateUin
	 *            the duplicate uin
	 * @param regId
	 *            the reg id
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException 
	 * @throws IntrospectionException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private boolean authenticateFingerBiometric(List<String> biometriclist, String type, String duplicateUin,
			String regId) throws ApisResourceAccessException, IOException, ParseException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		for (String biometricName : biometriclist) {
			String biometric = BIOMETRIC_APPLICANT + biometricName.toUpperCase();
			if (adapter.checkFileExistence(regId, biometric)) {
				InputStream biometricFileName = adapter.getFile(regId, biometric);
				byte[] fingerPrintByte = IOUtils.toByteArray(biometricFileName);
				setAuthDto();
				identityInfoDTO.setValue(new String(fingerPrintByte));
				List<IdentityInfoDTO> biometricData = new ArrayList<>();
				biometricData.add(identityInfoDTO);
				// authTypeDTO.setFingerPrint(true);
				setFingerBiometric(biometricData, type);
			}
		}
		// change to return validateBiometric(duplicateUin); once the auth is fixed
		return biometricValidation.validateBiometric(duplicateUin,regId);
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
	 * @throws IntrospectionException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private void setFingerBiometricDto(Object obj, String fieldName, Object value) throws  IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		PropertyDescriptor pd;
		if(fieldName != null ) {
			pd = new PropertyDescriptor(fieldName, obj.getClass());
			pd.getWriteMethod().invoke(obj, value);
		}
	}

	/**
	 * Sets the finger biometric.
	 *
	 * @param biometricData
	 *            the biometric data
	 * @param type
	 *            the type
	 * @throws IntrospectionException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	void setFingerBiometric(List<IdentityInfoDTO> biometricData, String type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
		String finger = null;
		String[] fingerType = env.getProperty("fingerType").split(",");
		List<String> list = new ArrayList<>(Arrays.asList(fingerType));
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
	 * Authenticate iris biometric.
	 *
	 * @param biometriclist
	 *            the biometriclist
	 * @param type
	 *            the type
	 * @param duplicateUin
	 *            the duplicate uin
	 * @param regId
	 *            the reg id
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException 
	 */
	private boolean authenticateIrisBiometric(List<String> biometriclist, String type, String duplicateUin,
			String regId) throws ApisResourceAccessException, IOException, ParseException {
		// authTypeDTO.setIris(true);
		for (String biometricName : biometriclist) {
			String biometric = BIOMETRIC_APPLICANT + biometricName.toUpperCase();
			if (adapter.checkFileExistence(regId, biometric)) {
				InputStream biometricFileName = adapter.getFile(regId, biometric);
				byte[] biometricByte = IOUtils.toByteArray(biometricFileName);
				setAuthDto();
				identityInfoDTO.setValue(new String(biometricByte));
				List<IdentityInfoDTO> biometricData = new ArrayList<>();
				biometricData.add(identityInfoDTO);
				if (PacketFiles.LEFTEYE.name().equalsIgnoreCase(biometricName.toUpperCase())) {
					identityDTO.setLeftEye(biometricData);
				}
				if (PacketFiles.RIGHTEYE.name().equalsIgnoreCase(biometricName.toUpperCase())) {
					identityDTO.setRightEye(biometricData);
				}
			}
		}
		return biometricValidation.validateBiometric(duplicateUin,regId);
		// return validateBiometric(duplicateUin);
	}

	/**
	 * Validate biometric.
	 *
	 * @param duplicateUin
	 *            the duplicate uin
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	private boolean validateBiometric(String duplicateUin) throws ApisResourceAccessException {
		authRequestDTO.setIdvId(duplicateUin);
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
}
