package org.mosip.registration.service.packet.encryption.aes;

import org.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface for Encrypting the Registration Data using AES Algorithm
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface AESEncryption {

	/**
	 * Returns the byte array of packet data encrypted using AES Algorithm
	 * 
	 * @param plainDataByteArray
	 *            the byte array of packet data to be encrypted
	 * @param keyByteArray
	 *            the byte array of AES Session Key
	 * @return <b>byte[]</b> the byte array of encrypted packet data
	 * @throws RegBaseCheckedException
	 */
	byte[] encrypt(byte[] plainDataByteArray, byte[] keyByteArray) throws RegBaseCheckedException;

	/**
	 * Returns the byte array concatenating the AES Session Key and encrypted packet
	 * data separated by demiliter
	 * 
	 * @param keyByteArray
	 *            the byte array of AES Session Key
	 * @param encryptedDataByteArray
	 *            the byte array of AES encrypted packet data
	 * @return <b>byte[]</b> the concatenated byte array of AES Session Key and AES
	 *         encrypted packet data separated by delimiter
	 */
	byte[] combineKeyEncryptedData(byte[] keyByteArray, byte[] encryptedDataByteArray);
}
