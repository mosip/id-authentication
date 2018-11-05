/*package io.mosip.registration.processor.quality.check.service.impl.test;

import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.quality.check.entity.RoleListEntity;
import io.mosip.registration.processor.quality.check.entity.UserDetailEntity;
import io.mosip.registration.processor.quality.check.entity.UserRoleEntity;
import io.mosip.registration.processor.quality.check.repository.QcuserRegRepositary;
@RunWith(MockitoJUnitRunner.class)
public class ApplicantInfoDaoTest {
	
	@InjectMocks
	private ApplicantInfoDao ApplicantInfoDao;

	@Mock
	private QcuserRegRepositary<QcuserRegistrationIdEntity, String> qcuserRegRepositary;

	@Mock
	private QcuserRegRepositary<RoleListEntity, String> roleListRegRepositary;

	@Mock
	private QcuserRegRepositary<UserDetailEntity, String> userDetailRepositary;

	@Mock
	private QcuserRegRepositary<UserRoleEntity, String> userRoleRepositary;

	QcuserRegistrationIdEntity qcuserRegistrationIdEntity1;
	QcuserRegistrationIdEntity qcuserRegistrationIdEntity2;
	private Photograph photograph=mock(Photograph.class);
	private DemographicInfo demographicInfo=mock(DemographicInfo.class);
	@Before
	public void setUp() {
		qcuserRegistrationIdEntity1=new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity pkid1=new QcuserRegistrationIdPKEntity();
		pkid1.setUsrId("qc001");
		pkid1.setUsrId("2018782130000116102018124324");
		qcuserRegistrationIdEntity1.setCrBy("MOSIP_SYSTEM"); 
		qcuserRegistrationIdEntity1.setIsActive(true);
		qcuserRegistrationIdEntity1.setId(pkid1);
		qcuserRegistrationIdEntity1.setStatus(DecisionStatus.ACCEPTED.name());
		qcuserRegistrationIdEntity1.setUpdBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity1.setCrDtimesz(LocalDateTime.now());
		qcuserRegistrationIdEntity1.setIsDeleted(false);
		qcuserRegistrationIdEntity1.setUpdDtimesz(LocalDateTime.now());
		
		qcuserRegistrationIdEntity2=new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity pkid2=new QcuserRegistrationIdPKEntity();
		pkid2.setUsrId("qc001");
		pkid2.setUsrId("2018782130000116102018124325");
		qcuserRegistrationIdEntity2.setCrBy("MOSIP_SYSTEM"); 
		qcuserRegistrationIdEntity2.setIsActive(true);
		qcuserRegistrationIdEntity2.setId(pkid2);
		qcuserRegistrationIdEntity2.setStatus(DecisionStatus.ACCEPTED.name());
		qcuserRegistrationIdEntity2.setUpdBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity2.setCrDtimesz(LocalDateTime.now());
		qcuserRegistrationIdEntity2.setIsDeleted(false);
		qcuserRegistrationIdEntity2.setUpdDtimesz(LocalDateTime.now());
		
		Mockito.when(qcuserRegRepositary.findByUserId(ArgumentMatchers.anyString())).
				thenReturn(Arrays.asList(qcuserRegistrationIdEntity1,qcuserRegistrationIdEntity2));
	}
	
	@Test
	public void getPacketsforQCUser() {
		List<ApplicantInfoDto> applicantInfoDtoList = new ArrayList<>();
		ApplicantInfoDto applicantInfoDto = new ApplicantInfoDto();
		applicantInfoDto.setApplicantPhoto(photograph);
		applicantInfoDto.setDemoInLocalLang(demographicInfo);
		applicantInfoDto.setDemoInUserLang(demographicInfo);
		ApplicantPhotographEntity applicantPhotographEntity=new ApplicantPhotographEntity();
		ApplicantDemographicEntity applicantDemographicEntity=new ApplicantDemographicEntity();
		List<Object[]> applicantInfo = new ArrayList();
		applicantInfo.add(applicantPhotographEntity);
		applicantInfo.add(applicantDemographicEntity);
		Mockito.when(qcuserRegRepositary.getApplicantInfo(ArgumentMatchers.anyString())).
					thenReturn(applicantInfo);
	}
}
*/