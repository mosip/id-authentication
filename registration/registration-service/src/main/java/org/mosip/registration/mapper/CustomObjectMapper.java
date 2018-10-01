package org.mosip.registration.mapper;

import org.mosip.registration.dto.EnrollmentDTO;
import org.mosip.registration.dto.EnrollmentMetaDataDTO;
import org.mosip.registration.dto.demographic.AddressDTO;
import org.mosip.registration.dto.demographic.DemographicDTO;
import org.mosip.registration.dto.demographic.DemographicInfoDTO;
import org.mosip.registration.dto.json.demo.Address;
import org.mosip.registration.dto.json.demo.Demographic;
import org.mosip.registration.dto.json.demo.DemographicInfo;
import org.mosip.registration.dto.json.enrollmentmeta.EnrollmentMetaDataInfo;
import org.mosip.registration.dto.json.metadata.PacketInfo;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

/**
 * Class for Orika Object Mapper
 * 
 * @author YASWANTH S
 *
 */
public class CustomObjectMapper extends ConfigurableMapper {

	/**
	 * Instance of MapperFacade for object mapping
	 */
	public static MapperFacade mapperFacade = new CustomObjectMapper();

	/**
	 * Method to configure the Orika Mapper for object conversions
	 * 
	 * @param mapperFactory
	 *            the Orika MapperFactory
	 */
	@Override
	public void configure(MapperFactory mapperFactory) {
		ConverterFactory converterFactory = mapperFactory.getConverterFactory();
		converterFactory.registerConverter("auditListConverter", new AuditListConverter());
		converterFactory.registerConverter("osiDataConverter", new OSIDataConverter());

		converterFactory.registerConverter("applicantDocumentListConverter", new ApplicantDocumentListConverter());

		converterFactory.registerConverter("applicantIrisListConverter", new ApplicantIrisListConverter());
		converterFactory.registerConverter("applicantFingerprintListConverter",
				new ApplicantFingerPrintListConverter());
		converterFactory.registerConverter("applicantExceptionIrisConverter", new ApplicantExceptionIrisConverter());
		converterFactory.registerConverter("applicantExceptionFingerPrintListConverter",
				new ApplicantExceptionFingerPrintListConverter());

		mapperFactory.classMap(DemographicInfoDTO.class, DemographicInfo.class).byDefault().register();

		mapperFactory.classMap(AddressDTO.class, Address.class).byDefault().register();

		mapperFactory.classMap(DemographicDTO.class, Demographic.class).exclude("applicantDocument")
				.exclude("hofEnrollmentID").exclude("hofUIN").exclude("introducerUIN").byDefault().register();

		mapperFactory.classMap(EnrollmentMetaDataDTO.class, EnrollmentMetaDataInfo.class).exclude("hashSequence")
				.exclude("checkSum").mapNulls(true).byDefault().register();

		// packetInfo
		mapperFactory.classMap(EnrollmentDTO.class, PacketInfo.class)

				// 1photograph
				.field("packetDTO.demographicDTO.applicantDocumentDTO.photoName", "photograph.photographName")

				.field("packetDTO.demographicDTO.applicantDocumentDTO.hasExceptionPhoto",
						"photograph.hasExceptionPhoto")

				.field("packetDTO.demographicDTO.applicantDocumentDTO.exceptionPhotoName",
						"photograph.exceptionPhotoName")

				// 2.BiometricData

				// i)FingerPrints
				.fieldMap("packetDTO.biometricDTO.applicantBiometricDTO.fingerprintDetailsDTO",
						"biometericData.fingerprintData.fingerprints")
				.converter("applicantFingerprintListConverter").aToB().add()
				.field("packetDTO.biometricDTO.applicantBiometricDTO.numOfFingerPrintRetry",
						"biometericData.fingerprintData.numRetry")
				.fieldMap("packetDTO.biometricDTO.applicantBiometricDTO.exceptionFingerprintDetailsDTO",
						"biometericData.fingerprintData.exceptionFingerprints")
				.converter("applicantExceptionFingerPrintListConverter").aToB().add()

				// ii) Iris
				.fieldMap("packetDTO.biometricDTO.applicantBiometricDTO.irisDetailsDTO", "biometericData.irisData.iris")
				.converter("applicantIrisListConverter").aToB().add()
				.field("packetDTO.biometricDTO.applicantBiometricDTO.numOfIrisRetry",
						"biometericData.irisData.numRetry")
				.fieldMap("packetDTO.biometricDTO.applicantBiometricDTO.exceptionIrisDetailsDTO",
						"biometericData.irisData.exceptionIris")
				.converter("applicantExceptionIrisConverter").aToB().add()

				// 3.Document

				.fieldMap("packetDTO.demographicDTO.applicantDocumentDTO.documentDetailsDTO",
						"document.documentDetails")
				.converter("applicantDocumentListConverter").aToB().add()
				.field("packetDTO.demographicDTO.applicantDocumentDTO.acknowledgeReceiptName",
						"document.enrollmentAckCopy")
				// 4.MetaData
				.field("packetDTO.packetMetaDataDTO.geoLatitudeLoc", "metaData.geoLocation.latitude")
				.field("packetDTO.packetMetaDataDTO.geoLongitudeLoc", "metaData.geoLocation.longitude")
				.field("packetDTO.preEnrollmentId", "metaData.preEnrollmentId")
				.field("packetDTO.enrollmentID", "metaData.enrollmentId")
				.field("packetDTO.packetMetaDataDTO.applicationType", "metaData.applicationType")
				.field("packetDTO.packetMetaDataDTO.applicationCategory", "metaData.applicationCategory")
				// 5.OSI
				.fieldMap("packetDTO", "osiData").converter("osiDataConverter").aToB().add()

				// 6.HashSequence
				// After custom object mapping activity completed, retrieved packet info then
				// assign hash sequence

				// 7.Audit
				.fieldMap("packetDTO.auditDTOs", "audit").converter("auditListConverter").aToB().add()

				// 8.CheckSum
				// After custom object mapping activity completed, retrieved packet info then
				// assign checkSum

				.register();

	}

}
