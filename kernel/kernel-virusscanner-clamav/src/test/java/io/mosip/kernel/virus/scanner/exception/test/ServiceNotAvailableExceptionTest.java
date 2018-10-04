package io.mosip.kernel.virus.scanner.exception.test;

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

import io.mosip.kernel.virus.scanner.exception.ServerNotAccessibleException;
import io.mosip.kernel.virus.scanner.service.VirusScannerService;
import io.mosip.kernel.virus.scanner.service.impl.VirusScannerServiceImpl;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.exceptions.ClamavException;

/**
 * 
 * @author Mukul Puspam
 *
 */
//@RunWith(MockitoJUnitRunner.class)
public class ServiceNotAvailableExceptionTest {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	//@Mock
	ClamavClient clamavClient;

	//@InjectMocks
	private VirusScannerService<Boolean, String> virusScannerService = new VirusScannerServiceImpl() {
		@Override
		public void createConnection() {
			this.clamavClient = clamavClient;
		}
	};

	private File file;
	private File folder;

	//@Before
	public void setup() throws Exception {
		file = new File("C:/Users/M1039303/Desktop/disk/sdc/1001.zip");
		folder = new File("C:/Users/M1039303/Desktop/disk/sdc");
	}

	//@Test(expected = ServerNotAccessibleException.class)
	public void serviceUnavailableForScanFileTest() throws ClamavException {
		Mockito.doThrow(ClamavException.class).when(clamavClient).scan(file.toPath());
		virusScannerService.scanFile(file.getAbsolutePath());
	}

	//@Test(expected = ServerNotAccessibleException.class)
	public void serviceUnavailableForScanFolderTest() throws ClamavException {
		Mockito.doThrow(ClamavException.class).when(clamavClient).scan(folder.toPath(), false);
		virusScannerService.scanFolder(folder.getAbsolutePath());
	}
}
