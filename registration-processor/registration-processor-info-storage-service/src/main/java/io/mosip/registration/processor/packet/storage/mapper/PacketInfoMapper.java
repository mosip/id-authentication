package io.mosip.registration.processor.packet.storage.mapper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.BiometricDetails;
import io.mosip.registration.processor.core.packet.dto.BiometricExceptionDTO;


import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Introducer;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoJson;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.packet.dto.idjson.Document;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicInfoJsonEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicInfoJsonPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDocumentPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantFingerprintPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantIrisPKEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantPhotographPKEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionEntity;
import io.mosip.registration.processor.packet.storage.entity.BiometricExceptionPKEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegAbisRefEntity;
import io.mosip.registration.processor.packet.storage.entity.RegAbisRefPkEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachinePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiPkEntity;
import io.mosip.registration.processor.packet.storage.exception.DateParseException;

/**
 * The Class PacketInfoMapper.
 */
public class PacketInfoMapper {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PacketInfoMapper.class);

	/** The Constant REGISTRATION_ID. */
	private static final String REGISTRATION_ID = "registrationId";

	/** The Constant PRE_REGISTRATION_ID. */
	private static final String PRE_REGISTRATION_ID = "preRegistrationId";

	/** The languages. */
	private static StringBuilder languages = new StringBuilder();

	/**
	 * Instantiates a new packet info mapper.
	 */
	private PacketInfoMapper() {
		super();
	}

	/**
	 * Convert app doc dto to app doc entity.
	 *
	 * @param documentDto
	 *            the document dto
	 * @param metaData
	 *            the meta data
	 * @return the applicant document entity
	 */
	public static ApplicantDocumentEntity convertAppDocDtoToEntity(Document documentDto, List<FieldValue> metaData) {
		
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertAppDocDtoToEntity()::entry");

		Optional<FieldValue> regId = metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId = "";
		if (regId.isPresent())
			registrationId = regId.get().getValue();

		Optional<FieldValue> preregId = metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID))
				.findFirst();
		String preregistrationId = "";
		if (preregId.isPresent())
			preregistrationId = preregId.get().getValue();

		ApplicantDocumentEntity applicantDocumentEntity = new ApplicantDocumentEntity();
		ApplicantDocumentPKEntity applicantDocumentPKEntity = new ApplicantDocumentPKEntity();
		applicantDocumentPKEntity.setDocCatCode(documentDto.getDocumentCategory());
		applicantDocumentPKEntity.setDocTypCode(documentDto.getDocumentType());
		applicantDocumentPKEntity.setRegId(registrationId);

		applicantDocumentEntity.setId(applicantDocumentPKEntity);
		applicantDocumentEntity.setPreRegId(preregistrationId);
		applicantDocumentEntity.setDocName(documentDto.getDocumentName());
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocFileFormat(documentDto.getFormat());
		applicantDocumentEntity.setActive(true);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertAppDocDtoToEntity()::exit");

		return applicantDocumentEntity;
	}

	/**
	 * Convert iris to iris entity.
	 *
	 * @param iris
	 *            the iris
	 * @param metaData
	 *            the meta data
	 * @return the applicant iris entity
	 */
	public static ApplicantIrisEntity convertIrisDtoToEntity(BiometricDetails iris, List<FieldValue> metaData) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertIrisDtoToEntity()::entry");
		Optional<FieldValue> regId = metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId = "";
		if (regId.isPresent())
			registrationId = regId.get().getValue();

		Optional<FieldValue> preregId = metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID))
				.findFirst();
		String preregistrationId = "";
		if (preregId.isPresent())
			preregistrationId = preregId.get().getValue();

		ApplicantIrisEntity applicantIrisEntity = new ApplicantIrisEntity();
		ApplicantIrisPKEntity applicantIrisPKEntity = new ApplicantIrisPKEntity();

		applicantIrisPKEntity.setRegId(registrationId);
		applicantIrisPKEntity.setTyp(iris.getType());

		applicantIrisEntity.setId(applicantIrisPKEntity);
		applicantIrisEntity.setNoOfRetry(iris.getNumRetry());
		applicantIrisEntity.setImageName(iris.getImageName());
		applicantIrisEntity.setPreRegId(preregistrationId);
		applicantIrisEntity.setQualityScore(BigDecimal.valueOf(iris.getQualityScore()));
		applicantIrisEntity.setActive(true);

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertIrisDtoToEntity()::exit");
		return applicantIrisEntity;
	}

	/**
	 * Convert fingerprint to fingerprint entity.
	 *
	 * @param fingerprint
	 *            the fingerprint
	 * @param metaData
	 *            the meta data
	 * @return the applicant fingerprint entity
	 */
	public static ApplicantFingerprintEntity convertFingerprintDtoToEntity(BiometricDetails fingerprint,
			List<FieldValue> metaData) {
		
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertFingerprintDtoToEntity()::entry");
		Optional<FieldValue> regId = metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId = "";
		if (regId.isPresent())
			registrationId = regId.get().getValue();

		Optional<FieldValue> preregId = metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID))
				.findFirst();
		String preregistrationId = "";
		if (preregId.isPresent())
			preregistrationId = preregId.get().getValue();
		ApplicantFingerprintEntity applicantFingerprintEntity = new ApplicantFingerprintEntity();
		ApplicantFingerprintPKEntity applicantFingerprintPKEntity = new ApplicantFingerprintPKEntity();

		applicantFingerprintPKEntity.setRegId(registrationId);
		applicantFingerprintPKEntity.setTyp(fingerprint.getType());

		applicantFingerprintEntity.setId(applicantFingerprintPKEntity);
		applicantFingerprintEntity.setNoOfRetry(fingerprint.getNumRetry());
		applicantFingerprintEntity.setImageName(fingerprint.getImageName());
		applicantFingerprintEntity.setNoOfRetry(fingerprint.getNumRetry());
		applicantFingerprintEntity.setPreRegId(preregistrationId);
		applicantFingerprintEntity.setQualityScore(BigDecimal.valueOf(fingerprint.getQualityScore()));
		applicantFingerprintEntity.setActive(true);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertFingerprintDtoToEntity()::exit");

		return applicantFingerprintEntity;

	}

	/**
	 * Convert biometric exc to biometric exc entity.
	 *
	 * @param exception
	 *            the exception
	 * @param metaData
	 *            the meta data
	 * @return the biometric exception entity
	 */
	public static BiometricExceptionEntity convertBiometricExceptioDtoToEntity(BiometricExceptionDTO exception,
			List<FieldValue> metaData) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertBiometricExceptioDtoToEntity()::entry");
		Optional<FieldValue> regId = metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId = "";
		if (regId.isPresent())
			registrationId = regId.get().getValue();

		Optional<FieldValue> preregId = metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID))
				.findFirst();
		String preregistrationId = "";
		if (preregId.isPresent())
			preregistrationId = preregId.get().getValue();
		BiometricExceptionEntity bioMetricExceptionEntity = new BiometricExceptionEntity();
		BiometricExceptionPKEntity biometricExceptionPKEntity = new BiometricExceptionPKEntity();
		biometricExceptionPKEntity.setRegId(registrationId);
		biometricExceptionPKEntity.setMissingBio(exception.getMissingBiometric());
		biometricExceptionPKEntity.setLangCode("en");

		bioMetricExceptionEntity.setId(biometricExceptionPKEntity);
		bioMetricExceptionEntity.setPreregId(preregistrationId);
		bioMetricExceptionEntity.setBioTyp(exception.getType());
		bioMetricExceptionEntity.setExcpDescr(exception.getExceptionDescription());
		bioMetricExceptionEntity.setExcpTyp(exception.getExceptionType());
		bioMetricExceptionEntity.setIsDeleted(false);
		bioMetricExceptionEntity.setStatusCode("BiometricException Saved");
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertBiometricExceptioDtoToEntity()::exit");
		return bioMetricExceptionEntity;
	}

	/**
	 * Convert photo graph data to photo graph entity.
	 *
	 * @param photoGraphData
	 *            the photo graph data
	 * @param exceptionPhotographData
	 *            the exception photograph data
	 * @param metaData
	 *            the meta data
	 * @return the applicant photograph entity
	 */
	public static ApplicantPhotographEntity convertPhotoGraphDtoToEntity(Photograph photoGraphData,
			Photograph exceptionPhotographData, List<FieldValue> metaData) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertPhotoGraphDtoToEntity()::entry");
		Optional<FieldValue> regId = metaData.stream().filter(m -> m.getLabel().equals(REGISTRATION_ID)).findFirst();
		String registrationId = "";
		if (regId.isPresent())
			registrationId = regId.get().getValue();

		Optional<FieldValue> preregId = metaData.stream().filter(m -> m.getLabel().equals(PRE_REGISTRATION_ID))
				.findFirst();
		String preregistrationId = "";
		if (preregId.isPresent())
			preregistrationId = preregId.get().getValue();

		ApplicantPhotographEntity applicantPhotographEntity = new ApplicantPhotographEntity();

		Boolean isHasExceptionPhoto = false;
		if (exceptionPhotographData != null && !(exceptionPhotographData.getPhotographName().isEmpty())) {
			isHasExceptionPhoto = true;
			applicantPhotographEntity.setExcpPhotoName(exceptionPhotographData.getPhotographName());
		}

		ApplicantPhotographPKEntity applicantPhotographPKEntity = new ApplicantPhotographPKEntity();
		applicantPhotographPKEntity.setRegId(registrationId);

		applicantPhotographEntity.setId(applicantPhotographPKEntity);
		applicantPhotographEntity.setPreRegId(preregistrationId);

		applicantPhotographEntity.setImageName(photoGraphData.getPhotographName());
		applicantPhotographEntity.setHasExcpPhotograph(isHasExceptionPhoto);
		applicantPhotographEntity.setQualityScore(BigDecimal.valueOf(photoGraphData.getQualityScore()));
		applicantPhotographEntity.setActive(true);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertPhotoGraphDtoToEntity()::exit");

		return applicantPhotographEntity;
	}

	/**
	 * Convert osi data to osi entity.
	 *
	 * @param osiData
	 *            the osi data
	 * @param introducer
	 *            the meta data
	 * @param metaData
	 *            the meta data
	 * @return the reg osi entity
	 */
	public static RegOsiEntity convertOsiDataToEntity(List<FieldValue> osiData, Introducer introducer,
			List<FieldValue> metaData) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertOsiDataToEntity()::entry");

		RegOsiEntity regOsiEntity = new RegOsiEntity();

		RegOsiPkEntity regOsiPkEntity = new RegOsiPkEntity();
		IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
		regOsiPkEntity.setRegId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.REGISTRATIONID));
		regOsiEntity.setPreregId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.PREREGISTRATIONID));
		regOsiEntity.setIntroducerRegId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.INTRODUCERRID));
		regOsiEntity.setIntroducerUin(identityIteratorUtil.getFieldValue(metaData, JsonConstant.INTRODUCERUIN));
		regOsiEntity.setIntroducerTyp(identityIteratorUtil.getFieldValue(metaData, JsonConstant.INTRODUCERTYPE));
		regOsiEntity.setSupervisorFingerType(
				identityIteratorUtil.getFieldValue(metaData, JsonConstant.SUPERVISORFINGERPRINTTYPE));
		regOsiEntity.setIntroducerFingerpType(
				identityIteratorUtil.getFieldValue(metaData, JsonConstant.INTRODUCERFINGERPRINTTYPE));
		regOsiEntity
				.setIntroducerIrisType(identityIteratorUtil.getFieldValue(metaData, JsonConstant.INTRODUCERIRISTYPE));
		regOsiEntity.setOfficerfingerType(
				identityIteratorUtil.getFieldValue(metaData, JsonConstant.OFFICERFINGERPRINTTYPE));
		regOsiEntity.setOfficerIrisType(identityIteratorUtil.getFieldValue(metaData, JsonConstant.OFFICERIRISTYPE));
		regOsiEntity.setOfficerHashedPin(identityIteratorUtil.getFieldValue(metaData, JsonConstant.OFFICERPIN));
		regOsiEntity
				.setSupervisorIrisType(identityIteratorUtil.getFieldValue(metaData, JsonConstant.SUPERVISORIRISTYPE));

		regOsiEntity.setOfficerFingerpImageName(
				identityIteratorUtil.getFieldValue(osiData, JsonConstant.OFFICERFINGERPRINTIMAGE));
		String officerId = identityIteratorUtil.getFieldValue(osiData, JsonConstant.OFFICERID);
		regOsiEntity.setOfficerId(officerId);
		regOsiEntity
				.setOfficerIrisImageName(identityIteratorUtil.getFieldValue(osiData, JsonConstant.OFFICERIRISIMAGE));
		regOsiEntity.setSupervisorFingerpImageName(
				identityIteratorUtil.getFieldValue(osiData, JsonConstant.SUPERVISORFINGERPRINTIMAGE));
		regOsiEntity.setSupervisorIrisImageName(
				identityIteratorUtil.getFieldValue(osiData, JsonConstant.SUPERVISORIRISIMAGE));
		String supervisorId = identityIteratorUtil.getFieldValue(osiData, JsonConstant.SUPERVISORID);
		if (supervisorId != null) {
			regOsiEntity.setSupervisorId(supervisorId);
		} else {
			regOsiEntity.setSupervisorId(officerId);
		}
		regOsiEntity.setOfficerPhotoName(
				identityIteratorUtil.getFieldValue(osiData, JsonConstant.OFFICERAUTHENTICATIONIMAGE));
		regOsiEntity.setOfficerHashedPwd(identityIteratorUtil.getFieldValue(osiData, JsonConstant.OFFICERPWR));
		regOsiEntity.setSupervisorPhotoName(
				identityIteratorUtil.getFieldValue(osiData, JsonConstant.SUPERVISORAUTHENTICATIONIMAGE));
		regOsiEntity.setSupervisorHashedPwd(identityIteratorUtil.getFieldValue(osiData, JsonConstant.SUPERVISORPWR));
		regOsiEntity.setSupervisorHashedPin(identityIteratorUtil.getFieldValue(osiData, JsonConstant.SUPERVISORPIN));

		if (introducer.getIntroducerFingerprint() != null)
			regOsiEntity.setIntroducerFingerpImageName(introducer.getIntroducerFingerprint().getImageName());
		if (introducer.getIntroducerIris() != null)
			regOsiEntity.setIntroducerIrisImageName(introducer.getIntroducerIris().getImageName());
		if (introducer.getIntroducerImage() != null)
			regOsiEntity.setIntroducerPhotoName(introducer.getIntroducerImage().getImageName());

		regOsiEntity.setId(regOsiPkEntity);

		regOsiEntity.setIsActive(true);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertOsiDataToEntity()::exit");

		return regOsiEntity;
	}

	/**
	 * Convert reg abis ref to entity.
	 *
	 * @param regAbisRefDto
	 *            the reg abis ref dto
	 * @return the reg abis ref entity
	 */
	public static RegAbisRefEntity convertRegAbisRefToEntity(RegAbisRefDto regAbisRefDto) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				regAbisRefDto.getReg_id(), "PacketInfoMapper::convertRegAbisRefToEntity()::entry");

		RegAbisRefEntity regAbisRefEntity = new RegAbisRefEntity();

		RegAbisRefPkEntity regAbisRefPkEntity = new RegAbisRefPkEntity();

		regAbisRefPkEntity.setRegId(regAbisRefDto.getReg_id());
		regAbisRefEntity.setAbisRefId(regAbisRefDto.getAbis_ref_id());
		regAbisRefEntity.setId(regAbisRefPkEntity);
		regAbisRefEntity.setIsActive(true);

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				regAbisRefDto.getReg_id(), "PacketInfoMapper::convertRegAbisRefToEntity()::exit");
		return regAbisRefEntity;
	}

	/**
	 * Convert reg center machine to entity.
	 *
	 * @param metaData
	 *            the meta data
	 * @return the reg center machine entity
	 */
	public static RegCenterMachineEntity convertRegCenterMachineToEntity(List<FieldValue> metaData) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::RegCenterMachineEntity()::entry");
		IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
		RegCenterMachinePKEntity regCenterMachinePKEntity = new RegCenterMachinePKEntity();
		RegCenterMachineEntity regCenterMachineEntity = new RegCenterMachineEntity();

		regCenterMachinePKEntity.setRegId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.REGISTRATIONID));
		regCenterMachineEntity
				.setPreregId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.PREREGISTRATIONID));
		regCenterMachineEntity.setLatitude(identityIteratorUtil.getFieldValue(metaData, JsonConstant.GEOLOCLATITUDE));
		regCenterMachineEntity.setLongitude(identityIteratorUtil.getFieldValue(metaData, JsonConstant.GEOLOCLONGITUDE));
		regCenterMachineEntity.setCntrId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.CENTERID));
		regCenterMachineEntity.setMachineId(identityIteratorUtil.getFieldValue(metaData, JsonConstant.MACHINEID));
		String creationTime = identityIteratorUtil.getFieldValue(metaData, JsonConstant.CREATIONDATE);
		if (creationTime != null)
			regCenterMachineEntity.setPacketCreationDate(
					DateUtils.parseUTCToLocalDateTime(creationTime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

		regCenterMachineEntity.setId(regCenterMachinePKEntity);
		regCenterMachineEntity.setIsActive(true);
		regCenterMachineEntity.setId(regCenterMachinePKEntity);
		regCenterMachineEntity.setIsActive(true);

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::RegCenterMachineEntity()::exit");
		return regCenterMachineEntity;
	}

	/**
	 * Gets the json values.
	 *
	 * @param jsonNode
	 *            the json node
	 * @param language
	 *            the language
	 * @return the json values
	 */
	private static String getJsonValues(JsonValue[] jsonNode, String language) {
		String value = null;
		if (jsonNode != null) {
			for (int i = 0; i < jsonNode.length; i++) {
				if (jsonNode[i].getLanguage().equals(language)) {
					value = jsonNode[i].getValue();
				}
			}
		}

		return value;
	}

	/**
	 * Gets the languages.
	 *
	 * @param jsonNode
	 *            the json node
	 * @return the languages
	 */
	private static String[] getLanguages(JsonValue[] jsonNode) {
		if (jsonNode != null) {
			for (int i = 0; i < jsonNode.length; i++) {
				if (!(languages.toString().contains(jsonNode[i].getLanguage())))
					languages = languages.append(jsonNode[i].getLanguage()).append(",");

			}
		}

		return languages.toString().split(",");
	}

	/**
	 * Conver demographic dedupe dto to entity.
	 *
	 * @param demoDto
	 *            the demo dto
	 * @param regId
	 *            the reg id
	 * @return the list
	 */
	public static List<IndividualDemographicDedupeEntity> converDemographicDedupeDtoToEntity(
			IndividualDemographicDedupe demoDto, String regId) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				regId, "PacketInfoMapper::converDemographicDedupeDtoToEntity()::entry");
		IndividualDemographicDedupeEntity entity;
		IndividualDemographicDedupePKEntity applicantDemographicPKEntity;
		List<IndividualDemographicDedupeEntity> demogrphicDedupeEntities = new ArrayList<>();
		if (demoDto.getName() != null) {
			getLanguages(demoDto.getName());
		}
		String[] languageArray = getLanguages(demoDto.getGender());
		for (int i = 0; i < languageArray.length; i++) {
			entity = new IndividualDemographicDedupeEntity();
			applicantDemographicPKEntity = new IndividualDemographicDedupePKEntity();

			applicantDemographicPKEntity.setRegId(regId);
			applicantDemographicPKEntity.setLangCode(languageArray[i]);

			entity.setId(applicantDemographicPKEntity);
			entity.setIsActive(true);
			entity.setIsDeleted(false);
			String applicantName = null;
			if (demoDto.getName() != null) {
				applicantName = getJsonValues(demoDto.getName(), languageArray[i]);
			entity.setName(getHMACHashCode(applicantName.trim().toUpperCase()));
			}

			if (demoDto.getDateOfBirth() != null) {
				try {
					Date date = new SimpleDateFormat("yyyy/MM/dd").parse(demoDto.getDateOfBirth());

					entity.setDob(getHMACHashCode(demoDto.getDateOfBirth()));
				} catch (ParseException e) {
					regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
							regId, e.getMessage() + ExceptionUtils.getStackTrace(e));
					throw new DateParseException(PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getMessage(), e);
				}
			}
			entity.setGender(getHMACHashCode(getJsonValues(demoDto.getGender(), languageArray[i])));
			demogrphicDedupeEntities.add(entity);

		}

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				regId, "PacketInfoMapper::converDemographicDedupeDtoToEntity()::exit");
		return demogrphicDedupeEntities;
	}

	/**
	 * Convert demographic info json to entity.
	 *
	 * @param infoJson
	 *            the info json
	 * @return the applicant demographic info json entity
	 */
	public static ApplicantDemographicInfoJsonEntity convertDemographicInfoJsonToEntity(DemographicInfoJson infoJson) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertDemographicInfoJsonToEntity()::entry");
		ApplicantDemographicInfoJsonEntity applicantDemographicDataEntity = new ApplicantDemographicInfoJsonEntity();
		ApplicantDemographicInfoJsonPKEntity applicantDemographicDataPKEntity = new ApplicantDemographicInfoJsonPKEntity();
		applicantDemographicDataPKEntity.setRegId(infoJson.getRegId());

		applicantDemographicDataEntity.setId(applicantDemographicDataPKEntity);
		applicantDemographicDataEntity.setDemographicDetails(infoJson.getDemographicDetails());
		applicantDemographicDataEntity.setIsActive(true);
		applicantDemographicDataEntity.setIsDeleted(false);
		applicantDemographicDataEntity.setPreRegId(infoJson.getPreRegId());
		applicantDemographicDataEntity.setStatusCode(infoJson.getStatusCode());
		applicantDemographicDataEntity.setLangCode(infoJson.getLangCode());
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(),
				"", "PacketInfoMapper::convertDemographicInfoJsonToEntity()::exit");
		return applicantDemographicDataEntity;
	}

	public static String getHMACHashCode(String value) {
		return  CryptoUtil.encodeBase64(HMACUtils.generateHash(value.getBytes()));
		
	}
}
