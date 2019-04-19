/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.MosipUserDtoToken;

/**
 * @author Ramadurai Pandian
 *
 */
public interface AuthZService {

	MosipUserDtoToken validateToken(String token) throws Exception;

}
