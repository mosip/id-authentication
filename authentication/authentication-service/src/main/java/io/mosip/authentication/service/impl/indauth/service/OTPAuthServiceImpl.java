package io.mosip.authentication.service.impl.indauth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;
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

	/** The otp manager. */
	@Autowired
	private OTPManager otpManager;

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	/** The mosipLogger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPAuthServiceImpl.class);

	/** The env. */
	@Autowired
	private Environment env;

	/**
	 * Validates generated OTP via OTP Manager.
	 *
	 * @param authreqdto the authreqdto
	 * @param refId      the ref id
	 * @return true - when the OTP is Valid.
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Override
	public AuthStatusInfo validateOtp(AuthRequestDTO authreqdto, String refId)
			throws IdAuthenticationBusinessException {
		boolean isOtpValid = false;
		String txnId = authreqdto.getTxnID();
		String tspCode = authreqdto.getMuaCode();
		Optional<String> otp = getOtpValue(authreqdto);
		if (otp.isPresent()) {
			boolean isValidRequest = validateTxnId(txnId, refId);
			if (isValidRequest) {
				mosipLogger.info("SESSION_ID", METHOD_VALIDATE_OTP, "Inside Validate Otp Request", "");
				String otpKey = OTPUtil.generateKey(env.getProperty("application.id"), refId, txnId, tspCode);
				String key = Optional.ofNullable(otpKey).orElseThrow(
						() -> new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_OTP_KEY));
				isOtpValid = otpManager.validateOtp(otp.get(), key);
			} else {
				mosipLogger.debug(DEAFULT_SESSSION_ID, METHOD_VALIDATE_OTP, "Inside Invalid Txn ID",
						getClass().toString());
				mosipLogger.error(DEAFULT_SESSSION_ID, "NA", "NA", "Key Invalid");
				throw new IdValidationFailedException(IdAuthenticationErrorConstants.INVALID_TXN_ID);
			}
		} else {
			// FIXME throw otp is not specified
		}

		return constructAuthStatusInfo(isOtpValid);
	}

	private Optional<String> getOtpValue(AuthRequestDTO authreqdto) {
		return Optional.ofNullable(authreqdto.getPinInfo())
				.flatMap(pinInfos -> 
						pinInfos.stream()
								.filter(pinInfo -> pinInfo.getType() != null && pinInfo.getType().equalsIgnoreCase(PinType.OTP.getType()))
								.findAny())
				.map(PinInfo::getValue);
	}

	/**
	 * Construct auth status info.
	 *
	 * @param isOtpValid the is otp valid
	 * @return the auth status info
	 */
	private static AuthStatusInfo constructAuthStatusInfo(boolean isOtpValid) {
		AuthStatusInfoBuilder statusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		statusInfoBuilder.setStatus(isOtpValid).addAuthUsageDataBits(AuthUsageDataBit.USED_OTP);

		if (isOtpValid) {
			statusInfoBuilder.addAuthUsageDataBits(AuthUsageDataBit.MATCHED_OTP);
		}

		return statusInfoBuilder.build();
	}

	/**
	 * Validates Transaction ID and Unique ID.
	 *
	 * @param txnId the txn id
	 * @param uIN   the u IN
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */

	public boolean validateTxnId(String txnId, String uIN) throws IdAuthenticationBusinessException {
		boolean isValidTxn = false;
		List<AutnTxn> authtxns = autntxnrepository.findAllByRequestTrnIdAndRefId(txnId, uIN);
		if (authtxns != null && !authtxns.isEmpty() && authtxns.get(0) != null) {
			isValidTxn = true;
		} else {
			isValidTxn = false;
		}
		return isValidTxn;
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
