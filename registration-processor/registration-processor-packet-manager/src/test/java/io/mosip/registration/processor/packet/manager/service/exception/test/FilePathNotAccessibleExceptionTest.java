
package io.mosip.registration.processor.packet.manager.service.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;

import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FilePathNotAccessibleException;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

/**
 * @author M1022006
 *
 */
@RefreshScope
@RunWith(PowerMockRunner.class)
public class FilePathNotAccessibleExceptionTest {

	@Mock
	private Environment env;

	@InjectMocks
	private FileManager<DirectoryPathDto, InputStream> fileManager = new FileManagerImpl();

	@Test
	public void TestFilePathNotAccessibleException() {
		String fileName = "sample.zip";
		FilePathNotAccessibleException ex = new FilePathNotAccessibleException(
				PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage());
		
		Mockito.when(env.getProperty(any())).thenThrow(ex);
		try {
			fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileName);
			fail();
		} catch (FilePathNotAccessibleException e) {
			assertThat("Should throw File PathNot Accessible Exception with correct error codes", e.getErrorCode()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getCode()));
			assertThat("Should throw File PathNot Accessible Exception with correct messages", e.getErrorText()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_PATH_NOT_ACCESSIBLE.getMessage()));

		}

	}

}
