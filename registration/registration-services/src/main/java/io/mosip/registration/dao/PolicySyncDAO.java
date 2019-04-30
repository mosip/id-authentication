package io.mosip.registration.dao;

import io.mosip.registration.entity.KeyStore;

/**
 * DAO class for PolicySync
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface PolicySyncDAO {
	

	/**
	 * Update policy.
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
	 * Find public key.
	 *
	 * @param refId the ref id
	 * @return the key store
	 */
	KeyStore getPublicKey(String refId);
	
	

}
