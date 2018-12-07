package io.mosip.registration.processor.packet.storage.mapper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.codec.language.Soundex;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.codec.language.bm.NameType;
import org.apache.commons.codec.language.bm.PhoneticEngine;
import org.apache.commons.codec.language.bm.RuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.packet.dto.BiometricDetails;
import io.mosip.registration.processor.core.packet.dto.BiometricException;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Introducer;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoJson;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.IndividualDemographicDedupe;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
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
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachinePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiPkEntity;
import io.mosip.registration.processor.packet.storage.exception.DateParseException;

/**
 * The Class PacketInfoMapper.
 */
public class PacketInfoMapper {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoMapper.class);

	private static final String REGISTRATION_ID = "registrationId";
	private static final String PRE_REGISTRATION_ID = "preRegistrationId";
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
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocName(documentDto.getDocumentName());
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocFileFormat(".zip");
		applicantDocumentEntity.setActive(true);

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

		return applicantFingerprintEntity;

	}

	/**
	 * Convert biometric exc to biometric exc entity.
	 *
	 * @param exceptionFingerprint
	 *            the exception fingerprint
	 * @param metaData
	 *            the meta data
	 * @return the biometric exception entity
	 */
	public static BiometricExceptionEntity convertBiometricExceptioDtoToEntity(BiometricException exception,
			List<FieldValue> metaData) {
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
		return bioMetricExceptionEntity;
	}

	/**
	 * Convert photo graph data to photo graph entity.
	 *
	 * @param photoGraphData
	 *            the photo graph data
	 * @param exceptionPhotographData
	 * @param metaData
	 *            the meta data
	 * @return the applicant photograph entity
	 */
	public static ApplicantPhotographEntity convertPhotoGraphDtoToEntity(Photograph photoGraphData,
			Photograph exceptionPhotographData, List<FieldValue> metaData) {
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
		if (!(exceptionPhotographData.getPhotographName().isEmpty())) {
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
	 * @return the reg osi entity
	 */
	public static RegOsiEntity convertOsiDataToEntity(List<FieldValue> osiData, Introducer introducer,
			List<FieldValue> metaData) {

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
		regOsiEntity.setOfficerId(identityIteratorUtil.getFieldValue(osiData, JsonConstant.OFFICERID));
		regOsiEntity
				.setOfficerIrisImageName(identityIteratorUtil.getFieldValue(osiData, JsonConstant.OFFICERIRISIMAGE));
		regOsiEntity.setSupervisorFingerpImageName(
				identityIteratorUtil.getFieldValue(osiData, JsonConstant.SUPERVISORFINGERPRINTIMAGE));
		regOsiEntity.setSupervisorIrisImageName(
				identityIteratorUtil.getFieldValue(osiData, JsonConstant.SUPERVISORIRISIMAGE));
		regOsiEntity.setSupervisorId(identityIteratorUtil.getFieldValue(osiData, JsonConstant.SUPERVISORID));
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

		return regOsiEntity;
	}

	public static RegCenterMachineEntity convertRegCenterMachineToEntity(List<FieldValue> metaData) {
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
			regCenterMachineEntity.setPacketCreationDate(LocalDateTime.parse(creationTime));

		regCenterMachineEntity.setId(regCenterMachinePKEntity);
		regCenterMachineEntity.setIsActive(true);
		regCenterMachineEntity.setId(regCenterMachinePKEntity);
		regCenterMachineEntity.setIsActive(true);

		return regCenterMachineEntity;
	}

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

	private static String[] getLanguages(JsonValue[] jsonNode) {
		if (jsonNode != null) {
			for (int i = 0; i < jsonNode.length; i++) {
				if (!(languages.toString().contains(jsonNode[i].getLanguage())))
					languages = languages.append(jsonNode[i].getLanguage()).append(",");

			}
		}

		return languages.toString().split(",");
	}

	private static String getName(List<JsonValue[]> jsonValueList, String language) {
		StringBuilder name = new StringBuilder();
		for (int i = 0; i < jsonValueList.size(); i++) {

			for (int j = 0; j < jsonValueList.get(i).length; j++) {
				if (language.equals(jsonValueList.get(i)[j].getLanguage())) {
					name = name.append(jsonValueList.get(i)[j].getValue());

				}
			}

		}
		return name.toString();
	}

	public static List<IndividualDemographicDedupeEntity> converDemographicDedupeDtoToEntity(
			IndividualDemographicDedupe demoDto, String regId) {
		IndividualDemographicDedupeEntity entity;
		IndividualDemographicDedupePKEntity applicantDemographicPKEntity;
		List<IndividualDemographicDedupeEntity> demogrphicDedupeEntities = new ArrayList<>();
		for (int i = 0; i < demoDto.getName().size(); i++) {
			getLanguages(demoDto.getName().get(i));

		}
		getLanguages(demoDto.getDateOfBirth());
		String[] languageArray = getLanguages(demoDto.getGender());
		for (int i = 0; i < languageArray.length; i++) {
			entity = new IndividualDemographicDedupeEntity();
			applicantDemographicPKEntity = new IndividualDemographicDedupePKEntity();

			applicantDemographicPKEntity.setRegId(regId);
			applicantDemographicPKEntity.setLangCode(languageArray[i]);

			entity.setId(applicantDemographicPKEntity);
			entity.setIsActive(true);
			entity.setIsDeleted(false);
			entity.setName(getName(demoDto.getName(), languageArray[i]));

			Locale loc = new Locale(languageArray[i]);
			String languageName = loc.getDisplayLanguage();

			PhoneticEngine phoneticEngine = new PhoneticEngine(NameType.GENERIC, RuleType.EXACT, true);
			Set<String> languageSet = new HashSet<>();
			languageSet.add(languageName.toLowerCase());

			String encodedInputString = phoneticEngine.encode(getName(demoDto.getName(), languageArray[i]),
					Languages.LanguageSet.from(languageSet));
			Soundex soundex = new Soundex();
			if (soundex.encode(encodedInputString) != null)
				entity.setPhoneticName(soundex.encode(encodedInputString));

			String dob = getJsonValues(demoDto.getDateOfBirth(), languageArray[i]);
			if (dob != null) {
				try {
					Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dob);
					entity.setDob(date);
				} catch (ParseException e) {
					LOGGER.error("ErrorWhile Parsing Date");
					throw new DateParseException(PlatformErrorMessages.RPR_SYS_PARSING_DATE_EXCEPTION.getMessage(), e);
				}
			}
			entity.setGender(getJsonValues(demoDto.getGender(), languageArray[i]));
			demogrphicDedupeEntities.add(entity);

		}

		return demogrphicDedupeEntities;
	}

	public static ApplicantDemographicInfoJsonEntity convertDemographicInfoJsonToEntity(DemographicInfoJson infoJson) {
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
		return applicantDemographicDataEntity;
	}

}
