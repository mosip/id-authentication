package org.mosip.kernel.vidgenerator.cache;

import org.mosip.kernel.vidgenerator.entity.Vid;

/**
 * @author M1043226
 * @since 1.0.0
 *
 */
public interface VidCacheManager {

	public Vid findByUin(String uin);

	public boolean saveOrUpdate(Vid vid);

	public boolean containsUin(String uin);

	public boolean containsVid(String vid);
}
