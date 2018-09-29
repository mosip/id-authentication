
package org.mosip.registration.processor.file.system.connector.exception.systemexception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.mosip.registration.processor.packet.manager.exception.systemexception.ServiceUnavailableException;
import org.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
public class ServiceUnavailableExceptionTest {

	private static final String SERVICE_UNAVAILABLE_EXCEPTION = "This service unavailable exception";

	@MockBean
	private FileManager<DirectoryPathDto, File> fileManager;

	private File file;

	@Test
	public void TestServiceUnavailableException() throws IOException {
		String fileName = "sample.zip";
		ServiceUnavailableException ex = new ServiceUnavailableException(SERVICE_UNAVAILABLE_EXCEPTION);
		doThrow(ex).when(fileManager).put(fileName, file, DirectoryPathDto.LANDING_ZONE);

		try {
			fileManager.put(fileName, file, DirectoryPathDto.LANDING_ZONE);
			fail();

		} catch (ServiceUnavailableException e) {
			assertThat("Should throw ServiceUnavailable Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(IISPlatformErrorCodes.IIS_EPU_FSS_SERVICE_UNAVAILABLE));
			assertThat("Should throw ServiceUnavailable Exception with correct messages",
					e.getErrorText().equalsIgnoreCase(SERVICE_UNAVAILABLE_EXCEPTION));
		}

	}
}
