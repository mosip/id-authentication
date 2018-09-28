package org.mosip.kernel.vidgenerator.cache.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.mosip.kernel.vidgenerator.cache.VidCacheManager;
import org.mosip.kernel.vidgenerator.dao.VidDao;
import org.mosip.kernel.vidgenerator.model.Vid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author M1043226
 * @since 1.0.0
 *
 */
@Component
public class VidCacheManagerImpl implements VidCacheManager {
	@Autowired
	VidDao vidDao;
	Map<String, Vid> uinVidMap = new HashMap<>();
	Map<String, Long> vidTimeStampMap = new HashMap<>();
	List<Vid> vids = new ArrayList<>();





	@PostConstruct
	public void vidCacheManagerPostConstruct() {
		vids = vidDao.findAll();
		uinVidMap = vids.stream().collect(Collectors.toMap(Vid::getUin, vid -> vid));
		vidTimeStampMap = vids.stream().collect(Collectors.toMap(Vid::getId, Vid::getCreatedAt));
	}





	@Override
	public Vid findByUin(String uin) {
		return uinVidMap.get(uin);
	}





	@Override
	public boolean saveOrUpdate(Vid vid) {
		uinVidMap.put(vid.getUin(), vid);
		vidTimeStampMap.put(vid.getId(), vid.getCreatedAt());
		return true;
	}





	@Override
	public boolean containsUin(String uin) {
		return uinVidMap.containsKey(uin);
	}





	@Override
	public boolean containsVid(String vid) {
		return vidTimeStampMap.containsKey(vid);
	}
}
