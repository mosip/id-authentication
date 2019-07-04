
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.config.PacketManagerConfigTest;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

/**
 * @author M1022006
 *
 */
@RefreshScope
@RunWith(PowerMockRunner.class)
@SpringBootTest
@ContextConfiguration(classes = PacketManagerConfigTest.class)
public class FileNotFoundInDestinationExceptionTest {

	@Mock
	private Environment env;

	@InjectMocks
	private FileManager<DirectoryPathDto, InputStream> fileManager = new FileManagerImpl();

	@Test
	public void TestFileNotFoundInDestinationException() {
		String fileName = "sample.zip";

		Mockito.when(env.getProperty(any())).thenReturn("src/test/resources/decrypted");
		FileNotFoundInDestinationException ex = new FileNotFoundInDestinationException(
				PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION.getMessage());

		try {
			fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileName);
			fail();
		} catch (FileNotFoundInDestinationException e) {
			assertThat("Should throw File Not Found In Destination Exception with correct error codes", e.getErrorCode()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION.getCode()));
			assertThat("Should throw File Not Found In Destination Exception with correct messages", e.getErrorText()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION.getMessage()));

		}

	}
}