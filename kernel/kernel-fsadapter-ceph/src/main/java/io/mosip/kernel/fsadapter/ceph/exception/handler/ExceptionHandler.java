package io.mosip.kernel.fsadapter.ceph.exception.handler;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.fsadapter.ceph.constant.PlatformErrorMessages;

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
	 * @param e the e
	 */
	public static void exceptionHandler(AmazonS3Exception e) {
		if (e.getStatusCode() == 403) {
			throw new FSAdapterException(PlatformErrorMessages.RPR_FAC_INVALID_CONNECTION_PARAMETERS.getErrorCode(),
					PlatformErrorMessages.RPR_FAC_INVALID_CONNECTION_PARAMETERS.getMessage(), e);
		} else if (e.getStatusCode() == 404) {
			throw new FSAdapterException(PlatformErrorMessages.RPR_FAC_PACKET_NOT_AVAILABLE.getErrorCode(),
					PlatformErrorMessages.RPR_FAC_PACKET_NOT_AVAILABLE.getMessage(), e);
		}
	}

	/**
	 * Exception handler.
	 *
	 * @param e the e
	 */
	public static void exceptionHandler(SdkClientException e) {
		throw new FSAdapterException(PlatformErrorMessages.RPR_FAC_CONNECTION_NOT_AVAILABLE.getErrorCode(),
				PlatformErrorMessages.RPR_FAC_CONNECTION_NOT_AVAILABLE.getMessage(), e);
	}

}
