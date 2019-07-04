package io.mosip.registration.service.operator;

import io.mosip.registration.dto.ResponseDTO;

/**
 * The {@code UserMachineMappingService} represents to sync the user mapping information
 * against the machine. 
 * <p> The call happens from client application to server using the object {@code RegistrationCenterUserMachineMappingDto}
 * 
 * <p>Along with that this also validates the current user is new to machine or not.</p>
 * 
 * @author Brahmananda Reddy
 * 
 */
public interface UserMachineMappingService {
	
	/**
	 * This method performs to invoke the user machine mapping server call based on the center-id,machine-id and user-id.
	 *  
	 * <p>This is a push call from client to sever, after successful on-boarded the details will be persisted to the database. 
	 * 	  The same data will be sent to server</p>
	 * 
	 * <p>The center-id will be picked up based on the mac-id of the machine and the respective user id will be picked up based on the mapping machine id, 
	 *    While performing the call the online connectivity check will be performed as a preliminary step and then user mapping details pushed  
	 *    from client to server</p>
	 *    
	 * <p>The {@code RegistrationCenterUserMachineMappingDto} contains the center-id, machine-id and user-id information
	 * 
	 * <p>If Online : </p>
	 * 		<p>The server call performs and based on the result the return response will be formed.</p> 
	 * 		
	 * 		<p>If Success: </p>
	 * 			<p>The success response DTO will be formed and returned from this method.</p>
	 * 		
	 * 		<p>If Failure: </p>
	 * 			<p>The failure response DTO will be formed and returned from this method.</p>
	 * 
	 * <p>If Offline: </p>
	 * 		<p>The failure response DTO will be formed and returned from this method.</p>
	 * 
	 * @return {@code ResponseDTO}
	 * 				based on the result the response DTO will be formed and return to the caller.
	 */
	public ResponseDTO syncUserDetails();
	
	/**
	 * This method provides to validate the user is new to machine or not
	 *    
	 * <p>The check performed based on the user id. 
	 * 	  The local database check happens for this call {@code MachineMappingDAO}to validate the user is mapped or not.
	 * 
	 * <p>If Success: </p>
	 * 			<p>The success response DTO will be formed and returned from this method.</p>
	 * 		
	 * <p>If Failure: </p>
	 * 			<p>The failure response DTO will be formed and returned from this method.</p>
	 * 
	 * @param userId
	 * 		  logged in user {@code String}
	 *  
	 * @return {@code ResponseDTO}
	 * 				based on the result the response DTO will be formed and return to the caller.
	 */
	public ResponseDTO isUserNewToMachine(String userId);
}
