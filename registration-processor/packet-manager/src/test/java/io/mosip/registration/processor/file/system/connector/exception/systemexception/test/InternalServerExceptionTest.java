
package io.mosip.registration.processor.file.system.connector.exception.systemexception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.systemexception.InternalServerException;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
public class InternalServerExceptionTest {

	@MockBean
	private FileManager<DirectoryPathDto, File> fileManager;

	private File file;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("1001.zip").getFile());

	}

	@Test
	public void TestInternalServerException() throws IOException {
		String fileName = "sample";
		InternalServerException ex = new InternalServerException(PlatformErrorMessages.RPR_SYS_SERVER_ERROR.getMessage());
		doThrow(ex).when(fileManager).put(fileName, file, DirectoryPathDto.LANDING_ZONE);

		try {
			fileManager.put(fileName, file, DirectoryPathDto.LANDING_ZONE);
			fail();
		} catch (InternalServerException e) {
			assertThat("Should throw Server error with correct error codes",
					e.getErrorCode().equalsIgnoreCase(PlatformErrorMessages.RPR_SYS_SERVER_ERROR.getCode()));
			assertThat("Should throw Server error with correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_SYS_SERVER_ERROR.getMessage()));
		}

	}

}