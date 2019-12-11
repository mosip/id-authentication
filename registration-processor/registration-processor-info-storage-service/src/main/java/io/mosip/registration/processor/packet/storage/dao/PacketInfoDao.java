package io.mosip.registration.processor.packet.storage.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.QcuserRegistrationIdEntity;
import io.mosip.registration.processor.packet.storage.entity.RegBioRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegDemoDedupeListEntity;
import io.mosip.registration.processor.packet.storage.mapper.PacketInfoMapper;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.status.entity.BaseRegistrationEntity;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;

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

	/** The abis request repository. */
	@Autowired
	private BasePacketRepository<AbisRequestEntity, String> abisRequestRepository;

	/** The abis response repository. */
	@Autowired
	private BasePacketRepository<AbisResponseEntity, String> abisResponseRepository;

	/** The reg bio ref repository. */
	@Autowired
	private BasePacketRepository<RegBioRefEntity, String> regBioRefRepository;

	@Autowired
	private RegistrationRepositary<BaseRegistrationEntity, String> registrationRepositary;

	/** The applicant info. */
	private List<Object[]> applicantInfo = new ArrayList<>();

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

	/** The Constant IS_ACTIVE_TRUE. */
	private static final boolean IS_ACTIVE_TRUE = true;

	/**
	 * Gets the packetsfor QC user.
	 *
	 * @param qcuserId
	 *            the qcuser id
	 * @return the packetsfor QC user
	 */
	public List<ApplicantInfoDto> getPacketsforQCUser(String qcuserId) {
		List<ApplicantInfoDto> applicantInfoDtoList = new ArrayList<>();
		ApplicantInfoDto applicantInfoDto = new ApplicantInfoDto();

		List<QcuserRegistrationIdEntity> assignedPackets = qcuserRegRepositary.findByUserId(qcuserId);
		if (!assignedPackets.isEmpty()) {
			assignedPackets.forEach(assignedPacket -> {
				String regId = assignedPacket.getId().getRegId();
				// TODO Need to clarify about QCUSER concept
				applicantInfo = new ArrayList<>();// qcuserRegRepositary.getApplicantInfo(regId);
			});
			List<DemographicInfoDto> demoDedupeList = new ArrayList<>();

			applicantInfo.forEach(objects -> {
				for (Object object : objects) {
					if (object instanceof IndividualDemographicDedupeEntity) {
						demoDedupeList.add(convertEntityToDemographicDto((IndividualDemographicDedupeEntity) object));
						applicantInfoDto.setDemoDedupeList(demoDedupeList);
					} /*
						 * else if (object instanceof ApplicantPhotographEntity) {
						 * applicantInfoDto.setApplicantPhotograph(
						 * convertEntityToPhotographDto((ApplicantPhotographEntity) object));
						 *
						 * }
						 */
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
	 * @param regId
	 *            the reg id
	 */
	public void updateIsActiveIfDuplicateFound(String regId) {
		demographicDedupeRepository.updateIsActiveIfDuplicateFound(regId);

	}

	/**
	 * Gets the all demographic entities.
	 *
	 * @param name
	 *            the name
	 * @param gender
	 *            the gender
	 * @param dob
	 *            the dob
	 * @param langCode
	 *            the lang code
	 * @return the all demographic entities
	 */
	private List<IndividualDemographicDedupeEntity> getAllDemographicEntities(String name, String gender, String dob,
			String langCode) {
		Map<String, Object> params = new HashMap<>();
		String className = IndividualDemographicDedupeEntity.class.getSimpleName();
		String alias = IndividualDemographicDedupeEntity.class.getName().toLowerCase().substring(0, 1);
		StringBuilder query = new StringBuilder();
		query.append(SELECT + alias + FROM + className + EMPTY_STRING + alias + WHERE);
		if (name != null) {
			query.append(alias + ".name=:name ").append(AND);
			params.put("name", name);
		}
		if (gender != null) {
			query.append(alias + ".gender=:gender ").append(AND);
			params.put("gender", gender);
		}
		if (dob != null) {
			query.append(alias + ".dob=:dob ").append(AND);
			params.put("dob", dob);
		}
		query.append(alias + ".id.langCode=:langCode").append(AND);
		params.put("langCode", langCode);
		query.append(alias + ".isActive=:isActive");
		params.put("isActive", IS_ACTIVE_TRUE);
		return demographicDedupeRepository.createQuerySelect(query.toString(), params);
	}

	/**
	 * Gets the all demographic info dtos.
	 *
	 * @param name
	 *            the name
	 * @param gender
	 *            the gender
	 * @param dob
	 *            the dob
	 * @param langCode
	 *            the lang code
	 * @return the all demographic info dtos
	 */
	public List<DemographicInfoDto> getAllDemographicInfoDtos(String name, String gender, String dob, String langCode) {

		List<DemographicInfoDto> demographicInfoDtos = new ArrayList<>();
		List<IndividualDemographicDedupeEntity> demographicInfoEntities = getAllDemographicEntities(name, gender, dob,
				langCode);
		for (IndividualDemographicDedupeEntity entity : demographicInfoEntities) {
			demographicInfoDtos.add(convertEntityToDemographicDto(entity));
		}
		return demographicInfoDtos;
	}

	/**
	 * Gets the reg id by UIN.
	 *
	 * @param uin
	 *            the uin
	 * @return the reg id by UIN
	 */

	/**
	 * Gets the insert or identify request.
	 *
	 * @param bioRefId
	 *            the bio ref id
	 * @param refRegtrnId
	 *            the ref regtrn id
	 * @return the insert or identify request
	 */
	public List<AbisRequestEntity> getInsertOrIdentifyRequest(String bioRefId, String refRegtrnId) {
		return abisRequestRepository.getInsertOrIdentifyRequest(bioRefId, refRegtrnId);

	}

	/**
	 * Gets the reference id by batch id.
	 *
	 * @param batchId the batch id
	 * @return the reference id by batch id
	 */
	public List<String> getReferenceIdByBatchId(String batchId) {
		return abisRequestRepository.getReferenceIdByBatchId(batchId);

	}

	/**
	 * Gets the abis transaction id by request id.
	 *
	 * @param requestId
	 *            the request id
	 * @return the abis transaction id by request id
	 */
	public List<String> getAbisTransactionIdByRequestId(String requestId) {
		return abisRequestRepository.getAbisTransactionIdByRequestId(requestId);
	}

	/**
	 * Gets the identify req list by transaction id.
	 *
	 * @param transactionId
	 *            the transaction id
	 * @param requestType
	 *            the request type
	 * @return the identify req list by transaction id
	 */
	public List<AbisRequestEntity> getIdentifyReqListByTransactionId(String transactionId, String requestType) {
		return abisRequestRepository.getIdentifyReqListByTransactionId(transactionId, requestType);
	}

	/**
	 * Gets the abis request by request id.
	 *
	 * @param abisRequestId
	 *            the abis request id
	 * @return the abis request by request id
	 */
	public List<AbisRequestEntity> getAbisRequestByRequestId(String abisRequestId) {
		return abisRequestRepository.getAbisRequestByRequestId(abisRequestId);
	}

	/**
	 * Gets the batch id by request id.
	 *
	 * @param requestId
	 *            the request id
	 * @return the batch id by request id
	 */
	public String getBatchIdByRequestId(String requestId) {
		List<String> batchreqId = abisRequestRepository.getBatchIdByRequestId(requestId);
		if (batchreqId != null && !batchreqId.isEmpty()) {
			return batchreqId.get(0);
		}
		return null;

	}

	/**
	 * Gets the batch statusby batch id.
	 *
	 * @param batchId
	 *            the batch id
	 * @return the batch statusby batch id
	 */
	public List<String> getBatchStatusbyBatchId(String batchId) {
		return abisRequestRepository.getBatchStatusbyBatchId(batchId);

	}

	/**
	 * Gets the insert or identify request.
	 *
	 * @param bioRefId
	 *            the bio ref id
	 * @param refRegtrnId
	 *            the ref regtrn id
	 * @param requestType
	 *            the request type
	 * @return the insert or identify request
	 */
	public List<AbisRequestEntity> getInsertOrIdentifyRequest(String bioRefId, String refRegtrnId, String requestType) {
		return abisRequestRepository.getInsertOrIdentifyRequest(bioRefId, refRegtrnId, requestType);

	}

	/**
	 * Gets the identify by transaction id.
	 *
	 * @param transactionId
	 *            the transaction id
	 * @param identify
	 *            the identify
	 * @return the identify by transaction id
	 */
	public List<AbisRequestEntity> getIdentifyByTransactionId(String transactionId, String identify) {
		return abisRequestRepository.getIdentifyByTransactionId(transactionId, identify);
	}

	/**
	 * Gets the bio ref id by reg id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the bio ref id by reg id
	 */
	public List<RegBioRefEntity> getBioRefIdByRegId(String regId) {
		return abisRequestRepository.getBioRefIdByRegId(regId);
	}

	/**
	 * Gets the bio ref id by reg ids.
	 *
	 * @param regId
	 *            the reg id
	 * @return the bio ref id by reg ids
	 */
	public List<String> getBioRefIdByRegIds(String regId) {
		return abisRequestRepository.getBioRefIdByRegIds(regId);
	}

	/**
	 * Gets the demo list by transaction id.
	 *
	 * @param transactionId
	 *            the transaction id
	 * @return the demo list by transaction id
	 */
	public List<RegDemoDedupeListEntity> getDemoListByTransactionId(String transactionId) {
		return abisRequestRepository.getDemoListByTransactionId(transactionId);
	}

	/**
	 * Gets the abis request I ds.
	 *
	 * @param latestTransactionId
	 *            the latest transaction id
	 * @return the abis request I ds
	 */
	public List<AbisRequestEntity> getAbisRequestIDs(String latestTransactionId) {
		return abisRequestRepository.getAbisRequestIDs(latestTransactionId);

	}

	/**
	 * Gets the abis response I ds.
	 *
	 * @param abisReqId
	 *            the abis req id
	 * @return the abis response I ds
	 */
	public List<AbisResponseEntity> getAbisResponseIDs(String abisReqId) {
		return abisRequestRepository.getAbisResponseIDs(abisReqId);

	}

	/**
	 * Gets the abis response details.
	 *
	 * @param abisResponseId
	 *            the abis response id
	 * @return the abis response details
	 */
	public List<AbisResponseDetEntity> getAbisResponseDetails(String abisResponseId) {
		return abisRequestRepository.getAbisResponseDetails(abisResponseId);

	}

	/**
	 * Gets the abis ref reg ids by matched ref ids.
	 *
	 * @param matchRefIds
	 *            the match ref ids
	 * @return the abis ref reg ids by matched ref ids
	 */
	public List<String> getAbisRefRegIdsByMatchedRefIds(List<String> matchRefIds) {
		return regBioRefRepository.getAbisRefRegIdsByMatchedRefIds(matchRefIds);

	}

	/**
	 * Gets the abis ref matched ref id by rid.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the abis ref matched ref id by rid
	 */
	public List<String> getAbisRefMatchedRefIdByRid(String registrationId) {
		return regBioRefRepository.getAbisRefMatchedRefIdByRid(registrationId);

	}

	/**
	 * Gets the abis response records.
	 *
	 * @param latestTransactionId
	 *            the latest transaction id
	 * @param requestType
	 *            the request type
	 * @return the abis response records
	 */
	public List<AbisResponseDto> getAbisResponseRecords(String latestTransactionId, String requestType) {
		List<AbisResponseEntity> abisResponseEntities = new ArrayList<>();
		List<AbisResponseDto> abisResponseDto = new ArrayList<>();
		List<AbisRequestEntity> abisRequestEntities = abisRequestRepository
				.getAbisRequestIDsbasedOnIdentity(latestTransactionId, requestType);
		for (AbisRequestEntity abisRequestEntity : abisRequestEntities) {
			abisResponseEntities.addAll(abisResponseRepository.getAbisResponseIDs(abisRequestEntity.getId().getId()));
		}
		abisResponseDto.addAll(PacketInfoMapper.convertAbisResponseEntityListToDto(abisResponseEntities));
		return abisResponseDto;
	}

	/**
	 * Gets the abis response records.
	 *
	 * @param abisRefId
	 *            the abis ref id
	 * @param latestTransactionId
	 *            the latest transaction id
	 * @param requestType
	 *            the request type
	 * @return the abis response records
	 */
	public List<AbisResponseDto> getAbisResponseRecords(String abisRefId, String latestTransactionId,
			String requestType) {
		List<AbisResponseEntity> abisResponseEntities = new ArrayList<>();
		List<AbisResponseDto> abisResponseDto = new ArrayList<>();
		List<AbisRequestEntity> abisRequestEntities = abisRequestRepository.getInsertOrIdentifyRequest(abisRefId,
				latestTransactionId, requestType);
		for (AbisRequestEntity abisRequestEntity : abisRequestEntities) {
			abisResponseEntities.addAll(abisResponseRepository.getAbisResponseIDs(abisRequestEntity.getId().getId()));
		}
		abisResponseDto.addAll(PacketInfoMapper.convertAbisResponseEntityListToDto(abisResponseEntities));
		return abisResponseDto;
	}

	/**
	 * Gets the abis response detailed records.
	 *
	 * @param abisResponseDto
	 *            the abis response dto
	 * @return the abis response detailed records
	 */
	public List<AbisResponseDetDto> getAbisResponseDetailedRecords(AbisResponseDto abisResponseDto) {
		List<AbisResponseDetDto> abisResponseDetDtoList = new ArrayList<>();
		List<AbisResponseDetEntity> abisResEntity = abisRequestRepository
				.getAbisResponseDetails(abisResponseDto.getId());
		abisResponseDetDtoList.addAll(PacketInfoMapper.convertAbisResponseDetEntityListToDto(abisResEntity));
		return abisResponseDetDtoList;
	}

	/**
	 * Gets the abis requests by bio ref id.
	 *
	 * @param bioRefId
	 *            the bio ref id
	 * @return the abis requests by bio ref id
	 */
	public List<AbisRequestEntity> getAbisRequestsByBioRefId(String bioRefId) {
		return abisRequestRepository.getAbisRequestsByBioRefId(bioRefId, "INSERT");
	}

	/**
	 * Gets the abis processed requests app code by bio ref id.
	 *
	 * @param bioRefId the bio ref id
	 * @param requestType the request type
	 * @param processed the processed
	 * @return the abis processed requests app code by bio ref id
	 */
	public List<String> getAbisProcessedRequestsAppCodeByBioRefId(String bioRefId, String requestType,
			String processed) {
		return abisRequestRepository.getAbisProcessedRequestsAppCodeByBioRefId(bioRefId, requestType, processed);

	}

	/**
	 * Gets the processed or processing reg ids.
	 *
	 * @param matchedRegIds the matched reg ids
	 * @param statusCode the status code
	 * @return the processed or processing reg ids
	 */
	public List<String> getProcessedOrProcessingRegIds(List<String> matchedRegIds, String statusCode) {
		return registrationRepositary.getProcessedOrProcessingRegIds(matchedRegIds, statusCode);
	}

	/**
	 * Gets the abis response det records list.
	 *
	 * @param abisResponseDto the abis response dto
	 * @return the abis response det records list
	 */
	public List<AbisResponseDetDto> getAbisResponseDetRecordsList(List<String> abisResponseDto) {
		List<AbisResponseDetDto> abisResponseDetDtoList = new ArrayList<>();
		List<AbisResponseDetEntity> abisResEntity = abisRequestRepository.getAbisResponseDetailsList(abisResponseDto);
		abisResponseDetDtoList.addAll(PacketInfoMapper.convertAbisResponseDetEntityListToDto(abisResEntity));
		return abisResponseDetDtoList;
	}
}