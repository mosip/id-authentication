package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;
/**
 * 
 * @author Dinesh Karuppiah.T
 */

@FunctionalInterface
public interface LocationInfoFetcher {
	public Optional<String> getLocation(LocationLevel targetLocationLevel, String locationCode);
}
