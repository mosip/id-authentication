package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.dto.RegPacketStatusDTO;
import io.mosip.registration.entity.Registration;

/**
 * DAO class for Repository
 * 
 * @author Himaja Dhanyamraju
 *
 */
public interface RegPacketStatusDAO {
	
	/**
	 * This method gets the list of packets which are having the status as uploaded-successfully
	 * 
	 * @return List<Registration>
	 * 				returns the list of registrationIds
	 */
	List<Registration> getPacketIdsByStatusUploaded();
	
	
	/**
	 * @param registrationId id
	 * @return registration
	 */
	Registration get(String registrationId);
	
	/**
	 * @param registration entity
	 * @return registration
	 */
	Registration update(Registration registration);
	
	/**
	 * Delete Registration
	 * @param registration
	 */
	void delete(Registration registration);
}
