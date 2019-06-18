package io.mosip.registration.service.operator;

import io.mosip.registration.dto.ResponseDTO;

/**
 * The {@code UserSaltDetailsService} represents to save/update the user salt related
 * information, while performing the user salt details sync(on-line) from server
 * to client and same information will be persisted to the database. This class
 * will be invoked while performing the automatic[batch job] and manual sync
 * operations.
 * 
 * @author Sreekar Chukka
 * 
 */
public interface UserSaltDetailsService {

	/**
	 * This method performs to invoke the user salt details sync operation in
	 * synchronous way.
	 * 
	 * This service provide unique salt(An Encrypted Key) to for all users. While
	 * performing the call the online connectivity check will be performed as
	 * preliminary step to connect to the server and then sync the user salt
	 * details.
	 * 
	 * If Online : The server call performs and based on the result the return
	 * response will be formed.
	 * 
	 * If Success: The Response JSON string will be converted to the
	 * {@code UserDetail} and persist to the database. The success response DTO will
	 * be formed and returned from this method.
	 * 
	 * If Failure: The failure response DTO will be formed and returned from this
	 * method.
	 * 
	 * If Offline: The failure response DTO will be formed and returned from this
	 * method.
	 * 
	 *
	 * @param tigger {@code String} to identify how this operation happens
	 *                     [Manual or batch trigger] Manual trigger - value is user
	 *                     id of the logged user. Batch trigger - value is "system"
	 *                     
	 * @return {@code ResponseDTO} based on the result the response DTO will be
	 *         formed and return to the caller.
	 */
	public ResponseDTO getUserSaltDetails(String tigger);

}
