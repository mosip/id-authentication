
package io.mosip.registration.processor.file.system.connector.service.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.CoreKernelNotRespondingException;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
public class CoreKernelNotRespondingExceptionTest {

	@MockBean
	private FileManager<DirectoryPathDto, File> fileManager;

	@Test
	public void TestCoreKernelNotRespondingException() {

		String fileName = "sample.zip";

		CoreKernelNotRespondingException ex = new CoreKernelNotRespondingException(
				PlatformErrorMessages.RPR_PKM_CORE_KERNEL_NOT_RESPONDING.getMessage());
		doThrow(ex).when(fileManager).cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
		
		try {
			fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileName);
			fail();
		} catch (CoreKernelNotRespondingException e) {
			assertThat("Should throw Core Kernel Not Responding Exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_CORE_KERNEL_NOT_RESPONDING.getCode()));
			assertThat("Should throw Core Kernel Not Responding Exceptionwith correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_CORE_KERNEL_NOT_RESPONDING.getMessage()));

		}

	}
	
}

