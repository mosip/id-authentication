package io.mosip.registration.processor.manual.adjudication.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.anyString;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationStatus;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.manual.adjudication.dao.ManualAdjudicationDao;
import io.mosip.registration.processor.manual.adjudication.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.adjudication.dto.UserDto;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationEntity;
import io.mosip.registration.processor.manual.adjudication.entity.ManualVerificationPKEntity;
import io.mosip.registration.processor.manual.adjudication.exception.InvalidFileNameException;
import io.mosip.registration.processor.manual.adjudication.exception.InvalidUpdateException;
import io.mosip.registration.processor.manual.adjudication.exception.NoRecordAssignedException;
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
import io.mosip.registration.processor.manual.adjudication.service.impl.ManualAdjudicationServiceImpl;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import javassist.bytecode.ByteArray;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManualAdjudicationServiceTest {
	
	private List<ManualVerificationEntity> entities;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@InjectMocks
	private ManualAdjudicationService manualAdjudicationService = new ManualAdjudicationServiceImpl();
	@Mock
	UserDto dto;
	@Mock
	ManualAdjudicationService mockManualAdjudicationService;
	@Mock
	AuditLogRequestBuilder auditLogRequestBuilder;
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
	@Mock
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;
	@Mock
	ManualAdjudicationDao manualAdjudicationDao;
	
	private InternalRegistrationStatusDto registrationStatusDto;
	private ManualVerificationPKEntity PKId;
	private ManualVerificationDTO manualVerificationDTO;
	private ManualVerificationEntity manualVerificationEntity;
	
	
	
	@Before
	public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	

		manualVerificationEntity = new ManualVerificationEntity();
		manualVerificationDTO = new ManualVerificationDTO();
		registrationStatusDto= new InternalRegistrationStatusDto();
		PKId = new ManualVerificationPKEntity();
		dto.setName("User");
		dto.setOffice("Office");
		dto.setStatus("PENDING");
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
		manualVerificationEntity.setPkId(PKId);
		manualVerificationEntity.setLangCode("eng");
		manualVerificationDTO.setRegId("RegID");
		manualVerificationDTO.setMatchedRefId("RefID");
		manualVerificationDTO.setMvUsrId("");
		registrationStatusDto.setStatusCode("");
		registrationStatusDto.setStatusComment("");
		entities = new ArrayList<>();
		entities.add(manualVerificationEntity);
		Mockito.when(manualAdjudicationDao.getFirstApplicantDetails(ManualVerificationStatus.PENDING.name())).thenReturn(entities);
	}
	@Test
	public void assignStatusMethodCheck()
	{
		
	    Mockito.when(manualAdjudicationDao.getAssignedApplicantDetails(dto.getUserId(), ManualVerificationStatus.ASSIGNED.name())).thenReturn(manualVerificationEntity);
		manualAdjudicationService.assignStatus(dto);
	}
	@Test
	public void assignStatusMethodNullEntityCheck()
	{
		//manualVerificationEntity.setStatusCode("PENDING");
		Mockito.when(manualAdjudicationDao.update(manualVerificationEntity)).thenReturn(manualVerificationEntity);
		manualAdjudicationService.assignStatus(dto);
	}
	@Test
	public void getApplicantFileMethodCheck() 
	{
		String regId="Id"; 
		String fileName=PacketFiles.APPLICANTPHOTO.name();
		byte[] file = "Str".getBytes();
		InputStream fileInStream = new ByteArrayInputStream(file);
		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(),anyString())).thenReturn(fileInStream);
		
		file=manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName=PacketFiles.PROOFOFADDRESS.name();
		file=manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName=PacketFiles.PROOFOFIDENTITY.name();
		file=manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName=PacketFiles.EXCEPTIONPHOTO.name();
		file=manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName=PacketFiles.RIGHTPALM.name();
		file=manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName=PacketFiles.LEFTPALM.name();
		file=manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName=PacketFiles.BOTHTHUMBS.name();
		file=manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName=PacketFiles.LEFTEYE.name();
		file=manualAdjudicationService.getApplicantFile(regId, fileName);
		fileName=PacketFiles.RIGHTEYE.name();
		file=manualAdjudicationService.getApplicantFile(regId, fileName);
		
	}
	@Test
	public void getApplicantDataMethodCheck()
	{
		String regId="Id"; 
		String fileName=PacketFiles.DEMOGRAPHICINFO.name();
		byte[] file = "Str".getBytes();
		InputStream fileInStream = new ByteArrayInputStream(file);
		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(),anyString())).thenReturn(fileInStream);
		file=manualAdjudicationService.getApplicantData(regId, fileName);
		fileName=PacketFiles.PACKETMETAINFO.name();
		file=manualAdjudicationService.getApplicantData(regId, fileName);
		
	}
	@Test(expected=InvalidFileNameException.class)
	public void testExceptionIngetApplicantFile() throws Exception {
		String regId="Id"; 
		String fileName="";
		manualAdjudicationService.getApplicantFile(regId, fileName);
	}
	@Test(expected=InvalidFileNameException.class)
	public void testExceptionIngetApplicantData() throws Exception {
		String regId="Id"; 
		String fileName="";
		manualAdjudicationService.getApplicantData(regId, fileName);
	}
	
	
	@Test(expected=NoRecordAssignedException.class)
	public void updatePacketStatusMethodCheck()
	{
		Mockito.when(manualAdjudicationDao.getByRegId(any(),any(),any())).thenReturn(manualVerificationEntity);
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(manualAdjudicationDao.getAssignedApplicantDetails(any(),any())).thenReturn(manualVerificationEntity);

		manualVerificationDTO.setStatusCode("APPROVED");

		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);
		manualVerificationDTO.setStatusCode(ManualVerificationStatus.REJECTED.name());

		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);
		Mockito.when(manualAdjudicationDao.getAssignedApplicantDetails(any(),any())).thenReturn(null);
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);
		
		Mockito.when(manualAdjudicationDao.getByRegId(any(),any(),any())).thenReturn(null);
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);
		
	}
	@Test(expected=InvalidUpdateException.class)
	public void updatePacketStatusExceptionCheck()
	{
		manualVerificationDTO.setStatusCode("");
		manualAdjudicationService.updatePacketStatus(manualVerificationDTO);

	}

	
}
