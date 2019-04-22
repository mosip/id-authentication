/**
 * 
 */
package io.mosip.kernel.auth.service;

import java.util.List;

import io.mosip.kernel.auth.entities.AuthNResponse;
import io.mosip.kernel.auth.entities.MosipUserDtoToken;
import io.mosip.kernel.auth.entities.MosipUserListDto;
import io.mosip.kernel.auth.entities.RolesListDto;

/**
 * @author Ramadurai Pandian
 *
 */
public interface AuthService extends AuthZService, AuthNService {

	public MosipUserDtoToken retryToken(String existingToken) throws Exception;

	public AuthNResponse invalidateToken(String token) throws Exception;

	public RolesListDto getAllRoles(String appId);

	public MosipUserListDto getListOfUsersDetails(List<String> userDetails, String appId) throws Exception;

}
