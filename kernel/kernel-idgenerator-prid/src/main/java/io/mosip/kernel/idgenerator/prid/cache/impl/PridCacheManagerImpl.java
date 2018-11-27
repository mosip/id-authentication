package io.mosip.kernel.idgenerator.prid.cache.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.idgenerator.prid.cache.PridCacheManager;
import io.mosip.kernel.idgenerator.prid.entity.Prid;
import io.mosip.kernel.idgenerator.prid.repository.PridRepository;

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
	 * @param prid-input parameter PRID
	 * @return true or false
	 */
	@Override
	public boolean add(String prid) {
		return prids.add(prid);
	}





	/**
	 * @param prid - input parameter PRID
	 * @return true or false
	 */
	@Override
	public boolean contains(String prid) {
		return prids.contains(prid);
	}
}
