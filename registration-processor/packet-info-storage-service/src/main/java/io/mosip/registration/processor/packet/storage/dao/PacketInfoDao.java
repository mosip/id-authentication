package io.mosip.registration.processor.packet.storage.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicDedupeDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.dto.PhotographDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachinePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;

@Component
public class PacketInfoDao {

	@Autowired
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;
	@Autowired
	private BasePacketRepository<RegCenterMachineEntity, RegCenterMachinePKEntity> regCenterMachineRepository;



	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	private List<Object[]> applicantInfo = new ArrayList<>();

	@Autowired
	private BasePacketRepository<RegOsiEntity, String> regOsiRepository;

	public List<ApplicantInfoDto> getPacketsforQCUser(String qcuserId) {
		List<ApplicantInfoDto> applicantInfoDtoList = new ArrayList<>();
		ApplicantInfoDto applicantInfoDto = new ApplicantInfoDto();

		List<QcuserRegistrationIdEntity> assignedPackets = qcuserRegRepositary.findByUserId(qcuserId);
		if (!assignedPackets.isEmpty()) {
			assignedPackets.forEach(assignedPacket -> {
				String regId = assignedPacket.getId().getRegId();
				applicantInfo = qcuserRegRepositary.getApplicantInfo(regId);
			});
			List<DemographicDedupeDto> demoDedupeList = new ArrayList<>();

			applicantInfo.forEach(objects -> {
				for (Object object : objects) {
					if (object instanceof IndividualDemographicDedupeEntity) {
						demoDedupeList.add(convertEntityToDemographicDto((IndividualDemographicDedupeEntity) object));
						applicantInfoDto.setDemoDedupeList(demoDedupeList);
					} else if (object instanceof ApplicantPhotographEntity) {
						applicantInfoDto.setApplicantPhotograph(
								convertEntityToPhotographDto((ApplicantPhotographEntity) object));

					}
				}
				applicantInfoDtoList.add(applicantInfoDto);
			});
		} else {
			applicantInfo.clear();
		}
		return applicantInfoDtoList;
	}

	public RegOsiDto getEntitiesforRegOsi(String regId) {
		RegOsiDto regOsiDto = null;
		List<RegOsiEntity> osiEntityList = regOsiRepository.findByRegOsiId(regId);
		if (osiEntityList != null) {
			regOsiDto = convertRegOsiEntityToDto(osiEntityList.get(0));
		}
		return regOsiDto;
	}

	public RegistrationCenterMachineDto getRegistrationCenterMachine(String regid) {
		RegCenterMachinePKEntity regCenterMachinePKEntity = new RegCenterMachinePKEntity();
		regCenterMachinePKEntity.setRegId(regid);
		RegCenterMachineEntity regCenterMachineEntity=regCenterMachineRepository.findById(RegCenterMachineEntity.class, regCenterMachinePKEntity);
		RegistrationCenterMachineDto dto=new RegistrationCenterMachineDto();
		dto.setIsActive(regCenterMachineEntity.getIsActive());
		dto.setLatitude(regCenterMachineEntity.getLatitude());
		dto.setLongitude(regCenterMachineEntity.getLongitude());
		dto.setRegcntrId(regCenterMachineEntity.getCntrId());
		dto.setRegId(regCenterMachineEntity.getId().getRegId());
		dto.setMachineId(regCenterMachineEntity.getMachineId());
		dto.setPacketCreationDate(regCenterMachineEntity.getPacketCreationDate());
		return dto;
	}
	private RegOsiDto convertRegOsiEntityToDto(RegOsiEntity regOsiEntity) {
		RegOsiDto regOsiDto = new RegOsiDto();
		regOsiDto.setRegId(regOsiEntity.getId().getRegId());
		regOsiDto.setPreregId(regOsiEntity.getPreregId());
		regOsiDto.setOfficerId(regOsiEntity.getOfficerId());
		regOsiDto.setOfficerIrisImageName(regOsiEntity.getOfficerIrisImageName());
		regOsiDto.setOfficerfingerType(regOsiEntity.getOfficerfingerType());
		regOsiDto.setOfficerIrisType(regOsiEntity.getOfficerIrisType());
		regOsiDto.setOfficerPhotoName(regOsiEntity.getOfficerPhotoName());
		regOsiDto.setOfficerHashedPin(regOsiEntity.getOfficerHashedPin());
		regOsiDto.setOfficerHashedPwd(regOsiEntity.getOfficerHashedPwd());
		regOsiDto.setOfficerFingerpImageName(regOsiEntity.getOfficerFingerpImageName());
		regOsiDto.setSupervisorId(regOsiEntity.getSupervisorId());
		regOsiDto.setSupervisorFingerpImageName(regOsiEntity.getSupervisorFingerpImageName());
		regOsiDto.setSupervisorIrisImageName(regOsiEntity.getSupervisorIrisImageName());
		regOsiDto.setSupervisorFingerType(regOsiEntity.getSupervisorFingerType());
		regOsiDto.setSupervisorIrisType(regOsiEntity.getSupervisorIrisType());
		regOsiDto.setSupervisorHashedPwd(regOsiEntity.getSupervisorHashedPwd());
		regOsiDto.setSupervisorHashedPin(regOsiEntity.getSupervisorHashedPin());
		regOsiDto.setSupervisorPhotoName(regOsiEntity.getSupervisorPhotoName());
		regOsiDto.setIntroducerId(regOsiEntity.getIntroducerId());
		regOsiDto.setIntroducerTyp(regOsiEntity.getIntroducerTyp());
		regOsiDto.setIntroducerRegId(regOsiEntity.getIntroducerRegId());
		regOsiDto.setIntroducerIrisImageName(regOsiEntity.getIntroducerIrisImageName());
		regOsiDto.setIntroducerFingerpType(regOsiEntity.getIntroducerFingerpType());
		regOsiDto.setIntroducerIrisType(regOsiEntity.getIntroducerIrisType());
		regOsiDto.setIntroducerFingerpImageName(regOsiEntity.getIntroducerFingerpImageName());
		regOsiDto.setIntroducerPhotoName(regOsiEntity.getIntroducerPhotoName());
		regOsiDto.setIntroducerUin(regOsiEntity.getIntroducerUin());
		regOsiDto.setIsActive(true);
		regOsiDto.setIsDeleted(false);

		return regOsiDto;

	}

