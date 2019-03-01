/*package io.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.context.ContextConfiguration;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.entity.TransactionEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.impl.RegistrationStatusServiceImpl;
import io.mosip.registration.processor.status.utilities.RegistrationStatusMapUtil;;

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
@RefreshScope
@ContextConfiguration
public class RegistrationStatusServiceTest {

	private InternalRegistrationStatusDto registrationStatusDto;
	private RegistrationStatusEntity registrationStatusEntity;
	private List<RegistrationStatusEntity> entities;
	private static final int threshholdTime = 48;
	@InjectMocks
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService = new RegistrationStatusServiceImpl();

	@Mock
	TransactionService<TransactionDto> transcationStatusService;
	@Mock
	private RegistrationStatusDao registrationStatusDao;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private RegistrationStatusMapUtil registrationStatusMapUtil;

	List<RegistrationStatusDto> registrations = new ArrayList<>();

	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setIsActive(true);
		registrationStatusDto.setStatusCode("PACKET_UPLOADED_TO_VIRUS_SCAN");
		registrationStatusDto.setCreateDateTime(LocalDateTime.now());
		registrationStatusDto.setRegistrationId("1000");

		registrationStatusEntity = new RegistrationStatusEntity();
		registrationStatusEntity.setIsActive(true);
		registrationStatusEntity.setStatusCode("PACKET_UPLOADED_TO_VIRUS_SCAN");
		registrationStatusEntity.setRetryCount(2);

		entities = new ArrayList<>();
		entities.add(registrationStatusEntity);

		Mockito.when(registrationStatusDao.findById(ArgumentMatchers.any())).thenReturn(registrationStatusEntity);

		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setStatusCode("PACKET_UPLOADED_TO_VIRUS_SCAN");
		transactionEntity.setId("1001");
		Mockito.when(transcationStatusService.addRegistrationTransaction(ArgumentMatchers.any()))
				.thenReturn(transactionEntity);

		Mockito.when(registrationStatusMapUtil.getExternalStatus(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(RegistrationExternalStatusCode.RESEND);
	}

	@Test
	public void testGetRegistrationStatusSuccess() {

		InternalRegistrationStatusDto dto = registrationStatusService.getRegistrationStatus("1001");
		assertEquals("PACKET_UPLOADED_TO_VIRUS_SCAN", dto.getStatusCode());

	}

	@Test(expected = TablenotAccessibleException.class)
	public void getRegistrationStatusFailureTest() throws TablenotAccessibleException {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(registrationStatusDao.findById(ArgumentMatchers.any())).thenThrow(exp);
		registrationStatusService.getRegistrationStatus("1001");
	}

	@Test
	public void testAddRegistrationStatusSuccess() {

		registrationStatusService.addRegistrationStatus(registrationStatusDto);
		InternalRegistrationStatusDto dto = registrationStatusService.getRegistrationStatus("1001");
		assertEquals("PACKET_UPLOADED_TO_VIRUS_SCAN", dto.getStatusCode());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void addRegistrationFailureTest() {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(registrationStatusDao.save(ArgumentMatchers.any())).thenThrow(exp);
		registrationStatusService.addRegistrationStatus(registrationStatusDto);
	}

	@Test
	public void testUpdateRegistrationStatusSuccess() {
		registrationStatusService.updateRegistrationStatus(registrationStatusDto);

		InternalRegistrationStatusDto dto = registrationStatusService.getRegistrationStatus("1001");
		assertEquals("PACKET_UPLOADED_TO_VIRUS_SCAN", dto.getStatusCode());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void updateRegistrationStatusFailureTest() {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());

		Mockito.when(registrationStatusDao.save(ArgumentMatchers.any())).thenThrow(exp);
		registrationStatusService.updateRegistrationStatus(registrationStatusDto);
	}

	@Test
	public void testGetByStatusSuccess() {
		Mockito.when(registrationStatusDao.getEnrolmentStatusByStatusCode(ArgumentMatchers.any())).thenReturn(entities);
		List<InternalRegistrationStatusDto> list = registrationStatusService
				.getByStatus("PACKET_UPLOADED_TO_VIRUS_SCAN");
		assertEquals("PACKET_UPLOADED_TO_VIRUS_SCAN", list.get(0).getStatusCode());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getByStatusFailureTest() {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(registrationStatusDao.getEnrolmentStatusByStatusCode(ArgumentMatchers.any())).thenThrow(exp);
		registrationStatusService.getByStatus("PACKET_UPLOADED_TO_VIRUS_SCAN");
	}

	@Test
	public void testGetByIdsSuccess() {

		Mockito.when(registrationStatusDao.getByIds(ArgumentMatchers.any())).thenReturn(entities);

		List<RegistrationStatusDto> list = registrationStatusService.getByIds("1001,1000");
		assertEquals("RESEND", list.get(0).getStatusCode());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getByIdsFailureTest() {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(registrationStatusDao.getByIds(ArgumentMatchers.any())).thenThrow(exp);
		registrationStatusService.getByIds("1001,1000");
	}

}*/