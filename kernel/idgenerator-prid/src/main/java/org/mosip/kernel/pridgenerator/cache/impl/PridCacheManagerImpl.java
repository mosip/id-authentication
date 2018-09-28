package org.mosip.kernel.pridgenerator.cache.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.mosip.kernel.pridgenerator.cache.PridCacheManager;
import org.mosip.kernel.pridgenerator.dao.PridGenRepository;
import org.mosip.kernel.pridgenerator.model.Prid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author M1037462
 * since 1.0.0
 *
 */
@Component
public class PridCacheManagerImpl implements PridCacheManager {
	@Autowired
	private PridGenRepository pridGenRepository;
	Set<String> prids = new HashSet<>();





	/**
	 * This method fetches the list of PRID from database and add in the Set
	 */
	@PostConstruct
	public void pridCacheManagerPostConstruct() {
		List<Prid> pidList = pridGenRepository.findAll();
		for (Prid id : pidList) {
			prids.add(id.getId());
		}
	}





	/**
	 * @param PRID
	 * @return Set of PRID
	 */
	@Override
	public boolean add(String prid) {
		return prids.add(prid);
	}





	/**
	 * @param PRID
	 * @return true if PRID is present in the set 
	 */
	@Override
	public boolean contains(String prid) {
		return prids.contains(prid);
	}
}
