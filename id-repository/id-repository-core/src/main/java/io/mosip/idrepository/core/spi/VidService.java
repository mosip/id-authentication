package io.mosip.idrepository.core.spi;

import io.mosip.idrepository.core.exception.IdRepoAppException;

/**
 * The Interface VidService.
 *
 * @author Manoj SP
 * @author Prem Kumar.
 * @param <REQUEST> the Request Object
 * @param <RESPONSE> the Response Object
 */
public interface VidService<REQUEST, RESPONSE> {
	
	/**
	 * This method will generate new Vid based on the provided request and based on
	 * the conditions provided by vid policy.
	 *
	 * @param vidRequest the vid request
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE generateVid(REQUEST vidRequest) throws IdRepoAppException;

	/**
	 * This Method will return the Vid Response with Respective Uin.
	 *
	 * @param vid the vid
	 * @return The Vid Response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE retrieveUinByVid(String vid) throws IdRepoAppException;

	/**
	 * This method will update the vid status based on the conditions provided by
	 * vid policy.
	 *
	 * @param vid the vid
	 * @param request the request
	 * @return The Vid Response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE updateVid(String vid, REQUEST request) throws IdRepoAppException;

	/**
	 * This method will regenerate vid based on the conditions provided by vid policy.
	 *
	 * @param vid the vid
	 * @return the response
	 * @throws IdRepoAppException the id repo app exception
	 */
	RESPONSE regenerateVid(String vid) throws IdRepoAppException;
}
