package io.mosip.registration.service.operator;

import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * The {@code UserDetailService} represents to save the user related
 * information, while performing the user details sync(on-line) from server to
 * client. The registered user details WRT the center id will be sync as part of
 * this and same information will be persisted to the database. This class will
 * be invoked while performing the automatic[batch job] and manual sync operations.
 * 
 * @author Sreekar Chukka
 * 
 */
public interface UserDetailService {

	/**
	 * This method performs to invoke the user details sync operation based on the center-id, in synchronous way. 
	 * 
	 * <p>The center-id will be picked up based on the mac-id of the machine. While performing the call
	 * the online connectivity check will be performed as preliminary step to connect to the server and then sync the user details.</p>
	 * 
	 * <p>If Online :</p>
	 * 		<p>The server call performs and based on the result the return response will be formed.</p>
	 * 		
	 * 		<p>If Success:</p>
	 * 			<p>The Response JSON string will be converted to the {@code UserDetailResponseDto} and persist to the database.</p>
	 * 			<p>The success response DTO will be formed and returned from this method.</p>
	 * 		
	 * 		<p>If Failure:</p>
	 * 			<p>The failure response DTO will be formed and returned from this method.</p>
	 * 
	 * <p>If Offline: </p>
	 * 		<p>The failure response DTO will be formed and returned from this method.</p>
	 * 
	 *
	 * @param triggerpoint  
	 * 				{@code String} to identify how this operation happens [Manual or batch trigger]
	 * 				Manual trigger - value is user id of the logged user.
	 * 				Batch trigger  - value is "system"
	 * @return {@code ResponseDTO}
	 * 				based on the result the response DTO will be formed and return to the caller.
	 * @throws RegBaseCheckedException 
	 */
	public ResponseDTO save(String triggerpoint) throws RegBaseCheckedException;

}
