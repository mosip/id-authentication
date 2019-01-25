package io.mosip.registration.service.sync;

import java.net.URISyntaxException;
import java.util.List;

import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

public interface PacketSynchService {

	
	/**This method is used to fetch the packets from the table which needs to be synched.
	 * @return
	 */
	List<Registration> fetchPacketsToBeSynched();

	
	/**This method is used to synch the packets to the server
	 * @param syncDtoList
	 * @return
	 * @throws RegBaseCheckedException
	 * @throws URISyntaxException
	 * @throws MosipJsonProcessingException
	 */
	Object syncPacketsToServer(List<SyncRegistrationDTO> syncDtoList)
			throws RegBaseCheckedException, URISyntaxException, JsonProcessingException;

	
	/**This method is used to update the synched packets in the table
	 * @param synchedPackets
	 * @return
	 */
	Boolean updateSyncStatus(List<Registration> synchedPackets);

}