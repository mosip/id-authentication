package io.mosip.registration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.repositories.PolicySyncRepository;

/**
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class PolicySyncDAOImpl implements PolicySyncDAO {
	@Autowired
	PolicySyncRepository policySyncRepository;
	private static final Logger LOGGER = AppConfig.getLogger(PolicySyncDAOImpl.class);

	

	@Override
	public void updatePolicy(KeyStore keyStore) {
		policySyncRepository.save(keyStore);

	}

	@Override
	public KeyStore findByMaxExpireTime() {
		return policySyncRepository.findFirst1ByOrderByValidTillDtimesDesc();
	}

}
