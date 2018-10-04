package io.mosip.registration.dao.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
	
	/** The registrationCenter repository. */
	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;
	
	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationCenterDAO#getCenterName(java.lang.String)
	 */
	public String getCenterName(String centerId) {
		
		String centerName = "";
		Optional<RegistrationCenter> registrationCenter = registrationCenterRepository.findById(centerId);
		if(registrationCenter.isPresent()) {
			centerName = registrationCenter.get().getCenterName();
		}
		
		return centerName;
	}
	
	/* (non-Javadoc)
	 * @see org.mosip.registration.dao.RegistrationCenterDAO#getRegistrationCenterDetails(java.lang.String)
	 */
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId) {
		
		Optional<RegistrationCenter> registrationCenter = registrationCenterRepository.findById(centerId); 
		RegistrationCenterDetailDTO registrationCenterDetailDTO = new RegistrationCenterDetailDTO();
		if(registrationCenter.isPresent()) {
			registrationCenterDetailDTO.setRegistrationCenterCode(registrationCenter.get().getCenterId());
			registrationCenterDetailDTO.setRegistrationCenterAddrLine1(registrationCenter.get().getAddrLine1());
			registrationCenterDetailDTO.setRegistrationCenterAddrLine2(registrationCenter.get().getAddrLine2());
			registrationCenterDetailDTO.setRegistrationCenterAddrLine3(registrationCenter.get().getAddrLine3());
			registrationCenterDetailDTO.setRegistrationCenterLocLine1(registrationCenter.get().getLocLine1());
			registrationCenterDetailDTO.setRegistrationCenterLocLine2(registrationCenter.get().getLocLine2());
			registrationCenterDetailDTO.setRegistrationCenterLocLine3(registrationCenter.get().getLocLine3());
			registrationCenterDetailDTO.setRegistrationCenterLocLine4(registrationCenter.get().getLocLine4());
			registrationCenterDetailDTO.setRegistrationCenterCountry(registrationCenter.get().getCountry());
			registrationCenterDetailDTO.setRegistrationCenterLatitude(registrationCenter.get().getLatitude());
			registrationCenterDetailDTO.setRegistrationCenterLongitude(registrationCenter.get().getLongitude());
			registrationCenterDetailDTO.setRegistrationCenterPincode(registrationCenter.get().getPincode());
		}
		return registrationCenterDetailDTO;
	}

}
