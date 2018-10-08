package io.mosip.registration.core.cache.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.core.cache.GroupidCacheManager;
import io.mosip.registration.core.entity.Groupid;
import io.mosip.registration.core.repository.GroupidRepository;


/**
 * 
 * @author M1037717
 * since 1.0.0
 *
 */
@Component
public class GroupidCacheManagerImpl implements GroupidCacheManager {
	@Autowired
	private GroupidRepository GroupidGenRepository;
	Set<String> Groupids = new HashSet<>();





	/**
	 * This method fetches the list of Groupid from database and add in the Set
	 */
	@PostConstruct
	public void pridCacheManagerPostConstruct() {
		List<Groupid> groupIdList = GroupidGenRepository.findAll();
		for (Groupid id : groupIdList) {
			Groupids.add(id.getId());
		}
	}





	/**
	 * @param Groupid
	 * @return Set of Groupid
	 */
	@Override
	public boolean add(String groupid) {
		return Groupids.add(groupid);
	}





	/**
	 * @param Groupid
	 * @return true if Groupid is present in the set 
	 */
	@Override
	public boolean contains(String groupid) {
		return Groupids.contains(groupid);
	}
}
