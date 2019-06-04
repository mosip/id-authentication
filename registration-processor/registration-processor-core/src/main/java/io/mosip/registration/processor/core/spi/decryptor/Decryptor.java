package io.mosip.registration.processor.core.spi.decryptor;

import java.io.InputStream;
import java.time.format.DateTimeParseException;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;

/**
 * Interface provide functionality for the packet decryption.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public interface Decryptor {

	/**
	 * This Method provide the functionality to decrypt packet
	 * 
	 * @param input
	 *            encrypted packet to be decrypted
	 * @return decrypted packet
	 * 
	 * @throws PacketDecryptionFailureException
	 *             if error occured while decrypting
	 * @throws ApisResourceAccessException
	 *             if error occured while
	 * @throws DateTimeParseException
	 *             if fail to parse date from registration id
	 */
	public InputStream decrypt(InputStream input, String registrationId)
			throws PacketDecryptionFailureException, ApisResourceAccessException;

}
