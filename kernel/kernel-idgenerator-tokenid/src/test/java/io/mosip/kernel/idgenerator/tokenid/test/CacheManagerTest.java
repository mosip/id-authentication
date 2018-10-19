package io.mosip.kernel.idgenerator.tokenid.test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.idgenerator.tokenid.cache.impl.TokenIdCacheManagerImpl;
import io.mosip.kernel.idgenerator.tokenid.repository.TokenIdRepository;

@RunWith(SpringRunner.class)
public class CacheManagerTest {
  
	@InjectMocks
	TokenIdCacheManagerImpl tokenIdCacheManager;
	
	@Mock
	TokenIdRepository tokenIdRepository;
	
	String testTokenId="95463470923203664956734524854603577";
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.tokenIdCacheManager.pridCacheManagerPostConstruct();
	}
	
	
	@Test
	public void addTokenSuccessTest() {
		boolean isAdded=tokenIdCacheManager.add(testTokenId);
		assertTrue(isAdded);
		}
	
	@Test
	public void addTokenFailureTest() {
		tokenIdCacheManager.add(testTokenId);
		boolean isAdded=tokenIdCacheManager.add(testTokenId);
		assertFalse(isAdded);
	}
	
	@Test
	public void checkTokenIdIsPresentInTheList() {
		tokenIdCacheManager.add(testTokenId);
		boolean isPresent=tokenIdCacheManager.contains(testTokenId);
		assertTrue(isPresent);
	}
}