	private PhotographDto convertEntityToPhotographDto(ApplicantPhotographEntity object) {
		PhotographDto photographDto = new PhotographDto();

		photographDto.setActive(object.isActive());
		photographDto.setCrBy(object.getCrBy());
		photographDto.setExcpPhotoName(object.getExcpPhotoName());
		photographDto.setExcpPhotoStore(object.getExcpPhotoStore());
		photographDto.setHasExcpPhotograph(object.getHasExcpPhotograph());
		photographDto.setImageName(object.getImageName());
		photographDto.setImageStore(object.getImageStore());
		photographDto.setNoOfRetry(object.getNoOfRetry());
		photographDto.setPreRegId(object.getPreRegId());
		photographDto.setQualityScore(object.getQualityScore());
		photographDto.setRegId(object.getId().getRegId());

		return photographDto;
	}

	private DemographicDedupeDto convertEntityToDemographicDto(IndividualDemographicDedupeEntity object) {
		DemographicDedupeDto demo = new DemographicDedupeDto();
		demo.setRegId(object.getId().getRegId());
		demo.setUin(object.getUinRefId());
		demo.setLangCode(object.getId().getLangCode());
		demo.setName(object.getName());
		demo.setGenderCode(object.getGender());
		demo.setDob(object.getDob());
		demo.setPhoneticName(object.getPhoneticName());

		return demo;
	}
	
	public List<DemographicDedupeDto> getAllDemoWithUIN() {
		List<DemographicDedupeDto> demographicDedupeDtoList = new ArrayList<>();
		List<IndividualDemographicDedupeEntity> individualDemographicDedupeEntityList = demographicDedupeRepository.getAllDemoWithUIN();
		if (individualDemographicDedupeEntityList != null) {
			for (IndividualDemographicDedupeEntity entity : individualDemographicDedupeEntityList) {
					demographicDedupeDtoList.add(convertEntityToDemographicDto(entity));
				}

			return demographicDedupeDtoList;
		}
		return demographicDedupeDtoList;
	}
	
	public List<DemographicDedupeDto> findDemoById(String regId) {
		List<DemographicDedupeDto> demographicDedupeDtoList = new ArrayList<>();
		List<IndividualDemographicDedupeEntity> individualDemographicDedupeEntityList = demographicDedupeRepository.findDemoById(regId);
		if (individualDemographicDedupeEntityList != null) {
			for (IndividualDemographicDedupeEntity entity : individualDemographicDedupeEntityList) {
					demographicDedupeDtoList.add(convertEntityToDemographicDto(entity));
				}

			return demographicDedupeDtoList;
		}
		return demographicDedupeDtoList;
	}

	public Set<String> getDedupeRefIds(String refId) {
		int score = 0;
		int threshold = 60;
		Set<String> duplicateRegIds = new HashSet<>();
		List<IndividualDemographicDedupeEntity> idsWithUin = demographicDedupeRepository.getAllDemoWithUIN();

		List<IndividualDemographicDedupeEntity> dedupeWithOutUin = demographicDedupeRepository.findDemoById(refId);


		for(IndividualDemographicDedupeEntity demo : idsWithUin) {

			for(IndividualDemographicDedupeEntity compareDemo :dedupeWithOutUin ) {

				if(demo.getId().getLangCode().equals(compareDemo.getId().getLangCode())) {

					if(demo.getName().equals(compareDemo.getName())) {
						score = score+30;
					}
					if(demo.getGender().equals(compareDemo.getGender())) {
						score = score+30;
					}
					if(demo.getDob().equals(compareDemo.getDob())) {
						score = score+30;
					}
					if(demo.getPhoneticName().equals(compareDemo.getPhoneticName())) {
						score = score+30;
					}

					if(score > threshold) {
						duplicateRegIds.add(demo.getId().getRegId());
						score = 0;
						break;
					}
				}


			}



		}
		return duplicateRegIds;


	}
}
