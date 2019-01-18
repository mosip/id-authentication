package io.mosip.kernel.idrepo.util;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class DFSConnectionUtilTest {
	DFSConnectionUtil util = new DFSConnectionUtil();
	
	@Test
	public void testGetConnection() {
		ReflectionTestUtils.setField(util, "accessKey", "accessKey");
		ReflectionTestUtils.setField(util, "secretKey", "secretKey");
		ReflectionTestUtils.setField(util, "endpoint", "endpoint");
		util.getConnection();
	}
}
