package io.mosip.registration.processor.packet.storage.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicDedupeDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.dto.PhotographDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

@Component
public class PacketInfoDao {

	@Autowired
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;

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
		demo.setRegId(object.getId().getRefId());
		demo.setPreRegId(object.getId().getRefId());
		demo.setLangCode(object.getId().getLangCode());
		demo.setName(object.getName());
		demo.setGenderCode(object.getGenderCode());
		demo.setDob(object.getDob());

		return demo;
	}
}
