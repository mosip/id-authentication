package io.mosip.kernel.filesystem.adapter.impl.exception.handler;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import io.mosip.kernel.filesystem.adapter.impl.exception.ConnectionUnavailableException;
import io.mosip.kernel.filesystem.adapter.impl.exception.InvalidConnectionParameters;
import io.mosip.kernel.filesystem.adapter.impl.exception.PacketNotFoundException;
import io.mosip.kernel.filesystem.adapter.impl.utils.PlatformErrorMessages;

/**
 * Global Exception handler.
 *
 * @author Pranav Kumar
 */
public class ExceptionHandler {

	/**
	 * Instantiates a new exception handler.
	 */
	private ExceptionHandler() {

	}

	/**
	 * Exception handler.
	 *
	 * @param e
	 *            the e
	 */
	public static void exceptionHandler(AmazonS3Exception e) {
		if (e.getStatusCode() == 403) {
			throw new InvalidConnectionParameters(
					PlatformErrorMessages.KER_FAC_INVALID_CONNECTION_PARAMETERS.getMessage(), e);
		} else if (e.getStatusCode() == 404) {
			throw new PacketNotFoundException(PlatformErrorMessages.KER_FAC_PACKET_NOT_AVAILABLE.getMessage(), e);
		}
	}

	/**
	 * Exception handler.
	 *
	 * @param e
	 *            the e
	 */
	public static void exceptionHandler(SdkClientException e) {
		throw new ConnectionUnavailableException(PlatformErrorMessages.KER_FAC_CONNECTION_NOT_AVAILABLE.getMessage(),
				e);
	}

}
