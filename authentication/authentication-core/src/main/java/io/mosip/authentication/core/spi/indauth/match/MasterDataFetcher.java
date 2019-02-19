package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

@FunctionalInterface
public interface MasterDataFetcher {
	Map<String,List<String>> get() throws IdAuthenticationBusinessException;
}
