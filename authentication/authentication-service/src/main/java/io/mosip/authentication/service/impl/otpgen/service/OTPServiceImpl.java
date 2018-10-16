package io.mosip.authentication.service.impl.otpgen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.dto.AuditRequestDto;
import io.mosip.authentication.core.util.dto.AuditResponseDto;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;

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
						IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
			}
			
			LOGGER.info("NA", "NA", "NA", " generated OTP is: " + otp);
		}

		return otp;
	}

	public void audit() throws IDDataValidationException  {
		//TODO Update audit details
		AuditRequestDto auditRequest = auditRequestFactory.buildRequest(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");

		RestRequestDTO restRequest = restRequestFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);

		restHelper.requestAsync(restRequest); 
	}
}
