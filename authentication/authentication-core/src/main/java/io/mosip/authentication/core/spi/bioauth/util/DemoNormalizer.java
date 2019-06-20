package io.mosip.authentication.core.spi.bioauth.util;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MasterDataFetcher;

/**
 * The Interface DemoNormalizer is used to normalize address and name 
 *  to support the authentication effectively.
 *  
 *  @author Arun Bose S
 */
public interface DemoNormalizer {

	
	/**
	 * Normalize the name attribute value(s) in demograghic authentication.
	 *
	 * @param nameInfo the name info
	 * @param language the language
	 * @param titleFetcher the title fetcher
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public String normalizeName(String nameInfo, String language, MasterDataFetcher titleFetcher)
			throws IdAuthenticationBusinessException;
	
	
	/**
	 * Normalize the name attribute value(s) in demograghic authentication.
	 *
	 * @param address the address
	 * @param language the language
	 * @return the string
	 */
	public String normalizeAddress(String address, String language);
}
