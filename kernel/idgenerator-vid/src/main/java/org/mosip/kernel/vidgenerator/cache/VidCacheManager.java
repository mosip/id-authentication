package org.mosip.kernel.vidgenerator.cache;

import org.mosip.kernel.vidgenerator.model.VId;

/**
 * @author M1043226
 * @since 1.0.0
 *
 */
public interface VidCacheManager {

	public VId findByUin(String uin);

	public boolean saveOrUpdate(VId vid);

	public boolean containsUin(String uin);

	public boolean containsVid(String vid);
}
