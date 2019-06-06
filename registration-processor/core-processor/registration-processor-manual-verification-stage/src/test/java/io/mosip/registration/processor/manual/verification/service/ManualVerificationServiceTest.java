package io.mosip.registration.processor.manual.verification.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.kernel.master.dto.UserResponseDTO;
import io.mosip.registration.processor.core.kernel.master.dto.UserResponseDTOWrapper;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileSystemManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationStatus;
import io.mosip.registration.processor.manual.verification.dto.UserDto;
import io.mosip.registration.processor.manual.verification.exception.InvalidFileNameException;
import io.mosip.registration.processor.manual.verification.exception.InvalidUpdateException;
import io.mosip.registration.processor.manual.verification.exception.MatchTypeNotFoundException;
import io.mosip.registration.processor.manual.verification.exception.NoRecordAssignedException;
import io.mosip.registration.processor.manual.verification.exception.UserIDNotPresentException;
import io.mosip.registration.processor.manual.verification.service.impl.ManualVerificationServiceImpl;
import io.mosip.registration.processor.manual.verification.stage.ManualVerificationStage;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationPKEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(SpringRunner.class)
public class ManualVerificationServiceTest {

	private List<ManualVerificationEntity> entities;
	private List<ManualVerificationEntity> entitiesTemp;
	@InjectMocks
	private ManualVerificationService manualAdjudicationService = new ManualVerificationServiceImpl();
	@Mock
	UserDto dto;
	@Mock
	ManualVerificationStage manualVerificationStage;
	@Mock
	ManualVerificationService mockManualAdjudicationService;
	@Mock
	AuditLogRequestBuilder auditLogRequestBuilder;
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
	@Mock
	FileSystemManager filesystemCephAdapterImpl;
	@Mock
	private BasePacketRepository<ManualVerificationEntity, String> basePacketRepository;
	@Mock
	private JsonUtil jsonUtil;
	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	private InternalRegistrationStatusDto registrationStatusDto;
	private ManualVerificationPKEntity PKId;
	private ManualVerificationDTO manualVerificationDTO;
	private ManualVerificationEntity manualVerificationEntity;

	private String stageName = "ManualVerificationStage";

