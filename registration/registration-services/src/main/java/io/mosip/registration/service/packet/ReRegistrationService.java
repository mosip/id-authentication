package io.mosip.registration.service.packet;

import java.util.List;
import java.util.Map;

import io.mosip.registration.dto.PacketStatusDTO;

/**
 * Service class for ReRegistration
 * 
 * @author saravanakumar gnanaguru
 *
 */
public interface ReRegistrationService {

	/**
	 * This methos is used to get all the packets that needs to be reregistered.
	 * @return All the Re-Registration Packets
	 */
	List<PacketStatusDTO> getAllReRegistrationPackets();

	/**
	 * THis method is used to update the status of the packets once it is approved.
	 * @param reRegistrationStatus List of Rereggistering packets
	 * @return Is Re-Registration updated or not
	 */
	boolean updateReRegistrationStatus(Map<String, String> reRegistrationStatus);

}