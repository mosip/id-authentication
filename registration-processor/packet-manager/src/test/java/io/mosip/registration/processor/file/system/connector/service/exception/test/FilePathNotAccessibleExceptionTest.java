
package io.mosip.registration.processor.file.system.connector.service.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
public class FilePathNotAccessibleExceptionTest {

	@MockBean
	private FileManager<DirectoryPathDto, ?> fileManager;

	@Test
	public void TestFilePathNotAccessibleException() {
		String fileName = "sample.zip";
		FilePathNotAccessibleException ex = new FilePathNotAccessibleException(PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage());
		doThrow(ex).when(fileManager).cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
		try {
			fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
			fail();
		} catch (FilePathNotAccessibleException e) {
			assertThat("Should throw File PathNot Accessible Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getCode()));
			assertThat("Should throw File PathNot Accessible Exception with correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage()));

		}

	}

}
