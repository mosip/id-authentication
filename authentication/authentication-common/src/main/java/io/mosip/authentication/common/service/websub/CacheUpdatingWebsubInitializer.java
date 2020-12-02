package io.mosip.authentication.common.service.websub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class CacheUpdatingWebsubInitializer.
 * @author Loganathan Sekar
 */
@Component
public abstract class CacheUpdatingWebsubInitializer extends BaseIDAWebSubInitializer {

	/** The cache type. */
	@Value("${spring.cache.type:simple}")
	public String cacheType;
	
	
	/**
	 * Checks if is cache enabled.
	 *
	 * @return true, if is cache enabled
	 */
	protected boolean isCacheEnabled() {
		return !StringUtils.equalsIgnoreCase(cacheType, "none");
	}
	
}
