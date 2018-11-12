package io.mosip.registration.processor.quality.check.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.mosip.registration.processor.core.packet.dto.AddressDTO;
import io.mosip.registration.processor.core.packet.dto.Demographic;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.LocationDTO;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.quality.check.dto.ApplicantInfoDto;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.quality.check.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.quality.check.repository.QcuserRegRepositary;

@Component
public class ApplicantInfoDao {
	/** The registration information. */

	/** The Constant AND. */
	public static final String AND = "AND";

	/** The Constant EMPTY_STRING. */
	public static final String EMPTY_STRING = " ";

	/** The Constant SELECT_DISTINCT. */
	public static final String SELECT_DISTINCT = "SELECT DISTINCT ";

	/** The Constant FROM. */
	public static final String FROM = " FROM  ";

	/** The Constant WHERE. */
	public static final String WHERE = " WHERE ";

	/** The Constant ISACTIVE. */
	public static final String ISACTIVE = "isActive";

	/** The Constant ISDELETED. */
	public static final String ISDELETED = "isDeleted";

	/** The Constant ISACTIVE_COLON. */
	public static final String ISACTIVE_COLON = ".isActive=:";

	/** The Constant ISDELETED_COLON. */
	public static final String ISDELETED_COLON = ".isDeleted=:";

	@Autowired
	private QcuserRegRepositary<QcuserRegistrationIdEntity, String> qcuserRegRepositary;

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
						validateObject(object, applicantInfoDto);
					} else if (object instanceof ApplicantPhotographEntity) {
						applicantInfoDto
								.setApplicantPhoto(convertEntityToPhotographDto((ApplicantPhotographEntity) object));
					}
				}
				applicantInfoDtoList.add(applicantInfoDto);
			});
		} else {
			applicantInfo.clear();
		}
		return applicantInfoDtoList;
	}

	private void validateObject(Object object, ApplicantInfoDto applicantInfoDto) {
		if ("en".equalsIgnoreCase(((ApplicantDemographicEntity) object).getId().getLangCode()))
			applicantInfoDto.setDemoInLocalLang(
					convertEntityToDemographicDto((ApplicantDemographicEntity) object).getDemoInLocalLang());
		else
			applicantInfoDto.setDemoInUserLang(
					convertEntityToDemographicDto((ApplicantDemographicEntity) object).getDemoInUserLang());
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

	/**
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

	public QcuserRegistrationIdEntity save(QcuserRegistrationIdEntity qcUserRegistrationIdEntity) {

		return qcuserRegRepositary.save(qcUserRegistrationIdEntity);
	}

	public QcuserRegistrationIdEntity update(QcuserRegistrationIdEntity qcUserRegistrationIdEntity) {

		return qcuserRegRepositary.save(qcUserRegistrationIdEntity);
	}

	public QcuserRegistrationIdEntity findById(String qcUserId, String regId) {
		Map<String, Object> params = new HashMap<>();
		String className = QcuserRegistrationIdEntity.class.getSimpleName();

		String alias = QcuserRegistrationIdEntity.class.getName().toLowerCase().substring(0, 1);

		String queryStr = SELECT_DISTINCT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias
				+ ".id=:QCUserId" + EMPTY_STRING + AND + EMPTY_STRING + alias + ISACTIVE_COLON + ISACTIVE + EMPTY_STRING
				+ AND + EMPTY_STRING + alias + ISDELETED_COLON + ISDELETED;

		QcuserRegistrationIdPKEntity pkEntity = new QcuserRegistrationIdPKEntity();
		pkEntity.setUsrId(qcUserId);
		pkEntity.setRegId(regId);

		params.put("QCUserId", pkEntity);
		params.put(ISACTIVE, Boolean.TRUE);
		params.put(ISDELETED, Boolean.FALSE);

		List<QcuserRegistrationIdEntity> qCuserRegistrationIdEntityList = qcuserRegRepositary
				.createQuerySelect(queryStr, params);

		return !CollectionUtils.isEmpty(qCuserRegistrationIdEntityList) ? qCuserRegistrationIdEntityList.get(0) : null;
	}
}
