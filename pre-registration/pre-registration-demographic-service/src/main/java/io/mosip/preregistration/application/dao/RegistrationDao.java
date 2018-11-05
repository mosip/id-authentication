package io.mosip.preregistration.application.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.preregistration.application.entity.RegistrationEntity;
import io.mosip.preregistration.application.repository.RegistrationRepository;

@Component
public class RegistrationDao {

	/** The registration status repositary. */
	@Autowired
	@Qualifier("registrationRepository")
	RegistrationRepository registrationRepository;

	/**
	 * Save.
	 *
	 * @param registrationEntity
	 *            the registration status entity
	 * @return the registration status entity
	 */
	public RegistrationEntity save(RegistrationEntity registrationEntity) {
		return registrationRepository.save(registrationEntity);
	}

}