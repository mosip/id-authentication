package io.mosip.authentication.internal.service.validator;

import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;

/**
 * Validator for internal authentication request
 * 
 * @author Prem Kumar
 *
 */
@Component
public class InternalAuthRequestValidator extends AuthRequestValidator {

	/** The Final Constant For allowed Internal auth type */
	private static final String INTERNAL_ALLOWED_AUTH_TYPE = "internal.auth.types.allowed";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(AuthRequestDTO.class);
	}
	

	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.validator.AuthRequestValidator#getAllowedAuthTypeProperty()
	 */
	@Override
	public String getAllowedAuthTypeProperty() {
		return INTERNAL_ALLOWED_AUTH_TYPE;
	}

}
