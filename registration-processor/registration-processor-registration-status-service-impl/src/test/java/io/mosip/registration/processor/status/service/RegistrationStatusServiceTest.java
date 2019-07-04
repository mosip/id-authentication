package io.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.context.ContextConfiguration;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.code.RegistrationExternalStatusCode;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusSubRequestDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.entity.TransactionEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.impl.RegistrationStatusServiceImpl;
import io.mosip.registration.processor.status.utilities.RegistrationExternalStatusUtility;;

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
@RefreshScope
@ContextConfiguration
public class RegistrationStatusServiceTest {

	private InternalRegistrationStatusDto registrationStatusDto;
	private RegistrationStatusEntity registrationStatusEntity;
	private List<RegistrationStatusEntity> entities;

	@InjectMocks
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService = new RegistrationStatusServiceImpl();

	@Mock
	TransactionService<TransactionDto> transcationStatusService;
	@Mock
	private RegistrationStatusDao registrationStatusDao;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private  RegistrationExternalStatusUtility regexternalstatusUtil;

	@Mock
	LogDescription description;
	
	List<RegistrationStatusDto> registrations = new ArrayList<>();

	List<String> statusList;

	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		Mockito.doNothing().when(description).setMessage(any());
		
		registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setIsActive(true);
		registrationStatusDto.setStatusCode("PACKET_UPLOADED_TO_VIRUS_SCAN");
		registrationStatusDto.setCreateDateTime(LocalDateTime.now());
		registrationStatusDto.setRegistrationId("1000");
		registrationStatusDto.setRegistrationStageName("PacketValidatorStage");
		registrationStatusDto.setReProcessRetryCount(0);
		registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
		registrationStatusEntity = new RegistrationStatusEntity();
		registrationStatusEntity.setIsActive(true);
		registrationStatusEntity.setStatusCode("PACKET_UPLOADED_TO_VIRUS_SCAN");
		registrationStatusEntity.setRetryCount(2);
		registrationStatusEntity.setRegistrationStageName("PacketValidatorStage");

		registrationStatusEntity.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
		entities = new ArrayList<>();
		entities.add(registrationStatusEntity);

		Mockito.when(registrationStatusDao.findById(any())).thenReturn(registrationStatusEntity);

		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setStatusCode("PROCESSING");
		transactionEntity.setId("1001");
		Mockito.when(transcationStatusService.addRegistrationTransaction(any())).thenReturn(transactionEntity);
		// Mockito.when(registrationStatusMapUtil.getExternalStatus(ArgumentMatchers.any(),
		// ArgumentMatchers.any()))
		// .thenReturn(RegistrationExternalStatusCode.RESEND);
		Mockito.when(registrationStatusDao.getByIds(any())).thenReturn(entities);

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
		Mockito.when(registrationStatusDao.findById(any())).thenThrow(exp);
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
		Mockito.when(registrationStatusDao.save(any())).thenThrow(exp);
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

		Mockito.when(registrationStatusDao.save(any())).thenThrow(exp);
		registrationStatusService.updateRegistrationStatus(registrationStatusDto);
	}

	@Test
	public void testGetByStatusSuccess() {
		Mockito.when(registrationStatusDao.getEnrolmentStatusByStatusCode(any())).thenReturn(entities);
		List<InternalRegistrationStatusDto> list = registrationStatusService
				.getByStatus("PACKET_UPLOADED_TO_VIRUS_SCAN");
		assertEquals("PACKET_UPLOADED_TO_VIRUS_SCAN", list.get(0).getStatusCode());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getByStatusFailureTest() {
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(registrationStatusDao.getEnrolmentStatusByStatusCode(any())).thenThrow(exp);
		registrationStatusService.getByStatus("PACKET_UPLOADED_TO_VIRUS_SCAN");
	}

	@Test
	public void testGetByIdsSuccess() {

		Mockito.when(registrationStatusDao.getByIds(any())).thenReturn(entities);
		Mockito.when(regexternalstatusUtil.getExternalStatus(any()))
				.thenReturn(RegistrationExternalStatusCode.PROCESSED);
		RegistrationStatusSubRequestDto registrationId = new RegistrationStatusSubRequestDto();
		registrationId.setRegistrationId("1001");
		List<RegistrationStatusSubRequestDto> registrationIds = new ArrayList<>();
		registrationIds.add(registrationId);
		List<RegistrationStatusDto> list = registrationStatusService.getByIds(registrationIds);
		assertEquals("PROCESSED", list.get(0).getStatusCode());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getByIdsFailureTest() {
		RegistrationStatusSubRequestDto registrationId = new RegistrationStatusSubRequestDto();
		registrationId.setRegistrationId("1001");
		List<RegistrationStatusSubRequestDto> registrationIds = new ArrayList<>();
		registrationIds.add(registrationId);

		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(registrationStatusDao.getByIds(any())).thenThrow(exp);

		registrationStatusService.getByIds(registrationIds);

	}

	@Test
	public void testGetUnProcessedPacketsCount() {
		List<String> statusList = new ArrayList<>();
		statusList.add("SUCCESS");
		statusList.add("REPROCESS");
		Mockito.when(registrationStatusDao.getUnProcessedPacketsCount(anyLong(), anyInt(), anyList())).thenReturn(1);
		int packetCount = registrationStatusService.getUnProcessedPacketsCount(21600, 3, statusList);
		assertEquals(1, packetCount);
	}

	@Test
	public void testGetUnProcessedPackets() {

		List<String> statusList = new ArrayList<>();
		statusList.add("SUCCESS");
		statusList.add("REPROCESS");
		Mockito.when(registrationStatusDao.getUnProcessedPackets(anyInt(), anyLong(), anyInt(), anyList()))
				.thenReturn(entities);
		List<InternalRegistrationStatusDto> dtolist = registrationStatusService.getUnProcessedPackets(1, 21600, 3,
				statusList);
		assertEquals("REPROCESS", dtolist.get(0).getLatestTransactionStatusCode());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getUnProcessedPacketsCountFailureTest() {
		List<String> statusList = new ArrayList<>();
		statusList.add("SUCCESS");
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(registrationStatusDao.getUnProcessedPacketsCount(anyLong(), anyInt(), anyList())).thenThrow(exp);

		registrationStatusService.getUnProcessedPacketsCount(21600, 3, statusList);
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getUnProcessedPacketsFailureTest() {
		List<String> statusList = new ArrayList<>();
		statusList.add("SUCCESS");
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(registrationStatusDao.getUnProcessedPackets(anyInt(), anyLong(), anyInt(), anyList()))
				.thenThrow(exp);

		registrationStatusService.getUnProcessedPackets(1, 21600, 3, statusList);
	}

}