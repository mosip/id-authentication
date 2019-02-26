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
public interface AuthCService extends AuthZService,AuthNService{
	
	public MosipUserDtoToken retryToken(String existingToken);
	public AuthNResponse invalidateToken(String token);

}
