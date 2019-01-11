package io.mosip.registration.processor.packet.storage.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.packet.dto.Applicant;
import io.mosip.registration.processor.core.packet.dto.ApplicantDocument;
import io.mosip.registration.processor.core.packet.dto.Biometric;
import io.mosip.registration.processor.core.packet.dto.BiometricDetails;
import io.mosip.registration.processor.core.packet.dto.BiometricExceptionDto;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.Introducer;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoJson;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicInfoJsonEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationPKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegAbisRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.exception.FieldNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.FileNotFoundInPacketStore;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.InstantanceCreationException;
import io.mosip.registration.processor.packet.storage.exception.MappingJsonException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;
import io.mosip.registration.processor.packet.storage.exception.StreamToBytesConversionException;
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

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoManagerImpl.class);

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant LOG_FORMATTER. */
	public static final String LOG_FORMATTER = "{} - {}";

	/** The Constant DEMOGRAPHIC_APPLICANT. */
	public static final String DEMOGRAPHIC_APPLICANT = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

	/** The Constant TABLE_NOT_ACCESSIBLE. */
	private static final String TABLE_NOT_ACCESSIBLE = "TABLE IS NOT ACCESSIBLE.";

	/** The applicant document repository. */
	@Autowired
	private BasePacketRepository<ApplicantDocumentEntity, String> applicantDocumentRepository;

	/** The biometric exception repository. */
	@Autowired
	private BasePacketRepository<BiometricExceptionEntity, String> biometricExceptionRepository;

	/** The applicant fingerprint repository. */
	@Autowired
	private BasePacketRepository<ApplicantFingerprintEntity, String> applicantFingerprintRepository;

	/** The applicant iris repository. */
	@Autowired
	private BasePacketRepository<ApplicantIrisEntity, String> applicantIrisRepository;

	/** The applicant photograph repository. */
	@Autowired
	private BasePacketRepository<ApplicantPhotographEntity, String> applicantPhotographRepository;

	/** The reg osi repository. */
	@Autowired
	private BasePacketRepository<RegOsiEntity, String> regOsiRepository;

	/** The Reg abis ref repository. */
	@Autowired
	private BasePacketRepository<RegAbisRefEntity, String> RegAbisRefRepository;

	/** The applicant demographic repository. */
	@Autowired
	private BasePacketRepository<ApplicantDemographicInfoJsonEntity, String> demographicJsonRepository;

	/** The demographic dedupe repository. */
	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The reg center machine repository. */
	@Autowired
	private BasePacketRepository<RegCenterMachineEntity, String> regCenterMachineRepository;

	/** The manual verfication repository. */
	@Autowired
	private BasePacketRepository<ManualVerificationEntity, String> manualVerficationRepository;

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	/** The description. */
	String description = "";

	/** The core audit request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The packet info dao. */
	@Autowired
	private PacketInfoDao packetInfoDao;

	/** The filesystem ceph adapter impl. */
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> filesystemCephAdapterImpl;

	/** The utility. */
	@Autowired
	private Utilities utility;

	/** The reg processor identity json. */
	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The meta data. */
	private List<FieldValue> metaData;

	/** The reg id. */
	private String regId;

	/** The pre reg id. */
	private String preRegId;

	/** The demographic identity. */
	private JSONObject demographicIdentity = null;

	/** The Constant LANGUAGE. */
	private static final String LANGUAGE = "language";

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	/** The Constant MATCHED_REFERENCE_TYPE. */
	private static final String MATCHED_REFERENCE_TYPE = "uin";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager
	 * #savePacketData(java.lang.Object)
	 */
	@Override
	public void savePacketData(Identity identity) {

		boolean isTransactionSuccessful = false;

		Biometric biometric = identity.getBiometric();

		List<FieldValue> osiData = identity.getOsiData();
		List<BiometricExceptionDto> exceptionBiometrics = identity.getExceptionBiometrics();
		Photograph applicantPhotographData = identity.getApplicantPhotograph();
		Photograph exceptionPhotographData = identity.getExceptionPhotograph();
		metaData = identity.getMetaData();

		try {

			saveApplicantBioMetricDatas(biometric.getApplicant());
			saveExceptionBiometricDatas(exceptionBiometrics);
			savePhotoGraph(applicantPhotographData, exceptionPhotographData);

			saveOsiData(osiData, biometric.getIntroducer());
			saveRegCenterData(metaData);
			isTransactionSuccessful = true;

		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(TABLE_NOT_ACCESSIBLE, e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful ? "Packet meta data saved successfully"
					: "Packet meta data unsuccessful";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());
		}

	}

	/**
	 * Save exception biometric datas.
	 *
	 * @param exceptionBiometrics
	 *            the exception biometrics
	 */
	private void saveExceptionBiometricDatas(List<BiometricExceptionDto> exceptionBiometrics) {
		for (BiometricExceptionDto exp : exceptionBiometrics) {
			BiometricExceptionEntity biometricExceptionEntity = PacketInfoMapper
					.convertBiometricExceptioDtoToEntity(exp, metaData);
			biometricExceptionRepository.save(biometricExceptionEntity);
			LOGGER.info(LOG_FORMATTER, biometricExceptionEntity.getId().getRegId(), " Biometric Exception DATA SAVED");
		}

	}

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

		List<ApplicantInfoDto> applicantInfoDtoList = null;
		try {
			applicantInfoDtoList = packetInfoDao.getPacketsforQCUser(qcUserId);
			isTransactionSuccessful = true;
			return applicantInfoDtoList;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(
					PlatformErrorMessages.RPR_PIS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful ? "QcUser packet Info fetch Success"
					: "QcUser packet Info fetch Unsuccessful";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());
		}
	}

	/**
	 * Save bio metric data.
	 *
	 * @param applicant
	 *            the applicant
	 */
	private void saveApplicantBioMetricDatas(Applicant applicant) {
		saveIris(applicant.getLeftEye());
		saveIris(applicant.getRightEye());
		saveFingerPrint(applicant.getLeftSlap());
		saveFingerPrint(applicant.getRightSlap());
		saveFingerPrint(applicant.getThumbs());

	}

	/**
	 * Save iris.
	 *
	 * @param irisData
	 *            the iris data
	 */
	private void saveIris(BiometricDetails irisData) {
		if (irisData != null) {
			ApplicantIrisEntity applicantIrisEntity = PacketInfoMapper.convertIrisDtoToEntity(irisData, metaData);
			applicantIrisRepository.save(applicantIrisEntity);
			LOGGER.info(LOG_FORMATTER, applicantIrisEntity.getId().getRegId(), " Applicant Iris DATA SAVED");

		}
	}

	/**
	 * Save finger print.
	 *
	 * @param fingerprintData
	 *            the fingerprint data
	 */
	private void saveFingerPrint(BiometricDetails fingerprintData) {
		if (fingerprintData != null) {
			ApplicantFingerprintEntity fingerprintEntity = PacketInfoMapper
					.convertFingerprintDtoToEntity(fingerprintData, metaData);
			applicantFingerprintRepository.save(fingerprintEntity);
			LOGGER.info(LOG_FORMATTER, fingerprintEntity.getId().getRegId(), " Fingerprint DATA SAVED");

		}
	}

	/**
	 * Save documents.
	 *
	 * @param documentDtos
	 *            the document dto
	 */
	public void saveDocuments(List<Document> documentDtos) {

		for (Document document : documentDtos) {
			saveDocument(document);
		}

	}

	/**
	 * Save document data.
	 *
	 * @param documentDetail
	 *            the document detail
	 */
	public void saveDocument(Document documentDetail) {
		ApplicantDocumentEntity applicantDocumentEntity = PacketInfoMapper.convertAppDocDtoToEntity(documentDetail,
				metaData);

		String fileName;

		fileName = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + documentDetail.getDocumentName().toUpperCase();

		Optional<FieldValue> filterRegId = metaData.stream().filter(m -> "registrationId".equals(m.getLabel()))
				.findFirst();

		String registrationId = "";
		if (filterRegId.isPresent())
			registrationId = filterRegId.get().getValue();
		applicantDocumentEntity.setDocStore(getDocumentAsByteArray(registrationId, fileName));
		applicantDocumentRepository.save(applicantDocumentEntity);
		LOGGER.info(LOG_FORMATTER, applicantDocumentEntity.getId().getRegId(), "  Document Demographic DATA SAVED");
	}

	/**
	 * Save osi data.
	 *
	 * @param osiData
	 *            the osi data
	 * @param introducer
	 *            the introducer
	 */
	private void saveOsiData(List<FieldValue> osiData, Introducer introducer) {
		if (osiData != null) {
			RegOsiEntity regOsiEntity = PacketInfoMapper.convertOsiDataToEntity(osiData, introducer, metaData);
			regOsiRepository.save(regOsiEntity);
			LOGGER.info(LOG_FORMATTER, regOsiEntity.getId(), "  Applicant OSI DATA SAVED");
		}
	}

	/**
	 * Save photo graph.
	 *
	 * @param photoGraphData
	 *            the photo graph data
	 * @param exceptionPhotographData
	 *            the exception photograph data
	 */
	private void savePhotoGraph(Photograph photoGraphData, Photograph exceptionPhotographData) {
		ApplicantPhotographEntity applicantPhotographEntity = PacketInfoMapper
				.convertPhotoGraphDtoToEntity(photoGraphData, exceptionPhotographData, metaData);
		applicantPhotographRepository.save(applicantPhotographEntity);
		LOGGER.info(LOG_FORMATTER, applicantPhotographEntity.getId().getRegId(), " Applicant Photograph DATA SAVED");
	}

	/**
	 * Save reg center data.
	 *
	 * @param metaData
	 *            the meta data
	 */
	private void saveRegCenterData(List<FieldValue> metaData) {
		RegCenterMachineEntity regCenterMachineEntity = PacketInfoMapper.convertRegCenterMachineToEntity(metaData);
		regCenterMachineRepository.save(regCenterMachineEntity);
		LOGGER.info(LOG_FORMATTER, regCenterMachineEntity.getId() + " --> Registration Center Machine DATA SAVED");

	}

	/**
	 * Gets the document as byte array.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param documentName
	 *            the document name
	 * @return the document as byte array
	 */
	private byte[] getDocumentAsByteArray(String registrationId, String documentName) {
		try {
			LOGGER.info("{}{} - {}{} ", "Packet-Name : ", registrationId, " FilePath: ", documentName);
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
			LOGGER.error(LOG_FORMATTER, "Error While reading  inputstream file", e);
			return new byte[1];
		}

	}

	/**
	 * Map json node to java object.
	 *
	 * @param <T>
	 *            the generic type
	 * @param genericType
	 *            the generic type
	 * @param demographicJsonNode
	 *            the demographic json node
	 * @return the t[]
	 */
	@SuppressWarnings("unchecked")
	private <T> T[] mapJsonNodeToJavaObject(Class<? extends Object> genericType, JSONArray demographicJsonNode) {
		String language;
		String value;
		T[] javaObject = (T[]) Array.newInstance(genericType, demographicJsonNode.size());
		try {
			for (int i = 0; i < demographicJsonNode.size(); i++) {

				T jsonNodeElement = (T) genericType.newInstance();

				JSONObject objects = (JSONObject) demographicJsonNode.get(i);
				language = (String) objects.get(LANGUAGE);
				value = (String) objects.get(VALUE);

				Field languageField = jsonNodeElement.getClass().getDeclaredField(LANGUAGE);
				languageField.setAccessible(true);
				languageField.set(jsonNodeElement, language);

				Field valueField = jsonNodeElement.getClass().getDeclaredField(VALUE);
				valueField.setAccessible(true);
				valueField.set(jsonNodeElement, value);

				javaObject[i] = jsonNodeElement;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.error("Error while Creating Instance of generic type", e);
			throw new InstantanceCreationException(PlatformErrorMessages.RPR_SYS_INSTANTIATION_EXCEPTION.getMessage(),
					e);

		} catch (NoSuchFieldException | SecurityException e) {
			LOGGER.error("no such field exception", e);
			throw new FieldNotFoundException(PlatformErrorMessages.RPR_SYS_NO_SUCH_FIELD_EXCEPTION.getMessage(), e);

		}

		return javaObject;

	}

	/**
	 * Gets the json values.
	 *
	 * @param identityKey
	 *            the identity key
	 * @return the json values
	 */
	private JsonValue[] getJsonValues(Object identityKey) {
		JSONArray demographicJsonNode = null;

		if (demographicIdentity != null)
			demographicJsonNode = (JSONArray) demographicIdentity.get(identityKey);
		return (demographicJsonNode != null)
				? (JsonValue[]) mapJsonNodeToJavaObject(JsonValue.class, demographicJsonNode)
				: null;

	}

	/**
	 * Gets the identity keys and fetch values from JSON.
	 *
	 * @param demographicJsonString
	 *            the demographic json string
	 * @return the identity keys and fetch values from JSON
	 */
	public IndividualDemographicDedupe getIdentityKeysAndFetchValuesFromJSON(String demographicJsonString) {
		IndividualDemographicDedupe demographicData = new IndividualDemographicDedupe();
		try {
			// Get Identity Json from config server and map keys to Java Object
			String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
					utility.getGetRegProcessorIdentityJson());
			ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
			regProcessorIdentityJson = mapIdentityJsonStringToObject.readValue(getIdentityJsonString,
					RegistrationProcessorIdentity.class);
			JSONParser parser = new JSONParser();
			JSONObject demographicJson = (JSONObject) parser.parse(demographicJsonString);
			demographicIdentity = (JSONObject) demographicJson.get(utility.getGetRegProcessorDemographicIdentity());
			if (demographicIdentity == null)
				throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

			demographicData.setName(getJsonValues(regProcessorIdentityJson.getIdentity().getName().getValue()));
			demographicData.setDateOfBirth(
					(String) (demographicIdentity.get(regProcessorIdentityJson.getIdentity().getDob().getValue())));
			demographicData.setGender(getJsonValues(regProcessorIdentityJson.getIdentity().getGender().getValue()));
		} catch (IOException e) {
			LOGGER.error("Error while mapping Identity Json  ", e);
			throw new MappingJsonException(PlatformErrorMessages.RPR_SYS_IDENTITY_JSON_MAPPING_EXCEPTION.getMessage(),
					e);

		} catch (ParseException e) {
			LOGGER.error("Error while parsing Json file", e);
			throw new ParsingException(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getMessage(), e);
		}
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
			if ("registrationId".equals(metaData.get(i).getLabel())) {
				regId = metaData.get(i).getValue();

			}
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
	 */
	private void saveIndividualDemographicDedupe(byte[] demographicJsonBytes) {

		String getJsonStringFromBytes = new String(demographicJsonBytes);
		IndividualDemographicDedupe demographicData = getIdentityKeysAndFetchValuesFromJSON(getJsonStringFromBytes);
		boolean isTransactionSuccessful = false;
		try {
			List<IndividualDemographicDedupeEntity> applicantDemographicEntities = PacketInfoMapper
					.converDemographicDedupeDtoToEntity(demographicData, regId);
			for (IndividualDemographicDedupeEntity applicantDemographicEntity : applicantDemographicEntities) {
				demographicDedupeRepository.save(applicantDemographicEntity);
				LOGGER.info(applicantDemographicEntity.getId().getRegId() + " --> DemographicDedupeData SAVED");
			}
			isTransactionSuccessful = true;
		} catch (DataAccessLayerException e) {
			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + regId, e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful ? "Demographic Dedupe data saved successfully"
					: "Demographic Dedupe data Failed to save";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());

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
	public void saveDemographicInfoJson(InputStream demographicJsonStream, List<FieldValue> metaData) {
		DemographicInfoJson demoJson = new DemographicInfoJson();
		getRegistrationId(metaData);
		boolean isTransactionSuccessful = false;
		if (demographicJsonStream == null)
			throw new FileNotFoundInPacketStore(PlatformErrorMessages.RPR_PIS_FILE_NOT_FOUND_IN_DFS.getMessage());

		try {
			byte[] bytes = IOUtils.toByteArray(demographicJsonStream);
			demoJson.setDemographicDetails(bytes);
			demoJson.setLangCode("eng");
			demoJson.setPreRegId(preRegId);
			demoJson.setRegId(regId);
			demoJson.setStatusCode("DemographicJson saved");
			ApplicantDemographicInfoJsonEntity entity = PacketInfoMapper.convertDemographicInfoJsonToEntity(demoJson);
			demographicJsonRepository.save(entity);

			saveIndividualDemographicDedupe(bytes);

			isTransactionSuccessful = true;
		} catch (IOException e) {
			LOGGER.error("Unable to convert InputStream to bytes", e);
			throw new StreamToBytesConversionException(
					PlatformErrorMessages.RPR_SYS_UNABLE_TO_CONVERT_STREAM_TO_BYTES.getMessage(), e);
		} catch (DataAccessLayerException e) {
			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + regId, e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful ? "Demographic Dedupe data saved successfully"
					: "Demographic Dedupe data Failed to save";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());

		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getOsi(java.lang.String)
	 */
	@Override
	public RegOsiDto getOsi(String regid) {
		return packetInfoDao.getEntitiesforRegOsi(regid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getRegistrationCenterMachine(java.lang.String)
	 */
	@Override
	public RegistrationCenterMachineDto getRegistrationCenterMachine(String regid) {

		return packetInfoDao.getRegistrationCenterMachine(regid);
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
	 * getApplicantFingerPrintImageNameById(java.lang.String)
	 */
	@Override
	public List<String> getApplicantFingerPrintImageNameById(String regId) {
		return packetInfoDao.getApplicantFingerPrintImageNameById(regId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getApplicantIrisImageNameById(java.lang.String)
	 */
	@Override
	public List<String> getApplicantIrisImageNameById(String regId) {
		return packetInfoDao.getApplicantIrisImageNameById(regId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * getRegIdByUIN(java.lang.String)
	 */
	@Override
	public List<String> getRegIdByUIN(String uin) {
		return packetInfoDao.getRegIdByUIN(uin);
	}

	@Override
	public List<ApplicantDocument> getDocumentsByRegId(String regId) {
		return packetInfoDao.getDocumentsByRegId(regId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager#
	 * saveManualAdjudicationData(java.util.Set, java.lang.String)
	 */
	@Override
	public void saveManualAdjudicationData(List<String> uniqueMatchedRefIds, String registrationId) {
		boolean isTransactionSuccessful = false;

		try {
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
				manualVerificationEntity.setStatusCode("PENDING");
				manualVerificationEntity.setStatusComment("Assigned to manual Adjudication");
				manualVerificationEntity.setIsActive(true);
				manualVerificationEntity.setIsDeleted(false);
				manualVerificationEntity.setCrBy("SYSTEM");

				manualVerficationRepository.save(manualVerificationEntity);
				isTransactionSuccessful = true;

			}

		} catch (DataAccessLayerException e) {
			throw new UnableToInsertData(PlatformErrorMessages.RPR_PIS_UNABLE_TO_INSERT_DATA.getMessage() + regId, e);
		} finally {

			eventId = isTransactionSuccessful ? EventId.RPR_407.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventName.ADD.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_407.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();
			description = isTransactionSuccessful ? "Manual Adjudication data saved successfully"
					: "Manual Adjudication data Failed to save";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					AuditLogConstant.NO_ID.toString());

		}
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
		return RegAbisRefRepository.getReferenceIdByRid(rid);
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
		return RegAbisRefRepository.getRidByReferenceId(refId);
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
		if (regAbisRefDto != null) {
			RegAbisRefEntity regAbisRefEntity = PacketInfoMapper.convertRegAbisRefToEntity(regAbisRefDto);
			RegAbisRefRepository.save(regAbisRefEntity);
			LOGGER.info(LOG_FORMATTER, regAbisRefEntity.getId(), "Registration ABIS Reference Date saved");
		}
	}

}
