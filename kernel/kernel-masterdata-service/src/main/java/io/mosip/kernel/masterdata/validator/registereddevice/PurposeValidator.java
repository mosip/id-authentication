package io.mosip.kernel.masterdata.validator.registereddevice;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.client.RestClientException;

import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.RegisteredDeviceErrorCode;
import io.mosip.kernel.masterdata.exception.RequestException;
import lombok.Data;

/**
 * To validate Purpose as per ISO:639-3 standard during creation and updation of
 * RegisteredDevice API
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
@Data
public class PurposeValidator implements ConstraintValidator<ValidPurpose, String> {

	private static final String REGISTRATION = "Registration";
	private static final String AUTH = "Auth";

	private static final String PURPOSEARR[] = { REGISTRATION, AUTH };

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
	 * javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String statusCode, ConstraintValidatorContext context) {
		if (EmptyCheckUtils.isNullEmpty(statusCode)) {
			return false;
		} else {
			try {

				for (String string : PURPOSEARR) {
					if (statusCode.equalsIgnoreCase(string)) {
						return true;
					}
				}
			} catch (RestClientException e) {
				throw new RequestException(RegisteredDeviceErrorCode.PURPOSEVALIDATION_EXCEPTION.getErrorCode(),
						RegisteredDeviceErrorCode.PURPOSEVALIDATION_EXCEPTION.getErrorMessage() + " " + e.getMessage());
			}
			return false;
		}
	}
}