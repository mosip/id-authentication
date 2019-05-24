package io.mosip.registration.service.security;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.security.impl.AuthenticationService;
import io.mosip.registration.validator.AuthenticationBaseValidator;

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

	private List<AuthenticationBaseValidator> authenticationBaseValidators;

	/* (non-Javadoc)
	 * @see io.mosip.registration.service.security.AuthenticationServiceImpl#authValidator(java.lang.String, io.mosip.registration.dto.AuthenticationValidatorDTO)
	 */
	@Override
	public Boolean authValidator(String validatorType, AuthenticationValidatorDTO authenticationValidatorDTO) {

		for (AuthenticationBaseValidator validator : authenticationBaseValidators) {
			if (validator.getClass().getName().toLowerCase().contains(validatorType.toLowerCase())) {
				return validator.validate(authenticationValidatorDTO);
			}
		}
		return false;
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

		UserDetail userDetail = loginService.getUserDetail(authenticationValidatorDTO.getUserId());
		// TO DO-- Yet to implement SSHA512
		/*HMACUtils.digestAsPlainTextWithSalt(authenticationValidatorDTO.getPassword().getBytes(),
				userDetail.getSalt().getBytes()).equals(userDetail)*/
		String hashPassword = null;

		// password hashing
		if (!(authenticationValidatorDTO.getPassword().isEmpty())) {
			byte[] bytePassword = authenticationValidatorDTO.getPassword().getBytes();
			hashPassword = HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytePassword));
		}
		if ("E2E488ECAF91897D71BEAC2589433898414FEEB140837284C690DFC26707B262"
				.equals(hashPassword)) {
			return RegistrationConstants.PWD_MATCH;
		} else {
			return RegistrationConstants.PWD_MISMATCH;
		}
	}

}
