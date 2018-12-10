package io.mosip.kernel.auditmanager.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import io.mosip.kernel.auditmanager.constant.AuditErrorCode;
import io.mosip.kernel.auditmanager.dto.AuditRequestDto;
import io.mosip.kernel.auditmanager.exception.AuditManagerException;

/**
 * Utility class for Audit Manager
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class AuditUtils {

	/**
	 * Private constructor for AuditUtils
	 */
	private AuditUtils() {

	}

	/**
	 * Function to validate {@link AuditRequestDto}
	 * 
	 * @param auditRequest
	 *            The audit request
	 */
	public static void validateAuditRequest(AuditRequestDto auditRequest) {
		ValidatorFactory factory = null;
		try {
			factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();

			Set<ConstraintViolation<AuditRequestDto>> violations = validator.validate(auditRequest);

			if (!violations.isEmpty()) {
				throw new AuditManagerException(AuditErrorCode.HANDLEREXCEPTION.getErrorCode(),
						AuditErrorCode.HANDLEREXCEPTION.getErrorMessage());
			}
		} finally {
			if (factory != null) {
				factory.close();
			}
		}
	}
}
