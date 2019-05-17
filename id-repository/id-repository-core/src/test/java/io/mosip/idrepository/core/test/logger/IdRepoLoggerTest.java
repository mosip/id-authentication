package io.mosip.idrepository.core.test.logger;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.mosip.idrepository.core.logger.IdRepoLogger;

public class IdRepoLoggerTest {

	@Test
	public void test() {
		IdRepoLogger.setUin("123");
		IdRepoLogger.setRid("123");
		IdRepoLogger.setVid("123");
		assertEquals("123", IdRepoLogger.getUin());
		assertEquals("123", IdRepoLogger.getRid());
		assertEquals("123", IdRepoLogger.getVid());
	}
}
