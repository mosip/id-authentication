package io.mosip.registration.processor.packet.storage.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.packet.dto.ApplicantDocument;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.dto.PhotographDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachinePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

/**
 * The Class PacketInfoDao.
 */
@Component
public class PacketInfoDao {

	/** The qcuser reg repositary. */
	@Autowired
	private BasePacketRepository<QcuserRegistrationIdEntity, String> qcuserRegRepositary;

	/** The reg center machine repository. */
	@Autowired
	private BasePacketRepository<RegCenterMachineEntity, RegCenterMachinePKEntity> regCenterMachineRepository;

	/** The demographic dedupe repository. */
	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;
	
	@Autowired
	private BasePacketRepository<ApplicantDocumentEntity, String> applicantDocumentEntity;

	/** The applicant info. */
	private List<Object[]> applicantInfo = new ArrayList<>();

	/** The reg osi repository. */
	@Autowired
	private BasePacketRepository<RegOsiEntity, String> regOsiRepository;

	/** The Constant SELECT. */
	private static final String SELECT = " SELECT ";

	/** The Constant FROM. */
	private static final String FROM = " FROM  ";

	/** The Constant EMPTY_STRING. */
	private static final String EMPTY_STRING = " ";

	/** The Constant WHERE. */
	private static final String WHERE = " WHERE ";

	/** The Constant AND. */
	private static final String AND = " AND ";

	/** The Constant IS_NOT_NULL. */
	private static final String IS_NOT_NULL = " IS NOT NULL ";

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

	/**
	 * Gets the entitiesfor reg osi.
	 *
	 * @param regId the reg id
	 * @return the entitiesfor reg osi
	 */
	public RegOsiDto getEntitiesforRegOsi(String regId) {
		RegOsiDto regOsiDto = null;
		List<RegOsiEntity> osiEntityList = regOsiRepository.findByRegOsiId(regId);
		if (osiEntityList != null) {
			regOsiDto = convertRegOsiEntityToDto(osiEntityList.get(0));
		}
		return regOsiDto;
	}

	/**
	 * Gets the registration center machine.
	 *
	 * @param regid the regid
	 * @return the registration center machine
	 */
	public RegistrationCenterMachineDto getRegistrationCenterMachine(String regid) {
		RegCenterMachinePKEntity regCenterMachinePKEntity = new RegCenterMachinePKEntity();
		regCenterMachinePKEntity.setRegId(regid);
		RegCenterMachineEntity regCenterMachineEntity = regCenterMachineRepository
				.findById(RegCenterMachineEntity.class, regCenterMachinePKEntity);
		RegistrationCenterMachineDto dto = new RegistrationCenterMachineDto();
		dto.setIsActive(regCenterMachineEntity.getIsActive());
		dto.setLatitude(regCenterMachineEntity.getLatitude());
		dto.setLongitude(regCenterMachineEntity.getLongitude());
		dto.setRegcntrId(regCenterMachineEntity.getCntrId());
		dto.setRegId(regCenterMachineEntity.getId().getRegId());
		dto.setMachineId(regCenterMachineEntity.getMachineId());
		dto.setPacketCreationDate(regCenterMachineEntity.getPacketCreationDate());
		return dto;
	}

	/**
	 * Convert reg osi entity to dto.
	 *
	 * @param regOsiEntity the reg osi entity
	 * @return the reg osi dto
	 */
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
	 * Convert entity to demographic dto.
	 *
	 * @param object the object
	 * @return the demographic info dto
	 */
	private DemographicInfoDto convertEntityToDemographicDto(IndividualDemographicDedupeEntity object) {
		DemographicInfoDto demo = new DemographicInfoDto();
		demo.setRegId(object.getId().getRegId());
		demo.setUin(object.getUin());
		demo.setLangCode(object.getId().getLangCode());
		demo.setName(object.getName());
		demo.setGenderCode(object.getGender());
		demo.setDob(object.getDob());
		demo.setPhoneticName(object.getPhoneticName());

		return demo;
	}

