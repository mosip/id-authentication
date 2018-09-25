
package org.mosip.registration.processor.file.system.connector.service.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import org.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;
import org.mosip.registration.processor.packet.manager.service.FileManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
public class FileNotFoundInDestinationExceptionTest {

	private static final String FILE_NOT_FOUND_IN_DESTINATION = "The File is not present in Destination Folder";

	@MockBean
	private FileManager<DirectoryPathDto, ?> fileManager;

	@Test
	public void TestFileNotFoundInDestinationException() {
		String fileName = "sample.zip";

		FileNotFoundInDestinationException ex = new FileNotFoundInDestinationException(FILE_NOT_FOUND_IN_DESTINATION);
		doThrow(ex).when(fileManager).cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
		try {
			fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
			fail();
		} catch (FileNotFoundInDestinationException e) {
			assertThat("Should throw File Not Found In Destination Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(IISPlatformErrorCodes.IIS_EPU_FSS_FILE_NOT_FOUND_IN_DESTINATION));
			assertThat("Should throw File Not Found In Destination Exception with correct messages",
					e.getErrorText().equalsIgnoreCase(FILE_NOT_FOUND_IN_DESTINATION));

		}

	}
}