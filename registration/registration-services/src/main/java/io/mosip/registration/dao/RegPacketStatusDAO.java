package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.Registration;

/**
 * This DAO class is used to fetch/update/delete the registrations in the 
 * Registration/Registration_transaction table.
 * 
 * @author Himaja Dhanyamraju
 *
 */
public interface RegPacketStatusDAO {
	
	/**
	 * This method gets the list of packets which are having the client status as PUSHED
	 * The fetched data will be returned in the ascending order of createdd time
	 * 
	 * @return {@link Registration}
	 * 				returns the list of registrationIds which are uploaded successfully
	 */
	List<Registration> getPacketIdsByStatusUploaded();
	
	
	/**
	 * Fetch the registration based on the registration id.
	 * @param registrationId - the id of required Registration Entity
	 * @return registration	- the registration entity with given id
	 */
	Registration get(String registrationId);
	
	/**
	 * Update the details in the {@link Registration} to the Registration as well as Registration Transaction table.
	 * @param registration entity
	 * @return registration	- the updated registration entity
	 */
	Registration update(Registration registration);
	
	/**
	 * Delete the particular registration from the table
	 * Delete Registration
	 * @param registration	- the registration entity that has to be deleted
	 */
	void delete(Registration registration);
}
