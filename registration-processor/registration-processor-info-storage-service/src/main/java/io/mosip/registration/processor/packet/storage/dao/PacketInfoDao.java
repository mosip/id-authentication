package io.mosip.registration.processor.packet.storage.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

/**
 * The Class PacketInfoDao.
 */
@Component
public class PacketInfoDao {

	/** The qcuser reg repositary. */
	@Autowired
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;


	/** The demographic dedupe repository. */
	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;


	/** The applicant info. */
	private List<Object[]> applicantInfo = new ArrayList<>();

	/**
	 * Gets the packetsfor QC user.
	 *
	 * @param qcuserId the qcuser id
	 * @return the packetsfor QC user
	 */
	public List<ApplicantInfoDto> getPacketsforQCUser(String qcuserId) {
		List<ApplicantInfoDto> applicantInfoDtoList = new ArrayList<>();
		ApplicantInfoDto applicantInfoDto = new ApplicantInfoDto();

		List<QcuserRegistrationIdEntity> assignedPackets = qcuserRegRepositary.findByUserId(qcuserId);
		if (!assignedPackets.isEmpty()) {
			assignedPackets.forEach(assignedPacket -> {
				String regId = assignedPacket.getId().getRegId();
				//TODO Need to clarify about QCUSER concept
				applicantInfo = new ArrayList<>();//qcuserRegRepositary.getApplicantInfo(regId);
			});
			List<DemographicInfoDto> demoDedupeList = new ArrayList<>();

			applicantInfo.forEach(objects -> {
				for (Object object : objects) {
					if (object instanceof IndividualDemographicDedupeEntity) {
						demoDedupeList.add(convertEntityToDemographicDto((IndividualDemographicDedupeEntity) object));
						applicantInfoDto.setDemoDedupeList(demoDedupeList);
					} /*else if (object instanceof ApplicantPhotographEntity) {
						applicantInfoDto.setApplicantPhotograph(
								convertEntityToPhotographDto((ApplicantPhotographEntity) object));

					}*/
				}
				applicantInfoDtoList.add(applicantInfoDto);
			});
		} else {
			applicantInfo.clear();
		}
		return applicantInfoDtoList;
	}





	/**
	 * Convert entity to demographic dto.
	 *
	 * @param object
	 *            the object
	 * @return the demographic info dto
	 */
	private DemographicInfoDto convertEntityToDemographicDto(IndividualDemographicDedupeEntity object) {
		DemographicInfoDto demo = new DemographicInfoDto();
		demo.setRegId(object.getId().getRegId());
		demo.setLangCode(object.getId().getLangCode());
		demo.setName(object.getName());
		demo.setGenderCode(object.getGender());
		demo.setDob(object.getDob());

		return demo;
	}

	/**
	 * Find demo by id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the list
	 */
	public List<DemographicInfoDto> findDemoById(String regId) {
		List<DemographicInfoDto> demographicDedupeDtoList = new ArrayList<>();
		List<IndividualDemographicDedupeEntity> individualDemographicDedupeEntityList = demographicDedupeRepository
				.findDemoById(regId);
		if (individualDemographicDedupeEntityList != null) {
			for (IndividualDemographicDedupeEntity entity : individualDemographicDedupeEntityList) {
				demographicDedupeDtoList.add(convertEntityToDemographicDto(entity));
			}

		}
		return demographicDedupeDtoList;
	}


	/**
	 * Update is active if duplicate found.
	 *
	 * @param regId the reg id
	 */
	public void updateIsActiveIfDuplicateFound(String regId) {
		demographicDedupeRepository.updateIsActiveIfDuplicateFound(regId);

	}

}