package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.GenderDAO;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.repositories.GenderRepository;

/**
 * implementation class of {@link GenderDAO}
 * 
 * @author brahmananda Reddy
 * @since 1.0.0
 *
 */

@Repository(value="gender")
public class GenderDAOImpl implements GenderDAO {

	/** instance of {@link GenderRepository} */
	@Autowired
	private GenderRepository registrationGenderRepository;

	/** instance of {@link Logger} */
	private static final Logger LOGGER = AppConfig.getLogger(GenderDAOImpl.class);

	/**
	 * (non-javadoc)
	 * 
	 * @see io.mosip.registration.dao.GenderDAO#getGenders()
	 */

	@Override
	public List<Gender> getGenders() {
		LOGGER.info("REGISTRATION-PACKET_CREATION-GENDERDAO", APPLICATION_NAME,
				APPLICATION_ID, "fetching the genders");

		return registrationGenderRepository.findAll();

	}

}
