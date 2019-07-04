
package io.mosip.registration.processor.packet.manager.systemexception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;

import java.io.FileNotFoundException;
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
import io.mosip.registration.processor.packet.manager.exception.systemexception.BadGatewayException;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

/**
 * @author M1022006
 *
 */
@RefreshScope
@RunWith(PowerMockRunner.class)
public class BadGatewayExceptionTest {

	@Mock
	private Environment env;

	@InjectMocks
	private FileManager<DirectoryPathDto, InputStream> fileManager = new FileManagerImpl();

	private InputStream inputStream;

	@Test
	public void TestBadGatewayException() throws FileNotFoundException, IOException {
		String fileName = "sample";
		BadGatewayException ex = new BadGatewayException(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getMessage());

		Mockito.when(env.getProperty(any())).thenThrow(ex);

		try {
			fileManager.put(fileName, inputStream, DirectoryPathDto.ARCHIVE_LOCATION);
			fail();
		} catch (BadGatewayException e) {
			assertThat("Should throw Bad Gateway exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode()));
			assertThat("Should throw  Bad Gateway exception with correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getMessage()));
		}

	}
}
