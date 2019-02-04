package io.mosip.authentication.service.impl.indauth.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.service.PinAuthService;
import io.mosip.authentication.service.entity.StaticPinEntity;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.pin.PinAuthType;
import io.mosip.authentication.service.impl.indauth.service.pin.PinMatchType;
import io.mosip.authentication.service.repository.StaticPinRepository;

/**
 * The Class PinAuthServiceImpl.
 * 
 * @author Sanjay Murali
 */
@Service
public class PinAuthServiceImpl implements PinAuthService {

	/** The id info helper. */
	@Autowired
	public IdInfoHelper idInfoHelper;

	/** The static pin repo. */
	@Autowired
	private StaticPinRepository staticPinRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.service.PinAuthService#validatePin(
	 * io.mosip.authentication.core.dto.indauth.AuthRequestDTO, java.lang.String)
	 */
	@Override
	public AuthStatusInfo validatePin(AuthRequestDTO authRequestDTO, String uin)
			throws IdAuthenticationBusinessException {
		List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
		List<MatchOutput> listMatchOutputs = constructMatchOutput(authRequestDTO, listMatchInputs, uin);
		boolean isPinMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
		return idInfoHelper.buildStatusInfo(isPinMatched, listMatchInputs, listMatchOutputs, PinAuthType.values());
	}

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @return the list
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return idInfoHelper.constructMatchInput(authRequestDTO, PinAuthType.values(), PinMatchType.values());
	}

	/**
	 * Construct match output.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param listMatchInputs
	 *            the list match inputs
	 * @param uin
	 *            the uin
	 * @return the list
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private List<MatchOutput> constructMatchOutput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInputs,
			String uin) throws IdAuthenticationBusinessException {
		return idInfoHelper.matchIdentityData(authRequestDTO, uin, listMatchInputs, this::getSPin);
	}

	/**
	 * Gets the s pin.
	 *
	 * @param uinValue
	 *            the uin value
	 * @param authReq
	 *            the match type
	 * @return the s pin
	 */
	public Map<String, String> getSPin(String uinValue, AuthRequestDTO authReq) {
		Map<String, String> map = new HashMap<>();
		String pin = null;
		Optional<StaticPinEntity> entityValues = staticPinRepo.findById(uinValue);
		if (entityValues.isPresent()) {
			pin = entityValues.get().getPin();
			map.put("value", pin);
		}
		return map;
	}
}
