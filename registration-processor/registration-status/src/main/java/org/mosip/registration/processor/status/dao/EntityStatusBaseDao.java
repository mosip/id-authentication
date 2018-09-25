package org.mosip.registration.processor.status.dao;

import java.util.List;

import org.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityStatusBaseDao  {
	
	List<RegistrationStatusEntity> findbyfilesByThreshold(String statusCode, int threshholdTime);
	/**
	 * @param registrationIds
	 * @return the list of registration status object
	 */
	public List<RegistrationStatusEntity> getByIds(List<String> registrationIds);
}
