package io.mosip.registration.processor.quality.check.service.impl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.kernel.dataaccess.hibernate.exception.DataAccessLayerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.spi.auditmanager.AuditHandler;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dao.QCUserInfoDao;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.quality.check.entity.UserDetailPKEntity;
import io.mosip.registration.processor.quality.check.exception.InvalidQcUserIdException;
import io.mosip.registration.processor.quality.check.exception.InvalidRegistrationIdException;
import io.mosip.registration.processor.quality.check.exception.ResultNotFoundException;
import io.mosip.registration.processor.quality.check.exception.TablenotAccessibleException;
import io.mosip.registration.processor.quality.check.service.impl.QualityCheckManagerImpl;

@RunWith(MockitoJUnitRunner.class)
public class QualityCheckManagerImplTest {
	@InjectMocks
	QualityCheckManager<String, ApplicantInfoDto, QCUserDto> qualityCheckManager = new QualityCheckManagerImpl();

	@Mock
	private ApplicantInfoDao applicantInfoDao;

	@Mock
	private AuditRequestBuilder auditRequestBuilder;

	@Mock
	private QCUserInfoDao qcUserInfoDao;
	@Mock
	private AuditHandler<AuditRequestDto> auditHandler;
	private List<QCUserDto> qcUserDtos;
	private QcuserRegistrationIdEntity entity;
	QcuserRegistrationIdPKEntity pkEntity;
	QCUserDto qCUserDto1;
	List<String> qcuserlist;
	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		 
		 
		 qcuserlist=Arrays.asList("qc001","qc002","qc003");
		qcUserDtos = new ArrayList<>();
		entity = new QcuserRegistrationIdEntity();
		pkEntity = new QcuserRegistrationIdPKEntity();
		qCUserDto1 = new QCUserDto();

		qCUserDto1.setQcUserId("123");
		qCUserDto1.setRegId("2018782130000116102018124324");
		qCUserDto1.setDecisionStatus(DecisionStatus.ACCEPTED);

		AuditRequestBuilder auditRequestBuilder1 = new AuditRequestBuilder();
		AuditHandler<AuditRequestDto> auditHandler = new AuditHandler<AuditRequestDto>() {

			@Override
			public boolean writeAudit(AuditRequestDto arg0) {

				return true;
			}
		};
		Field f1 = qualityCheckManager.getClass().getDeclaredField("auditRequestBuilder");
		f1.setAccessible(true);
		f1.set(qualityCheckManager, auditRequestBuilder1);

		Field f2 = qualityCheckManager.getClass().getDeclaredField("auditHandler");
		f2.setAccessible(true);
		f2.set(qualityCheckManager, auditHandler);

	}
	@Test
	public void assignQCUserTest() {
		Mockito.when(qcUserInfoDao.getAllQcuserIds()).thenReturn(qcuserlist);
		QCUserDto qcUserDto=qualityCheckManager.assignQCUser("2018782130000116102018124324");
		assertEquals(DecisionStatus.PENDING, qcUserDto.getDecisionStatus());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void assignQCUserFailureTest() {
		Mockito.when(qcUserInfoDao.getAllQcuserIds()).thenReturn(qcuserlist);
		
		
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE, "errorMessage",
				new Exception());
		Mockito.when(applicantInfoDao.save(ArgumentMatchers.any(QcuserRegistrationIdEntity.class)))
				.thenThrow(exp);
		qualityCheckManager.assignQCUser("2018782130000116102018124324");
	}
	@Test
	public void getPacketsforQCUserTest(){
		ApplicantInfoDto applicantInfoDto=new ApplicantInfoDto();
		applicantInfoDto.setApplicantPhoto(mock(Photograph.class));
		applicantInfoDto.setDemoInLocalLang(mock(DemographicInfo.class));
		applicantInfoDto.setDemoInUserLang(mock(DemographicInfo.class));
		Mockito.when(applicantInfoDao.getPacketsforQCUser(ArgumentMatchers.anyString())).thenReturn(Arrays.asList(applicantInfoDto));
		List<ApplicantInfoDto> list=qualityCheckManager.getPacketsforQCUser("qc001");
		assertFalse(list.isEmpty());
	}
	
	@Test(expected = TablenotAccessibleException.class)
	public void getPacketsforQCUserFailureTest(){
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE, "errorMessage",
				new Exception());
		Mockito.when(applicantInfoDao.getPacketsforQCUser(ArgumentMatchers.anyString())).thenThrow(exp);
		qualityCheckManager.getPacketsforQCUser("qc001");
		
	}
	@Test
	public void updateQCUserStatusTest() {

		qcUserDtos.add(qCUserDto1);
		pkEntity.setRegId("2018782130000116102018124324");
		pkEntity.setUsrId("123");
		entity.setId(pkEntity);
		entity.setCrBy("SYSTEM");
		entity.setCrDtimesz(LocalDateTime.now());
		entity.setIsActive(true);
		entity.setIsDeleted(false);
		entity.setStatus(DecisionStatus.ACCEPTED.name());
		Mockito.when(applicantInfoDao.findById(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(entity);
		Mockito.when(applicantInfoDao.update(ArgumentMatchers.any())).thenReturn(entity);

		List<QCUserDto> dtolist = qualityCheckManager.updateQCUserStatus(qcUserDtos);

		assertEquals(DecisionStatus.ACCEPTED, dtolist.get(0).getDecisionStatus());

	}

	@Test(expected = InvalidRegistrationIdException.class)
	public void updateQCUserStatusInvalidRegistrationIdExceptionTest() {
		qCUserDto1.setRegId("");
		qcUserDtos.add(qCUserDto1);
		qualityCheckManager.updateQCUserStatus(qcUserDtos);

	}

	@Test(expected = InvalidQcUserIdException.class)
	public void updateQCUserStatusInvalidQcUserIdExceptionTest() {
		qCUserDto1.setQcUserId("");
		qcUserDtos.add(qCUserDto1);
		qualityCheckManager.updateQCUserStatus(qcUserDtos);

	}

	@Test(expected = ResultNotFoundException.class)
	public void updateQCUserStatusResultNotFoundExceptionExceptionTest() {
		qcUserDtos.add(qCUserDto1);
		Mockito.when(applicantInfoDao.findById(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenReturn(null);

		qualityCheckManager.updateQCUserStatus(qcUserDtos);

	}

	@Test(expected = TablenotAccessibleException.class)
	public void updateQCUserStatusDataAccessLayerExceptionTest() {
		qcUserDtos.add(qCUserDto1);
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE, "errorMessage",
				new Exception());
		Mockito.when(applicantInfoDao.findById(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenThrow(exp);

		qualityCheckManager.updateQCUserStatus(qcUserDtos);

	}

}
