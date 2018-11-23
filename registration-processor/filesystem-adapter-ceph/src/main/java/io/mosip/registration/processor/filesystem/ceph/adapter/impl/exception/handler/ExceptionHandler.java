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
			throw new InvalidConnectionParameters(PlatformErrorMessages.RPR_FAC_INVALID_CONNECTION_PARAMETERS.getMessage(),e);
		}
		else if(e.getStatusCode() == 404) {
			throw new PacketNotFoundException(PlatformErrorMessages.RPR_FAC_PACKET_NOT_AVAILABLE.getMessage(),e);
		}
	}
	public static void exceptionHandler(SdkClientException e) {
		throw new ConnectionUnavailableException(PlatformErrorMessages.RPR_FAC_CONNECTION_NOT_AVAILABLE.getMessage(),e);
	}

}
