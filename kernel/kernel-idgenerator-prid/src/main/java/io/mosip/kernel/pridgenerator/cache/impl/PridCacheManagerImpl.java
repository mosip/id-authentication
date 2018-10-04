package io.mosip.kernel.pridgenerator.cache.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.pridgenerator.cache.PridCacheManager;
import io.mosip.kernel.pridgenerator.entity.Prid;
import io.mosip.kernel.pridgenerator.repository.PridRepository;

/**
 * 
 * @author M1037462
 * since 1.0.0
 *
 */
@Component
public class PridCacheManagerImpl implements PridCacheManager {
	@Autowired
	private PridRepository pridGenRepository;
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
