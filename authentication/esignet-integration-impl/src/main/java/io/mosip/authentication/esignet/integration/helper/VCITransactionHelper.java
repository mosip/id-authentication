package io.mosip.authentication.esignet.integration.helper;

import java.util.Map;

import io.mosip.esignet.core.dto.OIDCTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class VCITransactionHelper {

	@Autowired
	CacheManager cacheManager;

	@Value("${mosip.esignet.ida.vci-user-info-cache}")
	private String userinfoCache;

	@SuppressWarnings("unchecked")
	public OIDCTransaction getOAuthTransaction(String accessTokenHash) throws Exception {
		if (cacheManager.getCache(userinfoCache) != null) {
			return cacheManager.getCache(userinfoCache).get(accessTokenHash, OIDCTransaction.class);	//NOSONAR getCache() will not be returning null here.
		}
		throw new Exception("cache_missing");
	}



}
