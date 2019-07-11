package io.mosip.registrationProcessor.perf.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.registrationProcessor.perf.service.SyncRequestCreater;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class TestSyncRequest {

	private static SyncRequestCreater syncRequest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String CONFIG_FILE = "config.properties";
		new PropertiesUtil().loadProperties(CONFIG_FILE);
		syncRequest = new SyncRequestCreater();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateSyncRequestMaster() {

		syncRequest.createSyncRequestMaster();
	}

}
