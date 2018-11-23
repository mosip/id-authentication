
package io.mosip.registration.processor.file.system.connector.exception.systemexception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.systemexception.BadGatewayException;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
public class BadGatewayExceptionTest {

	@MockBean
	private FileManager<DirectoryPathDto, File> fileManager;

	private File file;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("1001.zip").getFile());

	}

	@Test
	public void TestBadGatewayException() throws FileNotFoundException, IOException {
		String fileName = "sample";
		BadGatewayException ex = new BadGatewayException(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getMessage());

		doThrow(ex).when(fileManager).put(fileName, file, DirectoryPathDto.LANDING_ZONE);

		try {
			fileManager.put(fileName, file, DirectoryPathDto.LANDING_ZONE);
			fail();
		} catch (BadGatewayException e) {
			assertThat("Should throw Bad Gateway exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getCode()));
			assertThat("Should throw  Bad Gateway exception with correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_SYS_BAD_GATEWAY.getMessage()));
		}

	}
}
