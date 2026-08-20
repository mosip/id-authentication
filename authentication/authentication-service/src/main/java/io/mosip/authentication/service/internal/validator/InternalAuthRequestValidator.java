package io.mosip.authentication.service.internal.validator;

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
// NOT @Primary: AuthRequestValidator (this class's own parent) is already the single
// @Primary for the whole family (also covers KycAuthRequestValidator, KycExchangeRequestValidator,
// VciExchangeRequestValidator, IdentityKeyBindingRequestValidator). A second @Primary here would
// conflict with it for AuthRequestValidator-typed consumers (e.g. AuthController). Consumers that
// need this exact bean over AuthTxnValidator (its own subclass) use @Qualifier("internalAuthRequestValidator")
// instead - see InternalAuthController.
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
