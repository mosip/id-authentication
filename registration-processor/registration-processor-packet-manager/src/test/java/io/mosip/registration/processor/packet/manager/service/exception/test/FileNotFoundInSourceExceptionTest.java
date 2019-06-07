package io.mosip.registration.processor.packet.manager.service.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInSourceException;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

@RefreshScope
@RunWith(PowerMockRunner.class)
public class FileNotFoundInSourceExceptionTest {

	@Mock
	private Environment env;

	@InjectMocks
	private FileManager<DirectoryPathDto, InputStream> fileManager = new FileManagerImpl();

	private String virusScanDec = "src/test/resources/decrypted";

	private String virusScanEnc = "src/test/resources/encrypted";

	@Test
	public void TestFileNotFoundInSourceException() {

		String fileName = "1001";

		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString())).thenReturn(virusScanEnc);
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())).thenReturn(virusScanDec);
		FileNotFoundInSourceException ex = new FileNotFoundInSourceException(
				PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_SOURCE.getMessage());
		// doThrow(ex).when(fileManager).cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC,
		// DirectoryPathDto.VIRUS_SCAN_DEC,
		// fileName);
		try {
			fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileName);
			fail();
		} catch (FileNotFoundInSourceException e) {
			assertThat("Should throw File Not Found In Source Exception with correct error codes", e.getErrorCode()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_SOURCE.getCode()));
			assertThat("Should throw File Not Found In Source Exception with correct messages", e.getErrorText()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage()));

		}

	}

}
