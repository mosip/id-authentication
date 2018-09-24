package org.mosip.kernel.virus.scanner.service.test;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mosip.kernel.virus.scanner.service.VirusScannerService;
import org.mosip.kernel.virus.scanner.service.impl.VirusScannerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestPropertySource;

import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;
import xyz.capybara.clamav.commands.scan.result.ScanResult.Status;
import xyz.capybara.clamav.exceptions.ClamavException;

/**
 * 
 * @author Mukul Puspam
 *
 */
@RunWith(MockitoJUnitRunner.class)
@TestPropertySource({ "classpath:application.properties" })
public class VirusScannerServiceTest {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Mock
	ClamavClient clamavClient;

	@InjectMocks
	private VirusScannerService<Boolean, String> virusScannerService = new VirusScannerServiceImpl() {
		@Override
		public void createConnection() {
			this.clamavClient = clamavClient;
		}
	};

	private File file;
	private File folder;
	private ScanResult virusFound;
	private ScanResult virusNotFound;

	@Before
	public void setup() {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("files/0000.zip").getFile());
		folder = new File(classLoader.getResource("files").getFile());
		virusNotFound = new ScanResult(Status.OK);
		virusFound = new ScanResult(Status.VIRUS_FOUND);
	}

	@Test
	public void infectedFileCheck() throws ClamavException{
			Mockito.when(clamavClient.scan(file.toPath())).thenReturn(virusFound);
			Boolean result = virusScannerService.scanFile(file.getAbsolutePath());
			assertEquals(Boolean.FALSE, result);
	}

	@Test
	public void nonInfectedFileCheck() throws ClamavException{
			Mockito.when(clamavClient.scan(file.toPath())).thenReturn(virusNotFound);
			Boolean result = virusScannerService.scanFile(file.getAbsolutePath());
			assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void infectedFolderCheck() throws ClamavException{
			Mockito.when(clamavClient.scan(folder.toPath(), false)).thenReturn(virusFound);
			Boolean result = virusScannerService.scanFolder(folder.getAbsolutePath());
			assertEquals(Boolean.FALSE, result);
	}

	@Test
	public void nonInfectedFolderCheck() throws ClamavException{
			Mockito.when(clamavClient.scan(folder.toPath(), false)).thenReturn(virusNotFound);
			Boolean result = virusScannerService.scanFolder(folder.getAbsolutePath());
			assertEquals(Boolean.TRUE, result);
	
	}
}
