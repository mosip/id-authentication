package org.mosip.registration.dao;

import org.mosip.registration.dto.RegistrationCenterDetailDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationCenterDAO {
	
	public String getCenterName(String centerId);
	
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId);

}
