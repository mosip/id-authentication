package io.mosip.idrepository.core.spi;

import io.mosip.idrepository.core.exception.IdRepoAppException;

/**
 * 
 * @author Prem Kumar.
 * @param <REQUEST> the Request Object
 * @param <RESPONSE> the Response Object
 */
public interface VidService<REQUEST, RESPONSE> {

	RESPONSE retrieveUinByVid(String vid) throws IdRepoAppException;
}
