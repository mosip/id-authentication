package io.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.status.code.TransactionTypeCode;
import io.mosip.registration.processor.status.dto.RegistrationTransactionDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.TransactionEntity;
import io.mosip.registration.processor.status.exception.RegTransactionAppException;
import io.mosip.registration.processor.status.exception.TransactionTableNotAccessibleException;
import io.mosip.registration.processor.status.exception.TransactionsUnavailableException;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;
import io.mosip.registration.processor.status.service.impl.TransactionServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

	@InjectMocks
	private TransactionService<TransactionDto> transactionService = new TransactionServiceImpl();

	@Mock
	RegistrationRepositary<TransactionEntity, String> transactionRepositary;
	
	@Mock
	Environment environment;

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
		transcationEntity.setSubStatusCode("RPR-PKR-SUCCESS-001");
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
		Mockito.when(transactionRepositary.save(any())).thenReturn(transcationEntity);
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
		Mockito.when(transactionRepositary.save(any())).thenThrow(exception);
		transactionService.addRegistrationTransaction(transactionDto);
	}

	@Test
	public void getTransactionByRegIdAndStatusCodeSuccessCheck() {
		Mockito.when(transactionRepositary.getTransactionByRegIdAndStatusCode(any(), any()))
				.thenReturn(transcationEntities);
		TransactionDto dto = transactionService.getTransactionByRegIdAndStatusCode("1234", "status");

		assertNotEquals(dto, null);
	}

	
	@Test(expected = TransactionTableNotAccessibleException.class)
	public void testgetTransactionByRegIdFailure() throws TransactionsUnavailableException, RegTransactionAppException {
		DataAccessLayerException exception = new DataAccessLayerException(
				io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(transactionRepositary.getTransactionByRegId(any()))
		.thenThrow(exception);
		
		
		 transactionService.getTransactionByRegId("1221", "en");

		
	}
	
	@Test(expected = TransactionsUnavailableException.class)
	public void testgetTransactionByRegIdException() throws TransactionsUnavailableException, RegTransactionAppException {
		List<TransactionEntity> entities = new ArrayList<TransactionEntity>();
		Mockito.when(transactionRepositary.getTransactionByRegId(any()))
		.thenReturn(entities);
		
		
		 transactionService.getTransactionByRegId("1221", "en");

		
	}
	
	@Test
	public void testgetTransactionByRegId() throws TransactionsUnavailableException, RegTransactionAppException {
		List<TransactionEntity> entities = new ArrayList<TransactionEntity>();
		transcationEntity = new TransactionEntity();
		transcationEntity.setId("1");
		transcationEntity.setSubStatusCode("RPR-PKR-SUCCESS-001");
		transcationEntity.setLangCode("eng");
		transcationEntity.setParentid(null);
		transcationEntity.setRemarks("Packet has reached Packet Receiver");
		transcationEntity.setStatusCode("SUCCESS");
		transcationEntity.setStatusComment("Add Enrolment started");
		transcationEntity.setCreatedBy("MOSIP_SYSTEM");
		transcationEntity.setLangCode("eng");
		transcationEntity.setTrntypecode("PACKET_RECEIVER");
		entities.add(transcationEntity);
		Mockito.when(transactionRepositary.getTransactionByRegId(any()))
		.thenReturn(entities);
		Mockito.when(environment.getProperty( any()))
				.thenReturn("globalMessages_en.properties");
		
		List<RegistrationTransactionDto> dtolist = transactionService.getTransactionByRegId("1221", "en");

		assertEquals(dtolist.get(0).getStatusComment(), "Packet has reached Packet Receiver");
	}
	
	
}
