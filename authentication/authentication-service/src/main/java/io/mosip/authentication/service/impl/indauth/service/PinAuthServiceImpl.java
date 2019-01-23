package io.mosip.authentication.service.impl.indauth.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.service.PinAuthService;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.PinAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.PinMatchType;

public class PinAuthServiceImpl implements PinAuthService {
	
	/** The id info helper. */
	@Autowired
	public IdInfoHelper idInfoHelper;

	@Override
	public AuthStatusInfo validatePin(AuthRequestDTO authRequestDTO, String uin) throws IdAuthenticationBusinessException {		
		List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
		List<MatchOutput> listMatchOutputs = constructMatchOutput(authRequestDTO, listMatchInputs, uin);
		boolean isPinMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
		return idInfoHelper.buildStatusInfo(isPinMatched, listMatchInputs, listMatchOutputs, PinAuthType.values());
	}
	

	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return idInfoHelper.constructMatchInput(authRequestDTO, PinAuthType.values(), PinMatchType.values());
	}

	private List<MatchOutput> constructMatchOutput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInputs, String uin) throws IdAuthenticationBusinessException {
		return idInfoHelper.matchIdentityData(authRequestDTO, uin, listMatchInputs, this::getSPin);
	}
	
	public Map<String, String> getSPin(String uinValue, MatchType matchType) {
		if(matchType.equals(PinMatchType.SPIN)) {
			
		}
		return null;
	}
}