	private ResponseWrapper<UserResponseDTOWrapper> responseWrapper = new ResponseWrapper<>();
	private UserResponseDTOWrapper userResponseDTOWrapper = new UserResponseDTOWrapper();
	private List<UserResponseDTO> userResponseDto = new ArrayList<>();
	private UserResponseDTO userResponseDTO = new UserResponseDTO();

	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		manualVerificationEntity = new ManualVerificationEntity();
		manualVerificationDTO = new ManualVerificationDTO();
		registrationStatusDto = new InternalRegistrationStatusDto();
		dto = new UserDto();
		entities = new ArrayList<ManualVerificationEntity>();
		entitiesTemp = new ArrayList<ManualVerificationEntity>();
		PKId = new ManualVerificationPKEntity();
		PKId.setMatchedRefId("RefID");
		PKId.setMatchedRefType("Type");
		PKId.setRegId("RegID");
		dto.setUserId("mvusr22");
		manualVerificationEntity.setCrBy("regprc");
		manualVerificationEntity.setMvUsrId("test");
		manualVerificationEntity.setIsActive(true);
		Date date = new Date();
		manualVerificationEntity.setDelDtimes(new Timestamp(date.getTime()));
		manualVerificationEntity.setIsDeleted(true);
		manualVerificationEntity.setStatusComment("test");
		manualVerificationEntity.setStatusCode(ManualVerificationStatus.PENDING.name());
		manualVerificationEntity.setReasonCode("test");
		manualVerificationEntity.setIsActive(true);
		manualVerificationEntity.setId(PKId);
		manualVerificationEntity.setLangCode("eng");
		manualVerificationDTO.setRegId("RegID");
		manualVerificationDTO.setMatchedRefId("RefID");
		manualVerificationDTO.setMvUsrId("test");
		registrationStatusDto.setStatusCode(ManualVerificationStatus.PENDING.name());
		registrationStatusDto.setStatusComment("test");
		manualVerificationDTO.setMatchedRefType("Type");
		manualVerificationDTO.setStatusCode("PENDING");
		entities.add(manualVerificationEntity);
		Mockito.when(basePacketRepository.getFirstApplicantDetails(ManualVerificationStatus.PENDING.name(), "DEMO"))
				.thenReturn(entities);
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(anyString(), anyString())).thenReturn(entities);

		userResponseDTO.setStatusCode("ACT");
		userResponseDTOWrapper.setUserResponseDto(userResponseDto);
		responseWrapper.setResponse(userResponseDTOWrapper);

	}

	@Test
	public void assignStatusMethodCheck() {
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(anyString(), anyString())).thenReturn(entities);
		dto.setMatchType("DEMO");
		dto.setUserId("110003");

		userResponseDTO.setStatusCode("ACT");
		userResponseDto.add(userResponseDTO);
		userResponseDTOWrapper.setUserResponseDto(userResponseDto);
		responseWrapper.setResponse(userResponseDTOWrapper);
		try {
			Mockito.doReturn(responseWrapper).when(restClientService).getApi(any(), any(), any(), any(), any());
		} catch (ApisResourceAccessException e) {
			e.printStackTrace();
		}
		ManualVerificationDTO manualVerificationDTO1 = manualAdjudicationService.assignApplicant(dto);
		assertEquals(manualVerificationDTO, manualVerificationDTO1);

	}

	@Test
	public void assignStatusMethodNullEntityCheck() {
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(anyString(), anyString()))
				.thenReturn(entitiesTemp);
		Mockito.when(basePacketRepository.update(manualVerificationEntity)).thenReturn(manualVerificationEntity);
		dto.setMatchType("DEMO");
		dto.setUserId("110003");

		userResponseDTO.setStatusCode("ACT");
		userResponseDto.add(userResponseDTO);
		userResponseDTOWrapper.setUserResponseDto(userResponseDto);
		responseWrapper.setResponse(userResponseDTOWrapper);
		try {
			Mockito.doReturn(responseWrapper).when(restClientService).getApi(any(), any(), any(), any(), any());
		} catch (ApisResourceAccessException e) {
			e.printStackTrace();
		}

		manualAdjudicationService.assignApplicant(dto);
	}

	@Test(expected = NoRecordAssignedException.class)
	public void noRecordAssignedExceptionAssignStatus() {
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(anyString(), anyString()))
				.thenReturn(entitiesTemp);
		Mockito.when(basePacketRepository.getFirstApplicantDetails(ManualVerificationStatus.PENDING.name(), "DEMO"))
				.thenReturn(entitiesTemp);
		dto.setMatchType("DEMO");
		dto.setUserId("110003");

		userResponseDTO.setStatusCode("ACT");
		userResponseDto.add(userResponseDTO);
		userResponseDTOWrapper.setUserResponseDto(userResponseDto);
		responseWrapper.setResponse(userResponseDTOWrapper);
		try {
			Mockito.doReturn(responseWrapper).when(restClientService).getApi(any(), any(), any(), any(), any());
		} catch (ApisResourceAccessException e) {
			e.printStackTrace();
		}
		manualAdjudicationService.assignApplicant(dto);
	}

	@Test(expected = MatchTypeNotFoundException.class)
	public void noMatchTypeNotFoundException() {
		dto.setMatchType("test");
		dto.setUserId("110003");

		userResponseDTO.setStatusCode("ACT");
		userResponseDto.add(userResponseDTO);
		userResponseDTOWrapper.setUserResponseDto(userResponseDto);
		responseWrapper.setResponse(userResponseDTOWrapper);
		try {
			Mockito.doReturn(responseWrapper).when(restClientService).getApi(any(), any(), any(), any(), any());
		} catch (ApisResourceAccessException e) {
			e.printStackTrace();
		}
		manualAdjudicationService.assignApplicant(dto);
	}

	@Test(expected = UserIDNotPresentException.class)
	public void noUserIDNotPresentException() {
		dto.setUserId("dummyID");
		dto.setMatchType("DEMO");

		responseWrapper.setResponse(null);
		try {
			Mockito.doReturn(responseWrapper).when(restClientService).getApi(any(), any(), any(), any(), any());
		} catch (ApisResourceAccessException e) {
			e.printStackTrace();
		}
		manualAdjudicationService.assignApplicant(dto);
	}

	@Test
	public void TablenotAccessibleExceptionTest() throws Exception {
		manualVerificationDTO.setStatusCode("REJECTED");
		Mockito.when(basePacketRepository.getSingleAssignedRecord(any(), any(), any(), any())).thenReturn(entities);

		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.when(basePacketRepository.update(any(ManualVerificationEntity.class)))
				.thenThrow(new TablenotAccessibleException(""));
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO, stageName);

	}

	@Test
	public void getApplicantFileMethodCheck() throws PacketDecryptionFailureException, ApisResourceAccessException, IOException, java.io.IOException {
		String regId = "Id";

		byte[] file = "Str".getBytes();
		InputStream fileInStream = new ByteArrayInputStream(file);
		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(fileInStream);

		String fileName = PacketFiles.BIOMETRIC.name();
		file = manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName = PacketFiles.DEMOGRAPHIC.name();
		file = manualAdjudicationService.getApplicantFile(regId, fileName);

	}

	@Test(expected = InvalidFileNameException.class)
	public void testExceptionIngetApplicantFile() throws Exception {
		String regId = "Id";
		String fileName = "";
		manualAdjudicationService.getApplicantFile(regId, fileName);
	}

	@Test(expected = InvalidFileNameException.class)
	public void testExceptionIngetApplicantData() throws Exception {
		String regId = "Id";
		String fileName = "";
		manualAdjudicationService.getApplicantFile(regId, fileName);
	}

	@Test(expected = InvalidUpdateException.class)
	public void updatePacketStatusExceptionCheck() {
		manualVerificationDTO.setStatusCode("");
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO, stageName);

	}

	@Test(expected = NoRecordAssignedException.class)
	public void updatePacketStatusNoRecordAssignedExceptionCheck() {
		manualVerificationDTO.setStatusCode("REJECTED");
		Mockito.when(basePacketRepository.getSingleAssignedRecord(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(entitiesTemp);
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO, stageName);

	}

	@Ignore
	@Test
	public void updatePacketStatusApprovalMethodCheck() {
		Mockito.when(basePacketRepository.getSingleAssignedRecord(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(entities);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(anyString(), anyString())).thenReturn(null);
		Mockito.when(basePacketRepository.update(any(ManualVerificationEntity.class)))
				.thenReturn(manualVerificationEntity);
		manualVerificationDTO.setStatusCode(ManualVerificationStatus.APPROVED.name());

		Mockito.doNothing().when(manualVerificationStage).sendMessage(any(MessageDTO.class));
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO, stageName);
	}

	@Test
	public void updatePacketStatusRejectionMethodCheck() {
		manualVerificationDTO.setStatusCode(ManualVerificationStatus.REJECTED.name());
		;
		Mockito.when(basePacketRepository.getSingleAssignedRecord(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(entities);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(anyString(), anyString())).thenReturn(null);
		Mockito.when(basePacketRepository.update(any())).thenReturn(manualVerificationEntity);

		Mockito.doNothing().when(manualVerificationStage).sendMessage(any());
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO, stageName);
	}

	@Test(expected = InvalidUpdateException.class)
	public void invalidStatusUpdateCheck() {
		manualVerificationDTO.setStatusCode("ASSIGNED");
		manualVerificationDTO.setMvUsrId("abcde");
		manualVerificationDTO.setRegId("abcde");
		Mockito.when(basePacketRepository.getSingleAssignedRecord(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(entities);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(anyString(), anyString())).thenReturn(null);
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO, stageName);
	}

	@Test
	public void getApplicantPacketInfoSuccess() throws PacketDecryptionFailureException, ApisResourceAccessException, IOException, java.io.IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);
		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(idJsonStream);
		manualAdjudicationService.getApplicantPacketInfo("Id");
	}

}
