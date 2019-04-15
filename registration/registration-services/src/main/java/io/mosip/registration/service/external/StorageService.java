package io.mosip.registration.service.external;

import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to store the encrypted packet of the {@link Registration} in
 * configured location in local disk
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface StorageService {

	/**
	 * Writes the encrypted packet to the configured local storage
	 * 
	 * @param registrationId
	 *            the id of the {@link Registration}
	 * @param packet
	 *            the encrypted packet data to be stored in local storage
	 * @return the file path where the files had been stored
	 * @throws RegBaseCheckedException
	 *             any exception while saving the encrypted packet
	 */
	String storeToDisk(String registrationId, byte[] packet) throws RegBaseCheckedException;
}
