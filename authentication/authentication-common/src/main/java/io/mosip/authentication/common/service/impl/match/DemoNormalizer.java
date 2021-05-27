//package io.mosip.authentication.common.service.impl.match;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
//import io.mosip.kernel.demographics.spi.IDemoNormalizer;
//import io.mosip.kernel.demographics.spi.IMasterDataFetcher;
//
///**
// * Generic class to normalize individual name, address.
// *
// * @author Rakesh Roshan
// * @author Arun Bose S
// */
//@Component
//public class DemoNormalizer {
//
//	@Autowired
//	IDemoNormalizer iDemoNormalizer;
//
//	/**
//	 * This method is used to normalize name.
//	 *
//	 * @param nameInfo     the name info
//	 * @param language     the language
//	 * @param titleFetcher the title fetcher
//	 * @return the string
//	 * @throws IdAuthenticationBusinessException the id authentication business
//	 *                                           exception
//	 */
//	public String normalizeName(String nameInfo, String language, IMasterDataFetcher titleFetcher) {
//		return iDemoNormalizer.normalizeName(nameInfo, language, titleFetcher);
//
//	}
//
//	/**
//	 * This method is used to normalize address.
//	 *
//	 * @param address the address received from request or entity
//	 * @return the string output after normalization
//	 */
//	public String normalizeAddress(String address, String language) {
//		return iDemoNormalizer.normalizeAddress(address, language);
//	}
//}
