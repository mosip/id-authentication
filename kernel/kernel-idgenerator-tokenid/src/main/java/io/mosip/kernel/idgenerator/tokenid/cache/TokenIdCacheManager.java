package io.mosip.kernel.idgenerator.tokenid.cache;
/**
 * 
 * @author M1046464
 * @since 1.0.0
 *
 */

public interface TokenIdCacheManager {
	public boolean contains(String prid);

	public boolean add(String prid);
}
