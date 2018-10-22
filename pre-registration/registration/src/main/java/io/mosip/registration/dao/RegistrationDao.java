package io.mosip.registration.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.registration.entity.RegistrationEntity;
import io.mosip.registration.repositary.RegistrationRepositary;

@Component
public class RegistrationDao {

	/** The registration status repositary. */
	@Autowired
	@Qualifier("registrationRepository")
	RegistrationRepositary registrationRepositary;

	/**
	 * Save.
	 *
	 * @param registrationEntity
	 *            the registration status entity
	 * @return the registration status entity
	 */
	public RegistrationEntity save(RegistrationEntity registrationEntity) {

		return registrationRepositary.save(registrationEntity);
	}

}