package io.mosip.registration.service;

import java.util.List;

import io.mosip.registration.dto.RegistrationApprovalUiDto;
import io.mosip.registration.entity.Registration;

/**
 * {@code RegistrationApprovalService} is the registration approval service interface
 *
 * @author Mahesh Kumar
 */
public interface RegistrationApprovalService {

	/**
	 * {@code getAllEnrollments} method to get the created registration packets for approval
	 * 
	 * @return list of packets
	 */
	public List<RegistrationApprovalUiDto> getAllEnrollments();
	
	/**
	 * {@code getEnrollmentByStatus} method to fetch registration packets on status
	 * basis
	 * 
	 * @param status
	 * @return list of packets
	 */
	public List<Registration> getEnrollmentByStatus(String status);
	
	/**
	 * {@code packetUpdateStatus} method to update the status of the packet
	 * 
	 * @param id
	 * @param clientStatusCode
	 * @param approverUserId
	 * @param statusComments
	 * @param updBy
	 * @return Boolean
	 */
	public Boolean packetUpdateStatus(String id, String clientStatusCode, String approverUserId, String statusComments,String updBy);
	

}
