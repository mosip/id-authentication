package io.mosip.authentication.common.service.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;
import static io.mosip.authentication.core.constant.IdAuthenticationErrorConstants.SERVER_ERROR;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Bio-service to implement Biometric Authentication.
 *
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 */

@Service
public class BioAuthServiceImpl implements BioAuthService {
	
	private static Logger logger = IdaLogger.getLogger(BioAuthServiceImpl.class);

	/** Id Info helper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/** Match Input Builder. */
	@Autowired
	private MatchInputBuilder matchInputBuilder;

	/** The Ida Mapping Config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/**
	 * Validate Bio Auth details based on Bio auth request and Biometric Identity
	 * values.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param uin the uin
	 * @param bioIdentity the bio identity
	 * @param partnerId the partner id
	 * @return the auth status info
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Override
	public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String token,
			Map<String, List<IdentityInfoDTO>> bioIdentity, String partnerId, boolean isAuth) throws IdAuthenticationBusinessException {
		if (bioIdentity == null || bioIdentity.isEmpty()) {
			logger.error(SESSION_ID, this.getClass().getName(), 
					"authenticate",
					"throw new IdAuthenticationBusinessException - SERVER_ERROR - bioIdentity is null or empty");
			throw new IdAuthenticationBusinessException(SERVER_ERROR);
		} else {
			// TODO disabled temporarily. will be enabled after implementation of validation
			// based on black-listed device code
//			if (isAuth) {
//				verifyBiometricDevice(authRequestDTO.getRequest().getBiometrics());
//			}
			List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO, bioIdentity);
			List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs, authRequestDTO, bioIdentity,
					partnerId);
			// Using OR condition on the match output for Bio auth.
			boolean bioMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
			return AuthStatusInfoBuilder.buildStatusInfo(bioMatched, listMatchInputs, listMatchOutputs,
					BioAuthType.values(), idMappingConfig);
		}

	}

	/**
	 * Constucts Match inputs based on Matched Bio Authtype and Bio Match type.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the list
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> bioIdentity) {
		return matchInputBuilder.buildMatchInput(authRequestDTO, BioAuthType.values(), BioMatchType.values(), bioIdentity);
	}

	/**
	 * Gets the match output.
	 *
	 * @param listMatchInputs the list match inputs
	 * @param authRequestDTO the auth request DTO
	 * @param demoEntity the demo entity
	 * @param partnerId the partner id
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> demoEntity, String partnerId) throws IdAuthenticationBusinessException {
		return idInfoHelper.matchIdentityData(authRequestDTO, demoEntity, listMatchInputs, partnerId);
	}

}
