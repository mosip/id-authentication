
package io.mosip.registration.processor.packet.manager.systemexception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;

import java.io.IOException;
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
import io.mosip.registration.processor.packet.manager.exception.systemexception.TimeoutException;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

/**
 * @author M1022006
 *
 */
@RefreshScope
@RunWith(PowerMockRunner.class)
public class TimeoutExceptionTest {

	@Mock
	private Environment env;

	@InjectMocks
	private FileManager<DirectoryPathDto, InputStream> fileManager = new FileManagerImpl();

	private InputStream file;

	@Test
	public void TestTimeoutException() throws IOException {
		String fileName = "sample";
		TimeoutException ex = new TimeoutException(PlatformErrorMessages.RPR_SYS_TIMEOUT_EXCEPTION.getMessage());
		Mockito.when(env.getProperty(any())).thenThrow(ex);

		try {
			fileManager.put(fileName, file, DirectoryPathDto.ARCHIVE_LOCATION);
			fail();
		} catch (TimeoutException e) {
			assertThat("Should throw  Timeout Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(PlatformErrorMessages.RPR_SYS_TIMEOUT_EXCEPTION.getCode()));
			assertThat("Should throw   Timeout Exception with correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_SYS_TIMEOUT_EXCEPTION.getMessage()));
		}

	}
}
