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

import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicDedupeDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
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

@RunWith(MockitoJUnitRunner.class)
public class PacketInfoDaoTest {
	@InjectMocks
	PacketInfoDao packetInfodao;

	@Mock
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;
	@Mock
	private BasePacketRepository<RegCenterMachineEntity, RegCenterMachinePKEntity> regCenterMachineRepository;
	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;
	@Mock
	private BasePacketRepository<RegOsiEntity, String> regOsiRepository;

	private IndividualDemographicDedupeEntity dedupeEntity;
	private IndividualDemographicDedupePKEntity dedupePKEntity;

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

	@Test
	public void getAllDemoWithUINTest() {

		dedupeEntity.setUinRefId("12345");

		List<IndividualDemographicDedupeEntity> individualDemographicDedupeEntityList = new ArrayList<>();
		individualDemographicDedupeEntityList.add(dedupeEntity);

		Mockito.when(demographicDedupeRepository.getAllDemoWithUIN(ArgumentMatchers.any(), ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(individualDemographicDedupeEntityList);
		Date date = new Date(1995, 04, 16);

		List<DemographicDedupeDto> demoDtoList = packetInfodao.getAllDemoWithUIN("D254", "mâle", date);

		assertEquals("2018782130000224092018121229", demoDtoList.get(0).getRegId());
	}

	@Test
	public void findDemoByIdTest() {
		List<IndividualDemographicDedupeEntity> demographicDedupeEntityList = new ArrayList<>();
		demographicDedupeEntityList.add(dedupeEntity);

		Mockito.when(demographicDedupeRepository.findDemoById(ArgumentMatchers.anyString()))
				.thenReturn(demographicDedupeEntityList);

		List<DemographicDedupeDto> demographicDedupeDtoList = packetInfodao
				.findDemoById("2018782130000224092018121229");
		assertEquals("2018782130000224092018121229", demographicDedupeDtoList.get(0).getRegId());
	}
}
