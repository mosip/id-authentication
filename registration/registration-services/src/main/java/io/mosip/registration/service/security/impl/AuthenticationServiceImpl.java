package io.mosip.registration.service.security.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.security.AuthenticationService;
import io.mosip.registration.validator.AuthenticationBaseValidator;
import io.mosip.registration.validator.OTPValidatorImpl;

/**
 * Service class for Authentication
 * 
 * @author SaravanaKumar G
 *
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AuthenticationServiceImpl.class);
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private OTPValidatorImpl otpValidatorImpl;

	private List<AuthenticationBaseValidator> authenticationBaseValidators;

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.security.AuthenticationServiceImpl#authValidator(java.lang.String, io.mosip.registration.dto.AuthenticationValidatorDTO)
	 */
	public Boolean authValidator(String validatorType, AuthenticationValidatorDTO authenticationValidatorDTO) {
	
		for (AuthenticationBaseValidator validator : authenticationBaseValidators) {
			if (validator.getClass().getName().toLowerCase().contains(validatorType.toLowerCase())) {
				return validator.validate(authenticationValidatorDTO);
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.registration.service.security.AuthenticationServiceImpl#authValidator(java.lang.String, java.lang.String, java.lang.String)
	 */
	public AuthTokenDTO authValidator(String validatorType, String userId, String otp, boolean haveToSaveAuthToken) {		
		return otpValidatorImpl.validate(userId, otp, haveToSaveAuthToken);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.security.AuthenticationServiceImpl#setAuthenticationBaseValidator(java.util.List)
	 */
	@Override
	@Autowired
	public void setAuthenticationBaseValidator(List<AuthenticationBaseValidator> authBaseValidators) {
		this.authenticationBaseValidators = authBaseValidators;
	}
	
	/**
	 * to validate the password and send appropriate message to display.
	 *
	 * @param authenticationValidatorDTO
	 *            - DTO which contains the username and password entered by the user
	 * @return appropriate message after validation
	 */
	public String validatePassword(AuthenticationValidatorDTO authenticationValidatorDTO) {
		LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				"Validating credentials using database");

		UserDTO userDTO = loginService.getUserDetail(authenticationValidatorDTO.getUserId());
		try {

			if (null != userDTO && null != userDTO.getSalt()
					&& HMACUtils
							.digestAsPlainTextWithSalt(authenticationValidatorDTO.getPassword().getBytes(),
									CryptoUtil.decodeBase64(userDTO.getSalt()))
							.equals(userDTO.getUserPassword().getPwd())) {
				return RegistrationConstants.PWD_MATCH;
			} else {
				return RegistrationConstants.PWD_MISMATCH;
			}

		} catch (RuntimeException runtimeException) {

			LOGGER.info("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(runtimeException));

			return RegistrationConstants.PWD_MISMATCH;
		}
	}

}
