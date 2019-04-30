package io.mosip.registration.processor.status.exception;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.service.TransactionService;

@RunWith(SpringRunner.class)
public class TransactionTableNotAccessibleExceptionTest {

	@Mock
	TransactionService<TransactionDto> registrationTransactionService;

	@MockBean
	TransactionDto registrationTransactionDto;

	@Test
	public void TestTransactionTableNotAccessibleException() {

		TransactionTableNotAccessibleException ex = new TransactionTableNotAccessibleException(
				PlatformErrorMessages.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE.getMessage());

		Mockito.when(registrationTransactionService.addRegistrationTransaction(registrationTransactionDto)).thenThrow(ex);
		try {

			registrationTransactionService.addRegistrationTransaction(registrationTransactionDto);

		} catch (TransactionTableNotAccessibleException e) {
			assertThat("Should throw TransactionTableNotAccessibleException with correct error codes", e.getErrorCode()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE.getCode()));
			assertThat("Should throw TransactionTableNotAccessibleException  with correct messages",
					e.getErrorText().equalsIgnoreCase(PlatformErrorMessages.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE.getMessage()));

		}

	}
}
