
package org.mosip.registration.processor.file.system.connector.service.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.mosip.registration.processor.packet.manager.exception.CoreKernelNotRespondingException;
import org.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;
import org.mosip.registration.processor.packet.manager.service.FileManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
public class CoreKernelNotRespondingExceptionTest {

	private static final String CORE_KERNEL_NOT_RESPONDING = "The Core Kernel Configuration Service is not responding.";

	@MockBean
	private FileManager<DirectoryPathDto, File> fileManager;

	@Test
	public void TestCoreKernelNotRespondingException() {

		String fileName = "sample.zip";

		CoreKernelNotRespondingException ex = new CoreKernelNotRespondingException(CORE_KERNEL_NOT_RESPONDING);
		doThrow(ex).when(fileManager).cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
		
		try {
			fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
			fail();
		} catch (CoreKernelNotRespondingException e) {
			assertThat("Should throw Core Kernel Not Responding Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(IISPlatformErrorCodes.IIS_EPU_FSS_CORE_KERNEL_NOT_RESPONDING));
			assertThat("Should throw Core Kernel Not Responding Exceptionwith correct messages",
					e.getErrorText().equalsIgnoreCase(CORE_KERNEL_NOT_RESPONDING));

		}

	}
	
}

