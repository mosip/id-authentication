package io.kernel.core.idrepo.dao;

import org.json.JSONObject;

import io.kernel.core.idrepo.entity.Uin;

/**
 * @author Manoj SP
 *
 */
public interface IdRepoDao {

	 Uin addIdentity(String uin, String uinRefId, JSONObject identityInfo);
	
	 Uin retrieveIdentity(String uin);
	
	 Uin updateIdenityInfo(String uin, JSONObject identityInfo);
	
	 Uin updateUinStatus(String uin, String statusCode);
}
