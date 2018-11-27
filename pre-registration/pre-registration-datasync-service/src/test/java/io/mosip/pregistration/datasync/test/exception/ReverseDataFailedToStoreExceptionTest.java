package io.mosip.pregistration.datasync.test.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.pregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.pregistration.datasync.service.DataSyncService;

@RunWith(SpringRunner.class)
public class ReverseDataFailedToStoreExceptionTest {

	private static final String FAILED_TO_SAVE = "This is failed to save exception";

	@Mock
	private DataSyncService dataSyncService;

	@Test
	public void notfoundException() throws Exception {

		ReverseDataFailedToStoreException reverseDataFailed = new ReverseDataFailedToStoreException(FAILED_TO_SAVE);

		Mockito.when(dataSyncService.storeConsumedPreRegistrations(null)).thenThrow(reverseDataFailed);
		try {

			dataSyncService.storeConsumedPreRegistrations(null);
			fail();

		} catch (ReverseDataFailedToStoreException e) {
			assertThat("Should throw records not found exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString()));
			assertThat("Should throw records not found exception with correct messages",
					e.getErrorText().equalsIgnoreCase(FAILED_TO_SAVE));
		}
	}

}
