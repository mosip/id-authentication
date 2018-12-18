package io.mosip.registration.service.external;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to store the encrypted packet and acknowledgement receipt of the
 * Registration in local disk
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface StorageService {

	/**
	 * Writes the encrypted packet and the acknowledgement receipt to the local
	 * storage
	 * 
	 * @param registrationId
	 *            the id of the Registration
	 * @param packet
	 *            the encrypted packet data to be stored in local storage
	 * @param ackReceipt
	 *            the registration acknowledgement receipt to be stored in local
	 *            storage
	 * @return returns the file path where the files had been stored
	 * @throws RegBaseCheckedException
	 */
	String storeToDisk(String registrationId, byte[] packet, byte[] ackReceipt) throws RegBaseCheckedException;
}
