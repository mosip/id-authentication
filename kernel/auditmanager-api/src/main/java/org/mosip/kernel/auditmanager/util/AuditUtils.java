package org.mosip.kernel.auditmanager.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.mosip.kernel.auditmanager.constant.AuditErrorCodes;
import org.mosip.kernel.auditmanager.exception.MosipAuditManagerException;
import org.mosip.kernel.auditmanager.request.AuditRequestDto;

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

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		Set<ConstraintViolation<AuditRequestDto>> violations = validator.validate(auditRequest);

		if (!violations.isEmpty()) {
			throw new MosipAuditManagerException(AuditErrorCodes.HANDLEREXCEPTION.getErrorCode(),
					AuditErrorCodes.HANDLEREXCEPTION.getErrorMessage());
		}
	}
}
