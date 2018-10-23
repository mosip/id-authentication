package io.mosip.registration.processor.status.exception;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.exception.utils.RegistrationStatusErrorCodes;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(SpringRunner.class)
public class TableNotAccessibleExceptionTest {
	private static final String TABLE_NOTACCESSIBLE = "Table not accessible exception";

	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto> registrationStatusService;

	@MockBean
	InternalRegistrationStatusDto registrationStatusDto;

	@Test
	public void TestTableNotAccessibleException() {

		TablenotAccessibleException ex = new TablenotAccessibleException(TABLE_NOTACCESSIBLE);

		Mockito.doThrow(ex).when(registrationStatusService).addRegistrationStatus(registrationStatusDto);
		try {

			registrationStatusService.addRegistrationStatus(registrationStatusDto);

		} catch (TablenotAccessibleException e) {
			assertThat("Should throw TableNotAccessibleException with correct error codes", e.getErrorCode()
					.equalsIgnoreCase(RegistrationStatusErrorCodes.IIS_EPU_ATU_ENROLMENT_STATUS_TABLE_NOTACCESSIBLE));
			assertThat("Should throw TransactionTableNotAccessibleException  with correct messages",
					e.getErrorText().equalsIgnoreCase(TABLE_NOTACCESSIBLE));

		}

	}
}
