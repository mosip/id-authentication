package io.mosip.registration.service.packet;

import java.util.List;
import java.util.Map;

import io.mosip.registration.dto.PacketStatusDTO;

/**
 * Interface for approval of Re-Registration Packets. This class fetches the
 * Registration Packets pending for Re-Register and for updating the status of
 * Re-Register packets
 * 
 * @author Saravanakumar Gnanaguru
 * @since 1.0.0
 */
public interface ReRegistrationService {

	/**
	 * Gets all the registration packets that needs to be re-registered
	 * 
	 * <p>
	 * The status of packets has to be either <b>PUSHED</b> or <b>REREGISTER</b>
	 * </p>
	 * 
	 * @return All the Re-Registration Packets
	 */
	List<PacketStatusDTO> getAllReRegistrationPackets();

	/**
	 * Update the client status code and client status comments of the re-register
	 * packets
	 * 
	 * <p>
	 * Client Status code will be updated to RE_REGISTER_APPROVED
	 * </p>
	 * <p>
	 * Client Status Comment will be updated to either Re-Register-Informed or
	 * Re-Register-NotInformed based on the status received
	 * 
	 * @param reRegistrationStatus
	 *            List of Re-register packets and their status
	 * @return <code>true</code> if status of the re-register packets are updated
	 */
	boolean updateReRegistrationStatus(Map<String, String> reRegistrationStatus);

}