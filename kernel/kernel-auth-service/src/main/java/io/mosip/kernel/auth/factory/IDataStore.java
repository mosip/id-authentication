/**
 * 
 */
package io.mosip.kernel.auth.factory;

import java.util.List;

import io.mosip.kernel.auth.entities.MosipUserListDto;
import io.mosip.kernel.auth.entities.MosipUserSaltList;
import io.mosip.kernel.auth.entities.RolesListDto;
import io.mosip.kernel.auth.service.AuthNDataService;

/**
 * @author Ramadurai Pandian
 *
 */
public interface IDataStore extends AuthNDataService {

	public RolesListDto getAllRoles();

	public MosipUserListDto getListOfUsersDetails(List<String> userDetails) throws Exception;

	public MosipUserSaltList getAllUserDetailsWithSalt()  throws Exception;

}
