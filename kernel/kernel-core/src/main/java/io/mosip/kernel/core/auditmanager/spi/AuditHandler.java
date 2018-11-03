package io.mosip.kernel.core.auditmanager.spi;

/**
 * Interface with function to write {@link AuditRequest}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface AuditHandler<T> {

	/**
	 * Function to write {@link AuditRequest}
	 * 
	 * @param auditRequest
	 *            The {@link AuditRequest}
	 * @return true - if {@link AuditRequest} is successfully written
	 */
	boolean addAudit(T auditRequest);

}