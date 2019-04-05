package io.mosip.registration.service.packet;

import java.util.List;

import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.entity.Registration;

/**
 * {@code RegistrationApprovalService} is the registration approval service interface.
 *
 * @author Mahesh Kumar
 */
public interface RegistrationApprovalService {

	/**
	 * {@code getEnrollmentByStatus} method to fetch registration packets on status
	 * basis.
	 *
	 * @param status 
	 * 				the status
	 * @return list of packets
	 */
	public List<RegistrationApprovalDTO> getEnrollmentByStatus(String status);
	
	/**
	 * {@code packetUpdateStatus} method to update the status of the packet.
	 *
	 * @param registrationID 
	 * 					the registration ID
	 * @param statusComments 
	 * 					the status comments
	 * @param clientStatusCode 
	 * 					the client status code
	 * @return {@link Registration} entity
	 */
	public Registration updateRegistration(String registrationID,String statusComments,String clientStatusCode);

}
