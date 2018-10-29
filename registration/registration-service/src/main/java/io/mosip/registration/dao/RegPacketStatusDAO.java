package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.dto.RegPacketStatusDTO;

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
	 * @return List<String>
	 * 				returns the list of registrationIds
	 */
	List<String> getPacketIdsByStatusUploaded();
	
	/**
	 * This method updates the status of packets after sync with the server
	 * 
	 * @param packetStatus
	 * 				the list of RegPacketStatusDTOs that represents registrationIds with their respective packet status
	 */
	void updatePacketIdsByServerStatus(List<RegPacketStatusDTO> packetStatus);
}
