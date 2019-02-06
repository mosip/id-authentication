package io.mosip.kernel.idrepo.dfsadapter.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;

@RunWith(MockitoJUnitRunner.class)
public class AmazonS3DFSProviderTest {
	
	@Mock
	AmazonS3 connection;

	AmazonS3DFSProvider s3 = new AmazonS3DFSProvider();
	
	@Before
	public void setup() {
		ReflectionTestUtils.setField(s3, "accessKey", "accessKey");
		ReflectionTestUtils.setField(s3, "secretKey", "secretKey");
		ReflectionTestUtils.setField(s3, "endpoint", "endpoint");
		ReflectionTestUtils.setField(s3, "connection", connection);
	}
	
	@Test
	public void testStoreFile() throws IdRepoAppException {
		when(connection.doesBucketExistV2(Mockito.any())).thenReturn(false);
		s3.storeFile("1233", "123", new byte[] { 0 });
	}
	
	@Test(expected = IdRepoAppException.class)
	public void testStoreFileException() throws IdRepoAppException {
		when(connection.doesBucketExistV2(Mockito.any())).thenThrow(new SdkClientException(""));
		s3.storeFile("1233", "123", new byte[] { 0 });
	}
	
	@Test
	public void testGetFile() throws IdRepoAppException {
		when(connection.doesBucketExistV2(Mockito.any())).thenReturn(true);
		when(connection.doesObjectExist(Mockito.any(), Mockito.any())).thenReturn(true);
		S3Object s3ObjectMock = mock(S3Object.class);
		when(s3ObjectMock.getObjectContent())
				.thenReturn(new S3ObjectInputStream(IOUtils.toInputStream("1234", Charset.defaultCharset()), null));
		when(connection.getObject(Mockito.any())).thenReturn(s3ObjectMock);
		s3.getFile("1233", "123");
	}
	
	@Test(expected = IdRepoAppException.class)
	public void testGetFileException() throws IdRepoAppException {
		when(connection.doesBucketExistV2(Mockito.any())).thenThrow(new SdkClientException(""));
		s3.getFile("1233", "123");
	}
	
	@Test
	public void testGetFileNoBucket() throws IdRepoAppException {
		when(connection.doesBucketExistV2(Mockito.any())).thenReturn(false);
		s3.getFile("1233", "123");
	}
}
