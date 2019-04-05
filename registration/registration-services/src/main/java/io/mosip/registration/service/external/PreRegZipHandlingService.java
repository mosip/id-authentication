package io.mosip.registration.service.external;

import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Handles the pre-registration packet zip files
 * 
 * @author balamurugan ramamoorthy
 * @since 1.0.0
 *
 */
public interface PreRegZipHandlingService {

	/**
	 * This method is used to extract the pre registration packet zip file and reads
	 * the content
	 * 
	 * @param preREgZipFile
	 *            - the pre registration zip file
	 * @return RegistrationDTO - This holds the extracted demographic data and other
	 *         values
	 * @throws RegBaseCheckedException
	 *             - holds the checked exception
	 */
	RegistrationDTO extractPreRegZipFile(byte[] preREgZipFile) throws RegBaseCheckedException;

	/**
	 * This method is used to encrypt the pre registration packet and save it into
	 * the disk
	 * 
	 * @param PreRegistrationId
	 *            - pre registration id
	 * @param preRegPacket
	 *            - pre reg packet in bytes
	 * @return PreRegistrationDTO - pre reg dto holds the pre reg data
	 * @throws RegBaseCheckedException
	 *             - holds the checked exceptions
	 */
	PreRegistrationDTO encryptAndSavePreRegPacket(String PreRegistrationId, byte[] preRegPacket)
			throws RegBaseCheckedException;

	/**
	 * This method is used to store the encrypted packet into to the configured disk
	 * location
	 * 
	 * @param PreRegistrationId
	 *            - pre reg id
	 * @param encryptedPacket
	 *            - pre reg encrypted packet in bytes
	 * @return String - pre reg packet file path
	 * @throws RegBaseCheckedException
	 *             - holds the checked exceptions
	 */
	String storePreRegPacketToDisk(String PreRegistrationId, byte[] encryptedPacket) throws RegBaseCheckedException;

	/**
	 * This method is used to decrypt the pre registration packet using the
	 * symmetric key
	 * 
	 * @param symmetricKey
	 *            - key to decrypt the pre reg packet
	 * @param encryptedPacket
	 *            - pre reg encrypted packet in bytes
	 * @return byte[] - decrypted pre reg packet
	 */
	byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket);

}