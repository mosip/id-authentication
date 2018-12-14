package io.mosip.registration.validator;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.OtpValidatorResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

/**
 * @author SaravanaKumar G
 *
 */
@Component
public class OTPValidator extends AuthenticationValidatorImplementation {

	@Autowired
	ServiceDelegateUtil serviceDelegateUtil;

	/* (non-Javadoc)
	 * @see io.mosip.registration.validator.AuthenticationValidatorImplementation#validate(io.mosip.registration.dto.AuthenticationValidatorDTO)
	 */
	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		boolean status = false;
		OtpValidatorResponseDTO otpValidatorResponseDto = null;

		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, authenticationValidatorDTO.getUserId());
		requestParamMap.put(RegistrationConstants.OTP_GENERATED, authenticationValidatorDTO.getOtp());

		try {
			// Obtain otpValidatorResponseDto from service delegate util
			otpValidatorResponseDto = (OtpValidatorResponseDTO) serviceDelegateUtil
					.get(RegistrationConstants.OTP_VALIDATOR_SERVICE_NAME, requestParamMap);
			if (otpValidatorResponseDto != null && otpValidatorResponseDto.getStatus() != null
					&& RegistrationConstants.OTP_VALIDATION_SUCCESS.equals(otpValidatorResponseDto.getStatus())) {

				status = true;

			} else {
				status = false;
			}

		} catch (RegBaseCheckedException | HttpClientErrorException | HttpServerErrorException | SocketTimeoutException
				| ResourceAccessException exception) {
			status = false;
		}

		return status;
	}

}
