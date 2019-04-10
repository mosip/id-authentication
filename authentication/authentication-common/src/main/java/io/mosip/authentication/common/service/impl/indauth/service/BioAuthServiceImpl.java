package io.mosip.authentication.common.service.impl.indauth.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.config.IDAMappingConfig;
import io.mosip.authentication.common.helper.IdInfoHelper;
import io.mosip.authentication.common.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.common.impl.indauth.service.bio.BioMatchType;
import io.mosip.authentication.common.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.impl.indauth.builder.MatchInputBuilder;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@Service
public class BioAuthServiceImpl implements BioAuthService {

	/**
	 * Id Info helper
	 */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/**
	 * Match Input Builder
	 */
	@Autowired
	private MatchInputBuilder matchInputBuilder;

	/**
	 * The Ida Mapping Config
	 */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/**
	 * Validate Bio Auth details based on Bio auth request and Biometric Identity
	 * values
	 */
	@Override
	public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String uin,
			Map<String, List<IdentityInfoDTO>> bioIdentity, String partnerId) throws IdAuthenticationBusinessException {
		if (bioIdentity == null || bioIdentity.isEmpty()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		} else {
			List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
			List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs, authRequestDTO, bioIdentity,
					partnerId);
			// Using OR condition on the match output for Bio auth.
			boolean bioMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
			return AuthStatusInfoBuilder.buildStatusInfo(bioMatched, listMatchInputs, listMatchOutputs,
					BioAuthType.values(), idMappingConfig);
		}

	}

	/**
	 * Constucts Match inputs based on Matched Bio Authtype and Bio Match type
	 * 
	 * @param authRequestDTO
	 * @return
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return matchInputBuilder.buildMatchInput(authRequestDTO, BioAuthType.values(), BioMatchType.values());
	}

	private List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> demoEntity, String partnerId) throws IdAuthenticationBusinessException {
		return idInfoHelper.matchIdentityData(authRequestDTO, demoEntity, listMatchInputs, partnerId);
	}

}
