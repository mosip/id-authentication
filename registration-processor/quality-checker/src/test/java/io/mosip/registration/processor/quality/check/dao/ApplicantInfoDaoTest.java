package io.mosip.registration.processor.quality.check.dao;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographPKEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupePKEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.entity.RoleListEntity;
import io.mosip.registration.processor.quality.check.entity.UserDetailEntity;
import io.mosip.registration.processor.quality.check.entity.UserRoleEntity;
import io.mosip.registration.processor.quality.check.repository.QcuserRegRepositary;

@RunWith(MockitoJUnitRunner.class)
public class ApplicantInfoDaoTest {

	@InjectMocks
	private ApplicantInfoDao applicantInfoDao;

	@Mock
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;

	@Mock
	private QcuserRegRepositary<RoleListEntity, String> roleListRegRepositary;

	@Mock
	private QcuserRegRepositary<UserDetailEntity, String> userDetailRepositary;

	@Mock
	private QcuserRegRepositary<UserRoleEntity, String> userRoleRepositary;

	QcuserRegistrationIdEntity qcuserRegistrationIdEntity1;
	QcuserRegistrationIdEntity qcuserRegistrationIdEntity2;

	@Before
	public void setUp() {
		qcuserRegistrationIdEntity1 = new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity pkid1 = new QcuserRegistrationIdPKEntity();
		pkid1.setUsrId("qc001");
		pkid1.setRegId("2018782130000116102018124324");
		qcuserRegistrationIdEntity1.setCrBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity1.setIsActive(true);
		qcuserRegistrationIdEntity1.setId(pkid1);
		qcuserRegistrationIdEntity1.setStatus_code(DecisionStatus.ACCEPTED.name());
		qcuserRegistrationIdEntity1.setUpdBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity1.setCrDtimes(LocalDateTime.now());
		qcuserRegistrationIdEntity1.setIsDeleted(false);
		qcuserRegistrationIdEntity1.setUpdDtimes(LocalDateTime.now());

		qcuserRegistrationIdEntity2 = new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity pkid2 = new QcuserRegistrationIdPKEntity();
		pkid2.setUsrId("qc001");
		pkid2.setRegId("2018782130000116102018124325");
		qcuserRegistrationIdEntity2.setCrBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity2.setIsActive(true);
		qcuserRegistrationIdEntity2.setId(pkid2);
		qcuserRegistrationIdEntity2.setStatus_code(DecisionStatus.ACCEPTED.name());
		qcuserRegistrationIdEntity2.setUpdBy("MOSIP_SYSTEM");
		qcuserRegistrationIdEntity2.setCrDtimes(LocalDateTime.now());
		qcuserRegistrationIdEntity2.setIsDeleted(false);
		qcuserRegistrationIdEntity2.setUpdDtimes(LocalDateTime.now());

		Mockito.when(qcuserRegRepositary.findByUserId(ArgumentMatchers.anyString()))
				.thenReturn(Arrays.asList(qcuserRegistrationIdEntity1, qcuserRegistrationIdEntity2));

		Mockito.when(qcuserRegRepositary.save(ArgumentMatchers.any(QcuserRegistrationIdEntity.class)))
				.thenReturn(qcuserRegistrationIdEntity1);
		Map<String, Object> params = new HashMap<>();
		params.put("QCUserId", pkid1);
		params.put("isActive", Boolean.TRUE);
		params.put("isDeleted", Boolean.FALSE);
		Mockito.when(qcuserRegRepositary.createQuerySelect(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(Arrays.asList(qcuserRegistrationIdEntity1));

	}

	@Test
	public void getPacketsforQCUserPhotographic() {
		ApplicantPhotographEntity[] applicantPhotographEntity = new ApplicantPhotographEntity[1];
		applicantPhotographEntity[0] = new ApplicantPhotographEntity();
		applicantPhotographEntity[0].setImageName("new_image");
		;
		applicantPhotographEntity[0].setExcpPhotoName("new_image");
		applicantPhotographEntity[0].setNoOfRetry(2);
		applicantPhotographEntity[0].setHasExcpPhotograph(true);
		ApplicantPhotographPKEntity pkid = new ApplicantPhotographPKEntity();
		pkid.setRegId("2018782130000116102018124324");
		applicantPhotographEntity[0].setId(pkid);
		applicantPhotographEntity[0].setQualityScore(new BigDecimal(123456123456.78));

		IndividualDemographicDedupeEntity[] dedupeArray = new IndividualDemographicDedupeEntity[1];
		IndividualDemographicDedupeEntity dedupeEntity = new IndividualDemographicDedupeEntity();
		IndividualDemographicDedupePKEntity dedupePKEntity = new IndividualDemographicDedupePKEntity();
		Date date = new Date(1995, 04, 16);
		dedupePKEntity.setLangCode("fr");
		dedupePKEntity.setRegId("2018782130000224092018121229");
		dedupeEntity.setId(dedupePKEntity);
		dedupeEntity.setDob(date);
		dedupeEntity.setName("Ibrahim");
		dedupeEntity.setPhoneticName("I165");
		dedupeEntity.setGender("mâle");
		dedupeEntity.setIsActive(true);
		dedupeArray[0] = dedupeEntity;
		List<Object[]> applicantInfo2 = new ArrayList<>();
		applicantInfo2.add(applicantPhotographEntity);
		applicantInfo2.add(dedupeArray);
		Mockito.when(qcuserRegRepositary.getApplicantInfo(ArgumentMatchers.any())).thenReturn(applicantInfo2);
		List<ApplicantInfoDto> listDto = applicantInfoDao.getPacketsforQCUser("qc001");
		assertEquals(true, listDto.get(0).getApplicantPhotograph().getHasExcpPhotograph());
	}

	@Test
	public void save() {
		QcuserRegistrationIdEntity rEntity = applicantInfoDao.save(qcuserRegistrationIdEntity1);
		assertEquals(qcuserRegistrationIdEntity1, rEntity);

	}

	@Test
	public void update() {
		QcuserRegistrationIdEntity rEntity = applicantInfoDao.update(qcuserRegistrationIdEntity1);
		assertEquals(qcuserRegistrationIdEntity1, rEntity);

	}

	@Test
	public void findByIdTest() {
		QcuserRegistrationIdEntity rEntity = applicantInfoDao.findById(qcuserRegistrationIdEntity1.getId().getUsrId(),
				qcuserRegistrationIdEntity1.getId().getRegId());
		assertEquals(qcuserRegistrationIdEntity1, rEntity);

	}

}
