package io.mosip.registration.processor.status.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
@TestPropertySource({ "classpath:status-application.properties" })
@ContextConfiguration
public class SyncRegistrationServiceTest {

	private SyncRegistrationDto syncRegistrationDto;
	private SyncRegistrationEntity syncRegistrationEntity;
	private List<SyncRegistrationDto> entities;
	
	@Mock
	private SyncRegistrationDao syncRegistrationDao;
	
	@Mock
	private SyncRegistrationService<SyncRegistrationDto> syncRegistrationService;
	
	@Before
	public void setup() {
		
		entities = new ArrayList<>();
		syncRegistrationDto = new SyncRegistrationDto();
		syncRegistrationDto.setRegistrationId("1001");
		syncRegistrationDto.setParentRegistrationId("");
		syncRegistrationDto.setIsActive(true);
		
		syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setRegistrationId("1001");
		syncRegistrationEntity.setParentRegistrationId("");
		syncRegistrationEntity.setIsActive(true);
		
		entities.add(syncRegistrationDto);
		
		
		
	}
	
	@Test
	public void getSyncRegistrationStatusTest() {

		//Mockito.when(syncRegistrationDao.save(ArgumentMatchers.any())).thenReturn(syncRegistrationEntity);
		List<SyncRegistrationDto>  syncRegDtoList = syncRegistrationService.sync(entities);
		System.out.println(syncRegDtoList);
		//System.out.println("Testing");
	}
}
