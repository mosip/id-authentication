package io.mosip.kernal.fsadapter.hdfs.test.util;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtil;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConnectionUtil.class, loader = AnnotationConfigContextLoader.class)
public class ConnectionUtilTest {

	@Mock
	private ConnectionUtil connectionUtil;

	@Before
	public void setUp() throws IOException {
		ReflectionTestUtils.setField(connectionUtil, "nameNodeUrl", "hdfs://127.0.0.1:51000");
		ReflectionTestUtils.setField(connectionUtil, "kdcDomain", "EXAMPLE.COM");
		ReflectionTestUtils.setField(connectionUtil, "userName", "testuser");
		ReflectionTestUtils.setField(connectionUtil, "userPass", "testpass");
	}

	@Test
	public void getConfiguredFileSystemTest() throws IOException {
		connectionUtil.getConfiguredFileSystem();
	}
}
