package io.mosip.pregistration.datasync.test.exception;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.pregistration.datasync.exception.ZipFileCreationException;
import io.mosip.pregistration.datasync.service.DataSyncService;

@RunWith(SpringRunner.class)
public class ZipFileCreationExceptionTest {

	private static final String CREATION_FAILED = "This is zip file creation failed exception";

	@Mock
	private DataSyncService dataSyncService;

	@Test
	public void zipCreationFail() throws Exception {

		ZipFileCreationException zipFileCreationException = new ZipFileCreationException(CREATION_FAILED);

		try {
			Mockito.when(dataSyncService.getPreRegistration(" ")).thenThrow(zipFileCreationException);
			dataSyncService.getPreRegistration(" ");
			fail();

		} catch (ZipFileCreationException e) {
			assertThat("Should throw Zip creation failed exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_DATA_SYNC_005.toString()));
			assertThat("Should throw Zip creation failed exception with correct messages",
					e.getErrorText().equalsIgnoreCase(CREATION_FAILED));
		}
	}
}