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
import org.mosip.registration.processor.status.code.TransactionConstants;
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
		transactionDto.setEnrolmentId("1");
		transactionDto.setIsActive(true);
		transactionDto.setLangCode("eng");
		transactionDto.setParentid(null);
		transactionDto.setRemarks("Add Enrolment operation");
		transactionDto.setStatusCode(TransactionConstants.ADD.toString());
		transactionDto.setStatusComment("Add Enrolment started");
		transactionDto.setTransactionId("1");

		transcationEntity = new TransactionEntity();
		transcationEntity.setId("1");
		transcationEntity.setIsActive(true);
		transcationEntity.setLangCode("eng");
		transcationEntity.setParentid(null);
		transcationEntity.setRemarks("Add Enrolment operation");
		transcationEntity.setStatusCode(TransactionConstants.ADD.toString());
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
		TransactionTableNotAccessibleException exception = new TransactionTableNotAccessibleException(
				"Could not add Information to Transaction table");
		Mockito.when(transactionRepositary.save(ArgumentMatchers.any())).thenThrow(exception);
		transactionService.addRegistrationTransaction(transactionDto);
	}

}
