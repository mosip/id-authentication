package registration.processor.manual.adjudication.service.test;


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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.context.ContextConfiguration;
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
import io.mosip.registration.processor.manual.adjudication.service.ManualAdjudicationService;
import io.mosip.registration.processor.manual.adjudication.service.impl.ManualAdjudicationServiceImpl;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import javassist.bytecode.ByteArray;

@RunWith(MockitoJUnitRunner.class)
@DataJpaTest
@RefreshScope
@ContextConfiguration
public class ManualAdjudicationServiceTest {
	
	private List<ManualVerificationEntity> entities;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@InjectMocks
	private ManualAdjudicationService manualAdjudicationService = new ManualAdjudicationServiceImpl();
	@Mock
	ManualAdjudicationService mockManualAdjudicationService;
	@Mock
	ManualVerificationPKEntity PKId;
	@Mock
	AuditLogRequestBuilder auditLogRequestBuilder;
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
	@Mock
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;
	@Mock
	ManualAdjudicationDao manualAdjudicationDao;
	
	@Mock
	ManualVerificationDTO manualVerificationDTO;
	ManualVerificationEntity manualVerificationEntity;
	@Mock
	UserDto dto;
	
	
	@Before
	public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	

		//AuditResponseDto auditResponseDto=new AuditResponseDto();
		//Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder("test case description",EventId.RPR_401.toString(),EventName.ADD.toString(),EventType.BUSINESS.toString(), "1234testcase");
		manualVerificationEntity = new ManualVerificationEntity();
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
		Mockito.when(manualAdjudicationDao.update(manualVerificationEntity)).thenReturn(manualVerificationEntity);
		entities = new ArrayList<>();
		entities.add(manualVerificationEntity);
		
		
	

		

	}
	@Test
	public void assignStatusMethodCheck()
	{
		
		//manualVerificationEntity.setStatusCode("PENDING");
		Mockito.when(manualAdjudicationDao.getFirstApplicantDetails(anyString())).thenReturn(entities);
		
		
		manualVerificationDTO=manualAdjudicationService.assignStatus(dto);
	}
	@Test
	public void assignStatusMethodEntityCheck()
	{
		//manualVerificationEntity.setStatusCode("PENDING");
		Mockito.when(manualAdjudicationDao.getFirstApplicantDetails(anyString())).thenReturn(entities);
		Mockito.when(manualAdjudicationDao.update(manualVerificationEntity)).thenReturn(null);
		manualVerificationDTO=manualAdjudicationService.assignStatus(dto);
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
		//file=manualAdjudicationService.getApplicantData(regId, fileName);
	}
	@Test(expected=InvalidFileNameException.class)
	public void testExceptionIngetApplicantData() throws Exception {
		String regId="Id"; 
		String fileName="";
		
		
		
		//file=manualAdjudicationService.getApplicantFile(regId, fileName);
		manualAdjudicationService.getApplicantData(regId, fileName);
	}

	
}
