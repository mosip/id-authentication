package io.mosip.registration.core.cache;


public interface GroupidCacheManager {

	public boolean contains(String prid);

	public boolean add(String prid);

}
