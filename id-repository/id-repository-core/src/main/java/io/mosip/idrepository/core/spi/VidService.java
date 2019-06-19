package io.mosip.idrepository.core.spi;

import io.mosip.idrepository.core.exception.IdRepoAppException;

/**
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
	 * @param vidRequest
	 * @return
	 * @throws IdRepoAppException
	 */
	RESPONSE generateVid(REQUEST vidRequest) throws IdRepoAppException;

	/**
	 * This Method will return the Vid Response with Respective Uin.
	 * 
	 * @param vid
	 * @return The Vid Response
	 * @throws IdRepoAppException
	 */
	RESPONSE retrieveUinByVid(String vid) throws IdRepoAppException;

	/**
	 * This method will update the vid status based on the conditions provided by
	 * vid policy.
	 * 
	 * @param vid
	 * @param request
	 * @return The Vid Response
	 * @throws IdRepoAppException
	 */
	RESPONSE updateVid(String vid, REQUEST request) throws IdRepoAppException;

	/**
	 * This method will regenerate vid based on the conditions provided by vid policy.
	 * 
	 * @param vid
	 * @return
	 * @throws IdRepoAppException
	 */
	RESPONSE regenerateVid(String vid) throws IdRepoAppException;
}
