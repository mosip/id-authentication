package io.mosip.idrepository.core.spi;

import io.mosip.idrepository.core.exception.IdRepoAppException;

/**
 * 
 * @author Prem Kumar.
 * @param <REQUEST> the Request Object
 * @param <RESPONSE> the Response Object
 */
public interface VidService<REQUEST, RESPONSE> {

	RESPONSE createVid(REQUEST vidRequest) throws IdRepoAppException;

	/**
	 * This Method will return the Vid Response with Respective Uin.
	 * 
	 * @param vid
	 * @return The Vid Response
	 * @throws IdRepoAppException
	 */
	RESPONSE retrieveUinByVid(String vid) throws IdRepoAppException;

	/**
	 * This Method will Return The Vid Response with respective vid Status.
	 * 
	 * @param vid
	 * @param request
	 * @return The Vid Response
	 * @throws IdRepoAppException
	 */
	RESPONSE updateVid(String vid, REQUEST request) throws IdRepoAppException;

	/**
	 * This Method will return the Vid Response with Newly generated Vid.
	 * 
	 * @param vid
	 * @return
	 * @throws IdRepoAppException
	 */
	RESPONSE regenerateVid(String vid) throws IdRepoAppException;
}
