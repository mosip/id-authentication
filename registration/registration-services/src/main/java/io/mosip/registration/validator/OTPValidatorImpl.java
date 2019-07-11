package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.util.common.OTPManager;

/**
 * 
 * This class will validate the OTP entered by the user by calling the otp service  
 * 
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
		// TODO Auto-generated method stub
		 return false;
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.validator.AuthenticationBaseValidator#validate(java.lang.String, java.lang.String)
	 */
	@Override
	public AuthTokenDTO validate(String userId, String otp) {
		return otpManager.validateOTP(userId, otp);
	}

}
