package io.mosip.registration.processor.packet.storage.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.packet.dto.AddressDTO;
import io.mosip.registration.processor.core.packet.dto.Demographic;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.LocationDTO;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

@Component
public class PacketInfoDao {

	@Autowired
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;

	private List<Object[]> applicantInfo = new ArrayList<>();

	public List<ApplicantInfoDto> getPacketsforQCUser(String qcuserId) {
		List<ApplicantInfoDto> applicantInfoDtoList = new ArrayList<>();
		ApplicantInfoDto applicantInfoDto = new ApplicantInfoDto();

		List<QcuserRegistrationIdEntity> assignedPackets = qcuserRegRepositary.findByUserId(qcuserId);
		if (!assignedPackets.isEmpty()) {
			assignedPackets.forEach(assignedPacket -> {
				String regId = assignedPacket.getId().getRegId();
				applicantInfo = qcuserRegRepositary.getApplicantInfo(regId);
			});
			applicantInfo.forEach(objects -> {
				for (Object object : objects) {
					if (object instanceof ApplicantDemographicEntity) {
						if ("en".equalsIgnoreCase(((ApplicantDemographicEntity) object).getId().getLangCode()))
							applicantInfoDto.setDemoInLocalLang(
									convertEntityToDemographicDto((ApplicantDemographicEntity) object)
											.getDemoInLocalLang());
						else
							applicantInfoDto.setDemoInUserLang(
									convertEntityToDemographicDto((ApplicantDemographicEntity) object)
											.getDemoInUserLang());
					} else if (object instanceof ApplicantPhotographEntity) {
						applicantInfoDto.setApplicantPhoto(convertEntityToPhotographDto((ApplicantPhotographEntity) object));
					}
				}
				applicantInfoDtoList.add(applicantInfoDto);
			});
		} else {
			applicantInfo.clear();
		}
		return applicantInfoDtoList;
	}

	private Photograph convertEntityToPhotographDto(ApplicantPhotographEntity object) {
		Photograph photographDto = new Photograph();

		photographDto.setExceptionPhotoName(object.getExcpPhotoName());
		photographDto.setHasExceptionPhoto(object.getHasExcpPhotograph());
		photographDto.setNumRetry(object.getNoOfRetry());
		photographDto.setPhotographName(object.getImageName());
		photographDto.setQualityScore(Double.valueOf(object.getQualityScore().toString()));

		return photographDto;
	}

	/*
	 * private BiometericData
	 * convertEntityTotBiometericDto(ApplicantFingerprintEntity object) {
	 * BiometericData bioData = new BiometericData(); FingerprintData
	 * fingerprintData = new FingerprintData();
	 * fingerprintData.setExceptionFingerprints(null);
	 * fingerprintData.setFingerprints(null);
	 * 
	 * bioData.setFingerprintData(fingerprintData);
	 * 
	 * IrisData irisData = new IrisData(); irisData.setExceptionIris(null);
	 * irisData.setIris(null); irisData.setNumRetry(0);
	 * bioData.setIrisData(irisData); return bioData; }
	 */

	private Demographic convertEntityToDemographicDto(ApplicantDemographicEntity object) {
		Demographic demo = new Demographic();
		DemographicInfo demoInfo = new DemographicInfo();
		AddressDTO address = new AddressDTO();
		address.setLine1(object.getAddrLine1());
		address.setLine2(object.getAddrLine2());
		address.setLine3(object.getAddrLine3());
		LocationDTO location = new LocationDTO();
		location.setLine4(object.getAddrLine1());
		location.setLine5(object.getAddrLine1());
		location.setLine5(object.getAddrLine1());
		location.setLine5(object.getAddrLine1());
		location.setLine5(object.getAddrLine1());
		address.setLocationDTO(location);
		demoInfo.setAddressDTO(address);
		demoInfo.setAge(String.valueOf(object.getAge()));
		demoInfo.setDateOfBirth(object.getDob() != null ? object.getDob().toString() : null);
		demoInfo.setEmailId(object.getEmail());
		demoInfo.setFirstName(object.getFirstName());
		demoInfo.setGender(object.getGenderCode());
		demoInfo.setLanguageCode(object.getId().getLangCode());
		demoInfo.setMiddleName(object.getMiddleName());
		demoInfo.setLastName(object.getLastName());
		demoInfo.setMobile(object.getMobile());

		if ("en".equalsIgnoreCase(object.getId().getLangCode())) {
			demo.setDemoInLocalLang(demoInfo);
		} else {
			demo.setDemoInUserLang(demoInfo);
		}
		return demo;
	}
}
