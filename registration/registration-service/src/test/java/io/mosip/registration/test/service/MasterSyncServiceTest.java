package io.mosip.registration.test.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dao.SyncJobDAO;
import io.mosip.registration.dto.ErrorResponseDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.dto.mastersync.BiometricAttributeDto;
import io.mosip.registration.dto.mastersync.BiometricAttributeResponseDto;
import io.mosip.registration.dto.mastersync.MasterSyncDto;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.impl.MasterSyncServiceImpl;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
public class MasterSyncServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private MasterSyncServiceImpl masterSyncServiceImpl;
	@Mock
	private MasterSyncDao masterSyncDao;

	@Mock
	private AuditFactory auditFactory;

	private static ApplicationContext applicationContext = ApplicationContext.getInstance();

	@BeforeClass
	public static void beforeClass() {

		ReflectionTestUtils.setField(SessionContext.class, "sessionContext", null);
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterId("mosip");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(centerDetailDTO);
		applicationContext.setApplicationMessagesBundle();
	}

	@Test
	public void testMasterSyncSucessCase()
			throws RegBaseCheckedException, JsonParseException, JsonMappingException, IOException {

		MasterSyncDto masterSyncDto = new MasterSyncDto();
		ObjectMapper mapper = new ObjectMapper();

		BiometricAttributeDto biometricattributes = new BiometricAttributeDto();

		BiometricAttributeResponseDto biometricAttributeResponseDto = new BiometricAttributeResponseDto();

		biometricattributes.setBiometricTypeCode("1");
		biometricattributes.setCode("1");
		biometricattributes.setDescription("finerprints");
		biometricattributes.setLangCode("eng");
		biometricattributes.setName("littile finger");

		List<BiometricAttributeDto> biometricattribute = new ArrayList<>();
		biometricattribute.add(biometricattributes);

		biometricAttributeResponseDto.setBiometricattribute(biometricattribute);

		List<BiometricAttributeResponseDto> biometrictypes = new ArrayList<>();
		biometrictypes.add(biometricAttributeResponseDto);

		masterSyncDto.setBiometricattributes(biometrictypes);

		SyncControl masterSyncDetails = new SyncControl();

		masterSyncDetails.setSyncJobId("MDS_J00001");
		masterSyncDetails.setLastSyncDtimes(new Timestamp(System.currentTimeMillis()));
		masterSyncDetails.setCrBy("mosip");
		masterSyncDetails.setIsActive(true);
		masterSyncDetails.setLangCode("eng");
		masterSyncDetails.setCrDtime(new Timestamp(System.currentTimeMillis()));

		Mockito.when(masterSyncDao.getMasterSyncStatus(Mockito.anyString())).thenReturn(masterSyncDetails);

		Mockito.when(mapper.readValue(Mockito.anyString(), MasterSyncDto.class)).thenReturn(masterSyncDto);

		//Mockito.when(masterSyncServiceImpl.getMasterSync("MDS_J00001")).thenReturn(masterSyncResponse);

	}

}
