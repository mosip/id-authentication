package io.mosip.registration.processor.quality.check.serviceimpl;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.quality.check.client.QCUsersClient;
import io.mosip.registration.processor.quality.check.dao.ApplicantInfoDao;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.quality.check.exception.InvalidQcUserIdException;
import io.mosip.registration.processor.quality.check.exception.InvalidRegistrationIdException;
import io.mosip.registration.processor.quality.check.exception.ResultNotFoundException;
import io.mosip.registration.processor.quality.check.exception.TablenotAccessibleException;
import io.mosip.registration.processor.quality.check.service.impl.QualityCheckManagerImpl;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

@RunWith(MockitoJUnitRunner.class)
public class QualityCheckManagerImplTest {
	@InjectMocks
	QualityCheckManager<String, QCUserDto> qualityCheckManager = new QualityCheckManagerImpl();

	@Mock
	private ApplicantInfoDao applicantInfoDao;

	@Mock
	private AuditLogRequestBuilder auditRequestBuilder;

	@Mock
	QCUsersClient qcUsersClient;

	private List<QCUserDto> qcUserDtos;
	private QcuserRegistrationIdEntity entity;
	QcuserRegistrationIdPKEntity pkEntity;
	QCUserDto qCUserDto1;
	List<String> qcuserlist;

	@Before
	public void setup()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		qcUserDtos = new ArrayList<>();
		entity = new QcuserRegistrationIdEntity();
		pkEntity = new QcuserRegistrationIdPKEntity();
		qCUserDto1 = new QCUserDto();

		qCUserDto1.setQcUserId("123");
		qCUserDto1.setRegId("2018782130000116102018124324");
		qCUserDto1.setDecisionStatus(DecisionStatus.ACCEPTED);

	}

	@Test
	public void assignQCUserTest() {

		QCUserDto qcUserDto = qualityCheckManager.assignQCUser("2018782130000116102018124324");
		assertEquals(DecisionStatus.PENDING, qcUserDto.getDecisionStatus());
	}

	@Test(expected = TablenotAccessibleException.class)
	public void assignQCUserFailureTest() {

		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.toString(),
				"errorMessage", new Exception());
		Mockito.when(applicantInfoDao.save(ArgumentMatchers.any(QcuserRegistrationIdEntity.class))).thenThrow(exp);
		qualityCheckManager.assignQCUser("2018782130000116102018124324");
	}

	@Test
	public void updateQCUserStatusTest() {

		qcUserDtos.add(qCUserDto1);
		pkEntity.setRegId("2018782130000116102018124324");
		pkEntity.setUsrId("123");
		entity.setId(pkEntity);
		entity.setCrBy("SYSTEM");
		entity.setCrDtimes(LocalDateTime.now());
		entity.setIsActive(true);
		entity.setIsDeleted(false);
		entity.setStatus_code(DecisionStatus.ACCEPTED.name());
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
		DataAccessLayerException exp = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE.getErrorCode(),
				"errorMessage", new Exception());
		Mockito.when(applicantInfoDao.findById(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
				.thenThrow(exp);

		qualityCheckManager.updateQCUserStatus(qcUserDtos);

	}

}
