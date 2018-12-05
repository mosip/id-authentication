package io.mosip.kernel.virusscanner.clamav.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestPropertySource;

import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.kernel.virusscanner.clamav.impl.VirusScannerImpl;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;
import xyz.capybara.clamav.commands.scan.result.ScanResult.Status;
import xyz.capybara.clamav.exceptions.ClamavException;

/**
 * 
 * @author Mukul Puspam
 *
 */
//@RunWith(MockitoJUnitRunner.class)
@TestPropertySource({ "classpath:application.properties" })
public class VirusScannerServiceTest {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Mock
	ClamavClient clamavClient;

	@InjectMocks
	private VirusScanner<Boolean, String> virusScanner = new VirusScannerImpl() {
		@Override
		public void createConnection() {
			this.clamavClient = clamavClient;
		}
	};

	private File file;
	private File folder;
	private ScanResult virusFound;
	private ScanResult virusNotFound;
	private File doc;
	private byte[] byteArray;

	//@Before
	public void setup() {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("files/0000.zip").getFile());
		folder = new File(classLoader.getResource("files").getFile());
		virusNotFound = new ScanResult(Status.OK);
		virusFound = new ScanResult(Status.VIRUS_FOUND);
		doc= new File("D://test1.docx");
	    byteArray = new byte[(int) doc.length()]; 
	}

	//@Test
	public void infectedFileCheck() throws ClamavException{
			Mockito.when(clamavClient.scan(file.toPath())).thenReturn(virusFound);
			Boolean result = virusScanner.scanFile(file.getAbsolutePath());
			assertEquals(Boolean.FALSE, result);
	}

	//@Test
	public void nonInfectedFileCheck() throws ClamavException{
			Mockito.when(clamavClient.scan(file.toPath())).thenReturn(virusNotFound);
			Boolean result = virusScanner.scanFile(file.getAbsolutePath());
			assertEquals(Boolean.TRUE, result);
	}

	//@Test
	public void infectedFolderCheck() throws ClamavException{
			Mockito.when(clamavClient.scan(folder.toPath(), false)).thenReturn(virusFound);
			Boolean result = virusScanner.scanFolder(folder.getAbsolutePath());
			assertEquals(Boolean.FALSE, result);
	}

	//@Test
	public void nonInfectedFolderCheck() throws ClamavException{
			Mockito.when(clamavClient.scan(folder.toPath(), false)).thenReturn(virusNotFound);
			Boolean result = virusScanner.scanFolder(folder.getAbsolutePath());
			assertEquals(Boolean.TRUE, result);
	
	}
	
	//@Test
	public void nonInfectedDocCheck() throws ClamavException, IOException{
			Mockito.when(clamavClient.scan(any(InputStream.class))).thenReturn(virusNotFound);
			Boolean result = virusScanner.scanDocument(byteArray);
			assertEquals(Boolean.TRUE, result);
	
	}
	
	//@Test
	public void InfectedDocCheck() throws ClamavException, IOException{
			Mockito.when(clamavClient.scan(any(InputStream.class))).thenReturn(virusFound);
			Boolean result = virusScanner.scanDocument(byteArray);
			assertEquals(Boolean.FALSE, result);
	}
	
	//@Test
	public void nonInfectedDocumentCheck() throws ClamavException, IOException{
			Mockito.when(clamavClient.scan(any(FileInputStream.class))).thenReturn(virusNotFound);
			Boolean result = virusScanner.scanDocument(doc);
			assertEquals(Boolean.TRUE, result);
	}
	
	//@Test
	public void InfectedDocumentCheck() throws ClamavException, IOException{
			Mockito.when(clamavClient.scan(any(FileInputStream.class))).thenReturn(virusFound);
			Boolean result = virusScanner.scanDocument(doc);
			assertEquals(Boolean.FALSE, result);
	}
}
