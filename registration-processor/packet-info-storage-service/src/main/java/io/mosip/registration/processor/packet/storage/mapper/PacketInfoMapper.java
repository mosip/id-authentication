package io.mosip.registration.processor.packet.storage.mapper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		for (FieldValue field : metaData) {
			if (field.getLabel().matches(REGISTRATION_ID)) {
				regOsiPkEntity.setRegId(field.getValue());
			} else if (field.getLabel().matches(PRE_REGISTRATION_ID)) {
				regOsiEntity.setPreregId(field.getValue());
			} else if (field.getLabel().matches("introducerRID")) {
				regOsiEntity.setIntroducerId(field.getValue());
			} else if (field.getLabel().matches("introducerUIN")) {
				regOsiEntity.setIntroducerRegId(field.getValue());
				regOsiEntity.setIntroducerUin(field.getValue());
			} else if (field.getLabel().matches("introducerRIDHash")) {
				regOsiEntity.setIntroducerRegId(field.getValue());
			} else if (field.getLabel().matches("introducerType")) {
				regOsiEntity.setIntroducerTyp(field.getValue());
			} else if (field.getLabel().matches("supervisorFingerprintType")) {
				regOsiEntity.setSupervisorFingerType(field.getValue());
			} else if (field.getLabel().matches("introducerFingerprintType")) {
				regOsiEntity.setIntroducerFingerpType(field.getValue());
			} else if (field.getLabel().matches("introducerIrisType")) {
				regOsiEntity.setIntroducerIrisType(field.getValue());
			} else if (field.getLabel().matches("officerFingerprintType")) {
				regOsiEntity.setOfficerfingerType(field.getValue());
			} else if (field.getLabel().matches("officerIrisType")) {
				regOsiEntity.setOfficerIrisType(field.getValue());
			} else if (field.getLabel().matches("officerPIN")) {
				regOsiEntity.setOfficerHashedPin(field.getValue());
			} else if (field.getLabel().matches("supervisorIrisType")) {
				regOsiEntity.setSupervisorIrisType(field.getValue());
			}

		}

		for (FieldValue field : osiData) {
			if (field.getLabel().matches("officerFingerprintImage")) {
				regOsiEntity.setOfficerFingerpImageName(field.getValue());
			} else if (field.getLabel().matches("officerId")) {
				regOsiEntity.setOfficerId(field.getValue());
			} else if (field.getLabel().matches("officerIrisImage")) {
				regOsiEntity.setOfficerIrisImageName(field.getValue());
			} else if (field.getLabel().matches("supervisorFingerprintImage")) {
				regOsiEntity.setSupervisorFingerpImageName(field.getValue());
			} else if (field.getLabel().matches("supervisorIrisImage")) {
				regOsiEntity.setSupervisorIrisImageName(field.getValue());
			} else if (field.getLabel().matches("supervisorId")) {
				regOsiEntity.setSupervisorId(field.getValue());
			}
			// Added
			else if (field.getLabel().matches("officerAuthenticationImage")) {
				regOsiEntity.setOfficerPhotoName(field.getValue());
			} else if (field.getLabel().matches("officerPassword")) {
				regOsiEntity.setOfficerHashedPwd(field.getValue());
			} else if (field.getLabel().matches("supervisorAuthenticationImage")) {
				regOsiEntity.setSupervisorPhotoName(field.getValue());
			} else if (field.getLabel().matches("supervisorPassword")) {
				regOsiEntity.setSupervisorHashedPwd(field.getValue());
			} else if (field.getLabel().matches("supervisorPIN")) {
				regOsiEntity.setSupervisorHashedPin(field.getValue());
			}
		}
		if (introducer.getIntroducerFingerprint() != null)
			regOsiEntity.setIntroducerFingerpImageName(introducer.getIntroducerFingerprint().getImageName());
		if (introducer.getIntroducerIris() != null)
			regOsiEntity.setIntroducerIrisImageName(introducer.getIntroducerIris().getImageName());

		regOsiEntity.setId(regOsiPkEntity);

		regOsiEntity.setIsActive(true);

		return regOsiEntity;
	}

	public static RegCenterMachineEntity convertRegCenterMachineToEntity(List<FieldValue> metaData) {

		RegCenterMachinePKEntity regCenterMachinePKEntity = new RegCenterMachinePKEntity();
		RegCenterMachineEntity regCenterMachineEntity = new RegCenterMachineEntity();

		for (FieldValue field : metaData) {
			if (field.getLabel().matches(REGISTRATION_ID)) {
				regCenterMachinePKEntity.setRegId(field.getValue());
			} else if (field.getLabel().matches(PRE_REGISTRATION_ID)) {
				regCenterMachineEntity.setPreregId(field.getValue());
			} else if (field.getLabel().matches("geoLocLatitude")) {
				regCenterMachineEntity.setLatitude(field.getValue());
			} else if (field.getLabel().matches("geoLoclongitude")) {
				regCenterMachineEntity.setLongitude(field.getValue());

			}

		}

		regCenterMachineEntity.setCntrId("Center 1");
		regCenterMachineEntity.setMachineId("Machine 1");
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

	private static String getName(List<JsonValue[]> jsonValueList,String language) {
		String name ="";
		for(int i =0;i<jsonValueList.size();i++) {
			
			for(int j=0;j<jsonValueList.get(i).length;j++) {
				if(language.equals(jsonValueList.get(i)[j].getLanguage())) {
					name = name+jsonValueList.get(i)[j].getValue();
				}
			}
				
			}
		return name;
		}
		
	public static List<IndividualDemographicDedupeEntity> converDemographicDedupeDtoToEntity(
			IndividualDemographicDedupe demoDto, String regId, String preRegId) {
		IndividualDemographicDedupeEntity entity;
		IndividualDemographicDedupePKEntity applicantDemographicPKEntity;
		List<IndividualDemographicDedupeEntity> demogrphicDedupeEntities = new ArrayList<>();
		for( int i =0;i<demoDto.getName().size();i++) {
			getLanguages(demoDto.getName().get(i));

		}
		getLanguages(demoDto.getDateOfBirth());
		String[] languageArray =  getLanguages(demoDto.getGender());
		for (int i = 0; i < languageArray.length; i++) {
			entity = new IndividualDemographicDedupeEntity();
			applicantDemographicPKEntity = new IndividualDemographicDedupePKEntity();

			applicantDemographicPKEntity.setRefId(regId);
			applicantDemographicPKEntity.setRefIdType(preRegId);
			applicantDemographicPKEntity.setLangCode(languageArray[i]);

			entity.setId(applicantDemographicPKEntity);
			entity.setIsActive(true);
			entity.setIsDeleted(false);
			entity.setName(getName(demoDto.getName(),languageArray[i]));
			entity.setPheoniticName("testValue");
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
			entity.setGenderCode(getJsonValues(demoDto.getGender(), languageArray[i]));
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
