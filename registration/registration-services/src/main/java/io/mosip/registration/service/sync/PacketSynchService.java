package io.mosip.registration.service.sync;

import java.net.URISyntaxException;
import java.util.List;

import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Service class for Packet Sync
 * 
 * @author saravanakumar gnanaguru
 *
 */
public interface PacketSynchService {

	/**
	 * This method is used to fetch the packets from the table which needs to be
	 * synched.
	 *
	 * @return the list of packet Status DTO
	 */
	List<PacketStatusDTO> fetchPacketsToBeSynched();

	/**
	 * This method is used to synch the packets to the server.
	 *
	 * @param syncDtoList 
	 * 				the sync dto list
	 * @param triggerPoint 
	 * 				the trigger point
	 * @return the response DTO
	 * @throws RegBaseCheckedException 
	 * 				the reg base checked exception
	 * @throws URISyntaxException 
	 * 				the URI syntax exception
	 * @throws JsonProcessingException 
	 * 				the json processing exception
	 */
	ResponseDTO syncPacketsToServer(String syncDtoList,String triggerPoint)
			throws RegBaseCheckedException, URISyntaxException, JsonProcessingException;

	/**
	 * This method is used to update the synched packets in the table.
	 *
	 * @param synchedPackets 
	 * 				the synched packets
	 * @return the boolean
	 */
	Boolean updateSyncStatus(List<PacketStatusDTO> synchedPackets);

	/**
	 * Sync packet after creation when EOD is OFF .
	 *
	 * @param rId  
	 * 				the registration id
	 * @return the packet to sync
	 * @throws RegBaseCheckedException 
	 * 				the reg base checked exception
	 */
	String packetSync(String rId) throws RegBaseCheckedException;

	/**
	 * Sync packets after approval/rejection in EOD when EOD is ON.
	 *
	 * @param regIds 
	 * 				the list registration id's to sync
	 * @return the error message if any
	 * @throws RegBaseCheckedException 
	 * 				the reg base checked exception
	 */
	String syncEODPackets(List<String> regIds) throws RegBaseCheckedException;

	/**
	 * Packet sync.
	 *
	 * @param packetsToBeSynched 
	 * 				the packets to be synched
	 * @return the string
	 * @throws RegBaseCheckedException 
	 * 				the reg base checked exception
	 */
	String packetSync(List<PacketStatusDTO> packetsToBeSynched) throws RegBaseCheckedException;

	/**
	 * Sync all packets.
	 *
	 * @throws RegBaseCheckedException 
	 * 				the reg base checked exception
	 */
	void syncAllPackets() throws RegBaseCheckedException;
	
	Boolean fetchSynchedPacket(String rId);
}