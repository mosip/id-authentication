package io.mosip.registration.processor.manual.verification.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationStatus;
import io.mosip.registration.processor.manual.verification.dto.UserDto;
import io.mosip.registration.processor.manual.verification.exception.InvalidFileNameException;
import io.mosip.registration.processor.manual.verification.exception.InvalidUpdateException;
import io.mosip.registration.processor.manual.verification.exception.NoRecordAssignedException;
import io.mosip.registration.processor.manual.verification.service.ManualVerificationService;
import io.mosip.registration.processor.manual.verification.service.impl.ManualVerificationServiceImpl;
import io.mosip.registration.processor.manual.verification.stage.ManualVerificationStage;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationPKEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(SpringRunner.class)
@SpringBootTest
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
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;
	@Mock
	private BasePacketRepository<ManualVerificationEntity, String> basePacketRepository;

	private InternalRegistrationStatusDto registrationStatusDto;
	private ManualVerificationPKEntity PKId;
	private ManualVerificationDTO manualVerificationDTO;
	private ManualVerificationEntity manualVerificationEntity;

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
		manualVerificationEntity.setDelDtimes(null);
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
		Mockito.when(basePacketRepository.getFirstApplicantDetails(ManualVerificationStatus.PENDING.name()))
				.thenReturn(entities);
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(entities);
	}

	@Test
	public void assignStatusMethodCheck() {
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(entities);
		ManualVerificationDTO manualVerificationDTO1 = manualAdjudicationService.assignApplicant(dto);
		assertEquals(manualVerificationDTO, manualVerificationDTO1);

	}

	@Test
	public void assignStatusMethodNullEntityCheck() {
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(entitiesTemp);
		Mockito.when(basePacketRepository.update(manualVerificationEntity)).thenReturn(manualVerificationEntity);
		manualAdjudicationService.assignApplicant(dto);
	}

	@Test(expected = NoRecordAssignedException.class)
	public void noRecordAssignedExceptionAssignStatus() {
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(entitiesTemp);
		Mockito.when(basePacketRepository.getFirstApplicantDetails(ManualVerificationStatus.PENDING.name()))
				.thenReturn(entitiesTemp);
		manualAdjudicationService.assignApplicant(dto);
	}

	@Test
	public void getApplicantFileMethodCheck() {
		String regId = "Id";
		String fileName = PacketFiles.APPLICANTPHOTO.name();
		byte[] file = "Str".getBytes();
		InputStream fileInStream = new ByteArrayInputStream(file);
		Mockito.when(filesystemCephAdapterImpl.getFile(any(), any())).thenReturn(fileInStream);

		file = manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName = PacketFiles.PROOFOFADDRESS.name();
		file = manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName = PacketFiles.PROOFOFIDENTITY.name();
		file = manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName = PacketFiles.EXCEPTIONPHOTO.name();
		file = manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName = PacketFiles.RIGHTPALM.name();
		file = manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName = PacketFiles.LEFTPALM.name();
		file = manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName = PacketFiles.BOTHTHUMBS.name();
		file = manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName = PacketFiles.LEFTEYE.name();
		file = manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName = PacketFiles.RIGHTEYE.name();
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
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);

	}

	@Test(expected = NoRecordAssignedException.class)
	public void updatePacketStatusNoRecordAssignedExceptionCheck() {
		manualVerificationDTO.setStatusCode("REJECTED");
		Mockito.when(basePacketRepository.getSingleAssignedRecord(any(), any(), any(), any())).thenReturn(entitiesTemp);
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);

	}

	@Test
	public void updatePacketStatusApprovalMethodCheck() {
		Mockito.when(basePacketRepository.getSingleAssignedRecord(any(), any(), any(), any())).thenReturn(entities);
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(any(), any())).thenReturn(null);
		Mockito.when(basePacketRepository.update(any())).thenReturn(manualVerificationEntity);
		manualVerificationDTO.setStatusCode(ManualVerificationStatus.APPROVED.name());

		Mockito.doNothing().when(manualVerificationStage).sendMessage(any());
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);
	}

	@Test
	public void updatePacketStatusRejectionMethodCheck() {
		manualVerificationDTO.setStatusCode(ManualVerificationStatus.REJECTED.name());
		;
		Mockito.when(basePacketRepository.getSingleAssignedRecord(any(), any(), any(), any())).thenReturn(entities);
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(any(), any())).thenReturn(null);
		Mockito.when(basePacketRepository.update(any())).thenReturn(manualVerificationEntity);

		Mockito.doNothing().when(manualVerificationStage).sendMessage(any());
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);
	}

	@Test(expected = InvalidUpdateException.class)
	public void invalidStatusUpdateCheck() {
		manualVerificationDTO.setStatusCode("ASSIGNED");
		manualVerificationDTO.setMvUsrId("abcde");
		manualVerificationDTO.setRegId("abcde");
		Mockito.when(basePacketRepository.getSingleAssignedRecord(any(), any(), any(), any())).thenReturn(entities);
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(basePacketRepository.getAssignedApplicantDetails(any(), any())).thenReturn(null);
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);
	}
	
	@Test
	public void getApplicantPacketInfoSuccess() throws UnsupportedEncodingException, FileNotFoundException {
		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);
		Mockito.when(filesystemCephAdapterImpl.getFile(any(), any())).thenReturn(idJsonStream);
		manualAdjudicationService.getApplicantPacketInfo("Id");
	}

}
