package io.mosip.registration.processor.stages.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.idobjectvalidator.constant.IdObjectValidatorSupportedOperations;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
@RunWith(SpringRunner.class)
public class IdObjectsSchemaValidationOperationMapperTest {
	
	@InjectMocks
	IdObjectsSchemaValidationOperationMapper idObjectsSchemaValidationOperationMapper;
	
	@Mock
	private SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> syncRegistrationService;
	
	@Mock
	private Utilities utility;
	
	@Before
	public void setup() throws Exception {
		ReflectionTestUtils.setField(idObjectsSchemaValidationOperationMapper, "ageLimit", "10");
		Mockito.when(utility.getApplicantAge(anyString())).thenReturn(18);
	}
	
	@Test
	public void childRegistrationTest() throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		SyncRegistrationEntity regEntity=new SyncRegistrationEntity();
		regEntity.setRegistrationType(SyncTypeDto.NEW.getValue());
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		Mockito.when(utility.getApplicantAge(anyString())).thenReturn(9);
		assertEquals(IdObjectValidatorSupportedOperations.CHILD_REGISTRATION.getOperation(),
				idObjectsSchemaValidationOperationMapper.getOperation("1234567890").getOperation());
	}

	@Test
	public void newRegistrationTest() throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		SyncRegistrationEntity regEntity=new SyncRegistrationEntity();
		regEntity.setRegistrationType(SyncTypeDto.NEW.getValue());
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		assertEquals(IdObjectValidatorSupportedOperations.NEW_REGISTRATION.getOperation(),
				idObjectsSchemaValidationOperationMapper.getOperation("1234567890").getOperation());
	}

	@Test
	public void lostUINTest() throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		SyncRegistrationEntity regEntity=new SyncRegistrationEntity();
		regEntity.setRegistrationType(SyncTypeDto.LOST.getValue());
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		assertEquals(IdObjectValidatorSupportedOperations.LOST_UIN.getOperation(),
				idObjectsSchemaValidationOperationMapper.getOperation("1234567890").getOperation());
	}

	@Test
	public void updateUINTest() throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		SyncRegistrationEntity regEntity=new SyncRegistrationEntity();
		regEntity.setRegistrationType(SyncTypeDto.UPDATE.getValue());
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		assertEquals(IdObjectValidatorSupportedOperations.UPDATE_UIN.getOperation(),
				idObjectsSchemaValidationOperationMapper.getOperation("1234567890").getOperation());
	}

	@Test
	public void resUpdateTest() throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		SyncRegistrationEntity regEntity=new SyncRegistrationEntity();
		regEntity.setRegistrationType(SyncTypeDto.RES_UPDATE.getValue());
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		assertEquals(IdObjectValidatorSupportedOperations.UPDATE_UIN.getOperation(),
				idObjectsSchemaValidationOperationMapper.getOperation("1234567890").getOperation());
	}

	@Test
	public void activateTest() throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		SyncRegistrationEntity regEntity=new SyncRegistrationEntity();
		regEntity.setRegistrationType(SyncTypeDto.ACTIVATED.getValue());
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		assertEquals(IdObjectValidatorSupportedOperations.UPDATE_UIN.getOperation(),
				idObjectsSchemaValidationOperationMapper.getOperation("1234567890").getOperation());
	}

	@Test
	public void deactivateTest() throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		SyncRegistrationEntity regEntity=new SyncRegistrationEntity();
		regEntity.setRegistrationType(SyncTypeDto.DEACTIVATED.getValue());
		Mockito.when(syncRegistrationService.findByRegistrationId(anyString())).thenReturn(regEntity);
		assertEquals(IdObjectValidatorSupportedOperations.UPDATE_UIN.getOperation(),
				idObjectsSchemaValidationOperationMapper.getOperation("1234567890").getOperation());
	}
	
}
