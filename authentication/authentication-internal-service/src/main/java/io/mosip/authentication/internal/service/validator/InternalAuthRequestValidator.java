package io.mosip.authentication.internal.service.validator;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;

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
		return EnvUtil.getInternalAllowedAuthType();
	}
	
	@Override
	protected String getAllowedIdTypesConfigVal() {
		return EnvUtil.getInternalAllowedIdTypes();
	}

	@Override
	protected int getMaxFingerCount() {
		return FINGERPRINT_COUNT;
	}
	
	protected void validateDigitalIdTimestamp(DigitalId digitalId, Errors errors, String format) {
		// Skip for internal auth
		
	}
	
	@Override
	protected boolean nullCheckDigitalIdAndTimestamp(DigitalId digitalId, Errors errors, String field) {
		// Skip for internal auth
		return false;
	}
	
	@Override
	protected void validateSuccessiveDigitalIdTimestamp(List<BioIdentityInfoDTO> biometrics, Errors errors, int index,
			BioIdentityInfoDTO bioIdentityInfoDTO, Long allowedTimeDiffInSeconds) {
		// Skip for internal auth
	}

}
