package io.mosip.registration.processor.core.status.util;

/**
 * 
 * @author Girish Yarru
 * @since 1.0
 *
 */
public class TrimExceptionMessage {

	private static final int MESSAGE_LENGTH = 199;

	public String trimExceptionMessage(String exceptionMessage) {
		return exceptionMessage.substring(0, Math.min(exceptionMessage.length(), MESSAGE_LENGTH));

	}

}
