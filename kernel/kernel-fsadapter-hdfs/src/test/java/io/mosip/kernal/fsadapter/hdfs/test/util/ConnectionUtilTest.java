package io.mosip.kernal.fsadapter.hdfs.test.util;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtils;

@RunWith(PowerMockRunner.class)
@ContextConfiguration(classes = ConnectionUtils.class, loader = AnnotationConfigContextLoader.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(value = { UserGroupInformation.class, FileSystem.class })
public class ConnectionUtilTest {

	@Autowired
	private ConnectionUtils connectionUtil;

	@BeforeClass
	public static void setupOnce() {
		PowerMockito.mockStatic(UserGroupInformation.class);
		PowerMockito.mockStatic(FileSystem.class);
	}

	@Before
	public void setUp() throws IOException {
		ReflectionTestUtils.setField(connectionUtil, "nameNodeUrl", "hdfs://127.0.0.1:51000");
		ReflectionTestUtils.setField(connectionUtil, "kdcDomain", "EXAMPLE.COM");
		ReflectionTestUtils.setField(connectionUtil, "userName", "testuser");
		ReflectionTestUtils.setField(connectionUtil, "keytabPath", "test.keytab");
	}

	@After
	public void setAfter() {
		ReflectionTestUtils.setField(connectionUtil, "configuredFileSystem", null);
		ReflectionTestUtils.setField(connectionUtil, "isAuthEnable", false);
		UserGroupInformation.reset();
	}

	@Test
	public void getConfiguredFileSystemTest() throws IOException {
		ReflectionTestUtils.setField(connectionUtil, "isAuthEnable", false);
		connectionUtil.getConfiguredFileSystem();
	}

	@Test(expected = FSAdapterException.class)
	public void getConfiguredFileSystemTestWithAuth() throws IOException {
		ReflectionTestUtils.setField(connectionUtil, "isAuthEnable", true);
		connectionUtil.getConfiguredFileSystem();
	}

	@Test(expected = FSAdapterException.class)
	public void getConfiguredFileSystemTestNoKeytab() throws IOException {
		ReflectionTestUtils.setField(connectionUtil, "keytabPath", "abc.keytab");
		ReflectionTestUtils.setField(connectionUtil, "isAuthEnable", true);
		connectionUtil.getConfiguredFileSystem();
	}

	@Test
	public void getConfiguredFileSystemTestLoginSuccess() throws Exception {
		ReflectionTestUtils.setField(connectionUtil, "isAuthEnable", true);
		connectionUtil.getConfiguredFileSystem();
	}

	@Test(expected = FSAdapterException.class)
	public void getConfiguredFileSystemFailToCreateRemoteUser() throws Exception {
		PowerMockito.mockStatic(UserGroupInformation.class);
		PowerMockito.when(UserGroupInformation.class, "createRemoteUser", Mockito.anyString(), Mockito.any())
				.then(invocation -> {
					throw new IOException();
				});
		connectionUtil.getConfiguredFileSystem();
	}

	@Test(expected = FSAdapterException.class)
	public void getConfiguredFileSystemFailToCreateRemoteUserInterrupted() throws Exception {
		PowerMockito.mockStatic(UserGroupInformation.class);
		PowerMockito.when(UserGroupInformation.class, "createRemoteUser", Mockito.anyString(), Mockito.any())
				.then(invocation -> {
					throw new InterruptedException();
				});
		connectionUtil.getConfiguredFileSystem();
	}
}
