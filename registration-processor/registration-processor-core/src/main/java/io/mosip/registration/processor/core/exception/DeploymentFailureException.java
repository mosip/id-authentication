package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class DeploymentFailureException.
 */
public class DeploymentFailureException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new deployment failure exception.
	 */
	public DeploymentFailureException() {
		super();
	}

	/**
	 * Instantiates a new deployment failure exception.
	 *
	 * @param message
	 *            the message
	 */
	public DeploymentFailureException(String message) {
		super(PlatformErrorMessages.RPR_CMB_DEPLOYMENT_FAILURE.getCode(), message);
	}

	/**
	 * Instantiates a new deployment failure exception.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public DeploymentFailureException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_CMB_DEPLOYMENT_FAILURE.getCode() + EMPTY_SPACE, message, cause);
	}

}
