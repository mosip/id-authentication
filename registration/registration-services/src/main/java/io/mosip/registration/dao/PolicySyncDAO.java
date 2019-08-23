package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.KeyStore;

/**
 * This class is used to store the public key.
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface PolicySyncDAO {
	

	/**
	 * This will store the public key as {@link KeyStore}
	 *
	 * @param keyStore the key store
	 */
	void updatePolicy(KeyStore keyStore);

	/**
	 * Find by max expire time.
	 *
	 * @return the key store
	 */
	KeyStore findByMaxExpireTime();
	
	/**
	 * This method will get the public key from the DB
	 *
	 * @param refId the ref id
	 * @return the key store
	 */
	KeyStore getPublicKey(String refId);

	List<KeyStore> getAllKeyStore(String centerMachineId);
	
	

}
