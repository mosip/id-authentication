package io.mosip.authentication.common.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.common.service.impl.match.PinMatchType;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import lombok.NoArgsConstructor;

/**
 * Implementation for OTP Auth Service to authenticate OTP via OTP Manager.
 *
 * @author Dinesh Karuppiah.T
 */

@Service
@NoArgsConstructor
public class OTPAuthServiceImpl implements OTPAuthService {

	private static final String AUTHENTICATE = "authenticate";

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPAuthServiceImpl.class);

	/** The IdInfoHelper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/** The MatchInputBuilder. */
	@Autowired
	private MatchInputBuilder matchInputBuilder;

	/** The IdaMappingconfig. */
	@Autowired
	private IDAMappingConfig idaMappingConfig;

	/**
	 * Validates generated OTP via OTP Manager.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param uin        the ref id
	 * @param idInfo the id info
	 * @param partnerId the partner id
	 * @return true - when the OTP is Valid.
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Override
	public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String uin,
			Map<String, List<IdentityInfoDTO>> idInfo, String partnerId) throws IdAuthenticationBusinessException {
		String txnId = authRequestDTO.getTransactionID();
		Optional<String> otp = getOtpValue(authRequestDTO);
		if (otp.isPresent()) {
			boolean isValidRequest = validateTxnAndIdvid(txnId, authRequestDTO.getIndividualId());
			if (isValidRequest) {
				mosipLogger.info("SESSION_ID", this.getClass().getSimpleName(), "Inside Validate Otp Request", "");
				List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
				List<MatchOutput> listMatchOutputs = constructMatchOutput(authRequestDTO, listMatchInputs, uin,
						partnerId);
				boolean isPinMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
				return AuthStatusInfoBuilder.buildStatusInfo(isPinMatched, listMatchInputs, listMatchOutputs,
						PinAuthType.values(), idaMappingConfig);
			}
		} else {
			throw new IDDataValidationException(IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage(), "OTP"));
		}
		return null;
	}

	/**
	 * Gets the s pin.
	 *
	 * @param uin the uin
	 * @param authReq the auth req
	 * @param partnerId the partner id
	 * @return the s pin
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, String> getOtpKey(String uin, AuthRequestDTO authReq, String partnerId)
			throws IdAuthenticationBusinessException {
		Map<String, String> map = new HashMap<>();
		String key = Optional.ofNullable(uin).orElseThrow(
				() -> new IdValidationFailedException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED));
		map.put("value", key);
		return map;
	}

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the list
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return matchInputBuilder.buildMatchInput(authRequestDTO, PinAuthType.values(), PinMatchType.values());
	}

//	

	/**
	 * Construct match output.
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
		return idInfoHelper.matchIdentityData(authRequestDTO, uin, listMatchInputs, this::getOtpKey, partnerId);
	}

	private Optional<String> getOtpValue(AuthRequestDTO authreqdto) {
		return Optional.ofNullable(authreqdto.getRequest().getOtp());
	}

	/**
	 * Validates Transaction ID and Unique ID.
	 *
	 * @param txnId   the txn id
	 * @param idvid the idvid
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */

	public boolean validateTxnAndIdvid(String txnId, String idvid) throws IdAuthenticationBusinessException {
		boolean validOtpAuth;
		String hashedIdvid = HMACUtils.digestAsPlainText(HMACUtils.generateHash(idvid.getBytes()));
		Optional<AutnTxn> authTxn = autntxnrepository
				.findByTxnId(txnId, PageRequest.of(0, 1), RequestType.OTP_REQUEST.getType()).stream().findFirst();
		if (!authTxn.isPresent()) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTHENTICATE,
					"Invalid TransactionID");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_TXN_ID);
		} else {
			if (!authTxn.get().getRefId().equalsIgnoreCase(hashedIdvid)) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTHENTICATE,
						"OTP id mismatch");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_TXN_ID);
			}
			validOtpAuth = true;
		}
		return validOtpAuth;
	}

	/**
	 * Checks for Null or Empty.
	 *
	 * @param otpVal - OTP value
	 * @return true - When the otpVal is Not null or empty
	 */

	public boolean isEmpty(String otpVal) {
		boolean isnullorempty = false;
		if (otpVal == null || otpVal.isEmpty() || otpVal.trim().length() == 0) {
			isnullorempty = true;
		} else {
			isnullorempty = false;
		}
		return isnullorempty;
	}

}
