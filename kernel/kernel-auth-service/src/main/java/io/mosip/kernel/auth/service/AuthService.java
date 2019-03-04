/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.AuthNResponse;
import io.mosip.kernel.auth.entities.MosipUserDtoToken;

/**
 * @author Ramadurai Pandian
 *
 */
public interface AuthService extends AuthZService,AuthNService{
	
	public MosipUserDtoToken retryToken(String existingToken) throws Exception;
	public AuthNResponse invalidateToken(String token) throws Exception;

}
