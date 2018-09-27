package org.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import org.mosip.registration.processor.status.code.TransactionTypeCode;
import org.mosip.registration.processor.status.dto.TransactionDto;
import org.mosip.registration.processor.status.entity.TransactionEntity;
import org.mosip.registration.processor.status.exception.TransactionTableNotAccessibleException;
import org.mosip.registration.processor.status.repositary.TransactionRepositary;
import org.mosip.registration.processor.status.service.impl.TransactionServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

	@InjectMocks
	private TransactionService<TransactionDto> transactionService = new TransactionServiceImpl();

	@Mock
	TransactionRepositary transactionRepositary;

	private TransactionEntity transcationEntity;
	private TransactionDto transactionDto;

	@Before
	public void setup() {

		transactionDto = new TransactionDto();
		transactionDto.setRegistrationId("1");
		transactionDto.setIsActive(true);
		transactionDto.setLangCode("eng");
		transactionDto.setParentid(null);
		transactionDto.setRemarks("Add Enrolment operation");
		transactionDto.setStatusCode(TransactionTypeCode.CREATE.toString());
		transactionDto.setStatusComment("Add Enrolment started");
		transactionDto.setTransactionId("1");

		transcationEntity = new TransactionEntity();
		transcationEntity.setTransactionId("1");
		transcationEntity.setIsActive(true);
		transcationEntity.setLangCode("eng");
		transcationEntity.setParentid(null);
		transcationEntity.setRemarks("Add Enrolment operation");
		transcationEntity.setStatusCode(TransactionTypeCode.CREATE.toString());
		transcationEntity.setStatusComment("Add Enrolment started");
		transcationEntity.setTransactionId("1");
		transcationEntity.setCreatedBy("MOSIP_SYSTEM");
		transcationEntity.setLangCode("eng");

	}

	@Test
	public void addRegistrationTransactionSuccessCheck() {
		Mockito.when(transactionRepositary.save(ArgumentMatchers.any())).thenReturn(transcationEntity);
		TransactionEntity transcationEntity1 = transactionService.addRegistrationTransaction(transactionDto);
		assertEquals("The Transaction should be addded successfully", transcationEntity.getTransactionId(),
				transcationEntity1.getTransactionId());
		assertEquals("The Transaction should be addded successfully", transcationEntity.getRegistrationId(),
				transcationEntity1.getRegistrationId());
		assertEquals("The Transaction should be addded successfully", transcationEntity.getReferenceId(),
				transcationEntity1.getReferenceId());
		assertEquals("The Transaction should be addded successfully", transcationEntity.getStatusCode(),
				transcationEntity1.getStatusCode());
	}

	@Test(expected = TransactionTableNotAccessibleException.class)
	public void addRegistrationTransactionFailureCheck() throws Exception {
		DataAccessLayerException exception = new DataAccessLayerException(
				org.mosip.kernel.dataaccess.constants.HibernateErrorCodes.ERR_DATABASE, "errorMessage",
				new Exception());
		Mockito.when(transactionRepositary.save(ArgumentMatchers.any())).thenThrow(exception);
		transactionService.addRegistrationTransaction(transactionDto);
	}

}
