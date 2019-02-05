package io.mosip.kernal.fsadapter.hdfs.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtil;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConnectionUtil.class, loader = AnnotationConfigContextLoader.class)
public class ConnectionUtilTest {

	@Autowired
	private ConnectionUtil connectionUtil;

	@Before
	public void setUp() throws IOException {
		ReflectionTestUtils.setField(connectionUtil, "nameNodeUrl", "hdfs://127.0.0.1:51000");
		ReflectionTestUtils.setField(connectionUtil, "userName", "userName");
	}

	@Test
	public void getConfiguredFileSystemTest() throws IOException {
		FileSystem fs = connectionUtil.getConfiguredFileSystem();
		assertThat(fs, is(notNullValue()));
	}
}
