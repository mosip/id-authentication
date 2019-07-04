package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

/**
 * 
 * Functional Interface to fetch Master Data
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@FunctionalInterface
public interface MasterDataFetcher {
	Map<String, List<String>> get() throws IdAuthenticationBusinessException;
}
