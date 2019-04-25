package io.mosip.idrepository.core.spi;

import io.mosip.idrepository.core.exception.IdRepoAppException;

/**
 * The Interface IdRepoService.
 *
 * @author Manoj SP
 * @param <REQUEST> the Request Object
 * @param <RESPONSE> the Response Object
 */
public interface IdRepoService<REQUEST, RESPONSE> {

	/**
	 * Adds the identity.
	 *
	 * @param request the request
	 * @param uin     uin
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE addIdentity(REQUEST request, String uin) throws IdRepoAppException;

	/**
	 * Retrieve identity.
	 *
	 * @param uin    the uin
	 * @param filter filter
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE retrieveIdentityByUin(String uin, String filter) throws IdRepoAppException;
	/**
	 * Retrieve identity by RID
	 * 
	 * @param rid
	 * @param filter
	 * @return the response
	 * @throws IdRepoAppException  the id repo app exception
	 */ 
	RESPONSE retrieveIdentityByRid(String rid, String filter) throws IdRepoAppException;

	/**
	 * Update identity.
	 *
	 * @param request the request
	 * @param uin     uin
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE updateIdentity(REQUEST request, String uin) throws IdRepoAppException;
}
