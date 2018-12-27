package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.entity.RegistrationUserDetail;

/**
 * @author Saravanakumar G
 *
 */
public abstract class AuthenticationBaseValidator {
	/**
	 * It will hold the value of either single or multiple fingers
	 */
	protected String fingerPrintType;

	protected RegistrationUserDetail registrationUserDetail;

	@Autowired
	protected FingerprintValidatorImpl fingerprintValidator;

	/**
	 * Validate the fingerprint with the Database
	 * @param authenticationValidatorDTO
	 * @return
	 */
	public abstract boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO);

	/**
	 * It will return the fingerprint type
	 * @return
	 */
	public String getFingerPrintType() {
		return fingerPrintType;
	}

	/**
	 * It will set the fingerprint type
	 * @param fingerPrintType
	 */
	public void setFingerPrintType(String fingerPrintType) {
		this.fingerPrintType = fingerPrintType;
	}

}
