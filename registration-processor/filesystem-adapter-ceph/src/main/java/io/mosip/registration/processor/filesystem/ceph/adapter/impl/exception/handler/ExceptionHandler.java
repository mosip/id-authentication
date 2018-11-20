package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.handler;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.ConnectionUnavailableException;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.InvalidConnectionParameters;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.utils.ExceptionMessages;

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
			throw new InvalidConnectionParameters(ExceptionMessages.INVALID_CONNECTION_CREDENTIALS.name(),e);
		}
		else if(e.getStatusCode() == 404) {
			throw new PacketNotFoundException(ExceptionMessages.INVALID_PACKET_FILE_NAME.name(),e);
		}
	}
	public static void exceptionHandler(SdkClientException e) {
		throw new ConnectionUnavailableException(ExceptionMessages.INVALID_CONNECTION_PATH.name(), e);
	}

}
