package io.mosip.registration.processor.quality.check.service.impl.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.quality.check.service.impl.QualityCheckManagerImpl;

@RunWith(MockitoJUnitRunner.class)
public class QualityCheckManagerImplTest {
	@InjectMocks
	QualityCheckManager<String, ApplicantInfoDto, QCUserDto>  qualityCheckManager= new QualityCheckManagerImpl();
	
	@Mock
	private ApplicantInfoDao applicantInfoDao;

	@Mock
	private AuditRequestBuilder auditRequestBuilder;

	@Mock
	private AuditHandler<AuditRequestDto> auditHandler;
	
	
	@Test
	public void updateQCUserStatusTest() {
		List<QCUserDto> qcUserDtos = new ArrayList<>();
		QCUserDto qCUserDto1 = new QCUserDto();
		qCUserDto1.setQcUserId("123");
		qCUserDto1.setRegId("2018782130000116102018124324");
		qCUserDto1.setDecisionStatus(DecisionStatus.ACCEPTED);
		
		QCUserDto qCUserDto2 = new QCUserDto();
		qCUserDto2.setQcUserId("1234");
		qCUserDto1.setRegId("2018782130000224092018121229");
		qCUserDto1.setDecisionStatus(DecisionStatus.REJECTED);
		


		
		QcuserRegistrationIdEntity entity = new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity pkEntity = new QcuserRegistrationIdPKEntity();
		pkEntity.setRegId("2018782130000116102018124324");
		pkEntity.setUsrId("123");
		entity.setId(pkEntity);
		entity.setCrBy("SYSTEM");
		entity.setCrDtimesz(LocalDateTime.now());
		entity.setIsActive(true);
		entity.setIsDeleted(false);
		entity.setStatus(qCUserDto1.getDecisionStatus().name());
		
		Mockito.when(applicantInfoDao.findById(Matchers.anyString(), Matchers.anyString())).thenReturn(entity);
		Mockito.when(applicantInfoDao.update(Matchers.any())).thenReturn(entity);
		qcUserDtos.add(qCUserDto1);
		qcUserDtos.add(qCUserDto2);
		qualityCheckManager.updateQCUserStatus(qcUserDtos);

	}
}
