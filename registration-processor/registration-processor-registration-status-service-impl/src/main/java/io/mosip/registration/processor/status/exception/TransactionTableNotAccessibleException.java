package io.mosip.registration.processor.status.exception;
	
import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;


/**
 * The Class TransactionTableNotAccessibleException.
 */
public class TransactionTableNotAccessibleException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new transaction table not accessible exception.
	 */
	public TransactionTableNotAccessibleException() {
		super();
	}

	/**
	 * Instantiates a new transaction table not accessible exception.
	 *
	 * @param message the message
	 */
	public TransactionTableNotAccessibleException(String message) {
		super(PlatformErrorMessages.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE.getCode(), message);
	}

	/**
	 * Instantiates a new transaction table not accessible exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public TransactionTableNotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE.getCode() + EMPTY_SPACE, message, cause);
	}
}