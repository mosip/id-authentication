package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.handler;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import io.mosip.registration.processor.core.exception.util.RPRPlatformErrorMessages;
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
			throw new InvalidConnectionParameters(RPRPlatformErrorMessages.INVALID_CONNECTION_PARAMETERS.getValue());
		}
		else if(e.getStatusCode() == 404) {
			throw new PacketNotFoundException(RPRPlatformErrorMessages.INVALID_PACKET_FILE_NAME.getValue());
		}
	}
	public static void exceptionHandler(SdkClientException e) {
		throw new ConnectionUnavailableException(RPRPlatformErrorMessages.INVALID_CONNECTION_PATH.getValue());
	}

}
