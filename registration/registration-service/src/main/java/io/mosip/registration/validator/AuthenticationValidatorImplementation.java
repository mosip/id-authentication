package io.mosip.registration.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.RegistrationUserDetailDAO;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.entity.RegistrationUserDetail;

@Component
public abstract class AuthenticationValidatorImplementation {
	protected String fingerPrintType;

	protected RegistrationUserDetail registrationUserDetail;

	@Autowired
	protected FingerprintValidator fingerprintValidator;

	@Autowired
	private RegistrationUserDetailDAO registrationUserDetailDAO;

	public boolean validate(AuthenticationValidatorDTO authenticationValidatorDTO) {
		return fingerprintValidator.validate(authenticationValidatorDTO);
	}

	public String validatePassword(AuthenticationValidatorDTO authenticationValidatorDTO) {
		RegistrationUserDetail userDetail = registrationUserDetailDAO
				.getUserDetail(authenticationValidatorDTO.getUserId());

		if (userDetail == null) {
			return RegistrationConstants.USER_NOT_ONBOARDED;
		} else if (userDetail.getRegistrationUserPassword().getPwd().equals(authenticationValidatorDTO.getPassword())) {
			return RegistrationConstants.PWD_MATCH;
		} else {
			return RegistrationConstants.PWD_MISMATCH;
		}
	}

	public String getFingerPrintType() {
		return fingerPrintType;
	}

	public void setFingerPrintType(String fingerPrintType) {
		this.fingerPrintType = fingerPrintType;
	}

}
