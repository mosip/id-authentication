package org.mosip.kernel.auditmanager.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.mosip.kernel.auditmanager.constants.AuditErrorCodes;
import org.mosip.kernel.auditmanager.exception.MosipAuditManagerException;
import org.mosip.kernel.auditmanager.request.AuditRequest;

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
	 * Function to validate {@link AuditRequest}
	 * 
	 * @param auditRequest
	 *            The audit request
	 */
	public static void validateAuditRequest(AuditRequest auditRequest) {

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		Set<ConstraintViolation<AuditRequest>> violations = validator.validate(auditRequest);

		if (!violations.isEmpty()) {
			throw new MosipAuditManagerException(AuditErrorCodes.HANDLEREXCEPTION.getErrorCode(),
					AuditErrorCodes.HANDLEREXCEPTION.getErrorMessage());
		}
	}
}
