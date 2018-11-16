package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.handler;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.ConnectionUnavailableException;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.InvalidConnectionParameters;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;

/**
 * Global Exception handler
 * 
 * @author Pranav Kumar
 *
 */
public class ExceptionHandler {
	
	private ExceptionHandler() {
		
	}
	
	public static void exceptionHandler(AmazonS3Exception e) {
		if(e.getStatusCode() == 403) {
			throw new InvalidConnectionParameters(PlatformErrorMessages.INVALID_CONNECTION_PARAMETERS.getValue());
		}
		else if(e.getStatusCode() == 404) {
			throw new PacketNotFoundException(PlatformErrorMessages.INVALID_PACKET_FILE_NAME.getValue());
		}
	}
	public static void exceptionHandler(SdkClientException e) {
		throw new ConnectionUnavailableException(PlatformErrorMessages.INVALID_CONNECTION_PATH.getValue());
	}

}
