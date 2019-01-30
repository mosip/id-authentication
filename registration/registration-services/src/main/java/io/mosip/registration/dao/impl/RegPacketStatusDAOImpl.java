package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationTransactionType;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.RegPacketStatusDAO;
import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.RegistrationTransaction;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.RegTransactionRepository;
import io.mosip.registration.repositories.RegistrationRepository;

/**
 * The implementation class of {@link RegPacketStatusDAO}.
 *
 * @author Himaja Dhanyamraju
 */
@Repository
public class RegPacketStatusDAOImpl implements RegPacketStatusDAO {

	/** The registration repository. */
	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private RegTransactionRepository regTransactionRepository;

	/**
	 * Object for Logger
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegPacketStatusDAOImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.RegPacketStatusDAO#getPacketIdsByStatusUploaded()
	 */
	@Override
	public List<Registration> getPacketIdsByStatusUploaded() {
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
				"getting packets by status uploaded-successfully has been started");

		return registrationRepository
				.findByclientStatusCode(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.RegPacketStatusDAO#get(java.lang.String)
	 */
	@Override
	public Registration get(String registrationId) {
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
				"Get registration has been started");

		return registrationRepository.findById(Registration.class, registrationId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.RegPacketStatusDAO#update(io.mosip.registration.
	 * entity.Registration)
	 */
	@Override
	public Registration update(Registration registration) {
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
				"Update registration has been started");
		return registrationRepository.update(registration);

	}

	@Override
	public void delete(Registration registration) {
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
				"Delete registration has been started");

		Iterable<RegistrationTransaction> iterableTransaction = registration.getRegistrationTransaction();
		regTransactionRepository.deleteInBatch(iterableTransaction);
		registrationRepository.deleteById(registration.getId());

	}

}
