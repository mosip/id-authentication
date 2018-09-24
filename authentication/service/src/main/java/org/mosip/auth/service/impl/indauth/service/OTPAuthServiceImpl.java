package org.mosip.auth.service.impl.indauth.service;

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
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
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
			String UIN = authreqdto.getUniqueID();
			String TSPCode = authreqdto.getAuaCode();
			String otp = authreqdto.getPinDTO().getPinValue();
			boolean isValidRequest = validateTxnId(txnId, UIN);
			if (isValidRequest) {
				// FIXME audit integration
				LOGGER.info("SESSION_ID", "validateOtp", "Inside Validate Otp Request",
						OTPAuthServiceImpl.class.getName());
				//FIXME get from property
				String key = OTPUtil.generateKey("IDA", refId, txnId, TSPCode);
				// TODO IDA appId should be read from properties file
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

	private boolean validateTxnId(String txnId, String uIN) throws IdAuthenticationBusinessException {
		boolean isValidTxn = false;
		AutnTxn authtxn = autntxnrepository.findByRequestTxnIdAndUin(txnId, uIN);
		if (authtxn != null) {
			// FIXME audit integration
			isValidTxn = true;
		} else {
			// FIXME audit integration
			throw new IDDataValidationException(IdAuthenticationErrorConstants.TXNID_INVALID);
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
