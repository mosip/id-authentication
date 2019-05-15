/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.dto.MosipUserTokenDto;

/**
 * @author Ramadurai Pandian
 *
 */
public interface AuthZService {

	MosipUserTokenDto validateToken(String token) throws Exception;

}
