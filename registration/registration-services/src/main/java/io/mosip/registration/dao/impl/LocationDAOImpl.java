package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.LocationDAO;
import io.mosip.registration.entity.Location;
import io.mosip.registration.repositories.LocationRepository;

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
	/** instance of {@link Logger} */
	private static final Logger LOGGER = AppConfig.getLogger(LocationDAOImpl.class);

	/**
	 * (non-javadoc)
	 * 
	 * @see io.mosip.registration.dao.LocationDAO#getLocations()
	 */
	@Override
	public List<Location> getLocations() {
		LOGGER.info("REGISTRATION-PACKET_CREATION-LocationDAO", APPLICATION_NAME,
				APPLICATION_ID, "fetching the locations");

		return locationRepository.findAll();

	}

}
