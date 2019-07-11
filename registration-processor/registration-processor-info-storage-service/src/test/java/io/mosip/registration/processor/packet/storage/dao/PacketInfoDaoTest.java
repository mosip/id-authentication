package io.mosip.registration.processor.packet.storage.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.mockito.Matchers.any;
import io.mosip.registration.processor.packet.storage.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

/**
 * The Class PacketInfoDaoTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class PacketInfoDaoTest {

	/** The packet infodao. */
	@InjectMocks
	private PacketInfoDao packetInfodao;

	/** The qcuser reg repositary. */
	@Mock
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;

	/** The demographic dedupe repository. */
	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	@Mock
	private BasePacketRepository<AbisRequestEntity, String> abisRequestRepository;

	@Mock
	private BasePacketRepository<RegBioRefEntity, String> regBioRefRepository;


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

	@Test
	public void testgetAbisRequestsByBioRefId(){
		List<AbisRequestEntity> abisRequestEntityList = new ArrayList<>();
		AbisRequestEntity abisRequestEntity = new AbisRequestEntity();
		abisRequestEntity.setAbisAppCode("Abis");
		abisRequestEntityList.add(abisRequestEntity);
		Mockito.when(abisRequestRepository.getAbisRequestsByBioRefId(any(), any())).thenReturn(abisRequestEntityList);
		List<AbisRequestEntity> result = packetInfodao.getAbisRequestsByBioRefId("abc-efg");
		assertEquals("Abis", result.get(0).getAbisAppCode());
	}

	@Test
	public void testgetDemoListByTransactionId(){
		List<RegDemoDedupeListEntity> regDemoDedupeListEntityList = new ArrayList<>();
		RegDemoDedupeListEntity regDemoDedupeListEntity = new RegDemoDedupeListEntity();
		regDemoDedupeListEntity.setRegId("1234567890");
		regDemoDedupeListEntityList.add(regDemoDedupeListEntity);
		Mockito.when(abisRequestRepository.getDemoListByTransactionId(any())).thenReturn(regDemoDedupeListEntityList);
		List<RegDemoDedupeListEntity> result = packetInfodao.getDemoListByTransactionId("abc-efg");
		assertEquals("1234567890", result.get(0).getRegId());
	}

	@Test
	public void testgetBioRefIdByRegId(){
		List<RegBioRefEntity> regBioRefEntityList = new ArrayList<>();
		RegBioRefEntity entity = new RegBioRefEntity();
		entity.setBioRefId("abc-efg");
		regBioRefEntityList.add(entity);
		Mockito.when(abisRequestRepository.getBioRefIdByRegId(any())).thenReturn(regBioRefEntityList);
		List<RegBioRefEntity> result = packetInfodao.getBioRefIdByRegId("1234567890");
		assertEquals("abc-efg", result.get(0).getBioRefId());
	}

	@Test
	public void testgetIdentifyByTransactionId(){
		List<AbisRequestEntity> abisRequestEntityList = new ArrayList<>();
		AbisRequestEntity entity = new AbisRequestEntity();
		entity.setAbisAppCode("Abis");
		abisRequestEntityList.add(entity);
		Mockito.when(abisRequestRepository.getIdentifyByTransactionId(any(), any())).thenReturn(abisRequestEntityList);
		List<AbisRequestEntity> result = packetInfodao.getIdentifyByTransactionId("12345", "IDENTIFY");
		assertEquals("Abis", result.get(0).getAbisAppCode());
	}

}
