package io.mosip.registration.validator;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_VALIDATOR;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.device.iris.IrisFacade;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.entity.UserBiometric;

@Service("irisValidator")
public class IrisValidatorImpl extends AuthenticationBaseValidator{
	
	private static final Logger LOGGER = AppConfig.getLogger(IrisValidatorImpl.class);
	
	@Autowired
	private RegistrationUserDetailDAO registrationUserDetailDAO;

	@Autowired
	private IrisFacade irisFacade;

	/**
	 * Validate the Iris with the AuthenticationValidatorDTO as input
	 */
	@Override
	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		
		LOGGER.debug(LOG_REG_IRIS_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"Stubbing iris details for user registration");
		
		List<UserBiometric> userIrisDetails = registrationUserDetailDAO
				.getUserSpecificBioDetails(authenticationValidatorDTO.getUserId(), RegistrationConstants.VALIDATION_TYPE_IRIS);
		
		LOGGER.debug(LOG_REG_IRIS_VALIDATOR, APPLICATION_NAME, APPLICATION_ID,
				"validating iris details for user registration");
		
		return irisFacade.validateIris(authenticationValidatorDTO.getIrisDetails().get(RegistrationConstants.PARAM_ZERO), userIrisDetails);
	}

}
