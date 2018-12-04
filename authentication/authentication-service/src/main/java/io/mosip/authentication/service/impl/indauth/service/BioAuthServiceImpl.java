package io.mosip.authentication.service.impl.indauth.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.DeviceInfo;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Service
public class BioAuthServiceImpl implements BioAuthService {

	@Override
	public AuthStatusInfo validateBioDetails(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo,
			String refId) {

		return null;
	}

	@Override
	public boolean validateDevice(DeviceInfo deviceInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MosipBiometricProvider validateDevice(String bioType, DeviceInfo deviceInfo) {
		// TODO Auto-generated method stub
		return null;
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
