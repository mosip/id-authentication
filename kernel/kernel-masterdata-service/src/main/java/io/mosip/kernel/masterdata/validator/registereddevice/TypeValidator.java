package io.mosip.kernel.masterdata.validator.registereddevice;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.client.RestClientException;

import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.RegisteredDeviceErrorCode;
import io.mosip.kernel.masterdata.dto.RegisteredDevicePostReqDto;
import io.mosip.kernel.masterdata.exception.RequestException;
import lombok.Data;

/**
 * To validate Type value as per ISO:639-3 standard during creation and updation
 * of RegisteredDevice API
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
@Data
public class TypeValidator implements ConstraintValidator<ValidType, String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
	 * javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String type, ConstraintValidatorContext context) {
		if (EmptyCheckUtils.isNullEmpty(type)) {
			return false;
		} else {
			try {

				for (String string : RegisteredDeviceConstant.TYPEARR) {
					if (type.equalsIgnoreCase(string)) {
						return true;
					}
				}
			} catch (RestClientException e) {
				throw new RequestException(RegisteredDeviceErrorCode.TYPE_VALIDATION_EXCEPTION.getErrorCode(),
						RegisteredDeviceErrorCode.TYPE_VALIDATION_EXCEPTION.getErrorMessage() + " " + e.getMessage());
			}
			return false;
		}
	}
}
