package io.mosip.kernel.fsadapter.ceph.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;

/**
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FileSystemAdapterCephExceptionTest {

	@Autowired
	private FileSystemAdapter fileSystemAdapter;

	@Value("${mosip.kernel.fsadapter.ceph.test.check.enrolment.id}")
	private String enrollmentId;

	private AmazonS3 amazonS3;

	@Before
	public void setup() {
		amazonS3 = Mockito.mock(AmazonS3.class);
		ReflectionTestUtils.setField(fileSystemAdapter, "conn", amazonS3, AmazonS3.class);
	}

	@Test(expected = FSAdapterException.class)
	public void testGetpacketNotFoundException() {
		AmazonS3Exception amzException = new AmazonS3Exception("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 404);
		when(amazonS3.doesBucketExistV2(Mockito.anyString())).thenThrow(amzException);
		InputStream packet = new InputStream() {
			@Override
			public int read() throws IOException {
				return 0;
			}
		};
		this.fileSystemAdapter.storePacket(enrollmentId, packet);

	}

	@Test(expected = FSAdapterException.class)
	public void testGetpacketInvalidConnParameterException() {
		AmazonServiceException amzException = new AmazonServiceException("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		when(amazonS3.doesBucketExistV2(Mockito.anyString())).thenThrow(amzException);
		InputStream packet = new InputStream() {
			@Override
			public int read() throws IOException {
				return 0;
			}
		};
		this.fileSystemAdapter.storePacket(enrollmentId, packet);

	}

	@Test(expected = FSAdapterException.class)
	public void testdeletepacketException() {
		AmazonServiceException amzException = new AmazonServiceException("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		doThrow(amzException).when(amazonS3).deleteObject(Mockito.anyString(), Mockito.anyString());
		this.fileSystemAdapter.deletePacket(enrollmentId);

	}

	@Test(expected = FSAdapterException.class)
	public void testdeletepacketInvalidConnParameterException() {
		AmazonS3Exception amzException = new AmazonS3Exception("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		doThrow(amzException).when(amazonS3).deleteObject(Mockito.anyString(), Mockito.anyString());
		this.fileSystemAdapter.deletePacket(enrollmentId);

	}

	@Test(expected = FSAdapterException.class)
	public void testdeleteFileException() {
		AmazonServiceException amzException = new AmazonServiceException("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		doThrow(amzException).when(amazonS3).deleteObject(Mockito.anyString(), Mockito.anyString());
		this.fileSystemAdapter.deleteFile(enrollmentId, "filename");

	}

	@Test(expected = FSAdapterException.class)
	public void testdeleteFileInvalidConnParameterException() {
		AmazonS3Exception amzException = new AmazonS3Exception("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		doThrow(amzException).when(amazonS3).deleteObject(Mockito.anyString(), Mockito.anyString());
		this.fileSystemAdapter.deleteFile(enrollmentId, "filename");

	}

	@Test(expected = FSAdapterException.class)
	public void testgetFileInvalidConnParameterException() {
		AmazonS3Exception amzException = new AmazonS3Exception("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		doThrow(amzException).when(amazonS3).getObject(Mockito.any());
		this.fileSystemAdapter.getFile(enrollmentId, "filename");

	}

	@Test(expected = FSAdapterException.class)
	public void testStoreFileInvalidConnParameterException() {
		AmazonS3Exception amzException = new AmazonS3Exception("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		when(amazonS3.putObject(Mockito.anyString(), Mockito.anyString(), Mockito.any(File.class)))
				.thenThrow(amzException);
		this.fileSystemAdapter.storePacket(enrollmentId, new File("/path"));

	}

	@Test
	public void testIsPacketPresent() {
		S3Object object = new S3Object();
		object.setObjectContent(new S3ObjectInputStream(new ByteArrayInputStream("test".getBytes()), new HttpGet()));
		when(amazonS3.getObject(Mockito.any())).thenReturn(object);
		assertTrue(fileSystemAdapter.isPacketPresent(enrollmentId));

	}

	@Test
	public void testIsPacketPresentFalse() {
		when(amazonS3.getObject(Mockito.any())).thenReturn(null);
		assertFalse(fileSystemAdapter.isPacketPresent(enrollmentId));
	}

	@Test(expected = FSAdapterException.class)
	public void testCopyFileInvalidConnParameterException() {
		AmazonS3Exception amzException = new AmazonS3Exception("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		doThrow(amzException).when(amazonS3).doesBucketExistV2(Mockito.anyString());
		this.fileSystemAdapter.copyFile("test.zip", "test", "destination", "destinationFileName");
	}

	@Test(expected = FSAdapterException.class)
	public void testCopyFileInvalidException() {
		AmazonServiceException amzException = new AmazonServiceException("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		doThrow(amzException).when(amazonS3).doesBucketExistV2(Mockito.anyString());
		this.fileSystemAdapter.copyFile("test.zip", "test", "destination", "destinationFileName");
	}

	@Test
	public void testCopyFile() {
		doReturn(false).when(amazonS3).doesBucketExistV2(Mockito.anyString());
		this.fileSystemAdapter.copyFile("test.zip", "test", "destination", "destinationFileName");
	}

	@Test(expected = FSAdapterException.class)
	public void testStorePacketInvalidException() {
		AmazonServiceException amzException = new AmazonServiceException("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		when(amazonS3.doesBucketExistV2(Mockito.anyString())).thenThrow(amzException);
		this.fileSystemAdapter.storePacket("test", new File("test.zip"));
	}

	@Test(expected = FSAdapterException.class)
	public void testStoreFileWithKeyInvalidException() {
		AmazonServiceException amzException = new AmazonServiceException("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		when(amazonS3.doesBucketExistV2(Mockito.anyString())).thenThrow(amzException);
		this.fileSystemAdapter.storeFile("test", "test-key", new ByteArrayInputStream("test".getBytes()));
	}

	@Test(expected = FSAdapterException.class)
	public void testStoreFileWithKeyException() {
		AmazonS3Exception amzException = new AmazonS3Exception("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		when(amazonS3.doesBucketExistV2(Mockito.anyString())).thenThrow(amzException);
		this.fileSystemAdapter.storeFile("test", "test-key", new ByteArrayInputStream("test".getBytes()));
	}

	@Test(expected = FSAdapterException.class)
	public void testGetFileException() {
		AmazonServiceException amzException = new AmazonServiceException("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		when(amazonS3.getObject(Mockito.any())).thenThrow(amzException);
		this.fileSystemAdapter.getFile("test", "test-key");
	}

	@Test(expected = FSAdapterException.class)
	public void testGetPacketException() {
		AmazonServiceException amzException = new AmazonServiceException("test");
		ReflectionTestUtils.setField(amzException, "statusCode", 403);
		when(amazonS3.getObject(Mockito.any())).thenThrow(amzException);
		this.fileSystemAdapter.getPacket("test");
	}

}
