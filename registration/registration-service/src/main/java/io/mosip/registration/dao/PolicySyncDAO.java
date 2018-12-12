package io.mosip.registration.dao;

import io.mosip.registration.entity.KeyStore;

/**
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface PolicySyncDAO {
	

	void updatePolicy(KeyStore keyStore);

	KeyStore findByMaxExpireTime();

}
