package io.mosip.authentication.core.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.indauth.match.MasterDataFetcher;
import io.mosip.kernel.demographics.spi.IDemoNormalizer;

/**
 * Generic class to normalize individual name, address.
 *
 * @author Rakesh Roshan
 * @author Arun Bose S
 */
@Service
public class DemoNormalizer {
	
	@Autowired
	private IDemoNormalizer iDemoNormalizer;
	

	/**
	 * This method is used to normalize name.
	 *
	 * @param nameInfo     the name info
	 * @param language     the language
	 * @param titleFetcher the title fetcher
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public String normalizeName(String nameInfo, String language, MasterDataFetcher titleFetcher) throws IdAuthenticationBusinessException {
		return iDemoNormalizer.normalizeName(nameInfo, language, titleFetcher.get());

	}

	/**
	 * This method is used to normalize address.
	 *
	 * @param address the address received from request or entity
	 * @return the string output after normalization
	 */
	public String normalizeAddress(String address, String language) {
		return iDemoNormalizer.normalizeAddress(address, language);
	}
}