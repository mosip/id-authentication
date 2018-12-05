package io.mosip.kernel.virusscanner.clamav.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.kernel.core.virusscanner.exception.VirusScannerException;
import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.kernel.virusscanner.clamav.impl.VirusScannerImpl;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.exceptions.ClamavException;

/**
 * 
 * @author Mukul Puspam
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class VirusScannerExceptionTest {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Mock
	ClamavClient clamavClient;

	@InjectMocks
	private VirusScanner<Boolean, String> virusScannerService = new VirusScannerImpl() {
		@Override
		public void createConnection() {
			this.clamavClient = clamavClient;
		}
	};

	private File file;
	private File folder;
	private File doc;
	private byte[] byteArray;

	@Before
	public void setup() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("files/0000.zip").getFile());
		folder = new File(classLoader.getResource("files").getFile());
		doc= new File(classLoader.getResource("files/test1.docx").getFile());
	    byteArray = new byte[(int) doc.length()]; 
	}

	@Test(expected = VirusScannerException.class)
	public void serviceUnavailableForScanFileTest() throws ClamavException {
		Mockito.doThrow(ClamavException.class).when(clamavClient).scan(file.toPath());
		virusScannerService.scanFile(file.getAbsolutePath());
	}

	@Test(expected = VirusScannerException.class)
	public void serviceUnavailableForScanFolderTest() throws ClamavException {
		Mockito.doThrow(ClamavException.class).when(clamavClient).scan(folder.toPath(), false);
		virusScannerService.scanFolder(folder.getAbsolutePath());
	}
	
	@Test(expected = VirusScannerException.class)
	public void serviceUnavailableForScanDocTest() throws ClamavException, IOException {
		Mockito.doThrow(ClamavException.class).when(clamavClient).scan(any(InputStream.class));
		virusScannerService.scanDocument(byteArray);
	}
	
	@Test(expected = VirusScannerException.class)
	public void serviceUnavailableForScanDocumentTest() throws ClamavException, IOException {
		Mockito.doThrow(ClamavException.class).when(clamavClient).scan(any(FileInputStream.class));
		virusScannerService.scanDocument(doc);
	}
}
