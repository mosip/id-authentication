package io.mosip.pregistration.datasync.test.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.pregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.service.DataSyncService;

@RunWith(SpringRunner.class)
public class DataSyncRecordNotFoundExceptionTest {

	private static final String RECORD_NOT_FOUND = "This is record not found exception";

	@Mock
	private DataSyncService dataSyncService;

	@Test
	public void notfoundException() throws Exception {

		DataSyncRecordNotFoundException dataSyncRecordNotFoundException = new DataSyncRecordNotFoundException(
				RECORD_NOT_FOUND);

		Mockito.when(dataSyncService.getPreRegistration(" ")).thenThrow(dataSyncRecordNotFoundException);
		try {

			dataSyncService.getPreRegistration(" ");
			fail();

		} catch (DataSyncRecordNotFoundException e) {
			assertThat("Should throw records not found exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(ErrorCodes.PRG_DATA_SYNC_004.toString()));
			assertThat("Should throw records not found exception with correct messages",
					e.getErrorText().equalsIgnoreCase(RECORD_NOT_FOUND));
		}
	}
}
