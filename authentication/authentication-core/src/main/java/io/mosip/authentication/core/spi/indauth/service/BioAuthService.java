package io.mosip.authentication.core.spi.indauth.service;

import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.DeviceInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;

/**
 * 
 * This interface is used to authenticate Individual based on Biometric
 * attributes.
 * 
 * @author Dinesh Karuppiah.T
 */

public interface BioAuthService {

	AuthStatusInfo validateBioDetails(IdentityDTO inputIdentity, IdentityDTO storedIdentity);

	boolean validateDevice(DeviceInfo deviceInfo);

	double getMatchScore(String inputMinutiae, String storedTemplate);

	double getMatchScore(byte[] inputImage, String storedMinutiae);

}