package io.mosip.registration.service;

import java.util.List;

import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.entity.Registration;

/**
 * {@code RegistrationApprovalService} is the registration approval service interface
 *
 * @author Mahesh Kumar
 */
public interface RegistrationApprovalService {

	/**
	 * {@code getEnrollmentByStatus} method to fetch registration packets on status
	 * basis
	 * 
	 * @param status
	 * @return list of packets
	 */
	public List<RegistrationApprovalDTO> getEnrollmentByStatus(String status);
	
	/**
	 * {@code packetUpdateStatus} method to update the status of the packet.
	 *
	 * @param registrationID 
	 * @param statusComments 
	 * @param clientStatusCode 
	 * @return {@link Registration} entity
	 */
	public Registration updateRegistration(String registrationID,String statusComments,String clientStatusCode);

}
