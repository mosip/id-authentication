package io.mosip.registration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.dao.PreRegistrationDAO;
import io.mosip.registration.entity.PreRegistrationList;
import io.mosip.registration.repositories.PreRegistrationRepository;


/**
 * {@link PreRegistrationDAO}
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class PreRegistrationDAOImpl implements PreRegistrationDAO {
	
	/**
	 * Autowires Pre Registration Repository class
	 */
	@Autowired
	PreRegistrationRepository preRegistrationRepository;

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