	/**
	 * Find demo by id.
	 *
	 * @param regId the reg id
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
	 * Gets the applicant iris image name by id.
	 *
	 * @param regId the reg id
	 * @return the applicant iris image name by id
	 */
	public List<String> getApplicantIrisImageNameById(String regId) {
		return demographicDedupeRepository.getApplicantIrisImageNameById(regId);
	}

	/**
	 * Gets the applicant finger print image name by id.
	 *
	 * @param regId the reg id
	 * @return the applicant finger print image name by id
	 */
	public List<String> getApplicantFingerPrintImageNameById(String regId) {
		return demographicDedupeRepository.getApplicantFingerPrintImageNameById(regId);
	}

	/**
	 * Update is active if duplicate found.
	 *
	 * @param regId the reg id
	 */
	public void updateIsActiveIfDuplicateFound(String regId) {
		demographicDedupeRepository.updateIsActiveIfDuplicateFound(regId);

	}

	/**
	 * Gets the all demographic entities.
	 *
	 * @param phoneticName the phonetic name
	 * @param gender the gender
	 * @param dob the dob
	 * @param langCode the lang code
	 * @return the all demographic entities
	 */
	private List<IndividualDemographicDedupeEntity> getAllDemographicEntities(String phoneticName, String gender,
			Date dob, String langCode) {
		Map<String, Object> params = new HashMap<>();
		String className = IndividualDemographicDedupeEntity.class.getSimpleName();
		String alias = IndividualDemographicDedupeEntity.class.getName().toLowerCase().substring(0, 1);
		StringBuilder query = new StringBuilder();
		query.append(SELECT + alias + FROM + className + EMPTY_STRING + alias + WHERE + alias + ".uinRefId "
				+ IS_NOT_NULL + AND);
		if (phoneticName != null) {
			query.append(alias + ".phoneticName=:phoneticName ").append(AND);
			params.put("phoneticName", phoneticName);

		}
		if (gender != null) {
			query.append(alias + ".gender=:gender ").append(AND);
			params.put("gender", gender);
		}
		if (dob != null) {
			query.append(alias + ".dob=:dob ").append(AND);
			params.put("dob", dob);
		}
		query.append(alias + ".id.langCode=:langCode");
		params.put("langCode", langCode);

		return demographicDedupeRepository.createQuerySelect(query.toString(), params);
	}

	/**
	 * Gets the all demographic info dtos.
	 *
	 * @param phoneticName the phonetic name
	 * @param gender the gender
	 * @param dob the dob
	 * @param langCode the lang code
	 * @return the all demographic info dtos
	 */
	public List<DemographicInfoDto> getAllDemographicInfoDtos(String phoneticName, String gender, Date dob,
			String langCode) {

		List<DemographicInfoDto> demographicInfoDtos = new ArrayList<>();
		List<IndividualDemographicDedupeEntity> demographicInfoEntities = getAllDemographicEntities(phoneticName,
				gender, dob, langCode);
		for (IndividualDemographicDedupeEntity entity : demographicInfoEntities) {
			demographicInfoDtos.add(convertEntityToDemographicDto(entity));
		}
		return demographicInfoDtos;
	}

	public List<String> getRegIdByUIN(String uin) {
		return demographicDedupeRepository.getRegIdByUIN(uin);
	}
	
	public List<ApplicantDocument> getDocumentsByRegId(String regId) {
		List<ApplicantDocument> applicantDocumentDtos = new ArrayList<>();

		List<ApplicantDocumentEntity> applicantDocumentEntities = applicantDocumentEntity.getDocumentsByRegId(regId);
		for (ApplicantDocumentEntity entity : applicantDocumentEntities) {
			applicantDocumentDtos.add(convertEntityToApplicantDocumentDto(entity));
		}
		return applicantDocumentDtos;
	}

	private ApplicantDocument convertEntityToApplicantDocumentDto(ApplicantDocumentEntity entity) {
		ApplicantDocument applicantDocumentDto = new ApplicantDocument();
		applicantDocumentDto.setDocName(entity.getDocName());
		applicantDocumentDto.setDocStore(entity.getDocStore());
		return applicantDocumentDto;
	}
}
