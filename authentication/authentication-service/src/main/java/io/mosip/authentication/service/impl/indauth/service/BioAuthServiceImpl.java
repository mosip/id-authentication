package io.mosip.authentication.service.impl.indauth.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.DeviceInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.service.impl.indauth.builder.BioAuthType;
import io.mosip.authentication.service.impl.indauth.service.bio.BioMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.IdInfoMatcher;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchInput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchOutput;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Service
public class BioAuthServiceImpl implements BioAuthService {

	@Autowired
	private IdInfoMatcher idInfoMatcher;

	@Override
	public AuthStatusInfo validateBioDetails(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> demoIdentity, String refId) throws IdAuthenticationBusinessException {
		if (demoIdentity == null || demoIdentity.isEmpty()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		} else {
			List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
			List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs,
					authRequestDTO.getRequest().getIdentity(), demoIdentity);
			boolean bioMatched = listMatchOutputs.stream().allMatch(MatchOutput::isMatched);
			return idInfoMatcher.buildStatusInfo(bioMatched, listMatchInputs, listMatchOutputs, BioAuthType.values());
		}

	}

	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return idInfoMatcher.constructMatchInput(authRequestDTO, BioAuthType.values(), BioMatchType.values());
	}

	private List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, IdentityDTO identitydto,
			Map<String, List<IdentityInfoDTO>> demoEntity) {
		return idInfoMatcher.matchDemoData(identitydto, demoEntity, listMatchInputs);
	}

	private boolean validateDevice(DeviceInfo deviceInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	private MosipBiometricProvider validateDevice(String bioType, DeviceInfo deviceInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	private double getMatchScore(String inputMinutiae, String storedTemplate) {
		// TODO Auto-generated method stub
		return 0;
	}

	private double getMatchScore(byte[] inputImage, String storedMinutiae) {
		// TODO Auto-generated method stub
		return 0;
	}

}
