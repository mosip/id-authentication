package io.mosip.authentication.core.spi.indauth.service;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.DeviceInfo;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;

/**
 * 
 * This interface is used to authenticate Individual based on Biometric
 * attributes.
 * 
 * @author Dinesh Karuppiah.T
 */

public interface BioAuthService {

	AuthStatusInfo validateBioDetails(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String refId);

	boolean validateDevice(DeviceInfo deviceInfo);

	double getMatchScore(String inputMinutiae, String storedTemplate);

	double getMatchScore(byte[] inputImage, String storedMinutiae);

}