
package org.mosip.registration.processor.file.system.connector.service.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;
import org.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;
import org.mosip.registration.processor.packet.manager.service.FileManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
public class FilePathNotAccessibleExceptionTest {
	private static final String FILE_PATH_NOT_ACCESSIBLE = "The Folder Path is not Accessible.";

	@MockBean
	private FileManager<DirectoryPathDto, ?> fileManager;

	@Test
	public void TestFilePathNotAccessibleException() {
		String fileName = "sample.zip";
		FilePathNotAccessibleException ex = new FilePathNotAccessibleException(FILE_PATH_NOT_ACCESSIBLE);
		doThrow(ex).when(fileManager).cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
		try {
			fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
			fail();
		} catch (FilePathNotAccessibleException e) {
			assertThat("Should throw File PathNot Accessible Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_PATH_NOT_ACCESSIBLE));
			assertThat("Should throw File PathNot Accessible Exception with correct messages",
					e.getErrorText().equalsIgnoreCase(FILE_PATH_NOT_ACCESSIBLE));

		}

	}

}
