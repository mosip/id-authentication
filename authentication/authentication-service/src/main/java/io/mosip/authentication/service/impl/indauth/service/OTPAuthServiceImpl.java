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
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

/**
 * Implementation for OTP Auth Service to authenticate OTP via OTP Manager
 * 
 * @author Dinesh Karuppiah.T
 */

@Service
public class OTPAuthServiceImpl implements OTPAuthService {

	private static final String METHOD_VALIDATE_OTP = "validateOtp";

	private static final String DEAFULT_SESSSION_ID = "sessionID";

	@Autowired
	OTPManager otpManager;

	@Autowired
	AutnTxnRepository autntxnrepository;

	@Autowired
	AuditRequestFactory auditreqfactory;

	private MosipLogger logger;

	@Autowired
	private Environment env;

	/**
	 * 
	 * @param idaRollingFileAppender
	 */

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	/**
	 * 
	 * Validates generated OTP via OTP Manager
	 * 
	 * @return true - when the OTP is Valid.
	 * 
	 */
	@Override
	public AuthStatusInfo validateOtp(AuthRequestDTO authreqdto, String refId) throws IdAuthenticationBusinessException {
		boolean isOtpValid = false;
		String txnId = authreqdto.getTxnID();
		String UIN = authreqdto.getId();
		String TSPCode = authreqdto.getMuaCode();
		String otp = authreqdto.getPii().getPinDTO().getValue();
		boolean isValidRequest = validateTxnId(txnId, UIN);
		if (isValidRequest) {
			// FIXME audit integration
			logger.info("SESSION_ID", METHOD_VALIDATE_OTP, "Inside Validate Otp Request", "");
			String OtpKey = OTPUtil.generateKey(env.getProperty("application.id"), refId, txnId, TSPCode);
			String key = Optional.ofNullable(OtpKey)
					.orElseThrow(() -> new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_OTP_KEY));
			isOtpValid = otpManager.validateOtp(otp, key);
		} else {
			logger.debug(DEAFULT_SESSSION_ID, METHOD_VALIDATE_OTP, "Inside Invalid Txn ID", getClass().toString());
			logger.error(DEAFULT_SESSSION_ID, "NA", "NA", "Key Invalid");
			throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_TXN_ID);
			
		}

		return constructAuthStatusInfo(isOtpValid);
	}

	private AuthStatusInfo constructAuthStatusInfo(boolean isOtpValid) {
		AuthStatusInfoBuilder statusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		statusInfoBuilder
				.setStatus(isOtpValid)
				.addAuthUsageDataBits(AuthUsageDataBit.USED_OTP);
		
		if(isOtpValid) {
			statusInfoBuilder.addAuthUsageDataBits(AuthUsageDataBit.MATCHED_OTP);
		}
		
		return statusInfoBuilder.build();
	}

	/**
	 * 
	 * Validates Transaction ID and Unique ID
	 * 
	 * @param txnId
	 * @param uIN
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */

	public boolean validateTxnId(String txnId, String uIN) throws IdAuthenticationBusinessException {
		boolean isValidTxn = false;
		List<AutnTxn> authtxns = autntxnrepository.findAllByRequestTxnIdAndUin(txnId, uIN);
		if (authtxns != null && authtxns.size() > 0 && authtxns.get(0) != null) {
			// FIXME audit integration
			isValidTxn = true;
		} else {
			// FIXME audit integration
			isValidTxn = false;
		}
		return isValidTxn;
	}

	/**
	 * Checks for Null or Empty
	 * 
	 * @param otpVal - OTP value
	 * @return true - When the otpVal is Not null or empty
	 * 
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
