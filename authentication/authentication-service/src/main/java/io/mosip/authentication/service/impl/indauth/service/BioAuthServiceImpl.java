package io.mosip.authentication.service.impl.indauth.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.indauth.service.bio.BioMatchType;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Service
public class BioAuthServiceImpl implements BioAuthService {

	@Autowired
	private IdInfoHelper idInfoHelper;

	@Override
	public AuthStatusInfo validateBioDetails(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> bioIdentity, String refId) throws IdAuthenticationBusinessException {
		if (bioIdentity == null || bioIdentity.isEmpty()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		} else {
			List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
			List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs,
					authRequestDTO.getRequest().getIdentity(), bioIdentity);
			//Using OR condition on the match output for Bio auth.
			boolean bioMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
			return idInfoHelper.buildStatusInfo(bioMatched, listMatchInputs, listMatchOutputs, BioAuthType.values());
		}

	}

	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return idInfoHelper.constructMatchInput(authRequestDTO, BioAuthType.values(), BioMatchType.values());
	}

	private List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, IdentityDTO identitydto,
			Map<String, List<IdentityInfoDTO>> demoEntity) {
		return idInfoHelper.matchIdentityData(identitydto, demoEntity, listMatchInputs);
	}

}
