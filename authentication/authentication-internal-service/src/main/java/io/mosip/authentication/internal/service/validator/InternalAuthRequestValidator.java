package io.mosip.authentication.internal.service.validator;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;

/**
 * Validator for internal authentication request
 * 
 * @author Prem Kumar
 *
 */
@Component
public class InternalAuthRequestValidator extends AuthRequestValidator {

	private static final int FINGERPRINT_COUNT = 10;

	/* (non-Javadoc)
	 * @see io.mosip.authentication.common.service.validator.AuthRequestValidator#getAllowedAuthTypeProperty()
	 */
	@Override
	public String getAllowedAuthTypeProperty() {
		return IdAuthConfigKeyConstants.INTERNAL_ALLOWED_AUTH_TYPE;
	}
	
	@Override
	protected String getAllowedIdTypesConfigKey() {
		return IdAuthConfigKeyConstants.INTERNAL_AUTH_ALLOWED_IDTYPE;
	}

	@Override
	protected int getMaxFingerCount() {
		return FINGERPRINT_COUNT;
	}
	
	protected void isPartnerIdHotlisted(Optional<Object> metadata, Errors errors) {
		//Skipping partner id for internal auth
	}
	
	protected void isDeviceProviderHotlisted(List<BioIdentityInfoDTO> biometrics, Errors errors) {
		//Skipping partner id for internal auth
	}
	
	protected void isDevicesHotlisted(List<BioIdentityInfoDTO> biometrics, Errors errors) {
		//Skipping partner id for internal auth
	}

}
