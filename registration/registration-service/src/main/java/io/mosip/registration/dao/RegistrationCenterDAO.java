package io.mosip.registration.dao;

import org.springframework.stereotype.Repository;

import io.mosip.registration.dto.RegistrationCenterDetailDTO;

@Repository
public interface RegistrationCenterDAO {
	
	public String getCenterName(String centerId);
	
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId);

}
