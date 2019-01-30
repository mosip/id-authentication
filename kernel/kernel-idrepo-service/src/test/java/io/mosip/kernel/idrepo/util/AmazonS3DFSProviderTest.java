package io.mosip.kernel.idrepo.util;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.idrepo.dfsadapter.impl.AmazonS3DFSProvider;

public class AmazonS3DFSProviderTest {
	AmazonS3DFSProvider util = new AmazonS3DFSProvider();
	
	@Test
	public void testGetConnection() {
		ReflectionTestUtils.setField(util, "accessKey", "accessKey");
		ReflectionTestUtils.setField(util, "secretKey", "secretKey");
		ReflectionTestUtils.setField(util, "endpoint", "endpoint");
		util.getConnection();
	}
}
