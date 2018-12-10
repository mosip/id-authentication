package io.mosip.registration.processor.status.exception;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(SpringRunner.class)
public class TableNotAccessibleExceptionTest {

	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto,RegistrationStatusDto> registrationStatusService;

	@MockBean
	InternalRegistrationStatusDto registrationStatusDto;

	@Test
	public void TestTableNotAccessibleException() {

		TablenotAccessibleException ex = new TablenotAccessibleException(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage());

		Mockito.doThrow(ex).when(registrationStatusService).addRegistrationStatus(registrationStatusDto);
		try {

			registrationStatusService.addRegistrationStatus(registrationStatusDto);

		} catch (TablenotAccessibleException e) {
			assertThat("Should throw TableNotAccessibleException with correct error codes", e.getErrorCode()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode()));
			assertThat("Should throw TransactionTableNotAccessibleException  with correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage()));

		}

	}
}
