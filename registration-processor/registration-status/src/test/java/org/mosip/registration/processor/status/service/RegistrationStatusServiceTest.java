package org.mosip.registration.processor.status.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mosip.registration.processor.status.dao.EntityStatusBaseDao;
import org.mosip.registration.processor.status.dao.RegistrationStatusDao;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import org.mosip.registration.processor.status.exception.TablenotAccessibleException;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
import org.mosip.registration.processor.status.service.impl.RegistrationStatusServiceImpl;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
@TestPropertySource({ "classpath:status-application.properties" })
@ContextConfiguration
public class RegistrationStatusServiceTest {

	private static final int threshholdTime = 48;

	@InjectMocks
	private RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService = new RegistrationStatusServiceImpl() {
		@Override
		public int getThreshholdTime() {
			return threshholdTime;
		}

	};
	@Mock
	private RegistrationStatusDao registrationStatusDao;

	private Optional<RegistrationStatusEntity> entity;

	@Mock
	private EntityStatusBaseDao entityStatusBaseDao;

	@Before
	public void setup() {
		RegistrationStatusEntity sample = new RegistrationStatusEntity("1001", "PACKET_UPLOADED_TO_LANDING_ZONE", 0);
		entity = Optional.of(sample);

		Mockito.when(registrationStatusDao.findById("1001")).thenReturn(entity);
		List<RegistrationStatusEntity> entities = new ArrayList<RegistrationStatusEntity>();
		entities.add(sample);

		Mockito.when(entityStatusBaseDao.findbyfilesByThreshold("PACKET_UPLOADED_TO_LANDING_ZONE", 48))
				.thenReturn(entities);
	}

	@Test
	public void getRegistrationStatusSuccessCheck() {

		RegistrationStatusDto dto = registrationStatusService.getRegistrationStatus("1001");

		assertEquals("The registration status should be fetched successfully", "PACKET_UPLOADED_TO_LANDING_ZONE",
				dto.getStatus());
		assertEquals("The registration status should be fetched successfully", "1001", dto.getEnrolmentId());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getRegistrationStatusFailureCheck() throws Exception {

		TablenotAccessibleException exception = new TablenotAccessibleException("The table is not accessible");
		Mockito.when(registrationStatusDao.findById(ArgumentMatchers.any())).thenThrow(exception);

		registrationStatusService.getRegistrationStatus("1000");
	}

	@Test
	public void addRegistrationStatusSuccessCheck() {

		RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto("1001", "PACKET_UPLOADED_TO_LANDING_ZONE", 0,
				LocalDateTime.now(), LocalDateTime.now());

		registrationStatusService.addRegistrationStatus(registrationStatusDto);
		RegistrationStatusDto dto = registrationStatusService.getRegistrationStatus("1001");

		assertEquals("The registration status should get addded successfully", "1001", dto.getEnrolmentId());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void addRegistrationStatusFailureCheck() throws Exception {

		TablenotAccessibleException exception = new TablenotAccessibleException("The table is not accessible");
		RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto("1001", "PACKET_UPLOADED_TO_LANDING_ZONE", 0,
				LocalDateTime.now(), LocalDateTime.now());

		Mockito.when(registrationStatusDao.save(ArgumentMatchers.any())).thenThrow(exception);

		registrationStatusService.addRegistrationStatus(registrationStatusDto);
	}

	@Test
	public void updateRegistrationStatusSuccessCheck() {

		RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto("1001", "PACKET_UPLOADED_TO_LANDING_ZONE", 0,
				LocalDateTime.now(), LocalDateTime.now());

		registrationStatusService.updateRegistrationStatus(registrationStatusDto);
		RegistrationStatusDto dto = registrationStatusService.getRegistrationStatus("1001");

		assertEquals("The registration status should get updated successfully", "1001", dto.getEnrolmentId());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void updateRegistrationStatusFailureCheck() throws IOException {

		TablenotAccessibleException exception = new TablenotAccessibleException("The table is not accessible");
		RegistrationStatusDto registrationStatusDto = new RegistrationStatusDto("1001", "PACKET_UPLOADED_TO_LANDING_ZONE", 0,
				LocalDateTime.now(), LocalDateTime.now());

		Mockito.when(registrationStatusDao.save(ArgumentMatchers.any())).thenThrow(exception);

		registrationStatusService.updateRegistrationStatus(registrationStatusDto);
	}

	@Test
	public void findbyfilesByThresholdSuccessCheck() {

		List<RegistrationStatusDto> registrationStatusDtoList = registrationStatusService
				.findbyfilesByThreshold("PACKET_UPLOADED_TO_LANDING_ZONE");

		assertNotEquals(new ArrayList<RegistrationStatusDto>(), registrationStatusDtoList);
	}

	@Test(expected = TablenotAccessibleException.class)
	public void findbyfilesByThresholdFailureCheck() throws IOException {

		TablenotAccessibleException exception = new TablenotAccessibleException("The table is not accessible");

		Mockito.when(entityStatusBaseDao.findbyfilesByThreshold(ArgumentMatchers.any(), ArgumentMatchers.anyInt()))
				.thenThrow(exception);

		registrationStatusService.findbyfilesByThreshold("PACKET_UPLOADED_TO_LANDING_ZONE");
	}

	public void getByIdsTest() {

	}

}
