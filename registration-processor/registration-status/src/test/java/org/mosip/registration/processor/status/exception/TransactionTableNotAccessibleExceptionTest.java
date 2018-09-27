package org.mosip.registration.processor.status.exception;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.registration.processor.status.dto.TransactionDto;
import org.mosip.registration.processor.status.exception.utils.RegistrationStatusErrorCodes;
import org.mosip.registration.processor.status.service.TransactionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TransactionTableNotAccessibleExceptionTest {

	private static final String TRANSACTION_TABLE_NOTACCESSIBLE = "Transaction table not accessible exception";

	@Mock
	TransactionService<TransactionDto> registrationTransactionService;

	@MockBean
	TransactionDto registrationTransactionDto;

	@Test
	public void TestTransactionTableNotAccessibleException() {

		TransactionTableNotAccessibleException ex = new TransactionTableNotAccessibleException(
				TRANSACTION_TABLE_NOTACCESSIBLE);

		Mockito.when(registrationTransactionService.addRegistrationTransaction(registrationTransactionDto)).thenThrow(ex);
		try {

			registrationTransactionService.addRegistrationTransaction(registrationTransactionDto);

		} catch (TransactionTableNotAccessibleException e) {
			assertThat("Should throw TransactionTableNotAccessibleException with correct error codes", e.getErrorCode()
					.equalsIgnoreCase(RegistrationStatusErrorCodes.IIS_EPU_ATU_TRANSACTION_TABLE_NOTACCESSIBLE));
			assertThat("Should throw TransactionTableNotAccessibleException  with correct messages",
					e.getErrorText().equalsIgnoreCase(TRANSACTION_TABLE_NOTACCESSIBLE));

		}

	}
}
