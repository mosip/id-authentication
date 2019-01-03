package io.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.status.code.TransactionTypeCode;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.TransactionEntity;
import io.mosip.registration.processor.status.exception.TransactionTableNotAccessibleException;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;
import io.mosip.registration.processor.status.service.impl.TransactionServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

	@InjectMocks
	private TransactionService<TransactionDto> transactionService = new TransactionServiceImpl();

	@Mock
	RegistrationRepositary<TransactionEntity, String> transactionRepositary;

	private TransactionEntity transcationEntity;
	private TransactionDto transactionDto;

	private List<TransactionEntity> transcationEntities;

	@Before
	public void setup() {
		transcationEntities = new ArrayList<TransactionEntity>();
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
		transcationEntity.setId("1");

		transcationEntity.setLangCode("eng");
		transcationEntity.setParentid(null);
		transcationEntity.setRemarks("Add Enrolment operation");
		transcationEntity.setStatusCode(TransactionTypeCode.CREATE.toString());
		transcationEntity.setStatusComment("Add Enrolment started");
		transcationEntity.setCreatedBy("MOSIP_SYSTEM");
		transcationEntity.setLangCode("eng");
		transcationEntities.add(transcationEntity);

	}

	@Test
	public void addRegistrationTransactionSuccessCheck() {
		Mockito.when(transactionRepositary.save(ArgumentMatchers.any())).thenReturn(transcationEntity);
		TransactionEntity transcationEntity1 = transactionService.addRegistrationTransaction(transactionDto);
		assertEquals("The Transaction should be addded successfully", transcationEntity.getId(),
				transcationEntity1.getId());
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
				io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(transactionRepositary.save(ArgumentMatchers.any())).thenThrow(exception);
		transactionService.addRegistrationTransaction(transactionDto);
	}

	@Test
	public void getTransactionByRegIdAndStatusCodeSuccessCheck() {
		Mockito.when(transactionRepositary.getTransactionByRegIdAndStatusCode(ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(transcationEntities);
		TransactionDto dto = transactionService.getTransactionByRegIdAndStatusCode("1234", "status");

		assertNotEquals(dto, null);
	}

}
