package io.mosip.preregistration.core.cache;


public interface GroupidCacheManager {

	public boolean contains(String prid);

	public boolean add(String prid);

}
