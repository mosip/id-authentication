package org.mosip.kernel.vidgenerator.cache.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.mosip.kernel.vidgenerator.cache.VidCacheManager;
import org.mosip.kernel.vidgenerator.dao.VidDao;
import org.mosip.kernel.vidgenerator.model.VId;
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

	Map<String, VId> uinVidMap = new HashMap<>();
	Map<String, Long> vIdTimeStampMap = new HashMap<>();
	List<VId> vIds = new ArrayList<>();

	@PostConstruct
	public void  VidCacheManagerPostConstruct() {
		vIds = vidDao.findAll();
		uinVidMap = vIds.stream().collect(Collectors.toMap(VId::getUin, VId -> VId));
		vIdTimeStampMap = vIds.stream().collect(Collectors.toMap(VId::getVid, VId::getCreatedAt));

	}

	@Override
	public VId findByUin(String uin) {
		return uinVidMap.get(uin);
	}

	@Override
	public boolean saveOrUpdate(VId vid) {
		uinVidMap.put(vid.getUin(), vid);
		vIdTimeStampMap.put(vid.getVid(), vid.getCreatedAt());
		return true;
	}

	@Override
	public boolean containsUin(String uin) {
		return uinVidMap.containsKey(uin);
	}

	@Override
	public boolean containsVid(String vid) {
		return vIdTimeStampMap.containsKey(vid);
	}

}
