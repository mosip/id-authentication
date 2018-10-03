package io.mosip.kernel.pridgenerator.cache;

/**
 * @author M1037462
 * @since 1.0.0
 *
 */
public interface PridCacheManager {

	public boolean contains(String prid);

	public boolean add(String prid);

}
