package io.mosip.registration.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import io.mosip.registration.dao.LocationDAO;
import io.mosip.registration.entity.Location;
import io.mosip.registration.repositories.LocationRepository;
import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * implementation class of {@link LocationDAO}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class LocationDAOImpl implements LocationDAO {
	/** instance of {@link LocationRepository} */
	@Autowired
	private LocationRepository locationRepository;
	/** instance of {@link MosipLogger} */
	private static MosipLogger LOGGER;

	/** 
	 * Initialize logger
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
	 * @see io.mosip.registration.dao.LocationDAO#getLocations()
	 */
	@Override
	public List<Location> getLocations() {
		LOGGER.debug("REGISTRATION-PACKET_CREATION-LocationDAO", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "fetching the locations");

		return locationRepository.findAll();

	}

}
