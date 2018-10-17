package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dao.GenderDAO;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.repositories.GenderRepository;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

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

	/** instance of {@link MosipLogger} */
	private static MosipLogger LOGGER;

	/**
	 * Initialize the logger.
	 * 
	 * @param mosipRollingFileAppender
	 */

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * (non-javadoc)
	 * 
	 * @see io.mosip.registration.dao.GenderDAO#getGenders()
	 */

	@Override
	public List<Gender> getGenders() {
		LOGGER.debug("REGISTRATION-PACKET_CREATION-GENDERDAO", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "fetching the genders");

		return registrationGenderRepository.findAll();

	}

}
