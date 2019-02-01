package io.mosip.authentication.service.impl.indauth.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;

/**
 * The implementation of Demographic Authentication service.
 * 
 * @author Arun Bose
 */
@Service
public class DemoAuthServiceImpl implements DemoAuthService {

	/** The environment. */
	@Autowired
	public Environment environment;

	/** The id info helper. */
	@Autowired
	public IdInfoHelper idInfoHelper;

	/**
	 * Gets the match output.
	 *
	 * @param listMatchInputs the list match inputs
	 * @param authRequestDTO    the demo DTO
	 * @param demoEntity     the demo entity
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> demoEntity) throws IdAuthenticationBusinessException {
		return idInfoHelper.matchIdentityData(authRequestDTO, demoEntity, listMatchInputs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.service.DemoAuthService#
	 * getDemoStatus(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	public AuthStatusInfo getDemoStatus(AuthRequestDTO authRequestDTO, String uin,
			Map<String, List<IdentityInfoDTO>> demoEntity) throws IdAuthenticationBusinessException {

		if (demoEntity == null || demoEntity.isEmpty()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		}

		List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);

		List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs, authRequestDTO,
				demoEntity);
		// Using AND condition on the match output for Bio auth.
		boolean demoMatched = !listMatchOutputs.isEmpty() && listMatchOutputs.stream().allMatch(MatchOutput::isMatched);

		return idInfoHelper.buildStatusInfo(demoMatched, listMatchInputs, listMatchOutputs, DemoAuthType.values());

	}

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the list
	 */
	public List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return idInfoHelper.constructMatchInput(authRequestDTO, DemoAuthType.values(), DemoMatchType.values());
	}

}