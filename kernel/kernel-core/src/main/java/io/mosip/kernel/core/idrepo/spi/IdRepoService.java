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
	 * @param uin uin
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE addIdentity(REQUEST request, String uin) throws IdRepoAppException;

	/**
	 * Retrieve identity.
	 *
	 * @param uin the uin
	 * @param filter filter
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE retrieveIdentity(String uin, String filter) throws IdRepoAppException;

	/**
	 * Update identity.
	 *
	 * @param request the request
	 * @param uin uin
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE updateIdentity(REQUEST request, String uin) throws IdRepoAppException;
}
