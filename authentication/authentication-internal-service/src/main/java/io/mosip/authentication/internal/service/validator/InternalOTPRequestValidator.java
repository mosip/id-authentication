package io.mosip.authentication.internal.service.validator;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.validator.OTPRequestValidator;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;

/**
 * Validator for internal OTP request
 * 
 * @author Loganathan Sekar
 *
 */
@Component
public class InternalOTPRequestValidator extends OTPRequestValidator {
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.common.service.validator.IdAuthValidator#
	 * getAllowedIdTypesConfigKey()
	 */
	@Override
	protected String getAllowedIdTypesConfigKey() {
		return IdAuthConfigKeyConstants.INTERNAL_AUTH_ALLOWED_IDTYPE;
	}
}
