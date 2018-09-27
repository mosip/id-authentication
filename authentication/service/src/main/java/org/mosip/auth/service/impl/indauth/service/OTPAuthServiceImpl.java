package org.mosip.auth.service.impl.indauth.service;

import java.util.List;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.spi.indauth.service.OTPAuthService;
import org.mosip.auth.core.util.OTPUtil;
import org.mosip.auth.service.dao.AutnTxnRepository;
import org.mosip.auth.service.entity.AutnTxn;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.integration.OTPManager;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Implementation for OTP Auth Service to authenticate OTP via OTP Manager
 * 
 * @author Dinesh Karuppiah.T
 */

@Service
public class OTPAuthServiceImpl implements OTPAuthService {

	@Autowired
	OTPManager otpManager;

	@Autowired
	AutnTxnRepository autntxnrepository;

	@Autowired
	AuditRequestFactory auditreqfactory;

	private MosipLogger LOGGER;

	@Autowired
	private Environment env;

	/**
	 * 
	 * @param idaRollingFileAppender
	 */

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	/**
	 * 
	 * Validates generated OTP via OTP Manager
	 * 
	 * @return true - when the OTP is Valid.
	 * 
	 */
	@Override
	public boolean validateOtp(AuthRequestDTO authreqdto, String refId) throws IdAuthenticationBusinessException {
		boolean isOtpValid = false;
		try {
			String txnId = authreqdto.getTxnID();
			String UIN = authreqdto.getId();
			String TSPCode = authreqdto.getMuaCode();
			String otp = authreqdto.getPinDTO().getValue();
			boolean isValidRequest = validateTxnId(txnId, UIN);
			if (isValidRequest) {
				// FIXME audit integration
				System.err.println(env.getProperty("application.id"));
				LOGGER.info("SESSION_ID", "validateOtp", "Inside Validate Otp Request", "");
				String key = OTPUtil.generateKey(env.getProperty("application.id"), refId, txnId, TSPCode);
				if (!isEmpty(key)) {
					isOtpValid = otpManager.validateOtp(otp, key);
				} else {
					LOGGER.debug("SESSSION_ID", "validateOtp", "Inside key Null", getClass().toString());
					LOGGER.error("SESSSION_ID", "NA", "NA", "Key Invalid");
					throw new IDDataValidationException(IdAuthenticationErrorConstants.KEY_INVALID);
				}
			}
		} catch (IdAuthenticationBusinessException e) {
			LOGGER.debug("SESSSION_ID", "validateOtp", "Inside Invalid Request", getClass().toString());
			LOGGER.error("SESSSION_ID", "NA", "NA", "Arguments Invalid");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_VALDIATION_REQUEST_FAILED,
					e);
		}

		return isOtpValid;
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
			throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_TXN_ID);
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
