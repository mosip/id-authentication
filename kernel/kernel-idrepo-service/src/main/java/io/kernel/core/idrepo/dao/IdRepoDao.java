package io.kernel.core.idrepo.dao;

import io.kernel.core.idrepo.entity.Uin;
import io.kernel.core.idrepo.exception.IdRepoAppException;

/**
 * @author Manoj SP
 *
 */
public interface IdRepoDao {

	 Uin addIdentity(String uin, String uinRefId, byte[] identityInfo) throws IdRepoAppException;
	
	 Uin retrieveIdentity(String uin) throws IdRepoAppException;
	
	 Uin updateIdenityInfo(String uin, byte[] identityInfo) throws IdRepoAppException;
	
	 Uin updateUinStatus(String uin, String statusCode) throws IdRepoAppException;
}
