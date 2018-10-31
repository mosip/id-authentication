package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;

@FunctionalInterface
public interface LocationInfoFetcher {
	public Optional<String> getLocation(LocationLevel targetLocationLevel, String locationCode);
}
