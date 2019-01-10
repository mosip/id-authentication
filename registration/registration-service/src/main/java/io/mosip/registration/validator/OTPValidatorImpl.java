package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.util.common.OTPManager;

/**
 * @author SaravanaKumar G
 *
 */
@Service("oTPValidatorImpl")
public class OTPValidatorImpl extends AuthenticationBaseValidator {

	@Autowired
	private OTPManager otpManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.validator.AuthenticationValidatorImplementation#
	 * validate(io.mosip.registration.dto.AuthenticationValidatorDTO)
	 */
	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		return otpManager.validateOTP(authenticationValidatorDTO.getUserId(), authenticationValidatorDTO.getOtp());
	}

}
