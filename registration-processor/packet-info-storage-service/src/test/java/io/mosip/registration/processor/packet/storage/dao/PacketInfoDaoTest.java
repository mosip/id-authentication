package io.mosip.registration.processor.packet.storage.dao;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.processor.core.packet.dto.ApplicantDocument;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographPKEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupePKEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachinePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiPkEntity;
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

	/** The reg center machine repository. */
	@Mock
	private BasePacketRepository<RegCenterMachineEntity, RegCenterMachinePKEntity> regCenterMachineRepository;

	/** The demographic dedupe repository. */
	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The reg osi repository. */
	@Mock
	private BasePacketRepository<RegOsiEntity, String> regOsiRepository;

	/** The applicant document entity. */
	@Mock
	private BasePacketRepository<ApplicantDocumentEntity, String> applicantDocumentEntity;

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
		dedupeEntity.setDob(date);
		dedupeEntity.setName("Ibrahim");
		dedupeEntity.setPhoneticName("I165");
		dedupeEntity.setGender("mâle");
		dedupeEntity.setIsActive(true);

	}

	/**
	 * Gets the packetsfor QC user test.
	 *
	 * @return the packetsfor QC user test
	 */
	@Test
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
		applicantphotoArray[0] = applicantPhotoEntity;

		List<Object[]> applicantInfo = new ArrayList<>();
		applicantInfo.add(applicantphotoArray);
		applicantInfo.add(dedupeArray);

		Mockito.when(qcuserRegRepositary.findByUserId(ArgumentMatchers.anyString())).thenReturn(assignedPackets);
		Mockito.when(qcuserRegRepositary.getApplicantInfo(ArgumentMatchers.anyString())).thenReturn(applicantInfo);

		List<ApplicantInfoDto> applicantInfoList = packetInfodao.getPacketsforQCUser("2018782130000224092018121229");

		assertEquals("2018782130000224092018121229", applicantInfoList.get(0).getApplicantPhotograph().getRegId());

	}

	/**
	 * Gets the entitiesfor reg osi test.
	 *
	 * @return the entitiesfor reg osi test
	 */
	@Test
	public void getEntitiesforRegOsiTest() {
		List<RegOsiEntity> osiEntityList = new ArrayList<>();
		RegOsiEntity regOsiEntity = new RegOsiEntity();
		RegOsiPkEntity regOsiPKEntity = new RegOsiPkEntity();
		regOsiPKEntity.setRegId("2018782130000224092018121229");
		regOsiEntity.setId(regOsiPKEntity);
		regOsiEntity.setIntroducerFingerpImageName("IntroducerFingerImageName");
		regOsiEntity.setIntroducerId("2018234500321157812");
		regOsiEntity.setIntroducerFingerpType("BothThumbs");
		regOsiEntity.setIntroducerIrisType("LeftEye");
		regOsiEntity.setIntroducerPhotoName("introducerPhoto");
		regOsiEntity.setOfficerHashedPin("58086E976BA47A9F1A52099412665D8AF3FC587D946817553697E06A352D88E3");
		osiEntityList.add(regOsiEntity);

		Mockito.when(regOsiRepository.findByRegOsiId(ArgumentMatchers.anyString())).thenReturn(osiEntityList);

		RegOsiDto regOsiDto = packetInfodao.getEntitiesforRegOsi("2018782130000224092018121229");

		assertEquals("2018782130000224092018121229", regOsiDto.getRegId());

	}

	/**
	 * Gets the registration center machine test.
	 *
	 * @return the registration center machine test
	 */
	@Test
	public void getRegistrationCenterMachineTest() {
		RegCenterMachineEntity regMachineEntity = new RegCenterMachineEntity();
		RegCenterMachinePKEntity regMachinePKEntity = new RegCenterMachinePKEntity();
		regMachinePKEntity.setRegId("2018782130000224092018121229");
		regMachineEntity.setId(regMachinePKEntity);
		regMachineEntity.setIsActive(true);
		regMachineEntity.setLatitude("12.9716° N");
		regMachineEntity.setLongitude("77.5946° E");
		regMachineEntity.setCntrId("12245");
		regMachineEntity.getId().getRegId();
		regMachineEntity.setMachineId("yyeqy26356");
		regMachineEntity.setPacketCreationDate(LocalDateTime.now());

		Mockito.when(regCenterMachineRepository.findById(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(regMachineEntity);
		RegistrationCenterMachineDto regCenterMachineDto = packetInfodao
				.getRegistrationCenterMachine("2018782130000224092018121229");

		assertEquals("2018782130000224092018121229", regCenterMachineDto.getRegId());
	}

	/**
	 * Gets the all demo with UIN test.
	 *
	 * @return the all demo with UIN test
	 */
	@Test
	public void getAllDemoWithUINTest() {
		dedupeEntity.setUin("1234");
		List<IndividualDemographicDedupeEntity> dedupeList = new ArrayList<>();
		dedupeList.add(dedupeEntity);
		Mockito.when(demographicDedupeRepository.createQuerySelect(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(dedupeList);
		List<DemographicInfoDto> duplicateUin = packetInfodao.getAllDemographicInfoDtos("A125", "male", null, "ar");
		assertEquals("1234", duplicateUin.get(0).getUin());

	}

	/**
	 * Find demo by id test.
	 */
	@Test
	public void findDemoByIdTest() {
		List<IndividualDemographicDedupeEntity> demographicDedupeEntityList = new ArrayList<>();
		demographicDedupeEntityList.add(dedupeEntity);

		Mockito.when(demographicDedupeRepository.findDemoById(ArgumentMatchers.anyString()))
				.thenReturn(demographicDedupeEntityList);

		List<DemographicInfoDto> demographicDedupeDtoList = packetInfodao.findDemoById("2018782130000224092018121229");
		assertEquals("2018782130000224092018121229", demographicDedupeDtoList.get(0).getRegId());
	}

	/**
	 * Gets the applicant iris image name by id test.
	 *
	 * @return the applicant iris image name by id test
	 */
	@Test
	public void getApplicantIrisImageNameByIdTest() {
		List<String> irisImageList = new ArrayList<>();
		irisImageList.add("leftEye");
		Mockito.when(demographicDedupeRepository.getApplicantIrisImageNameById(ArgumentMatchers.anyString()))
				.thenReturn(irisImageList);

		List<String> result = packetInfodao.getApplicantIrisImageNameById("2018782130000224092018121229");
		assertEquals("leftEye", result.get(0));

	}

	/**
	 * Gets the applicant finger print image name by id test.
	 *
	 * @return the applicant finger print image name by id test
	 */
	@Test
	public void getApplicantFingerPrintImageNameByIdTest() {
		List<String> applicantFingerPrint = new ArrayList<>();
		applicantFingerPrint.add("leftThumb");
		Mockito.when(demographicDedupeRepository.getApplicantIrisImageNameById(ArgumentMatchers.anyString()))
				.thenReturn(applicantFingerPrint);

		List<String> result = packetInfodao.getApplicantIrisImageNameById("2018782130000224092018121229");
		assertEquals("leftThumb", result.get(0));

	}

	/**
	 * Gets the reg id by UIN test.
	 *
	 * @return the reg id by UIN test
	 */
	@Test
	public void getRegIdByUINTest() {
		List<String> regIdList = new ArrayList<>();
		regIdList.add("2018782130000224092018121229");
		Mockito.when(demographicDedupeRepository.getRegIdByUIN("493410317027")).thenReturn(regIdList);
		List<String> result = packetInfodao.getRegIdByUIN("493410317027");
		assertEquals("2018782130000224092018121229", result.get(0));
	}

	/**
	 * Gets the documents by reg id test.
	 *
	 * @return the documents by reg id test
	 */
	@Test
	public void getDocumentsByRegIdTest() {
		List<ApplicantDocumentEntity> applicantDocumentEntities = new ArrayList<>();
		ApplicantDocumentEntity applicantDocument = new ApplicantDocumentEntity();
		ApplicantDocumentPKEntity pkEntity = new ApplicantDocumentPKEntity();
		pkEntity.setDocCatCode("individualBiometrics");
		pkEntity.setDocTypCode("POA");
		pkEntity.setRegId("2018782130000224092018121229");
		applicantDocument.setId(pkEntity);
		String docValue = "dGVzdA";
		byte[] docStore = docValue.getBytes();
		applicantDocument.setDocStore(docStore);

		applicantDocumentEntities.add(applicantDocument);
		Mockito.when(applicantDocumentEntity.getDocumentsByRegId("2018782130000224092018121229"))
				.thenReturn(applicantDocumentEntities);
		List<ApplicantDocument> result = packetInfodao.getDocumentsByRegId("2018782130000224092018121229");
		assertEquals("individualBiometrics", result.get(0).getDocName());

	}
}
