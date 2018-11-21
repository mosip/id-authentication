package io.mosip.preregistration.application.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.preregistration.application.entity.PreRegistrationEntity;
import io.mosip.preregistration.application.repository.PreRegistrationRepository;

@Component
public class PreRegistrationDao {

	/** The registration status repositary. */
	@Autowired
	@Qualifier("registrationRepository")
	PreRegistrationRepository preRegistrationRepository;

	/**
	 * Save.
	 *
	 * @param preRegistrationEntity
	 *            the registration status entity
	 * @return the registration status entity
	 */
	public PreRegistrationEntity save(PreRegistrationEntity preRegistrationEntity) {
		return preRegistrationRepository.save(preRegistrationEntity);
	}

}