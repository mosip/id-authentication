package io.mosip.registration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.dao.PreRegistrationDataSyncDAO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.repositories.PreRegistrationDataSyncRepository;


/**
 * {@link PreRegistrationDataSyncDAO}
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class PreRegistrationDataSyncDAOImpl implements PreRegistrationDataSyncDAO {
	
	/**
	 * Autowires Pre Registration Repository class
	 */
	@Autowired
	PreRegistrationDataSyncRepository preRegistrationRepository;

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.PreRegistrationDAO#getPreRegistration(java.lang.String)
	 */
	@Override
	public PreRegistrationList getPreRegistration(String preRegId) {
		return preRegistrationRepository.findByPreRegId(preRegId);
		
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.dao.PreRegistrationDAO#savePreRegistration(io.mosip.registration.entity.PreRegistration)
	 */
	@Override
	public PreRegistrationList savePreRegistration(PreRegistrationList preRegistration) {
		return preRegistrationRepository.save(preRegistration);
		
	}

}
