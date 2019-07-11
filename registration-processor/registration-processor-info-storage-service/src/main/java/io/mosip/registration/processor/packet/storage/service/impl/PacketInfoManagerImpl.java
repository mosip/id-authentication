package io.mosip.registration.processor.packet.storage.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.DedupeSourceName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisApplicationDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDetDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegBioRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegDemoDedupeListDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisApplicationEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationPKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegAbisRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegBioRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegDemoDedupeListEntity;
import io.mosip.registration.processor.packet.storage.entity.RegLostUinDetEntity;
import io.mosip.registration.processor.packet.storage.entity.RegLostUinDetPKEntity;
import io.mosip.registration.processor.packet.storage.exception.FileNotFoundInPacketStore;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.MappingJsonException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
import io.mosip.registration.processor.packet.storage.exception.UnableToInsertData;
import io.mosip.registration.processor.packet.storage.mapper.PacketInfoMapper;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import lombok.Cleanup;

/**
 * The Class PacketInfoManagerImpl.
 *
 * @author Horteppa M1048399
 * @author Girish Yarru
 *
 */

@RefreshScope
@Service
public class PacketInfoManagerImpl implements PacketInfoManager<Identity, ApplicantInfoDto> {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant LOG_FORMATTER. */
	public static final String LOG_FORMATTER = "{} - {}";

	/** The Constant DEMOGRAPHIC_APPLICANT. */
	public static final String DEMOGRAPHIC_APPLICANT = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

	/** The Reg abis ref repository. */
	@Autowired
	private BasePacketRepository<RegAbisRefEntity, String> regAbisRefRepository;

	/** The reg bio ref repository. */
	@Autowired
	private BasePacketRepository<RegBioRefEntity, String> regBioRefRepository;

	/** The reg abis request repository. */
	@Autowired
	private BasePacketRepository<AbisRequestEntity, String> regAbisRequestRepository;

	/** The reg abis application repository. */
	@Autowired
	private BasePacketRepository<AbisApplicationEntity, String> regAbisApplicationRepository;

	/** The reg demo dedupe list repository. */
	@Autowired
	private BasePacketRepository<RegDemoDedupeListEntity, String> regDemoDedupeListRepository;

	/** The demographic dedupe repository. */
	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The manual verfication repository. */
	@Autowired
	private BasePacketRepository<ManualVerificationEntity, String> manualVerficationRepository;

	@Autowired
	private BasePacketRepository<RegLostUinDetEntity, String> regLostUinDetRepository;

	/** The core audit request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The packet info dao. */
	@Autowired
	private PacketInfoDao packetInfoDao;

	/** The filesystem ceph adapter impl. */
	@Autowired
	private PacketManager filesystemCephAdapterImpl;

	/** The utility. */
	@Autowired
	private Utilities utility;

	/** The reg processor identity json. */
	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	@Value("${registration.processor.demodedupe.manualverification.status}")
	private String manualVerificationStatus;

	/** The pre reg id. */
	private String preRegId;

