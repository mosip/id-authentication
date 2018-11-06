package io.mosip.kernel.masterdata.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.exception.BlacklistedWordsIllegalArgException;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BlacklistedServiceTest {
	@Autowired
	private BlacklistedWordsService blacklistedWordsService;
	
	@Test(expected=BlacklistedWordsIllegalArgException.class)
	public void testGetAllBlacklistedWordsNullvalue() {
		blacklistedWordsService.getAllBlacklistedWordsBylangCode(null);
	}
	
	@Test(expected=BlacklistedWordsIllegalArgException.class)
	public void testGetAllBlacklistedWordsEmptyvalue() {
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("");
	}
}
