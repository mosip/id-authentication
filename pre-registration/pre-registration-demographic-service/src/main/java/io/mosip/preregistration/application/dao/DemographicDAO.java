package io.mosip.preregistration.application.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.preregistration.application.entity.DemographicEntity;
import io.mosip.preregistration.application.repository.DemographicRepository;

/**
 * @author M1037717
 *
 */
@Component
public class DemographicDAO {

	/** The registration status repositary. */
	@Autowired
	@Qualifier("registrationRepository")
	DemographicRepository preRegistrationRepository;

	/**
	 * Save.
	 *
	 * @param preRegistrationEntity
	 *            the registration status entity
	 * @return the registration status entity
	 */
	public DemographicEntity save(DemographicEntity preRegistrationEntity) {
		return preRegistrationRepository.save(preRegistrationEntity);
	}

}