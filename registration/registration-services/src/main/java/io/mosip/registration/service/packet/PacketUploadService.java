package io.mosip.registration.service.packet;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Service class for Packet Upload
 * 
 * @author saravanakumar gnanaguru
 *
 */
public interface PacketUploadService {
	
	/**
	 * Get the list of Synched Packets or the packets that are marked are Reupload.
	 * @return All the Synched packets
	 */

	List<Registration> getSynchedPackets();
	
	/**
	 * Push the packets that are marked as Synched to the server using web api.
	 *
	 * @param packet 
	 * 				the packet
	 * @return 	the response DTO
	 * @throws URISyntaxException 
	 * 				the URI syntax exception
	 * @throws RegBaseCheckedException 
	 * 				the reg base checked exception
	 */
	ResponseDTO pushPacket(File packet) throws URISyntaxException, RegBaseCheckedException;
	
	/**
	 * Update the Packet upload status to the local Database.	
	 *
	 * @param packetUploadStatus 
	 * 					the packet upload status
	 * @return Boolean isUpdated
	 */
	 Boolean updateStatus(List<PacketStatusDTO> packetUploadStatus);
	 
	 /**
 	 * Uploading packets after creation of packet and successful sync when EOD is OFF.
 	 *
 	 * @param rid 			
 	 * 				the registration id
 	 */
 	void uploadPacket(String rid);

	/**
	 * Uploading packets after approval/rejection in EOD and successful sync when EOD is ON.
	 *
	 * @param regIds 
	 * 				the registration id's
	 */
	void uploadEODPackets(List<String> regIds);

	/**
	 * This method will upload all the synched packets which is used for machine remapping.
	 */
	void uploadAllSyncedPackets();
}
