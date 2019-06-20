package io.mosip.registration.service.sync;

import java.net.URISyntaxException;
import java.util.List;

import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.dto.PacketStatusDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This class invokes the external MOSIP service 'Packet Sync' to sync the
 * packet ids, which are ready for upload to the server from client. The packet
 * upload can't be done, without synching the packet ids to the server. While
 * sending this request, the data would be encrypted using MOSIP public key and
 * same can be decrypted at Server end using the respective private key.
 * 
 * @author saravanakumar gnanaguru
 *
 */
public interface PacketSynchService {

	/**
	 * This method is used to fetch the packets from the table which needs to be
	 * synched with the server from client machine. It picks the packet which are in
	 * the state of 'approved', 'reregisterapproved' and 'resend'.
	 *
	 * @return the list of packet Status DTO
	 */
	List<PacketStatusDTO> fetchPacketsToBeSynched();

	/**
	 * This method makes the actual service call to push the packet sync related
	 * data to server. It makes only external service call and doesn't have any db
	 * call.
	 * 
	 * @param syncDtoList
	 *            the sync dto list
	 * @param triggerPoint
	 *            the trigger point
	 * @return the response DTO
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws JsonProcessingException
	 *             the json processing exception
	 */
	ResponseDTO syncPacketsToServer(String syncDtoList, String triggerPoint)
			throws RegBaseCheckedException, URISyntaxException, JsonProcessingException;

	/**
	 * This method is used to update the packets sync status as 'SYNCHED' in the
	 * table, which are synched with the server.
	 *
	 * @param synchedPackets
	 *            the synched packets
	 * @return the boolean true or false based on the success or failure.
	 */
	Boolean updateSyncStatus(List<PacketStatusDTO> synchedPackets);

	/**
	 * This method is used to sync the multiple packet with the server once it is
	 * approved or rejected by the supervisor. It fetches the registration ids
	 * related packet data from db and frame the multiple packet detail in a single
	 * request along with the packet encryption using the Mosip public key and
	 * invokes the 'Packet Sync' service to sync with the server. Post sync with the
	 * MOSIP server, the sync status would be updated into the database table.
	 * 
	 * @param regIds
	 *            the list registration id's to sync
	 * @return it returns the success or failure error code.
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	String syncEODPackets(List<String> regIds) throws RegBaseCheckedException;

	/**
	 * It fetches the provided packet id related data from db and prepare the list
	 * of {@link PacketStatusDTO} object and pass it to overloaded 'packetSync'
	 * service. Then it invokes 'syncPacketsToServer' to make a call to external
	 * service 'Packet sync' to sync the packet with the server.
	 * 
	 * @param rId
	 *            the registration id
	 * @return it returns the success or failure error code.
	 * 
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	String packetSync(String rId) throws RegBaseCheckedException;

	/**
	 * Fetch the required packet related information from input 'PacketStatusDTO'
	 * object and map it to 'SyncRegistrationDTO' object. It encrypts the request
	 * before invoking the external service. Then it invokes 'syncPacketsToServer'
	 * to make a call to external service 'Packet sync' to sync the packet with the
	 * server.
	 *
	 * @param packetsToBeSynched
	 *            the packets to be synched
	 * @return it returns the success or failure error code.
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	String packetSync(List<PacketStatusDTO> packetsToBeSynched) throws RegBaseCheckedException;

	/**
	 * This is invoked from batch job process at the regular interval to sync the
	 * packets from client to server which are in particular status. It fetches the
	 * list of Registration ids from table having the client status code as
	 * 'Approved', 'Reregisterapproved', 'Rejected'.
	 * 
	 * 
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	void syncAllPackets() throws RegBaseCheckedException;

	/**
	 * To check the packets sync status with the db.
	 *
	 * @param rId - the registration id
	 * @return true - if the registration id exists and the status is 'SYNCED'.
	 *         otherwise return false.
	 */
	Boolean fetchSynchedPacket(String rId);
}