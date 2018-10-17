package io.mosip.registration.processor.packet.storage.mapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.registration.processor.core.packet.dto.DemoInLocalLang;
import io.mosip.registration.processor.core.packet.dto.DemoInUserLang;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.DocumentDetail;
import io.mosip.registration.processor.core.packet.dto.ExceptionFingerprint;
import io.mosip.registration.processor.core.packet.dto.ExceptionIris;
import io.mosip.registration.processor.core.packet.dto.Fingerprint;
import io.mosip.registration.processor.core.packet.dto.Iris;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.OsiData;
import io.mosip.registration.processor.core.packet.dto.Photograph;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicEntity;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicPKEntity;
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
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;

public class PacketInfoMapper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoMapper.class);
	
	
	
	private PacketInfoMapper() {
		super();
	}
	
	/**
	 * Convert app doc dto to app doc entity.
	 *
	 * @param documentDto
	 *            the document dto
	 * @return the applicant document entity
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static ApplicantDocumentEntity convertAppDocDtoToEntity(DocumentDetail documentDto,MetaData metaData) {

		ApplicantDocumentEntity applicantDocumentEntity = new ApplicantDocumentEntity();
		ApplicantDocumentPKEntity applicantDocumentPKEntity = new ApplicantDocumentPKEntity();
		applicantDocumentPKEntity.setDocCatCode(documentDto.getDocumentCategory());
		applicantDocumentPKEntity.setDocTypCode(documentDto.getDocumentType());
		applicantDocumentPKEntity.setRegId(metaData.getRegistrationId());

		applicantDocumentEntity.setId(applicantDocumentPKEntity);
		applicantDocumentEntity.setPreregId(metaData.getPreRegistrationId());
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocName(documentDto.getDocumentName());
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocFileFormat(".zip");

		return applicantDocumentEntity;
	}

	/**
	 * Convert iris to iris exc entity.
	 *
	 * @param exceptionIris
	 *            the exception iris
	 * @return the biometric exception entity
	 */
	public static BiometricExceptionEntity convertBiometricExcDtoToEntity(ExceptionIris exceptionIris,MetaData metaData) {
		BiometricExceptionEntity bioMetricExceptionEntity = new BiometricExceptionEntity();
		BiometricExceptionPKEntity biometricExceptionPKEntity = new BiometricExceptionPKEntity();
		biometricExceptionPKEntity.setRegId(metaData.getRegistrationId());
		biometricExceptionPKEntity.setMissingBio(exceptionIris.getMissingIris());
		biometricExceptionPKEntity.setLangCode("en");
		bioMetricExceptionEntity.setId(biometricExceptionPKEntity);
		bioMetricExceptionEntity.setPreregId(metaData.getPreRegistrationId());
		bioMetricExceptionEntity.setBioTyp(exceptionIris.getBiometricType());
		bioMetricExceptionEntity.setExcpDescr(exceptionIris.getExceptionDescription());
		bioMetricExceptionEntity.setExcpTyp(exceptionIris.getExceptionType());
		bioMetricExceptionEntity.setIsDeleted(false);
		bioMetricExceptionEntity.setStatusCode("");

		return bioMetricExceptionEntity;
	}

	/**
	 * Convert iris to iris entity.
	 *
	 * @param iris
	 *            the iris
	 * @return the applicant iris entity
	 */
	public static ApplicantIrisEntity convertIrisDtoToEntity(Iris iris,MetaData metaData) {

		ApplicantIrisEntity applicantIrisEntity = new ApplicantIrisEntity();
		ApplicantIrisPKEntity applicantIrisPKEntity = new ApplicantIrisPKEntity();

		applicantIrisPKEntity.setRegId(metaData.getRegistrationId());
		applicantIrisPKEntity.setTyp(iris.getIrisType());
		applicantIrisPKEntity.setLangCode("en");

		applicantIrisEntity.setId(applicantIrisPKEntity);
		applicantIrisEntity.setNoOfRetry(iris.getNumRetry());
		applicantIrisEntity.setImageName(iris.getIrisImageName());
		applicantIrisEntity.setPreregId(metaData.getPreRegistrationId());
		applicantIrisEntity.setQualityScore(BigDecimal.valueOf(iris.getQualityScore()));

		return applicantIrisEntity;
	}

	/**
	 * Convert fingerprint to fingerprint entity.
	 *
	 * @param fingerprint
	 *            the fingerprint
	 * @return the applicant fingerprint entity
	 */
	public static ApplicantFingerprintEntity convertFingerprintDtoToEntity(Fingerprint fingerprint,MetaData metaData) {

		ApplicantFingerprintEntity applicantFingerprintEntity = new ApplicantFingerprintEntity();
		ApplicantFingerprintPKEntity applicantFingerprintPKEntity = new ApplicantFingerprintPKEntity();

		applicantFingerprintPKEntity.setRegId(metaData.getRegistrationId());
		applicantFingerprintPKEntity.setTyp(fingerprint.getFingerType());
		applicantFingerprintPKEntity.setLangCode("en");

		applicantFingerprintEntity.setId(applicantFingerprintPKEntity);
		applicantFingerprintEntity.setNoOfRetry(fingerprint.getNumRetry());
		applicantFingerprintEntity.setImageName(fingerprint.getFingerprintImageName());
		applicantFingerprintEntity.setNoOfRetry(fingerprint.getNumRetry());
		applicantFingerprintEntity.setPreregId(metaData.getPreRegistrationId());
		applicantFingerprintEntity.setQualityScore(BigDecimal.valueOf(fingerprint.getQualityScore()));

		return applicantFingerprintEntity;

	}

	/**
	 * Convert biometric exc to biometric exc entity.
	 *
	 * @param exceptionFingerprint
	 *            the exception fingerprint
	 * @return the biometric exception entity
	 */
	public static BiometricExceptionEntity convertBiometricExceptioDtoToEntity(
			ExceptionFingerprint exceptionFingerprint,MetaData metaData) {

		BiometricExceptionEntity bioMetricExceptionEntity = new BiometricExceptionEntity();
		BiometricExceptionPKEntity biometricExceptionPKEntity = new BiometricExceptionPKEntity();
		biometricExceptionPKEntity.setRegId(metaData.getRegistrationId());
		biometricExceptionPKEntity.setMissingBio(exceptionFingerprint.getMissingFinger());
		biometricExceptionPKEntity.setLangCode("en");

		bioMetricExceptionEntity.setId(biometricExceptionPKEntity);
		bioMetricExceptionEntity.setPreregId(metaData.getPreRegistrationId());
		bioMetricExceptionEntity.setBioTyp(exceptionFingerprint.getBiometricType());
		bioMetricExceptionEntity.setExcpDescr(exceptionFingerprint.getExceptionDescription());
		bioMetricExceptionEntity.setExcpTyp(exceptionFingerprint.getExceptionType());
		bioMetricExceptionEntity.setIsDeleted(false);


		return bioMetricExceptionEntity;
	}

	/**
	 * Convert photo graph data to photo graph entity.
	 *
	 * @param photoGraphData
	 *            the photo graph data
	 * @return the applicant photograph entity
	 */
	public static ApplicantPhotographEntity convertPhotoGraphDtoToEntity(Photograph photoGraphData,MetaData metaData) {

		ApplicantPhotographEntity applicantPhotographEntity = new ApplicantPhotographEntity();

		ApplicantPhotographPKEntity applicantPhotographPKEntity = new ApplicantPhotographPKEntity();
		applicantPhotographPKEntity.setRegId(metaData.getRegistrationId());
		applicantPhotographPKEntity.setLangCode("en");
		
		applicantPhotographEntity.setId(applicantPhotographPKEntity);
		applicantPhotographEntity.setPreregId(metaData.getPreRegistrationId());
		applicantPhotographEntity.setExcpPhotoName(photoGraphData.getExceptionPhotoName());
		applicantPhotographEntity.setImageName(photoGraphData.getPhotographName());
		applicantPhotographEntity.setHasExcpPhotograph(photoGraphData.isHasExceptionPhoto());
		applicantPhotographEntity.setQualityScore(BigDecimal.valueOf(photoGraphData.getQualityScore()));


		return applicantPhotographEntity;
	}

	/**
	 * Convert osi data to osi entity.
	 *
	 * @param osiData
	 *            the osi data
	 * @return the reg osi entity
	 */
	public static RegOsiEntity convertOsiDataToEntity(OsiData osiData,MetaData metaData) {

		RegOsiEntity regOsiEntity = new RegOsiEntity();
		regOsiEntity.setIntroducerFingerpImageName(osiData.getIntroducerFingerprintImage());
		regOsiEntity.setIntroducerId(osiData.getIntroducerRID().toString());
		regOsiEntity.setIntroducerIrisImageName(osiData.getIntroducerIrisIrish());
		regOsiEntity.setIntroducerRegId(osiData.getIntroducerUIN());
		regOsiEntity.setIntroducerTyp(osiData.getIntroducerType());
		regOsiEntity.setIntroducerUin(osiData.getIntroducerUIN());
		regOsiEntity.setOfficerFingerpImageName(osiData.getOperatorFingerprintImage());
		regOsiEntity.setOfficerId(osiData.getOperatorID());
		regOsiEntity.setOfficerIrisImageName(osiData.getOperatorIrisImage());
		regOsiEntity.setRegId(metaData.getRegistrationId());
		regOsiEntity.setPreregId(metaData.getPreRegistrationId());
		regOsiEntity.setSupervisorId(osiData.getSupervisorID());
		regOsiEntity.setSupervisorFingerpImageName(osiData.getSupervisorFingerprintImage());
		regOsiEntity.setSupervisorIrisImageName(osiData.getSupervisorIrisName());
		regOsiEntity.setIsActive(true);

		return regOsiEntity;
	}

	

	/**
	 * Convert demographic info to app demographic info entity.
	 *
	 * @param demographicInfo
	 *            the demographic info
	 * @return the list
	 */
	public static List<ApplicantDemographicEntity> convertDemographicDtoToEntity(
			DemographicInfo demographicInfo,MetaData metaData) {

		DemoInLocalLang demoInLocalLang = demographicInfo.getDemoInLocalLang();
		DemoInUserLang demoInUserLang = demographicInfo.getDemoInUserLang();
		List<ApplicantDemographicEntity> applicantDemographicEntities = new ArrayList<>();

		ApplicantDemographicEntity applicantDemographicEntity = new ApplicantDemographicEntity();

		// adding Local Language Demographic data
		ApplicantDemographicPKEntity applicantDemographicPKEntity = new ApplicantDemographicPKEntity();
		applicantDemographicPKEntity.setLangCode(demoInLocalLang.getLanguageCode());
		applicantDemographicPKEntity.setRegId(metaData.getRegistrationId());

		applicantDemographicEntity.setId(applicantDemographicPKEntity);
		applicantDemographicEntity.setPreregId(metaData.getPreRegistrationId());
		applicantDemographicEntity.setAddrLine1(demoInLocalLang.getAddress().getLine1());
		applicantDemographicEntity.setAddrLine2(demoInLocalLang.getAddress().getLine2());
		applicantDemographicEntity.setAddrLine3(demoInLocalLang.getAddress().getLine3());
		applicantDemographicEntity.setAge(calculateAge(demoInLocalLang.getDateOfBirth()));
		applicantDemographicEntity.setApplicantType(metaData.getApplicationType());
		applicantDemographicEntity.setDob(demoInLocalLang.getDateOfBirth());
		applicantDemographicEntity.setEmail(demoInLocalLang.getEmailId());
		applicantDemographicEntity.setFamilyname(demoInLocalLang.getFamilyname());
		applicantDemographicEntity.setFirstname(demoInLocalLang.getFirstname());
		applicantDemographicEntity.setForename(demoInLocalLang.getForename());
		applicantDemographicEntity.setFullname(demoInLocalLang.getFullName());
		applicantDemographicEntity.setGenderCode(demoInLocalLang.getGender());
		applicantDemographicEntity.setGivenname(demoInLocalLang.getGivenname());
		applicantDemographicEntity.setLastname(demoInLocalLang.getLastname());
		applicantDemographicEntity.setMiddlename(demoInLocalLang.getMiddlename());
		applicantDemographicEntity.setMobile(demoInLocalLang.getMobile());
		applicantDemographicEntity.setSurname(demoInLocalLang.getSurname());
		
		applicantDemographicEntity.setLocationCode("Location Code");
		applicantDemographicEntity.setNationalid("National Id");
		applicantDemographicEntity.setParentFullname("Parent Full Name");
		applicantDemographicEntity.setParentRefId("ParentRefId");
		applicantDemographicEntity.setParentRefIdType("ParentRefIdType");
		
		applicantDemographicEntities.add(applicantDemographicEntity);

		// adding User Language Demographic data

		applicantDemographicEntity = new ApplicantDemographicEntity();

		ApplicantDemographicPKEntity applicantDemographicPKEntity1 = new ApplicantDemographicPKEntity();
		applicantDemographicPKEntity1.setLangCode(demoInUserLang.getLanguageCode());
		applicantDemographicPKEntity1.setRegId(metaData.getRegistrationId());

		applicantDemographicEntity.setId(applicantDemographicPKEntity1);
		applicantDemographicEntity.setPreregId(metaData.getPreRegistrationId());
		applicantDemographicEntity.setAddrLine1(demoInUserLang.getAddress().getLine1());
		applicantDemographicEntity.setAddrLine2(demoInUserLang.getAddress().getLine2());
		applicantDemographicEntity.setAddrLine3(demoInUserLang.getAddress().getLine3());
		applicantDemographicEntity.setAge(calculateAge(demoInLocalLang.getDateOfBirth()));
		applicantDemographicEntity.setApplicantType(metaData.getApplicationType());
		applicantDemographicEntity.setDob(demoInLocalLang.getDateOfBirth());
		applicantDemographicEntity.setEmail(demoInUserLang.getEmailId());
		applicantDemographicEntity.setFamilyname(demoInUserLang.getFamilyname());
		applicantDemographicEntity.setFirstname(demoInUserLang.getFirstname());
		applicantDemographicEntity.setForename(demoInUserLang.getForename());
		applicantDemographicEntity.setFullname(demoInUserLang.getFullName());
		applicantDemographicEntity.setGenderCode(demoInUserLang.getGender());
		applicantDemographicEntity.setGivenname(demoInUserLang.getGivenname());
		applicantDemographicEntity.setLastname(demoInUserLang.getLastname());
		
		applicantDemographicEntity.setMiddlename(demoInUserLang.getMiddlename());
		applicantDemographicEntity.setMobile(demoInUserLang.getMobile());
		applicantDemographicEntity.setSurname(demoInUserLang.getSurname());
		
		
		applicantDemographicEntity.setLocationCode("Location Code");
		applicantDemographicEntity.setNationalid("National Id");
		applicantDemographicEntity.setParentFullname("Parent Full Name");
		applicantDemographicEntity.setParentRefId("ParentRefId");
		applicantDemographicEntity.setParentRefIdType("ParentRefIdType");
		

		applicantDemographicEntities.add(applicantDemographicEntity);

		return applicantDemographicEntities;
	}
	
	
	public static int calculateAge(Date dateOfBirth) {
		final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		final Calendar c = Calendar.getInstance();
		try {
			String format = df.format(dateOfBirth);
			c.setTime(df.parse(format));
			return Calendar.getInstance().get(Calendar.YEAR) - c.get(Calendar.YEAR);
		} catch (ParseException e) {
			LOGGER.error("Invalid DOB : Failed to parse Date of Birth", e);
			return 0;
		}
	}
	

}
