package io.mosip.authentication.common.service.impl;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.common.service.impl.match.PinMatchType;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.VIDRepository;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.OTPUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.kernel.core.logger.spi.Logger;
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

	/** The Constant DEAFULT_SESSSION_ID. */
	private static final String DEAFULT_SESSSION_ID = "sessionID";

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPAuthServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	/** The IdInfoHelper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/** The MatchInputBuilder. */
	@Autowired
	private MatchInputBuilder matchInputBuilder;

	/** The Vid Repository. */
	@Autowired
	private VIDRepository vidrepository;

	/** The IdaMappingconfig. */
	@Autowired
	private IDAMappingConfig idaMappingConfig;

	/**
	 * Validates generated OTP via OTP Manager.
	 *
	 * @param authreqdto the authreqdto
	 * @param uin        the ref id
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
			String vid = "";
			if (IdType.VID.getType().equalsIgnoreCase(authRequestDTO.getIndividualIdType())) {
				vid = authRequestDTO.getIndividualId();
			} else {
				Optional<String> findVidByUin = vidrepository.findVIDByUIN(uin, PageRequest.of(0, 1)).stream()
						.findFirst();
				if (findVidByUin.isPresent()) {
					vid = findVidByUin.get().trim();
				}
			}

			boolean isValidRequest = validateTxnId(txnId, uin, vid, authRequestDTO.getRequestTime());
			if (isValidRequest) {
				mosipLogger.info("SESSION_ID", this.getClass().getSimpleName(), "Inside Validate Otp Request", "");
				List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
				List<MatchOutput> listMatchOutputs = constructMatchOutput(authRequestDTO, listMatchInputs, uin,
						partnerId);
				boolean isPinMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
				return AuthStatusInfoBuilder.buildStatusInfo(isPinMatched, listMatchInputs, listMatchOutputs,
						PinAuthType.values(), idaMappingConfig);
			} else {
				mosipLogger.debug(DEAFULT_SESSSION_ID, this.getClass().getSimpleName(), "Inside Invalid Txn ID",
						getClass().toString());
				mosipLogger.error(DEAFULT_SESSSION_ID, this.getClass().getSimpleName(), AUTHENTICATE, "Key Invalid");
				throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_TXN_ID);
			}
		} else {
			IDDataValidationException idDataValidationException = new IDDataValidationException(
					IdAuthenticationErrorConstants.MISSING_AUTHTYPE,
					String.format(IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage(), "OTP"), null);
			throw idDataValidationException;
		}
	}

	/**
	 * Gets the s pin.
	 *
	 * @param uinValue  the uin value
	 * @param matchType the match type
	 * @return the s pin
	 * @throws IdValidationFailedException
	 */
	public Map<String, String> getOtpKey(String uin, AuthRequestDTO authReq, String partnerId)
			throws IdAuthenticationBusinessException {
		Map<String, String> map = new HashMap<>();
		String txnID = authReq.getTransactionID();
		String tspID = partnerId;
		String otpKey = OTPUtil.generateKey(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID), uin, txnID, tspID);
		String key = Optional.ofNullable(otpKey).orElseThrow(
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
	 * @param uin     the uin
	 * @param reqTime
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws ParseException
	 */

	public boolean validateTxnId(String txnId, String uin, String vid, String reqTime)
			throws IdAuthenticationBusinessException {
		Optional<String> value = autntxnrepository
				.findByUinorVid(txnId, uin, vid, PageRequest.of(0, 1), RequestType.OTP_REQUEST.getType()).stream()
				.findFirst();
		return value.isPresent();
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
