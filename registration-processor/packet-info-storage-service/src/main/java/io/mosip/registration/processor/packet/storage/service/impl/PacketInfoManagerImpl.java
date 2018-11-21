package io.mosip.registration.processor.packet.storage.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
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

import io.mosip.registration.processor.core.code.AuditLogConstant;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.packet.dto.Applicant;
import io.mosip.registration.processor.core.packet.dto.Biometric;
import io.mosip.registration.processor.core.packet.dto.BiometricDetails;
import io.mosip.registration.processor.core.packet.dto.BiometricException;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.Introducer;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoJson;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
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
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.exception.FileNotFoundInPacketStore;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.MappingJsonException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;
import io.mosip.registration.processor.packet.storage.exception.RPR_PLATFORM_ERROR_MESSAGES;
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

	public static final String FILE_SEPARATOR = "\\";

	public static final String LOG_FORMATTER = "{} - {}";

	public static final String DEMOGRAPHIC_APPLICANT = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR
			+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR;

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

	/** The applicant demographic repository. */
	@Autowired
	private BasePacketRepository<ApplicantDemographicInfoJsonEntity, String> demographicJsonRepository;

	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The reg center machine repository. */
	@Autowired
	private BasePacketRepository<RegCenterMachineEntity, String> regCenterMachineRepository;

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

	@Autowired
	private PacketInfoDao packetInfoDao;

	@Autowired
	FilesystemCephAdapterImpl filesystemCephAdapterImpl;

	@Autowired
	private Utilities utility;

	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The meta data. */
	private List<FieldValue> metaData;
	private String regId;
	private String preRegId;

	private JSONObject demographicIdentity = null;
	private static final String LANGUAGE = "language";
	private static final String LABEL = "label";
	private static final String VALUE = "value";

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
		List<Document> documentDtos = identity.getDocuments();
		List<FieldValue> osiData = identity.getOsiData();
		List<BiometricException> exceptionBiometrics = identity.getExceptionBiometrics();
		Photograph applicantPhotographData = identity.getApplicantPhotograph();
		Photograph exceptionPhotographData = identity.getExceptionPhotograph();
		metaData = identity.getMetaData();

		try {
			saveDocuments(documentDtos);
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

	private void saveExceptionBiometricDatas(List<BiometricException> exceptionBiometrics) {
		for (BiometricException exp : exceptionBiometrics) {
			BiometricExceptionEntity biometricExceptionEntity = PacketInfoMapper
					.convertBiometricExceptioDtoToEntity(exp, metaData);
			biometricExceptionRepository.save(biometricExceptionEntity);
			LOGGER.info(LOG_FORMATTER, biometricExceptionEntity.getId().getRegId(), " Biometric Exception DATA SAVED");
		}

	}

	@Override
	public List<ApplicantInfoDto> getPacketsforQCUser(String qcUserId) {

		boolean isTransactionSuccessful = false;

		List<ApplicantInfoDto> applicantInfoDtoList = null;
		try {
			applicantInfoDtoList = packetInfoDao.getPacketsforQCUser(qcUserId);
			isTransactionSuccessful = true;
			return applicantInfoDtoList;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(PlatformErrorMessages.RPR_PIS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
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
	 * @param bioMetricData
	 *            the bio metric data
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
	private void saveDocuments(List<Document> documentDtos) {

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

		String fileName = "";
		if (PacketFiles.APPLICANTPHOTO.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.APPLICANTPHOTO.name();
		} else if (PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.REGISTRATIONACKNOWLEDGEMENT.name();
		} else if (PacketFiles.DEMOGRAPHICINFO.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name();
		} else if (PacketFiles.PROOFOFADDRESS.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.PROOFOFADDRESS.name();
		} else if (PacketFiles.EXCEPTIONPHOTO.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.EXCEPTIONPHOTO.name();
		} else if (PacketFiles.PROOFOFIDENTITY.name().equalsIgnoreCase(documentDetail.getDocumentName())) {
			fileName = DEMOGRAPHIC_APPLICANT + PacketFiles.PROOFOFIDENTITY.name();
		}
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
			LOGGER.info(LOG_FORMATTER,"Packet-Name : "+registrationId+" FilePath "+documentName );
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

	@SuppressWarnings("unchecked")
	private <T> T[] mapJsonNodeToJavaObject(Class<? extends Object> genericType, JSONArray demographicJsonNode) {
		String language;
		String label;
		String value;
		T[] javaObject = (T[]) Array.newInstance(genericType, demographicJsonNode.size());
		try {
			for (int i = 0; i < demographicJsonNode.size(); i++) {

				T jsonNodeElement = (T) genericType.newInstance();

				JSONObject objects = (JSONObject) demographicJsonNode.get(i);
				language = (String) objects.get(LANGUAGE);
				label = (String) objects.get(LABEL);
				value = (String) objects.get(VALUE);

				Field labelField = jsonNodeElement.getClass().getDeclaredField(LABEL);
				labelField.setAccessible(true);
				labelField.set(jsonNodeElement, label);

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
			throw new MappingJsonException(RPR_PLATFORM_ERROR_MESSAGES.INSTANTIATION_EXCEPTION.getValue(), e);

		} catch (NoSuchFieldException | SecurityException e) {
			LOGGER.error("no such field exception", e);
			throw new MappingJsonException(RPR_PLATFORM_ERROR_MESSAGES.NO_SUCH_FIELD_EXCEPTION.getValue(), e);

		}

		return javaObject;

	}

	private JsonValue[] getJsonValues(Object identityKey) {
		JSONArray demographicJsonNode = null;
		if (demographicIdentity != null)
			demographicJsonNode = (JSONArray) demographicIdentity.get(identityKey);
		return (demographicJsonNode != null)
				? (JsonValue[]) mapJsonNodeToJavaObject(JsonValue.class, demographicJsonNode)
				: null;

	}

	private IndividualDemographicDedupe getIdentityKeysAndFetchValuesFromJSON(String demographicJsonString) {
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
				throw new IdentityNotFoundException(RPR_PLATFORM_ERROR_MESSAGES.IDENTITY_NOT_FOUND.getValue());

			demographicData.setFirstName(getJsonValues(regProcessorIdentityJson.getIdentity().getFirstName()));
			demographicData.setMiddleName(getJsonValues(regProcessorIdentityJson.getIdentity().getMiddleName()));
			demographicData.setLastName(getJsonValues(regProcessorIdentityJson.getIdentity().getLastName()));
			demographicData.setFullName(getJsonValues(regProcessorIdentityJson.getIdentity().getFullName()));
			demographicData.setDateOfBirth(getJsonValues(regProcessorIdentityJson.getIdentity().getDob()));
			demographicData.setGender(getJsonValues(regProcessorIdentityJson.getIdentity().getGender()));
			demographicData.setAddressLine1(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine1()));
			demographicData.setAddressLine2(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine2()));
			demographicData.setAddressLine3(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine3()));
			demographicData.setAddressLine4(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine4()));
			demographicData.setAddressLine5(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine5()));
			demographicData.setAddressLine6(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine6()));
			demographicData.setZipcode(getJsonValues(regProcessorIdentityJson.getIdentity().getPincode()));
		} catch (IOException e) {
			LOGGER.error("Error while mapping Identity Json  ", e);
			throw new MappingJsonException(RPR_PLATFORM_ERROR_MESSAGES.IDENTITY_JSON_MAPPING_EXCEPTION.getValue(), e);

		} catch (ParseException e) {
			LOGGER.error("Error while parsing Json file", e);
			throw new ParsingException(RPR_PLATFORM_ERROR_MESSAGES.JSON_PARSING_EXCEPTION.getValue(), e);
		}
		return demographicData;

	}

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

	private void saveIndividualDemographicDedupe(byte[] demographicJsonBytes) {

		String getJsonStringFromBytes = new String(demographicJsonBytes);
		IndividualDemographicDedupe demographicData = getIdentityKeysAndFetchValuesFromJSON(getJsonStringFromBytes);
		boolean isTransactionSuccessful = false;
		try {

			List<IndividualDemographicDedupeEntity> applicantDemographicEntities = PacketInfoMapper
					.converDemographicDedupeDtoToEntity(demographicData, regId, preRegId);
			for (IndividualDemographicDedupeEntity applicantDemographicEntity : applicantDemographicEntities) {
				demographicDedupeRepository.save(applicantDemographicEntity);
				LOGGER.info(applicantDemographicEntity.getId().getRefId() + " --> DemographicDedupeData SAVED");
			}
			isTransactionSuccessful = true;
		} catch (DataAccessLayerException e) {
			throw new UnableToInsertData(RPR_PLATFORM_ERROR_MESSAGES.UNABLE_TO_INSERT_DATA.getValue() + regId, e);
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

	@Override
	public void saveDemographicInfoJson(InputStream demographicJsonStream, List<FieldValue> metaData) {
		DemographicInfoJson demoJson = new DemographicInfoJson();
		getRegistrationId(metaData);
		boolean isTransactionSuccessful = false;
		if (demographicJsonStream == null)
			throw new FileNotFoundInPacketStore(RPR_PLATFORM_ERROR_MESSAGES.FILE_NOT_FOUND_IN_DFS.getValue());

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
			throw new ParsingException(RPR_PLATFORM_ERROR_MESSAGES.UNABLE_TO_CONVERT_STREAM_TO_BYTES.getValue(), e);
		} catch (DataAccessLayerException e) {
			throw new UnableToInsertData(RPR_PLATFORM_ERROR_MESSAGES.UNABLE_TO_INSERT_DATA.getValue() + regId, e);
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

}