	/** The Constant MATCHED_REFERENCE_TYPE. */
	private static final String MATCHED_REFERENCE_TYPE = "rid";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketInfoManagerImpl.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getPacketsforQCUser(java.lang.String)
	 */
	@Override
	public List<ApplicantInfoDto> getPacketsforQCUser(String qcUserId) {

		boolean isTransactionSuccessful = false;
		LogDescription description=new LogDescription();
		List<ApplicantInfoDto> applicantInfoDtoList = null;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), qcUserId,
				"PacketInfoManagerImpl::getPacketsforQCUser()::entry");
		try {
			applicantInfoDtoList = packetInfoDao.getPacketsforQCUser(qcUserId);
			isTransactionSuccessful = true;
			description.setMessage("QcUser packet Info fetch Success");
			return applicantInfoDtoList;
		} catch (DataAccessLayerException e) {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));

		description.setMessage( "DataAccessLayerException while fetching QcUser packet Info" + "::" + e.getMessage());

			throw new TablenotAccessibleException(
					PlatformErrorMessages.RPR_PIS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString(), ApiName.AUDIT);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					qcUserId, "PacketInfoManagerImpl::getPacketsforQCUser()::exit");
		}

	}

	/**
	 * Gets the document as byte array.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param documentName
	 *            the document name
	 * @return the document as byte array
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws ApisResourceAccessException 
	 * @throws PacketDecryptionFailureException 
	 */
	private byte[] getDocumentAsByteArray(String registrationId, String documentName) throws PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		try {

			@Cleanup
			InputStream in = filesystemCephAdapterImpl.getFile(registrationId, documentName);
			byte[] buffer = new byte[1024];
			int len;
			@Cleanup
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			while ((len = in.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			return os.toByteArray();
		} catch (IOException e) {

			return new byte[1];
		}

	}

	/**
	 * Gets the identity keys and fetch values from JSON.
	 *
	 * @param demographicJsonString
	 *            the demographic json string
	 * @return the identity keys and fetch values from JSON
	 */
	@Override
	public IndividualDemographicDedupe getIdentityKeysAndFetchValuesFromJSON(String demographicJsonString) {
		IndividualDemographicDedupe demographicData = new IndividualDemographicDedupe();
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"PacketInfoManagerImpl::getIdentityKeysAndFetchValuesFromJSON()::entry");
		try {
			// Get Identity Json from config server and map keys to Java Object
			String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
					utility.getGetRegProcessorIdentityJson());
			ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
			regProcessorIdentityJson = mapIdentityJsonStringToObject.readValue(getIdentityJsonString,
					RegistrationProcessorIdentity.class);
			JSONObject demographicJson = (JSONObject) JsonUtil.objectMapperReadValue(demographicJsonString,
					JSONObject.class);
			JSONObject demographicIdentity = JsonUtil.getJSONObject(demographicJson,
					utility.getGetRegProcessorDemographicIdentity());
			if (demographicIdentity == null)
				throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

			demographicData.setName(JsonUtil.getJsonValues(demographicIdentity,
					regProcessorIdentityJson.getIdentity().getName().getValue()));
			demographicData.setDateOfBirth((String) JsonUtil.getJSONValue(demographicIdentity,
					regProcessorIdentityJson.getIdentity().getDob().getValue()));
			demographicData.setGender(JsonUtil.getJsonValues(demographicIdentity,
					regProcessorIdentityJson.getIdentity().getGender().getValue()));
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));

			throw new MappingJsonException(PlatformErrorMessages.RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION.getMessage(),
					e);

		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));

			throw new ParsingException(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage(), e);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"PacketInfoManagerImpl::getIdentityKeysAndFetchValuesFromJSON()::exit");
		return demographicData;

	}

	/**
	 * Gets the registration id.
	 *
	 * @param metaData
	 *            the meta data
	 * @return the registration id
	 */
	private void getRegistrationId(List<FieldValue> metaData) {
		for (int i = 0; i < metaData.size(); i++) {
			if ("preRegistrationId".equals(metaData.get(i).getLabel())) {
				preRegId = metaData.get(i).getValue();

			}
		}

	}

	/**
	 * Save individual demographic dedupe.
	 *
	 * @param demographicJsonBytes
	 *            the demographic json bytes
	 * @param description 
	 */
	private void saveIndividualDemographicDedupe(byte[] demographicJsonBytes, String regId, LogDescription description) {

		String getJsonStringFromBytes = new String(demographicJsonBytes);
		IndividualDemographicDedupe demographicData = getIdentityKeysAndFetchValuesFromJSON(getJsonStringFromBytes);
		boolean isTransactionSuccessful = false;
		try {
			List<IndividualDemographicDedupeEntity> applicantDemographicEntities = PacketInfoMapper
					.converDemographicDedupeDtoToEntity(demographicData, regId);
			for (IndividualDemographicDedupeEntity applicantDemographicEntity : applicantDemographicEntities) {
				demographicDedupeRepository.save(applicantDemographicEntity);

			}
			isTransactionSuccessful = true;
			description.setMessage("Individual Demographic Dedupe data saved ");

		} catch (DataAccessLayerException e) {
			description.setMessage("DataAccessLayerException while saving Individual Demographic Dedupe data " + "::"
					+ e.getMessage());

			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + regId, e);
		} finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString(), ApiName.AUDIT);

		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * saveIndividualDemographicDedupeUpdatePacket(io.mosip.registration.processor.
	 * core.packet.dto.demographicinfo.IndividualDemographicDedupe,
	 * java.lang.String)
	 */
	@Override
	public void saveIndividualDemographicDedupeUpdatePacket(IndividualDemographicDedupe demographicData,
			String registrationId) {
		boolean isTransactionSuccessful = false;
		LogDescription description=new LogDescription();
		
		try {
			List<IndividualDemographicDedupeEntity> applicantDemographicEntities = PacketInfoMapper
					.converDemographicDedupeDtoToEntity(demographicData, registrationId);
			for (IndividualDemographicDedupeEntity applicantDemographicEntity : applicantDemographicEntities) {
				demographicDedupeRepository.save(applicantDemographicEntity);

			}
			isTransactionSuccessful = true;
			description.setMessage("Individual Demographic Dedupe data saved ");

		} catch (DataAccessLayerException e) {
			description.setMessage("DataAccessLayerException while saving Individual Demographic Dedupe data " + "::"
					+ e.getMessage());

			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + registrationId, e);
		} finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString(), ApiName.AUDIT);

		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * saveDemographicInfoJson(java.io.InputStream, java.util.List)
	 */
	@Override
	public void saveDemographicInfoJson(byte[] bytes, String registrationId, List<FieldValue> metaData) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"PacketInfoManagerImpl::saveDemographicInfoJson()::entry");
		LogDescription description=new LogDescription();
		getRegistrationId(metaData);
		boolean isTransactionSuccessful = false;
		if (bytes == null)
			throw new FileNotFoundInPacketStore(
					PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_PACKET_STORE.getMessage());

		try {

			saveIndividualDemographicDedupe(bytes, registrationId, description);

			isTransactionSuccessful = true;
			description.setMessage("Demographic Json saved");

		} catch (DataAccessLayerException e) {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));

			description.setMessage("DataAccessLayerException while saving Demographic Json" + "::" + e.getMessage());

			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + registrationId, e);
		} finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString(), ApiName.AUDIT);

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"PacketInfoManagerImpl::saveDemographicInfoJson()::exit");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * findDemoById(java.lang.String)
	 */
	@Override
	public List<DemographicInfoDto> findDemoById(String regId) {
		return packetInfoDao.findDemoById(regId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * saveManualAdjudicationData(java.util.Set, java.lang.String)
	 */
	@Override
	public void saveManualAdjudicationData(List<String> uniqueMatchedRefIds, String registrationId,
			DedupeSourceName sourceName) {
		boolean isTransactionSuccessful = false;
		LogDescription description=new LogDescription();
		
		try {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
					registrationId, "PacketInfoManagerImpl::saveManualAdjudicationData()::entry");
			for (String matchedRefId : uniqueMatchedRefIds) {
				ManualVerificationEntity manualVerificationEntity = new ManualVerificationEntity();
				ManualVerificationPKEntity manualVerificationPKEntity = new ManualVerificationPKEntity();
				manualVerificationPKEntity.setMatchedRefId(matchedRefId);
				manualVerificationPKEntity.setMatchedRefType(MATCHED_REFERENCE_TYPE);
				manualVerificationPKEntity.setRegId(registrationId);

				manualVerificationEntity.setId(manualVerificationPKEntity);
				manualVerificationEntity.setLangCode("eng");
				manualVerificationEntity.setMatchedScore(null);
				manualVerificationEntity.setMvUsrId(null);
				manualVerificationEntity.setReasonCode("Potential Match");
				if (sourceName.equals(DedupeSourceName.DEMO)) {
					manualVerificationEntity.setStatusCode(manualVerificationStatus);

				} else {
					manualVerificationEntity.setStatusCode("PENDING");

				}
				manualVerificationEntity.setStatusComment("Assigned to manual Adjudication");
				manualVerificationEntity.setIsActive(true);
				manualVerificationEntity.setIsDeleted(false);
				manualVerificationEntity.setCrBy("SYSTEM");
				manualVerificationEntity.setTrnTypCode(sourceName.toString());
				manualVerficationRepository.save(manualVerificationEntity);
				isTransactionSuccessful = true;
				description.setMessage("Manual Adjudication data saved successfully");
			}

		} catch (DataAccessLayerException e) {
			description.setMessage("DataAccessLayerException while saving Manual Adjudication data for rid" + registrationId
					+ "::" + e.getMessage());

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));

			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + registrationId, e);
		} finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString(), ApiName.AUDIT);

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				registrationId, "PacketInfoManagerImpl::saveManualAdjudicationData()::exit");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getReferenceIdByRid(java.lang.String)
	 */
	@Override
	public List<String> getReferenceIdByRid(String rid) {
		return regAbisRefRepository.getReferenceIdByRid(rid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getRidByReferenceId(java.lang.String)
	 */
	@Override
	public List<String> getRidByReferenceId(String refId) {
		return regAbisRefRepository.getRidByReferenceId(refId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * saveAbisRef(io.mosip.registration.processor.core.packet.dto.RegAbisRefDto)
	 */
	@Override
	public void saveAbisRef(RegAbisRefDto regAbisRefDto) {
		boolean isTransactionSuccessful = false;
		LogDescription description=new LogDescription();
		String regisId = "";
		try {

			if (regAbisRefDto != null) {
				regisId = regAbisRefDto.getReg_id();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
						regisId, "PacketInfoManagerImpl::saveAbisRef()::entry");
				RegAbisRefEntity regAbisRefEntity = PacketInfoMapper.convertRegAbisRefToEntity(regAbisRefDto);
				regAbisRefRepository.save(regAbisRefEntity);
				isTransactionSuccessful = true;
				description.setMessage("ABIS data saved successfully");
			}
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regisId,
					"PacketInfoManagerImpl::saveAbisRef()::exit");
		} catch (DataAccessLayerException e) {
			description.setMessage("DataAccessLayerException while saving the ABIS data" + "::" + e.getMessage());

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + regisId, e);
		} finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString(), ApiName.AUDIT);

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getInsertOrIdentifyRequest(java.lang.String, java.lang.String)
	 */
	@Override
	public List<AbisRequestDto> getInsertOrIdentifyRequest(String bioRefId, String refRegtrnId) {
		List<AbisRequestEntity> abisRequestList = packetInfoDao.getInsertOrIdentifyRequest(bioRefId, refRegtrnId);
		return PacketInfoMapper.convertAbisRequestEntityListToDto(abisRequestList);
	}

	@Override
	public List<String> getReferenceIdByBatchId(String batchId) {
		return packetInfoDao.getReferenceIdByBatchId(batchId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getAbisTransactionIdByRequestId(java.lang.String)
	 */
	@Override
	public List<String> getAbisTransactionIdByRequestId(String requestId) {
		return packetInfoDao.getAbisTransactionIdByRequestId(requestId);
	}

	@Override
	public List<AbisRequestDto> getIdentifyReqListByTransactionId(String transactionId, String requestType) {
		List<AbisRequestEntity> abisRequestList = packetInfoDao.getIdentifyReqListByTransactionId(transactionId,
				requestType);
		return PacketInfoMapper.convertAbisRequestEntityListToDto(abisRequestList);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getBatchStatusbyBatchId(java.lang.String)
	 */
	@Override
	public List<String> getBatchStatusbyBatchId(String batchId) {
		return packetInfoDao.getBatchStatusbyBatchId(batchId);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getAbisRequestByRequestId(java.lang.String)
	 */
	@Override
	public AbisRequestDto getAbisRequestByRequestId(String abisRequestId) {
		List<AbisRequestEntity> abisRequestList = packetInfoDao.getAbisRequestByRequestId(abisRequestId);
		List<AbisRequestDto> abisRequestDtoList = PacketInfoMapper.convertAbisRequestEntityListToDto(abisRequestList);
		if (!abisRequestDtoList.isEmpty()) {
			return PacketInfoMapper.convertAbisRequestEntityListToDto(abisRequestList).get(0);
		} else {
			return null;

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getBatchIdByRequestId(java.lang.String)
	 */
	@Override
	public String getBatchIdByRequestId(String abisRequestId) {
		return packetInfoDao.getBatchIdByRequestId(abisRequestId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getInsertOrIdentifyRequest(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<AbisRequestDto> getInsertOrIdentifyRequest(String bioRefId, String refRegtrnId, String requestType) {
		List<AbisRequestEntity> abisRequestEntities = packetInfoDao.getInsertOrIdentifyRequest(bioRefId, refRegtrnId,
				requestType);
		return PacketInfoMapper.convertAbisRequestEntityListToDto(abisRequestEntities);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getIdentifyByTransactionId(java.lang.String, java.lang.String)
	 */
	@Override
	public Boolean getIdentifyByTransactionId(String transactionId, String identify) {
		List<AbisRequestEntity> abisRequestList = packetInfoDao.getIdentifyByTransactionId(transactionId, identify);
		return abisRequestList.isEmpty() ? Boolean.FALSE : Boolean.TRUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getBioRefIdByRegId(java.lang.String)
	 */
	@Override
	public List<RegBioRefDto> getBioRefIdByRegId(String regId) {
		List<RegBioRefEntity> regBioRefEntityList = packetInfoDao.getBioRefIdByRegId(regId);
		return PacketInfoMapper.convertRegBioRefEntityListToDto(regBioRefEntityList);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getAllAbisDetails()
	 */
	@Override
	public List<AbisApplicationDto> getAllAbisDetails() {
		List<AbisApplicationEntity> abisApplicationEntityList = regAbisApplicationRepository
				.findAll(AbisApplicationEntity.class);
		return PacketInfoMapper.convertAbisApplicationEntityListToDto(abisApplicationEntityList);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * saveBioRef(io.mosip.registration.processor.core.packet.dto.abis.RegBioRefDto)
	 */
	@Override
	public void saveBioRef(RegBioRefDto regBioRefDto) {
		LogDescription description=new LogDescription();
		try {
			RegBioRefEntity regBioRefEntity = PacketInfoMapper.convertBioRefDtoToEntity(regBioRefDto);
			regBioRefRepository.save(regBioRefEntity);
		} catch (DataAccessLayerException e) {
			description.setMessage("DataAccessLayerException while saving ABIS data" + "::" + e.getMessage());

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + regBioRefDto.getRegId(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * saveAbisRequest(io.mosip.registration.processor.core.packet.dto.abis.
	 * AbisRequestDto)
	 */
	@Override
	public void saveAbisRequest(AbisRequestDto abisRequestDto) {
		LogDescription description=new LogDescription();
		
		try {
			AbisRequestEntity abisRequestEntity = PacketInfoMapper.convertAbisRequestDtoToEntity(abisRequestDto);
			regAbisRequestRepository.save(abisRequestEntity);
		} catch (DataAccessLayerException e) {
			description.setMessage("DataAccessLayerException while saving ABIS data" + "::" + e.getMessage());

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + abisRequestDto.getId(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getDemoListByTransactionId(java.lang.String)
	 */
	@Override
	public List<RegDemoDedupeListDto> getDemoListByTransactionId(String transactionId) {
		List<RegDemoDedupeListEntity> regDemoDedupeListEntityList = packetInfoDao
				.getDemoListByTransactionId(transactionId);
		return PacketInfoMapper.convertDemoDedupeEntityListToDto(regDemoDedupeListEntityList);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * saveDemoDedupePotentialData(io.mosip.registration.processor.core.packet.dto.
	 * abis.RegDemoDedupeListDto)
	 */
	@Override
	public void saveDemoDedupePotentialData(RegDemoDedupeListDto regDemoDedupeListDto) {
		boolean isTransactionSuccessful = false;
		LogDescription description=new LogDescription();
		
		String regId="";
		try {

			if (regDemoDedupeListDto != null) {
				regId = regDemoDedupeListDto.getRegId();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
						regId, "PacketInfoManagerImpl::saveDemoDedupePotentialData()::entry");

				RegDemoDedupeListEntity regDemoDedupeListEntity = PacketInfoMapper
						.convertDemoDedupeEntityToDto(regDemoDedupeListDto);
				regDemoDedupeListRepository.save(regDemoDedupeListEntity);
				isTransactionSuccessful = true;
				description.setMessage("Demo dedupe potential match data saved successfully");

			}
		} catch (DataAccessLayerException e) {
			description.setMessage("DataAccessLayerException while saving Demo dedupe potential match data" + "::"
					+ e.getMessage());

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + regId, e);
		} finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString(), ApiName.AUDIT);

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regId,
				"PacketInfoManagerImpl::saveDemoDedupePotentialData()::exit");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getAbisResponseRecords(java.lang.String, java.lang.String)
	 */
	@Override
	public List<AbisResponseDto> getAbisResponseRecords(String latestTransactionId, String requestType) {
		return packetInfoDao.getAbisResponseRecords(latestTransactionId, requestType);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getAbisResponseRecords(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<AbisResponseDto> getAbisResponseRecords(String abisRefId, String latestTransactionId,
			String requestType) {
		return packetInfoDao.getAbisResponseRecords(abisRefId, latestTransactionId, requestType);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getAbisResponseDetRecords(io.mosip.registration.processor.core.packet.dto.
	 * abis.AbisResponseDto)
	 */
	@Override
	public List<AbisResponseDetDto> getAbisResponseDetRecords(AbisResponseDto abisResponseDto) {
		return packetInfoDao.getAbisResponseDetailedRecords(abisResponseDto);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getAbisResponseIDs(java.lang.String)
	 */
	@Override
	public List<AbisResponseDto> getAbisResponseIDs(String abisRequestId) {
		return PacketInfoMapper.convertAbisResponseEntityListToDto(packetInfoDao.getAbisResponseIDs(abisRequestId));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getAbisResponseDetails(java.lang.String)
	 */
	@Override
	public List<AbisResponseDetDto> getAbisResponseDetails(String abisResponseId) {
		return PacketInfoMapper
				.convertAbisResponseDetEntityListToDto(packetInfoDao.getAbisResponseDetails(abisResponseId));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getAbisRequestsByBioRefId(java.lang.String)
	 */
	@Override
	public List<AbisRequestDto> getAbisRequestsByBioRefId(String bioRefId) {
		List<AbisRequestEntity> abisRequestEntityList = packetInfoDao.getAbisRequestsByBioRefId(bioRefId);
		return PacketInfoMapper.convertAbisRequestEntityListToDto(abisRequestEntityList);
	}

	@Override
	public List<String> getAbisProcessedRequestsAppCodeByBioRefId(String bioRefId, String requestType,
			String processed) {
		return packetInfoDao.getAbisProcessedRequestsAppCodeByBioRefId(bioRefId, requestType, processed);
	}

	@Override
	public List<AbisResponseDetDto> getAbisResponseDetRecordsList(List<String> abisResponseDto) {
		return packetInfoDao.getAbisResponseDetRecordsList(abisResponseDto);
	}

	@Override
	public void saveRegLostUinDet(String regId, String latestRegId) {
		boolean isTransactionSuccessful = false;
		LogDescription description=new LogDescription();
		
		try {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regId,
					"PacketInfoManagerImpl::saveRegLostUinDetData()::entry");
			RegLostUinDetEntity regLostUinDetEntity = new RegLostUinDetEntity();
			RegLostUinDetPKEntity regLostUinDetPKEntity = new RegLostUinDetPKEntity();
			regLostUinDetPKEntity.setRegId(regId);

			regLostUinDetEntity.setId(regLostUinDetPKEntity);
			regLostUinDetEntity.setLatestRegId(latestRegId);
			regLostUinDetEntity.setCrBy("SYSTEM");
			regLostUinDetEntity.setIsDeleted(false);

			regLostUinDetRepository.save(regLostUinDetEntity);
			isTransactionSuccessful = true;
			description.setMessage("Lost Uin detail data saved successfully");
		} catch (DataAccessLayerException e) {
			description.setMessage("DataAccessLayerException while saving Lost Uin detail data for rid" + regId + "::"
					+ e.getMessage());

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", e.getMessage() + ExceptionUtils.getStackTrace(e));

			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + regId, e);
		} finally {

			String eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			String eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			String eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString(), ApiName.AUDIT);

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), regId,
				"PacketInfoManagerImpl::saveRegLostUinDetData()::exit");

	}

}
