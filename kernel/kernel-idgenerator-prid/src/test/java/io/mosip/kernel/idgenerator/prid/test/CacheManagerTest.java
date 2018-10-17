package io.mosip.kernel.idgenerator.prid.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.idgenerator.prid.cache.PridCacheManager;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CacheManagerTest {

	
	@Autowired
	PridCacheManager pridCacheManager;
	
	private String prid="1456290634502789";
	
	@Test
	public void addPridToList() {
         boolean isAdded= pridCacheManager.add(prid);
         assertTrue(isAdded);
	}
	
	@Test
	public void addPridToListFailure() {
         boolean isAdded= pridCacheManager.add(prid);
         assertFalse(isAdded);
	}
	
	@Test
	public void constiansPridtest() {
		boolean contains=pridCacheManager.contains(prid);
		assertTrue(contains);
	}
	
}
