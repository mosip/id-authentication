
package io.mosip.registration.processor.file.system.connector.exception.systemexception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.systemexception.UnexpectedException;
import io.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
public class UnexceptedExceptionTest {

	private static final String UNEXCEPTED_EXCEPTION = "This is unexcepted exception";

	@MockBean
	private FileManager<DirectoryPathDto, File> fileManager;

	private File file;

	@Test
	public void TestUnexceptedException() throws IOException {
		String fileName = "sample";
		UnexpectedException ex = new UnexpectedException(UNEXCEPTED_EXCEPTION);
		doThrow(ex).when(fileManager).put(fileName, file, DirectoryPathDto.LANDING_ZONE);

		try {
			fileManager.put(fileName, file, DirectoryPathDto.LANDING_ZONE);
			fail();
		} catch (UnexpectedException e) {
			assertThat("Should throw  Unexpected Exception  with correct error codes",
					e.getErrorCode().equalsIgnoreCase(IISPlatformErrorCodes.IIS_EPU_FSS_UNEXCEPTED_ERROR));
			assertThat("Should throw  Unexpected Exception  with correct messages",
					e.getErrorText().equalsIgnoreCase(UNEXCEPTED_EXCEPTION));
		}

	}

}
