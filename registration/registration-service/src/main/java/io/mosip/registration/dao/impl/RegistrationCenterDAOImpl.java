package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.RegistrationCenterDAO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.repositories.RegistrationCenterRepository;

/**
 * The implementation class of {@link RegistrationCenterDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationCenterDAOImpl implements RegistrationCenterDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationCenterDAOImpl.class);

	/** The registrationCenter repository. */
	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationCenterDAO#getRegistrationCenterDetails
	 * (java.lang.String)
	 */
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId) {

		LOGGER.debug("REGISTRATION - CENTER_NAME - REGISTRATION_CENTER_DAO_IMPL", APPLICATION_NAME,
				APPLICATION_ID, "Fetching Registration Center details");

		Optional<RegistrationCenter> registrationCenter = registrationCenterRepository
				.findByCenterIdAndIsActiveTrue(centerId);
		RegistrationCenterDetailDTO registrationCenterDetailDTO = new RegistrationCenterDetailDTO();
		if (registrationCenter.isPresent()) {
			registrationCenterDetailDTO
					.setRegistrationCenterId(registrationCenter.get().getCenterId());
			registrationCenterDetailDTO.setRegistrationCenterName(registrationCenter.get().getCenterName());
			registrationCenterDetailDTO.setRegsitrationCenterTypeCode(registrationCenter.get().getCntrTypCode());
			registrationCenterDetailDTO.setRegistrationCenterAddrLine1(registrationCenter.get().getAddrLine1());
			registrationCenterDetailDTO.setRegistrationCenterAddrLine2(registrationCenter.get().getAddrLine2());
			registrationCenterDetailDTO.setRegistrationCenterAddrLine3(registrationCenter.get().getAddrLine3());
			registrationCenterDetailDTO.setRegistrationCenterLatitude(registrationCenter.get().getLatitude());
			registrationCenterDetailDTO.setRegistrationCenterLongitude(registrationCenter.get().getLongitude());
			registrationCenterDetailDTO.setRegistrationCenterLocationCode(registrationCenter.get().getLocationCode());
			registrationCenterDetailDTO.setRegistrationCenterContactPhone(registrationCenter.get().getContactPhone());
			registrationCenterDetailDTO.setRegistrationCenterWorkingHours(registrationCenter.get().getWorkingHours());
			registrationCenterDetailDTO.setRegistrationCenterNumberOfKiosks(registrationCenter.get().getNumberOfKiosks());
			registrationCenterDetailDTO.setRegistrationCenterPerKioskProcessTime(registrationCenter.get().getPerKioskProcessTime());
			registrationCenterDetailDTO.setRegistrationCenterHolidayLocCode(registrationCenter.get().getHolidayLocCode());
			registrationCenterDetailDTO.setRegistrationCenterProcessEndTime(registrationCenter.get().getProcessStartTime());
			registrationCenterDetailDTO.setRegistrationCenterProcessEndTime(registrationCenter.get().getProcessEndTime());
		}

		LOGGER.debug("REGISTRATION - CENTER_NAME - REGISTRATION_CENTER_DAO_IMPL", APPLICATION_NAME,
				APPLICATION_ID, "Registration Center details fetched successfulyy");

		return registrationCenterDetailDTO;
	}

}
