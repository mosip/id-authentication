package io.mosip.registration.processor.file.system.connector.service.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInSourceException;
import io.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;

@RunWith(SpringRunner.class)
public class FileNotFoundInSourceExceptionTest {
	private static final String FILE_NOT_FOUND_IN_SOURCE = "The File is not present in Source Folder";

	@MockBean
	private FileManager<DirectoryPathDto, ?> fileManager;

	@Test
	public void TestFileNotFoundInSourceException() {

		String fileName = "sample.zip";
		FileNotFoundInSourceException ex = new FileNotFoundInSourceException(FILE_NOT_FOUND_IN_SOURCE);
		doThrow(ex).when(fileManager).cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
		try {
			fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
			fail();
		} catch (FileNotFoundInSourceException e) {
			assertThat("Should throw File Not Found In Source Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_NOT_FOUND_IN_SOURCE));
			assertThat("Should throw File Not Found In Source Exception with correct messages",
					e.getErrorText().equalsIgnoreCase(FILE_NOT_FOUND_IN_SOURCE));

		}

	}

}
