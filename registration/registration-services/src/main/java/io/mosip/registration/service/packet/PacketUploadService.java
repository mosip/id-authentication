package io.mosip.registration.service.packet;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

public interface PacketUploadService {
	
	/**
	 * Get the list of Synched Packets or the packets that are marked are Reupload.
	 * @return
	 */

	List<Registration> getSynchedPackets();
	
	/**
	 * Push the packets that are marked as Synched to the server using web api
	 * @param packet
	 * @return
	 * @throws URISyntaxException
	 * @throws RegBaseCheckedException
	 */
	ResponseDTO pushPacket(File packet) throws URISyntaxException, RegBaseCheckedException;
	
	/**
	 * Update the Packet upload status to the local Database.	
	 * @param packetStatus
	 * @return
	 */
	 Boolean updateStatus(List<PacketStatusDTO> packetUploadStatus);
	 
	 /**
 	 * Uploading packets after creation of packet and successful sync when EOD is OFF
 	 *
 	 * @param rid 
 	 * 			the registration id
 	 */
 	void uploadPacket(String rid);

	/**
	 * Uploading packets after approval/rejection in EOD and successful sync when EOD is ON.
	 *
	 * @param regIds the registration id's
	 */
	void uploadEODPackets(List<String> regIds);

	void uploadAllSyncedPackets();
}
