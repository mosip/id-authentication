package io.mosip.kernel.virusscanner.clamav.test;

import java.io.File;

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

	@Before
	public void setup() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("files/0000.zip").getFile());
		folder = new File(classLoader.getResource("files").getFile());
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
}
