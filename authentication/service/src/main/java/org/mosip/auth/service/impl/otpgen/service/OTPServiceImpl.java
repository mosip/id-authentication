package org.mosip.auth.service.impl.otpgen.service;

import org.mosip.auth.core.constant.AuditServicesConstants;
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
import org.mosip.auth.service.integration.OTPManager;
import org.mosip.auth.service.util.RestUtil;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service implementation of OtpTriggerService.
 * 
 * @author Rakesh Roshan
 */
@Service
public class OTPServiceImpl implements OTPService {

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

	@Override
	public String generateOtp(String otpKey) throws IdAuthenticationBusinessException {
		String otp = null;

		if (otpKey.isEmpty() || otpKey.length() == 0 || otpKey == null) {
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

		RestUtil.requestAsync(restRequest); 
	}
}
