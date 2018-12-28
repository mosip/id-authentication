package io.mosip.registration.processor.quality.check.dao;
	
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.dto.PhotographDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdPKEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

/**
 * The Class ApplicantInfoDao.
 */
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

	/** The qcuser reg repositary. */
	@Autowired
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;

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
				applicantInfo = qcuserRegRepositary.getApplicantInfo(regId);
			});
			List<DemographicInfoDto> demoDedupeList = new ArrayList<>();

			applicantInfo.forEach(objects -> {
				for (Object object : objects) {
					if (object instanceof IndividualDemographicDedupeEntity) {
						demoDedupeList.add(convertEntityToDemographicDto((IndividualDemographicDedupeEntity) object));
						applicantInfoDto.setDemoDedupeList(demoDedupeList);

					} else if (object instanceof ApplicantPhotographEntity) {
						applicantInfoDto
								.setApplicantPhotograph(convertEntityToPhotographDto((ApplicantPhotographEntity) object));
					}
				}
				applicantInfoDtoList.add(applicantInfoDto);
			});
		} else {
			applicantInfo.clear();
		}
		return applicantInfoDtoList;
	}



	/**
	 * Convert entity to photograph dto.
	 *
	 * @param object the object
	 * @return the photograph dto
	 */
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
	 *
	 * @param object the object
	 * @return the demographic info dto
	 */

	private DemographicInfoDto convertEntityToDemographicDto(IndividualDemographicDedupeEntity object) {
		DemographicInfoDto demo = new DemographicInfoDto();
		demo.setRegId(object.getId().getRegId());
		demo.setUin(object.getUinRefId());
		demo.setLangCode(object.getId().getLangCode());
		demo.setName(object.getName());
		demo.setGenderCode(object.getGender());
		demo.setDob(object.getDob());
		demo.setPhoneticName(object.getPhoneticName());


		return demo;
	}

	/**
	 * Save.
	 *
	 * @param qcUserRegistrationIdEntity the qc user registration id entity
	 * @return the qcuser registration id entity
	 */
	public QcuserRegistrationIdEntity save(QcuserRegistrationIdEntity qcUserRegistrationIdEntity) {

		return qcuserRegRepositary.save(qcUserRegistrationIdEntity);
	}

	/**
	 * Update.
	 *
	 * @param qcUserRegistrationIdEntity the qc user registration id entity
	 * @return the qcuser registration id entity
	 */
	public QcuserRegistrationIdEntity update(QcuserRegistrationIdEntity qcUserRegistrationIdEntity) {

		return qcuserRegRepositary.save(qcUserRegistrationIdEntity);
	}

	/**
	 * Find by id.
	 *
	 * @param qcUserId the qc user id
	 * @param regId the reg id
	 * @return the qcuser registration id entity
	 */
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
