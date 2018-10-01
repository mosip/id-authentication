package org.mosip.registration.util.keymanager;

import java.util.List;

import javax.crypto.SecretKey;

import org.mosip.registration.exception.RegBaseCheckedException;

/**
 * Interface to generate the AES Session Key
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface AESKeyManager {

	/**
	 * Returns the AES Session Key - The input list will be taken as seed to
	 * generate the AES Session Key
	 * 
	 * @param aesKeySeeds
	 *            the list of seeds to generate the AES Session Key
	 * @return the AES Session Key
	 * @throws RegBaseCheckedException 
	 */
	SecretKey generateSessionKey(List<String> aesKeySeeds) throws RegBaseCheckedException;
}
