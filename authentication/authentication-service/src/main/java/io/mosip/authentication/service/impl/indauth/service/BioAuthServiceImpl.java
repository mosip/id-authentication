package io.mosip.authentication.service.impl.indauth.service;

import org.springframework.stereotype.Service;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.DeviceInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Service
public class BioAuthServiceImpl implements BioAuthService {

	@Override
	public AuthStatusInfo validateBioDetails(IdentityDTO inputIdentity, IdentityDTO storedIdentity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateDevice(DeviceInfo deviceInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getMatchScore(String inputMinutiae, String storedTemplate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMatchScore(byte[] inputImage, String storedMinutiae) {
		// TODO Auto-generated method stub
		return 0;
	}

}
