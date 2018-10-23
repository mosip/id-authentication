package io.mosip.registration.processor.packet.storage.mapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.registration.processor.core.packet.dto.Demographic;
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
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachineEntity;
import io.mosip.registration.processor.packet.storage.entity.RegCenterMachinePKEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiEntity;
import io.mosip.registration.processor.packet.storage.entity.RegOsiPkEntity;

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
	public static ApplicantDocumentEntity convertAppDocDtoToEntity(DocumentDetail documentDto, MetaData metaData) {

		ApplicantDocumentEntity applicantDocumentEntity = new ApplicantDocumentEntity();
		ApplicantDocumentPKEntity applicantDocumentPKEntity = new ApplicantDocumentPKEntity();
		applicantDocumentPKEntity.setDocCatCode(documentDto.getDocumentCategory());
		applicantDocumentPKEntity.setDocTypCode(documentDto.getDocumentType());
		applicantDocumentPKEntity.setRegId(metaData.getRegistrationId());

		applicantDocumentEntity.setId(applicantDocumentPKEntity);
		applicantDocumentEntity.setPreRegId(metaData.getPreRegistrationId());
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocName(documentDto.getDocumentName());
		applicantDocumentEntity.setDocOwner(documentDto.getDocumentOwner());
		applicantDocumentEntity.setDocFileFormat(".zip");
		applicantDocumentEntity.setActive(true);

		return applicantDocumentEntity;
	}

	/**
	 * Convert iris to iris exc entity.
	 *
	 * @param exceptionIris
	 *            the exception iris
	 * @return the biometric exception entity
	 */
	public static BiometricExceptionEntity convertBiometricExcDtoToEntity(ExceptionIris exceptionIris,
			MetaData metaData) {
		BiometricExceptionEntity bioMetricExceptionEntity = new BiometricExceptionEntity();
		BiometricExceptionPKEntity biometricExceptionPKEntity = new BiometricExceptionPKEntity();
		biometricExceptionPKEntity.setRegId(metaData.getRegistrationId());
		biometricExceptionPKEntity.setMissingBio(exceptionIris.getMissingBiometric());
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
	public static ApplicantIrisEntity convertIrisDtoToEntity(Iris iris, MetaData metaData) {

		ApplicantIrisEntity applicantIrisEntity = new ApplicantIrisEntity();
		ApplicantIrisPKEntity applicantIrisPKEntity = new ApplicantIrisPKEntity();

		applicantIrisPKEntity.setRegId(metaData.getRegistrationId());
		applicantIrisPKEntity.setTyp(iris.getIrisType());

		applicantIrisEntity.setId(applicantIrisPKEntity);
		applicantIrisEntity.setNoOfRetry(iris.getNumRetry());
		applicantIrisEntity.setImageName(iris.getIrisImageName());
		applicantIrisEntity.setPreRegId(metaData.getPreRegistrationId());
		applicantIrisEntity.setQualityScore(BigDecimal.valueOf(iris.getQualityScore()));
		applicantIrisEntity.setActive(true);

		return applicantIrisEntity;
	}

	/**
	 * Convert fingerprint to fingerprint entity.
	 *
	 * @param fingerprint
	 *            the fingerprint
	 * @return the applicant fingerprint entity
	 */
	public static ApplicantFingerprintEntity convertFingerprintDtoToEntity(Fingerprint fingerprint, MetaData metaData) {

		ApplicantFingerprintEntity applicantFingerprintEntity = new ApplicantFingerprintEntity();
		ApplicantFingerprintPKEntity applicantFingerprintPKEntity = new ApplicantFingerprintPKEntity();

		applicantFingerprintPKEntity.setRegId(metaData.getRegistrationId());
		applicantFingerprintPKEntity.setTyp(fingerprint.getFingerType());

		applicantFingerprintEntity.setId(applicantFingerprintPKEntity);
		applicantFingerprintEntity.setNoOfRetry(fingerprint.getNumRetry());
		applicantFingerprintEntity.setImageName(fingerprint.getFingerprintImageName());
		applicantFingerprintEntity.setNoOfRetry(fingerprint.getNumRetry());
		applicantFingerprintEntity.setPreRegId(metaData.getPreRegistrationId());
		applicantFingerprintEntity.setQualityScore(BigDecimal.valueOf(fingerprint.getQualityScore()));
		applicantFingerprintEntity.setActive(true);

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
			ExceptionFingerprint exceptionFingerprint, MetaData metaData) {

		BiometricExceptionEntity bioMetricExceptionEntity = new BiometricExceptionEntity();
		BiometricExceptionPKEntity biometricExceptionPKEntity = new BiometricExceptionPKEntity();
		biometricExceptionPKEntity.setRegId(metaData.getRegistrationId());
		biometricExceptionPKEntity.setMissingBio(exceptionFingerprint.getMissingBiometric());
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
	public static ApplicantPhotographEntity convertPhotoGraphDtoToEntity(Photograph photoGraphData, MetaData metaData) {

		ApplicantPhotographEntity applicantPhotographEntity = new ApplicantPhotographEntity();

		ApplicantPhotographPKEntity applicantPhotographPKEntity = new ApplicantPhotographPKEntity();
		applicantPhotographPKEntity.setRegId(metaData.getRegistrationId());

		applicantPhotographEntity.setId(applicantPhotographPKEntity);
		applicantPhotographEntity.setPreRegId(metaData.getPreRegistrationId());
		applicantPhotographEntity.setExcpPhotoName(photoGraphData.getExceptionPhotoName());
		applicantPhotographEntity.setImageName(photoGraphData.getPhotographName());
		applicantPhotographEntity.setHasExcpPhotograph(photoGraphData.isHasExceptionPhoto());
		applicantPhotographEntity.setQualityScore(BigDecimal.valueOf(photoGraphData.getQualityScore()));
		applicantPhotographEntity.setActive(true);

		return applicantPhotographEntity;
	}

	/**
	 * Convert osi data to osi entity.
	 *
	 * @param osiData
	 *            the osi data
	 * @return the reg osi entity
	 */
	public static RegOsiEntity convertOsiDataToEntity(OsiData osiData, MetaData metaData) {

		RegOsiEntity regOsiEntity = new RegOsiEntity();
		RegOsiPkEntity regOsiPkEntity = new RegOsiPkEntity();
		regOsiPkEntity.setRegId(metaData.getRegistrationId());

		regOsiEntity.setIntroducerFingerpImageName(osiData.getIntroducerFingerprintImage());
		regOsiEntity.setIntroducerId(osiData.getIntroducerRID().toString());
		regOsiEntity.setIntroducerIrisImageName(osiData.getIntroducerIrisImage());
		regOsiEntity.setIntroducerRegId(osiData.getIntroducerUIN());
		regOsiEntity.setIntroducerTyp(osiData.getIntroducerType());
		regOsiEntity.setIntroducerUin(osiData.getIntroducerUIN());
		regOsiEntity.setOfficerFingerpImageName(osiData.getOperatorFingerprintImage());
		regOsiEntity.setOfficerId(osiData.getOperatorId());
		regOsiEntity.setOfficerIrisImageName(osiData.getOperatorIrisName());
		regOsiEntity.setId(regOsiPkEntity);
		regOsiEntity.setPreregId(metaData.getPreRegistrationId());
		regOsiEntity.setSupervisorId(osiData.getSupervisorId());
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
	public static List<ApplicantDemographicEntity> convertDemographicDtoToEntity(Demographic demographicInfo,
			MetaData metaData) {

		DemographicInfo demoInLocalLang = demographicInfo.getDemoInLocalLang();
		DemographicInfo demoInUserLang = demographicInfo.getDemoInUserLang();
		List<ApplicantDemographicEntity> applicantDemographicEntities = new ArrayList<>();

		ApplicantDemographicEntity applicantDemographicEntity = new ApplicantDemographicEntity();

		// adding Local Language Demographic data
		ApplicantDemographicPKEntity applicantDemographicPKEntity = new ApplicantDemographicPKEntity();
		applicantDemographicPKEntity.setLangCode(demoInLocalLang.getLanguageCode());
		applicantDemographicPKEntity.setRegId(metaData.getRegistrationId());

		applicantDemographicEntity.setId(applicantDemographicPKEntity);
		applicantDemographicEntity.setPreRegId(metaData.getPreRegistrationId());
		applicantDemographicEntity.setAddrLine1(demoInLocalLang.getAddressDTO().getLine1());
		applicantDemographicEntity.setAddrLine2(demoInLocalLang.getAddressDTO().getLine2());
		applicantDemographicEntity.setAddrLine3(demoInLocalLang.getAddressDTO().getLine3());
		int age = demoInLocalLang.getAge() != null ? Integer.parseInt(demoInLocalLang.getAge()) : 0;
		applicantDemographicEntity.setAge(age);
		applicantDemographicEntity.setApplicantType(metaData.getApplicationType());
		Long dobTime = demoInLocalLang.getDateOfBirth() != null ? Long.parseLong(demoInLocalLang.getDateOfBirth())
				: null;
		applicantDemographicEntity.setDob(dobTime != null ? new Date(dobTime) : null);
		applicantDemographicEntity.setEmail(demoInLocalLang.getEmailId());

		applicantDemographicEntity.setFirstName(demoInLocalLang.getFirstName());

		applicantDemographicEntity.setFullName(demoInLocalLang.getFullName());
		applicantDemographicEntity.setGenderCode(demoInLocalLang.getGender());

		applicantDemographicEntity.setLastName(demoInLocalLang.getLastName());
		applicantDemographicEntity.setMiddleName(demoInLocalLang.getMiddleName());
		applicantDemographicEntity.setMobile(demoInLocalLang.getMobile());

		applicantDemographicEntity.setIsActive(true);

		applicantDemographicEntity.setLocationCode("Location Code");
		applicantDemographicEntity.setNationalId("National Id");
		applicantDemographicEntity.setParentFullName("Parent Full Name");
		applicantDemographicEntity.setParentRefId("ParentRefId");
		applicantDemographicEntity.setParentRefIdType("ParentRefIdType");

		applicantDemographicEntities.add(applicantDemographicEntity);

		// adding User Language Demographic data

		applicantDemographicEntity = new ApplicantDemographicEntity();

		ApplicantDemographicPKEntity applicantDemographicPKEntity1 = new ApplicantDemographicPKEntity();
		applicantDemographicPKEntity1.setLangCode(demoInUserLang.getLanguageCode());
		applicantDemographicPKEntity1.setRegId(metaData.getRegistrationId());

		applicantDemographicEntity.setId(applicantDemographicPKEntity1);
		applicantDemographicEntity.setPreRegId(metaData.getPreRegistrationId());
		applicantDemographicEntity.setAddrLine1(demoInUserLang.getAddressDTO().getLine1());
		applicantDemographicEntity.setAddrLine2(demoInUserLang.getAddressDTO().getLine2());
		applicantDemographicEntity.setAddrLine3(demoInUserLang.getAddressDTO().getLine3());
		int userAge = demoInUserLang.getAge() != null ? Integer.parseInt(demoInUserLang.getAge()) : 0;
		applicantDemographicEntity.setAge(userAge);
		applicantDemographicEntity.setApplicantType(metaData.getApplicationType());
		Long dobUserTime = demoInLocalLang.getDateOfBirth() != null ? Long.parseLong(demoInUserLang.getDateOfBirth())
				: null;
		applicantDemographicEntity.setDob(dobTime != null ? new Date(dobUserTime) : null);
		applicantDemographicEntity.setEmail(demoInUserLang.getEmailId());
		applicantDemographicEntity.setFirstName(demoInUserLang.getFirstName());
		applicantDemographicEntity.setFullName(demoInUserLang.getFullName());
		applicantDemographicEntity.setGenderCode(demoInUserLang.getGender());
		applicantDemographicEntity.setLastName(demoInUserLang.getLastName());
		applicantDemographicEntity.setIsActive(true);
		applicantDemographicEntity.setMiddleName(demoInUserLang.getMiddleName());
		applicantDemographicEntity.setMobile(demoInUserLang.getMobile());
		applicantDemographicEntity.setLocationCode("Location Code");
		applicantDemographicEntity.setNationalId("National Id");
		applicantDemographicEntity.setParentFullName("Parent Full Name");
		applicantDemographicEntity.setParentRefId("ParentRefId");
		applicantDemographicEntity.setParentRefIdType("ParentRefIdType");

		applicantDemographicEntities.add(applicantDemographicEntity);

		return applicantDemographicEntities;
	}

	public static RegCenterMachineEntity convertRegCenterMachineToEntity(MetaData metaData) {
		RegCenterMachinePKEntity regCenterMachinePKEntity = new RegCenterMachinePKEntity();
		regCenterMachinePKEntity.setRegId(metaData.getRegistrationId());

		RegCenterMachineEntity regCenterMachineEntity = new RegCenterMachineEntity();
		regCenterMachineEntity.setCntrId("Center 1");
		regCenterMachineEntity.setMachineId("Machine 1");
		regCenterMachineEntity.setId(regCenterMachinePKEntity);
		regCenterMachineEntity.setIsActive(true);
		regCenterMachineEntity.setPreregId(metaData.getPreRegistrationId());
		regCenterMachineEntity.setLatitude(metaData.getGeoLocation().getLatitude().toString());
		regCenterMachineEntity.setLongitude(metaData.getGeoLocation().getLongitude().toString());

		return regCenterMachineEntity;
	}

}
