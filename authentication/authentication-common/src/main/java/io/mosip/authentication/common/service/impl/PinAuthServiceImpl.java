package io.mosip.authentication.common.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.entity.StaticPin;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.common.service.impl.match.PinMatchType;
import io.mosip.authentication.common.service.repository.StaticPinRepository;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.PinAuthService;

/**
 * This class is used to give the implementation for the
 * pin based authentication service which receives the pin
 * from the request and compares it the pin stored in the DB.
 * 
 * @author Sanjay Murali
 */
@Service
public class PinAuthServiceImpl implements PinAuthService {

	/** The id info helper. */
	@Autowired
	public IdInfoHelper idInfoHelper;

	/** The id info helper. */
	@Autowired
	public MatchInputBuilder matchInputBuilder;

	/** The static pin repo. */
	@Autowired
	private StaticPinRepository staticPinRepo;

	/** The ida mapping config. */
	@Autowired
	private IDAMappingConfig idaMappingConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.service.PinAuthService#validatePin(
	 * io.mosip.authentication.core.dto.indauth.AuthRequestDTO, java.lang.String)
	 */
	@Override
	public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String uin,
			Map<String, List<IdentityInfoDTO>> idInfo, String partnerId) throws IdAuthenticationBusinessException {
		List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
		List<MatchOutput> listMatchOutputs = constructMatchOutput(authRequestDTO, listMatchInputs, uin, partnerId);
		boolean isPinMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
		return AuthStatusInfoBuilder.buildStatusInfo(isPinMatched, listMatchInputs, listMatchOutputs,
				PinAuthType.values(), idaMappingConfig);
	}

	/**
	 * constructMatchInput method used to construct the Match input for the
	 * authentication request {@link AuthRequestDTO}
	 *
	 * @param authRequestDTO {@link AuthRequestDTO}
	 * @return the list
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return matchInputBuilder.buildMatchInput(authRequestDTO, PinAuthType.values(), PinMatchType.values());
	}

	/**
	 * constructMatchOutput method used to construct the Match output computing with
	 * particular match matching strategy
	 *
	 * @param authRequestDTO  the auth request DTO
	 * @param listMatchInputs the list match inputs
	 * @param uin             the uin
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private List<MatchOutput> constructMatchOutput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInputs,
			String uin, String partnerId) throws IdAuthenticationBusinessException {
		return idInfoHelper.matchIdentityData(authRequestDTO, uin, listMatchInputs, this::getSPin, partnerId);
	}

	/**
	 * getSPin method used to fetch the SPIN value of the individual's from the
	 * database.
	 *
	 * @param uinValue the uin value
	 * @param authReq  the match type
	 * @param partnerId the partner id
	 * @return the s pin
	 */
	public Map<String, String> getSPin(String uinValue, AuthRequestDTO authReq, String partnerId) {
		Map<String, String> map = new HashMap<>();
		String pin = null;
		Optional<StaticPin> entityValues = staticPinRepo.findById(uinValue);
		if (entityValues.isPresent()) {
			pin = entityValues.get().getPin();
			map.put("value", pin);
		}
		return map;
	}
}
