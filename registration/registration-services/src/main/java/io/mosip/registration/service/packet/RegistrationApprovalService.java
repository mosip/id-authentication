package io.mosip.registration.service.packet;

import java.util.List;

import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * {@code RegistrationApprovalService} is the interface for registration
 * approval service. This class fetches the Registration Packets pending for
 * approval and for updating the status of Registration packets
 *
 * @author Mahesh Kumar
 * @since 1.0.0
 */
public interface RegistrationApprovalService {

	/**
	 * {@code getEnrollmentByStatus} method fetches the list of registration packets
	 * based on the status.
	 * 
	 * <p>
	 * Returns the list of Registration Packets matching the input status
	 * </p>
	 *
	 * @param status
	 *            the status based on which registration packets have to be
	 *            retrieved
	 * @return list of packets
	 * @throws RegBaseCheckedException 
	 */
	List<RegistrationApprovalDTO> getEnrollmentByStatus(String status) throws RegBaseCheckedException;
	
	/**
	 * {@code packetUpdateStatus} method to update the Client Status Code and Client
	 * Status Comment of the Registration Data
	 * 
	 * <p>
	 * Returns the updated {@link Registration} entity
	 * </p>
	 *
	 * @param registrationID
	 *            the registration ID of the {@link Registration} data to be updated
	 * @param statusComments
	 *            the data to be updated as Client Status Comment
	 * @param clientStatusCode
	 *            the data to be updated as Client Status Code
	 * @return {@link Registration} entity which had been updated
	 * @throws RegBaseCheckedException 
	 */
	Registration updateRegistration(String registrationID,String statusComments,String clientStatusCode) throws RegBaseCheckedException;

}
