package io.mosip.kernel.core.auditmanager.spi;

/**
 * Interface with function to write AuditRequest
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface AuditHandler<T> {

	/**
	 * Function to write AuditRequest
	 * 
	 * @param auditRequest
	 *            The AuditRequest
	 * @return true - if AuditRequest is successfully written
	 */
	boolean addAudit(T auditRequest);

}