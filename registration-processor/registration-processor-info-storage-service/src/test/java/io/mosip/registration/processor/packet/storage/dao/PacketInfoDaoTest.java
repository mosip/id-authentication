package io.mosip.registration.processor.packet.storage.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupePKEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

/**
 * The Class PacketInfoDaoTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class PacketInfoDaoTest {

	/** The packet infodao. */
	@InjectMocks
	PacketInfoDao packetInfodao;

	/** The qcuser reg repositary. */
	@Mock
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;


	/** The demographic dedupe repository. */
	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;


	/** The applicant document entity. */
	//@Mock
	//private BasePacketRepository<ApplicantDocumentEntity, String> applicantDocumentEntity;

	/** The dedupe entity. */
	private IndividualDemographicDedupeEntity dedupeEntity;

	/** The dedupe PK entity. */
	private IndividualDemographicDedupePKEntity dedupePKEntity;

	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		dedupeEntity = new IndividualDemographicDedupeEntity();
		dedupePKEntity = new IndividualDemographicDedupePKEntity();
		Date date = new Date(1995, 04, 16);
		dedupePKEntity.setLangCode("fr");
		dedupePKEntity.setRegId("2018782130000224092018121229");
		dedupeEntity.setId(dedupePKEntity);
		dedupeEntity.setDob(date.toString());
		dedupeEntity.setName("Ibrahim");
		dedupeEntity.setGender("mâle");
		dedupeEntity.setIsActive(true);

	}

	/**
	 * Gets the packetsfor QC user test.
	 *
	 * @return the packetsfor QC user test
	 */
	/*@Test
	public void getPacketsforQCUserTest() {
		List<QcuserRegistrationIdEntity> assignedPackets = new ArrayList<>();
		QcuserRegistrationIdEntity qcUserEntity = new QcuserRegistrationIdEntity();
		QcuserRegistrationIdPKEntity qcUserPKEntity = new QcuserRegistrationIdPKEntity();
		qcUserPKEntity.setRegId("2018782130000224092018121229");
		qcUserPKEntity.setUsrId("Q1234");
		qcUserEntity.setId(qcUserPKEntity);
		qcUserEntity.setIsActive(true);
		qcUserEntity.setLang_code("fr");
		qcUserEntity.setStatus_code("Assigned");
		assignedPackets.add(qcUserEntity);

		IndividualDemographicDedupeEntity[] dedupeArray = new IndividualDemographicDedupeEntity[1];
		dedupeArray[0] = dedupeEntity;

		ApplicantPhotographEntity[] applicantphotoArray = new ApplicantPhotographEntity[1];
		ApplicantPhotographEntity applicantPhotoEntity = new ApplicantPhotographEntity();
		ApplicantPhotographPKEntity applicantPhotoPKEntity = new ApplicantPhotographPKEntity();
		applicantPhotoPKEntity.setRegId("2018782130000224092018121229");
		applicantPhotoEntity.setId(applicantPhotoPKEntity);
		applicantPhotoEntity.setExcpPhotoName("ExpPhoto");
		applicantPhotoEntity.setHasExcpPhotograph(false);
		applicantPhotoEntity.setImageName("PhotoImageName");
		String docValue = "dGVzdA";
		byte[] docStore = docValue.getBytes();
		applicantPhotoEntity.setExcpPhotoStore(docStore);
		applicantPhotoEntity.setImageStore(docStore);

		applicantphotoArray[0] = applicantPhotoEntity;

		List<Object[]> applicantInfo = new ArrayList<>();
		applicantInfo.add(applicantphotoArray);
		applicantInfo.add(dedupeArray);

		Mockito.when(qcuserRegRepositary.findByUserId(anyString())).thenReturn(assignedPackets);
		Mockito.when(qcuserRegRepositary.getApplicantInfo(anyString())).thenReturn(applicantInfo);

		List<ApplicantInfoDto> applicantInfoList = packetInfodao.getPacketsforQCUser("2018782130000224092018121229");

		assertEquals("2018782130000224092018121229", applicantInfoList.get(0).getApplicantPhotograph().getRegId());

	}*/



	/**
	 * Find demo by id test.
	 */
	@Test
	public void findDemoByIdTest() {
		List<IndividualDemographicDedupeEntity> demographicDedupeEntityList = new ArrayList<>();
		demographicDedupeEntityList.add(dedupeEntity);

		Mockito.when(demographicDedupeRepository.findDemoById(anyString())).thenReturn(demographicDedupeEntityList);

		List<DemographicInfoDto> demographicDedupeDtoList = packetInfodao.findDemoById("2018782130000224092018121229");
		assertEquals("2018782130000224092018121229", demographicDedupeDtoList.get(0).getRegId());
	}

}
