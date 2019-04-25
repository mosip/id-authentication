package io.mosip.registration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.entity.KeyStore;
import io.mosip.registration.repositories.PolicySyncRepository;

/**
 * The implementation class of {@link PolicySyncDAO}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class PolicySyncDAOImpl implements PolicySyncDAO {

	/** The policy sync repository. */
	@Autowired
	PolicySyncRepository policySyncRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.PolicySyncDAO#updatePolicy(io.mosip.registration.
	 * entity.KeyStore)
	 */
	@Override
	public void updatePolicy(KeyStore keyStore) {
		policySyncRepository.save(keyStore);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.PolicySyncDAO#findByMaxExpireTime()
	 */
	@Override
	public KeyStore findByMaxExpireTime() {
		return policySyncRepository.findFirst1ByOrderByValidTillDtimesDesc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.PolicySyncDAO#getPublicKey()
	 */
	@Override
	public KeyStore getPublicKey(String refId) {
		return policySyncRepository.findByRefIdOrderByValidTillDtimesDesc(refId);
	}

}
