package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.Registration;
import io.mosip.registration.entity.TemplateFileFormat;

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
	 * @return {@link Registration}
	 * 				returns the list of registrationIds which are uploaded successfully
	 */
	List<Registration> getPacketIdsByStatusUploaded();
	
	
	/**
	 * @param registrationId - the id of required Registration Entity
	 * @return registration	- the registration entity with given id
	 */
	Registration get(String registrationId);
	
	/**
	 * @param registration entity
	 * @return registration	- the updated registration entity
	 */
	Registration update(Registration registration);
	
	/**
	 * Delete Registration
	 * @param registration	- the registration entity that has to be deleted
	 */
	void delete(Registration registration);
}
