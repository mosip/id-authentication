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
public class CertificateLevelValidator implements ConstraintValidator<ValidCertificateLevel, String> {

	private static final String L0 = "L0";
	private static final String L1 = "L1";
	

	private static final String CERTIFICATELEVELARR[] = {L0, L1};

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
	 * javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String certificationLevel, ConstraintValidatorContext context) {
		if (EmptyCheckUtils.isNullEmpty(certificationLevel) || certificationLevel.trim().length() > 3 ) {
			return false;
		} else {
			try {

				for (String string : CERTIFICATELEVELARR) {
					if (certificationLevel.equals(string) || certificationLevel.equals("all")) {
						return true;
					}
				}
			} catch (RestClientException e) {
				throw new RequestException(RegisteredDeviceErrorCode.CERTIFICATION_LEVEL_VALIDATION_EXCEPTION.getErrorCode(),
						RegisteredDeviceErrorCode.CERTIFICATION_LEVEL_VALIDATION_EXCEPTION.getErrorMessage() + " "
								+ e.getMessage());
			}
			return false;
		}
	}
}
