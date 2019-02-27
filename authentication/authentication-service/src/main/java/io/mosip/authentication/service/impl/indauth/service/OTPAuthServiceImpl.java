package io.mosip.authentication.service.impl.indauth.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.pin.PinAuthType;
import io.mosip.authentication.service.impl.indauth.service.pin.PinMatchType;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.authentication.service.repository.VIDRepository;
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

	/** The Constant METHOD_VALIDATE_OTP. */
	private static final String METHOD_VALIDATE_OTP = "validateOtp";

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

	@Autowired
	private IdInfoHelper idInfoHelper;

	@Autowired
	private VIDRepository vidrepository;

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
	public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String uin,Map<String,List<IdentityInfoDTO>> idInfo)
			throws IdAuthenticationBusinessException {
		String txnId = authRequestDTO.getTransactionID();
		Optional<String> otp = getOtpValue(authRequestDTO);
		if (otp.isPresent()) {
			String vid = "";
			if (IdType.VID.getType().equalsIgnoreCase(authRequestDTO.getIdvIdType())) {
				vid = authRequestDTO.getIdvId();
			} else {
				Optional<String> findVidByUin = vidrepository.findVIDByUIN(uin, PageRequest.of(0, 1)).stream()
						.findFirst();
				if (findVidByUin.isPresent()) {
					vid = findVidByUin.get().trim();
				}
			}

			boolean isValidRequest = validateTxnId(txnId, uin, vid, authRequestDTO.getRequestTime());
			if (isValidRequest) {
				mosipLogger.info("SESSION_ID", METHOD_VALIDATE_OTP, "Inside Validate Otp Request", "");
				List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
				List<MatchOutput> listMatchOutputs = constructMatchOutput(authRequestDTO, listMatchInputs, uin);
				boolean isPinMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
				return idInfoHelper.buildStatusInfo(isPinMatched, listMatchInputs, listMatchOutputs,
						PinAuthType.values());
			} else {
				mosipLogger.debug(DEAFULT_SESSSION_ID, METHOD_VALIDATE_OTP, "Inside Invalid Txn ID",
						getClass().toString());
				mosipLogger.error(DEAFULT_SESSSION_ID, "NA", "NA", "Key Invalid");
				throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_TXN_ID);
			}
		} else {
			throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
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
	public Map<String, String> getOtpKey(String uin, AuthRequestDTO authReq) throws IdAuthenticationBusinessException {
		Map<String, String> map = new HashMap<>();
		String txnID = authReq.getTransactionID();
		String tspID = authReq.getPartnerID();
		String otpKey = OTPUtil.generateKey(env.getProperty("application.id"), uin, txnID, tspID);
		String key = Optional.ofNullable(otpKey)
				.orElseThrow(() -> new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_OTP_KEY));
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
		return idInfoHelper.constructMatchInput(authRequestDTO, PinAuthType.values(), PinMatchType.values());
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
			String uin) throws IdAuthenticationBusinessException {
		return idInfoHelper.matchIdentityData(authRequestDTO, uin, listMatchInputs, this::getOtpKey);
	}

	private Optional<String> getOtpValue(AuthRequestDTO authreqdto) {
		return Optional.ofNullable(authreqdto.getPinInfo())
				.flatMap(pinInfos -> pinInfos.stream()
						.filter(pinInfo -> pinInfo.getType() != null
								&& pinInfo.getType().equalsIgnoreCase(PinType.OTP.getType()))
						.findAny())
				.map(PinInfo::getValue);
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
			Optional<String> value = autntxnrepository.findByUinorVid(txnId, uin, vid, PageRequest.of(0, 1), RequestType.OTP_REQUEST.getType()).stream()
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
