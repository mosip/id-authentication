package io.mosip.kernel.idgenerator.tokenid.cache.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.idgenerator.tokenid.cache.TokenIdCacheManager;
import io.mosip.kernel.idgenerator.tokenid.entity.TokenId;
import io.mosip.kernel.idgenerator.tokenid.repository.TokenIdRepository;

@Component
public class TokenIdCacheManagerImpl implements TokenIdCacheManager {
   
	@Autowired
	private TokenIdRepository tokenGenRepository;
	
	
	Set<String> tokenIdList = new HashSet<>();

     /**
	 * This method fetches the list of TokenId from database and add in the Set
	 */
	@PostConstruct
	public void pridCacheManagerPostConstruct() {
		
		List<TokenId> tokenIds = tokenGenRepository.findAll();
		for (TokenId tokenIdObj : tokenIds) {
			tokenIdList.add(tokenIdObj.getId());
		}
	}
	@Override
	public boolean contains(String tokenId) {
		
		return tokenIdList.contains(tokenId);
	}

	@Override
	public boolean add(String tokenId) {
		
		return tokenIdList.add(tokenId);	}

	

}
