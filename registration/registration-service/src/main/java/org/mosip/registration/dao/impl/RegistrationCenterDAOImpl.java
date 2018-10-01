package org.mosip.registration.dao.impl;

import static org.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static org.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import java.util.Optional;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.dao.RegistrationCenterDAO;
import org.mosip.registration.dto.RegistrationCenterDetailDTO;
import org.mosip.registration.entity.RegistrationCenter;
import org.mosip.registration.repositories.RegistrationCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * The implementation class of {@link RegistrationCenterDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationCenterDAOImpl implements RegistrationCenterDAO {
	
	/** The registrationCenter repository. */
	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;
	
	/** Object for Logger. */
	private static MosipLogger LOGGER;

	/**
	 * Initialize logger.
	 *
	 * @param mosipRollingFileAppender the mosip rolling file appender
	 */
	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationCenterDAO#getCenterName(java.lang.String)
	 */
	public String getCenterName(String centerId) {
		
		LOGGER.debug("REGISTRATION - CENTER_NAME - REGISTRATION_CENTER_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieving Registration Center name");
		String centerName = "";
		Optional<RegistrationCenter> registrationCenter = registrationCenterRepository.findById(centerId);
		if(registrationCenter.isPresent()) {
			centerName = registrationCenter.get().getName();
		}
		
		LOGGER.debug("REGISTRATION - CENTER_NAME - REGISTRATION_CENTER_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieved Registration Center name");
		
		return centerName;
	}
	
	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationCenterDAO#getRegistrationCenterDetails(java.lang.String)
	 */
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId) {
		
		LOGGER.debug("REGISTRATION - CENTER_DETAILS - REGISTRATION_CENTER_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieving Registration Center details");
		Optional<RegistrationCenter> registrationCenter = registrationCenterRepository.findById(centerId); 
		RegistrationCenterDetailDTO registrationCenterDetailDTO = new RegistrationCenterDetailDTO();
		if(registrationCenter.isPresent()) {
			registrationCenterDetailDTO.setRegistrationCenterCode(registrationCenter.get().getId());
			registrationCenterDetailDTO.setAddrLine1(registrationCenter.get().getAddrLine1());
			registrationCenterDetailDTO.setAddrLine2(registrationCenter.get().getAddrLine2());
			registrationCenterDetailDTO.setAddrLine3(registrationCenter.get().getAddrLine3());
			registrationCenterDetailDTO.setLocLine1(registrationCenter.get().getLocLine1());
			registrationCenterDetailDTO.setLocLine2(registrationCenter.get().getLocLine2());
			registrationCenterDetailDTO.setLocLine3(registrationCenter.get().getLocLine3());
			registrationCenterDetailDTO.setLocLine4(registrationCenter.get().getLocLine4());
			registrationCenterDetailDTO.setCountry(registrationCenter.get().getCountry());
			registrationCenterDetailDTO.setLatitude(registrationCenter.get().getLatitude());
			registrationCenterDetailDTO.setLongitude(registrationCenter.get().getLongitude());
			registrationCenterDetailDTO.setPincode(registrationCenter.get().getPincode());
		}
		LOGGER.debug("REGISTRATION - CENTER_DETAILS - REGISTRATION_CENTER_DAO", 
				getPropertyValue(APPLICATION_NAME), getPropertyValue(APPLICATION_ID), 
				"Retrieved Registration Center details");
		return registrationCenterDetailDTO;
	}

}
