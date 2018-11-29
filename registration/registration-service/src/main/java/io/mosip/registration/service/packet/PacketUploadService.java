package io.mosip.registration.service.packet;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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
	Object pushPacket(File packet) throws URISyntaxException, RegBaseCheckedException;
	
	/**
	 * Update the Packet upload status to the local Database.	
	 * @param packetStatus
	 * @return
	 */
	 Boolean updateStatus(List<Registration> packetUploadStatus);
}
