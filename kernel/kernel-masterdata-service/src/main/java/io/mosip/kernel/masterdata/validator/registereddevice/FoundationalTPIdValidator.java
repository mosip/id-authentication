package io.mosip.kernel.masterdata.validator.registereddevice;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.client.RestClientException;

import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.RegisteredDeviceErrorCode;
import io.mosip.kernel.masterdata.exception.RequestException;
import lombok.Data;

/**
 * To validate Status codes as per ISO:639-3 standard during creation and
 * updation of RegisteredDevice API
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
@Data
public class FoundationalTPIdValidator implements ConstraintValidator<ValidStatusCode, String> {

	private static final String REGISTERED = "registered";
	private static final String RETIRED = "retired";
	private static final String REVOKED = "revoked";

	private static final String STATUSARR[] = { REGISTERED, RETIRED, REVOKED };

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

				for (String string : STATUSARR) {
					if (statusCode.equalsIgnoreCase(string)) {
						return true;
					}
				}
			} catch (RestClientException e) {
				throw new RequestException(RegisteredDeviceErrorCode.STATUS_CODE_VALIDATION_EXCEPTION.getErrorCode(),
						RegisteredDeviceErrorCode.STATUS_CODE_VALIDATION_EXCEPTION.getErrorMessage() + " "
								+ e.getMessage());
			}
			return false;
		}
	}
}