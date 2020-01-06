package io.mosip.registration.processor.packet.manager.decryptor;

import java.io.InputStream;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;

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
