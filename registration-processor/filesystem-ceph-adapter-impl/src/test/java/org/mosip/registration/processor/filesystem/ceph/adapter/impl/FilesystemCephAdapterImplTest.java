package org.mosip.registration.processor.filesystem.ceph.adapter.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.registration.processor.filesystem.adapter.FileSystemAdapter;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.PacketNotFoundException;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.ConnectionUtil;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.findify.s3mock.S3Mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * This class tests the functionalities of DFSAdapterImpl
 * 
 * @author Pranav Kumar
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ConnectionUtil.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class FilesystemCephAdapterImplTest {

	private S3Mock api;
	private AmazonS3 client;
	private String checkEnrolmentId;
	private String fileExtension;
	private static final String CONFIG_FILE_NAME = "config.properties";
	private FileSystemAdapter<InputStream, PacketFiles, Boolean> dfsAdapter;
	private static final String FAILURE_ENROLMENT_ID = "1234"; 

	/**
	 * This method sets up the required configuration before execution of test cases
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		Properties properties = new Properties();
		InputStream inputStream;
		inputStream = ConnectionUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.checkEnrolmentId = properties.getProperty("check.enrolment.id");
		this.fileExtension = properties.getProperty("file.extension");
		api = new S3Mock.Builder().withPort(8001).withInMemoryBackend().build();
		api.start();
		EndpointConfiguration endpoint = new EndpointConfiguration("http://localhost:8001", "us-west-2");
		client = AmazonS3ClientBuilder.standard().withPathStyleAccessEnabled(true).withEndpointConfiguration(endpoint)
				.withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials())).build();
		PowerMockito.mockStatic(ConnectionUtil.class);
		when(ConnectionUtil.getConnection()).thenReturn(client);
		
		//Putting a file to mocked ceph instance
		this.dfsAdapter = new FilesystemCephAdapterImpl();
		ClassLoader classLoader = getClass().getClassLoader();
		String filePath = classLoader.getResource(checkEnrolmentId + fileExtension).getFile();
		File packet = new File(filePath);
		dfsAdapter.storePacket(this.checkEnrolmentId, packet);
	}

	/**
	 * This method tests uploading of a packet to DFS
	 * 
	 * @throws IOException
	 */
	@Test
	public void uploadPacketTest() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String filePath = classLoader.getResource(checkEnrolmentId + fileExtension).getFile();
		File packet = new File(filePath);
		boolean result = this.dfsAdapter.storePacket(this.checkEnrolmentId, packet);
		assertEquals(true, result);
	}
	
	/**
	 * This method tests getting a packet successfully from DFS
	 * 
	 * @throws IOException
	 */
	@Test
	public void getPacketSuccessTest() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String filePath = classLoader.getResource(checkEnrolmentId + fileExtension).getFile();
		InputStream packet = dfsAdapter.getPacket(checkEnrolmentId);
		ZipInputStream zis = new ZipInputStream(packet);
		ZipEntry ze = zis.getNextEntry();
		int actualFileCount = 0;
		while (ze != null) {
			actualFileCount++;
			ze = zis.getNextEntry();
		}
		ZipFile actualZipFile = new ZipFile(filePath);
		Enumeration<? extends ZipEntry> entries = actualZipFile.entries();
		int expectedFileCount = 0;
		while (entries.hasMoreElements()) {
			expectedFileCount++;
			entries.nextElement();
		}
		assertEquals(expectedFileCount, actualFileCount);
		actualZipFile.close();
	}
	
	/**
	 * This method tests getting a packet which is not present in DFS
	 * 
	 * @throws IOException
	 */
	@Test(expected = PacketNotFoundException.class)
	public void getPacketFailureTest() throws IOException {
		this.dfsAdapter.unpackPacket(FAILURE_ENROLMENT_ID);
	}
	
	/**
	 * This method checks unpacking of a packet in DFS
	 * 
	 * @throws IOException
	 */
	@Test
	public void unpackPacketTest() throws IOException {
		this.dfsAdapter.unpackPacket(checkEnrolmentId);
		int noOfextractedFiles = this.client.listObjects(this.checkEnrolmentId).getObjectSummaries().size();
		assertEquals(5, noOfextractedFiles);
		//check file exists test
		boolean findResult = this.dfsAdapter.checkFileExistence(checkEnrolmentId, PacketFiles.BIOMETRICS);
		assertEquals(true, findResult);
		//Get file test
		InputStream file = this.dfsAdapter.getFile(checkEnrolmentId, PacketFiles.DEMOGRAPHICS);
		assertNotNull(file);
		//Delete file test
		boolean result = this.dfsAdapter.deleteFile(checkEnrolmentId, PacketFiles.DEMOGRAPHICS);
		assertEquals(true, result);
	}
	
	/**
	 * This method checks fetching a file from a packet that is not present in DFS
	 */
	@Test(expected=PacketNotFoundException.class)
	public void getFileFailureTest() {
		this.dfsAdapter.getFile(FAILURE_ENROLMENT_ID, PacketFiles.DEMOGRAPHICS);
	}
	
	/**
	 * This method checks deleting a packet from DFS and later fetching it
	 */
	@Test(expected=PacketNotFoundException.class)
	public void deletePacketTest() {
		this.dfsAdapter.deletePacket(checkEnrolmentId);
		this.dfsAdapter.getPacket(checkEnrolmentId);
	}

	/**
	 * This method destroys the dummy DFS connection
	 */
	@After
	public void destroy() {
		api.stop();
	}

}