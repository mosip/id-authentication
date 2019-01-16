package io.mosip.authentication.service.impl.otpgen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Service implementation of OtpTriggerService.
 * 
 * @author Rakesh Roshan
 * @author Dineshkaruppiah Thiagarajan
 */
@Service
public class OTPServiceImpl implements OTPService {
	
	/** The otp manager. */
	@Autowired
	private OTPManager otpManager;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPServiceImpl.class);
	
	/**
	 * Generate otp.
	 *
	 * @param otpKey the otp key
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Override
	public String generateOtp(String otpKey) throws IdAuthenticationBusinessException {
		String otp = null;

		if (otpKey == null || otpKey.trim().isEmpty()) {
			return null;

		} else {
			otp = otpManager.generateOTP(otpKey);

			if (otp == null || otp.trim().isEmpty()) {
				mosipLogger.error("NA", "NA", "NA", "generated OTP is: " + otp);
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
			}
			
			mosipLogger.info("NA", "NA", "NA", " generated OTP is: " + otp);
		}

		return otp;
	}
}
