package org.mosip.auth.service.impl.otpgen.service;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.spi.otpgen.service.OTPService;
import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.auth.core.util.dto.AuditResponseDto;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.auth.service.integration.OTPManager;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation of OtpTriggerService.
 * 
 * @author Rakesh Roshan
 * @author Dineshkaruppiah Thiagarajan
 */
@Service
public class OTPServiceImpl implements OTPService {
	
	@Autowired
	private RestHelper restHelper;

	@Autowired
	OTPManager otpManager;

	private MosipLogger LOGGER;
	
	@Autowired
	RestRequestFactory restRequestFactory;
	
	@Autowired
	AuditRequestFactory auditRequestFactory;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}

	/**
	 * 
	 * @param otpKey
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	@Override
	public String generateOtp(String otpKey) throws IdAuthenticationBusinessException {
		String otp = null;

		if (otpKey == null || otpKey.trim().isEmpty()) {
			return null;

		} else {
			otp = otpManager.generateOTP(otpKey);

			if (otp == null || otp.trim().isEmpty()) {
				LOGGER.error("NA", "NA", "NA", "generated OTP is: " + otp);
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED);
			}
			
			LOGGER.info("NA", "NA", "NA", " generated OTP is: " + otp);
		}

		return otp;
	}

	public void audit() throws IDDataValidationException  {
		//TODO Update audit details
		AuditRequestDto auditRequest = auditRequestFactory.buildRequest("moduleId", "description");

		RestRequestDTO restRequest = restRequestFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restHelper.requestAsync(restRequest); 
	}
}
