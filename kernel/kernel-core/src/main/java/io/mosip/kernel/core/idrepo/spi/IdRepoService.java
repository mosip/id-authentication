package io.mosip.kernel.core.idrepo.spi;

import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;

/**
 * The Interface IdRepoService.
 *
 * @author Manoj SP
 * @param <REQUEST> the Request Object
 * @param <RESPONSE> the Response Object
 * @param <UIN> the Uin Object
 */
public interface IdRepoService<REQUEST, RESPONSE, UIN> {

	/**
	 * Adds the identity.
	 *
	 * @param request the request
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE addIdentity(REQUEST request) throws IdRepoAppException;

	/**
	 * Retrieve identity.
	 *
	 * @param uin the uin
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE retrieveIdentity(String uin) throws IdRepoAppException;

	/**
	 * Update identity.
	 *
	 * @param request the request
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE updateIdentity(REQUEST request) throws IdRepoAppException;

	/**
	 * Generate UIN.
	 *
	 * @return the string
	 * @throws IdRepoAppException the id repo app exception
	 */
	String generateUIN() throws IdRepoAppException;
	
	/**
	 * Check UIN.
	 *
	 * @param uin the uin
	 * @return true, if successful
	 * @throws IdRepoAppException the id repo app exception
	 */
	void checkUIN(String uin) throws IdRepoAppException;

	/**
	 * Construct id response.
	 *
	 * @param id the id
	 * @param uin the uin
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE constructIdResponse(String id, UIN uin) throws IdRepoAppException;
}
