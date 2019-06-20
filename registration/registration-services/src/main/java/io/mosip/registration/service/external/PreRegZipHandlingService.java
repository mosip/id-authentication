package io.mosip.registration.service.external;

import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to handles the Pre-Registration Packet data.
 * 
 * @author balamurugan ramamoorthy
 * @since 1.0.0
 */
public interface PreRegZipHandlingService {

	/**
	 * This method is used to extract the pre-registration packet zip file and
	 * converts to {@link RegistrationDTO}
	 * 
	 * <p>
	 * The Pre-Registration packet contains the Identity JSON and documents
	 * </p>
	 * 
	 * <p>
	 * This {@link RegistrationDTO} object will be returned
	 * </p>
	 * 
	 * @param preRegZipFile
	 *            - the pre-registration zip file which has to be extracted into
	 *            {@link RegistrationDTO}
	 * @return RegistrationDTO - This holds the extracted demographic data and other
	 *         values
	 * @throws RegBaseCheckedException
	 *             - holds the checked exception
	 */
	RegistrationDTO extractPreRegZipFile(byte[] preRegZipFile) throws RegBaseCheckedException;

	/**
	 * This method is used to encrypt the pre-registration packet and save it into
	 * the disk
	 * 
	 * <p>
	 * This method internally uses {@link MosipEncryptor} component for encryption
	 * </p>
	 * 
	 * <p>
	 * The encrypted Pre-Registration packet will be saved into the configured
	 * location in the local system
	 * </p>
	 * <p>
	 * The encrypted Pre-Registration packet will be saved with the name as input
	 * PreRegistrationId
	 * </p>
	 * 
	 * <p>
	 * The file path where the encrypted Pre-Registration Packet stored in local
	 * storage and key used for encryption will be stored in the
	 * {@link PreRegistrationList} table
	 * </p>
	 * 
	 * @param preRegistrationId
	 *            - the Pre-Registration Id. This will be name of the encrypted
	 *            packet stored in local system
	 * @param preRegPacket
	 *            - Pre-Registration packet in bytes
	 * @return {@link PreRegistrationDTO} object holding the Pre-Registration data
	 * @throws RegBaseCheckedException
	 *             - holds the checked exceptions
	 */
	PreRegistrationDTO encryptAndSavePreRegPacket(String preRegistrationId, byte[] preRegPacket)
			throws RegBaseCheckedException;

	/**
	 * This method is used to store the encrypted Pre-Registration packet into to
	 * the configured disk location
	 * 
	 * <p>
	 * The encrypted Pre-Registration packet will be saved with the name as input
	 * PreRegistrationId
	 * </p>
	 * 
	 * @param preRegistrationId
	 *            - the Pre-Registration Id. This will be name of the encrypted
	 *            packet stored in local system
	 * @param encryptedPacket
	 *            - encrypted Pre-Registartion packet in bytes
	 * @return {@link String} object specifying local system path where
	 *         Pre-Registration packet has been stored
	 * @throws RegBaseCheckedException
	 *             - holds the checked exceptions
	 */
	String storePreRegPacketToDisk(String preRegistrationId, byte[] encryptedPacket) throws RegBaseCheckedException;

	/**
	 * This method is used to decrypt the encrypted Pre-Registration packet using
	 * the symmetric key
	 * 
	 * <p>This method internally uses {@link MosipEncryptor} component for decryption</p>
	 * 
	 * @param symmetricKey
	 *            - key to decrypt the Pre-Registration packet
	 * @param encryptedPacket
	 *            - Encrypted Pre-Reg packet in bytes
	 * @return bytes of the decrypted Pre-Registration packet
	 */
	byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket);

}