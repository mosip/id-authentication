package org.mosip.registration.mapper;

import java.time.OffsetDateTime;

import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.dto.demographic.AddressDTO;
import org.mosip.registration.dto.demographic.DemographicDTO;
import org.mosip.registration.dto.demographic.DemographicInfoDTO;
import org.mosip.registration.dto.json.demo.Address;
import org.mosip.registration.dto.json.demo.Demographic;
import org.mosip.registration.dto.json.demo.DemographicInfo;
import org.mosip.registration.dto.json.metadata.PacketInfo;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
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
	public static final MapperFacade MAPPER_FACADE = new CustomObjectMapper();

	/**
	 * Method to configure the Orika Mapper for object conversions
	 * 
	 * @param mapperFactory
	 *            the Orika MapperFactory
	 */
	@Override
	public void configure(MapperFactory mapperFactory) {
		ConverterFactory converterFactory = mapperFactory.getConverterFactory();
		converterFactory.registerConverter(new PassThroughConverter(OffsetDateTime.class));

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
				.exclude("hofRegistrationId").exclude("hofUIN").exclude("introducerUIN").byDefault().register();

		// packetInfo
		mapperFactory.classMap(RegistrationDTO.class, PacketInfo.class)

				// 1photograph
				.field("demographicDTO.applicantDocumentDTO.photoName", "photograph.photographName")

				.field("demographicDTO.applicantDocumentDTO.hasExceptionPhoto", "photograph.hasExceptionPhoto")

				.field("demographicDTO.applicantDocumentDTO.exceptionPhotoName", "photograph.exceptionPhotoName")

				// 2.BiometricData

				// i)FingerPrints
				.fieldMap("biometricDTO.applicantBiometricDTO.fingerprintDetailsDTO",
						"biometericData.fingerprintData.fingerprints")
				.converter("applicantFingerprintListConverter").aToB().add()
				.field("biometricDTO.applicantBiometricDTO.numOfFingerPrintRetry",
						"biometericData.fingerprintData.numRetry")
				.fieldMap("biometricDTO.applicantBiometricDTO.exceptionFingerprintDetailsDTO",
						"biometericData.fingerprintData.exceptionFingerprints")
				.converter("applicantExceptionFingerPrintListConverter").aToB().add()

				// ii) Iris
				.fieldMap("biometricDTO.applicantBiometricDTO.irisDetailsDTO", "biometericData.irisData.iris")
				.converter("applicantIrisListConverter").aToB().add()
				.field("biometricDTO.applicantBiometricDTO.numOfIrisRetry", "biometericData.irisData.numRetry")
				.fieldMap("biometricDTO.applicantBiometricDTO.exceptionIrisDetailsDTO",
						"biometericData.irisData.exceptionIris")
				.converter("applicantExceptionIrisConverter").aToB().add()

				// 3.Document

				.fieldMap("demographicDTO.applicantDocumentDTO.documentDetailsDTO", "document.documentDetails")
				.converter("applicantDocumentListConverter").aToB().add()
				.field("demographicDTO.applicantDocumentDTO.acknowledgeReceiptName", "document.registrationAckCopy")
				// 4.MetaData
				.field("registrationMetaDataDTO.geoLatitudeLoc", "metaData.geoLocation.latitude")
				.field("registrationMetaDataDTO.geoLongitudeLoc", "metaData.geoLocation.longitude")
				.field("preRegistrationId", "metaData.preRegistrationId")
				.field("registrationId", "metaData.registrationId")
				.field("registrationMetaDataDTO.applicationType", "metaData.applicationType")
				.field("registrationMetaDataDTO.applicationCategory", "metaData.applicationCategory")
				// 5.OSI
				// .fieldMap("registrationDTO",
				// "osiData").converter("osiDataConverter").aToB().add()

				// 6.HashSequence
				// After custom object mapping activity completed, retrieved packet info then
				// assign hash sequence

				// 7.Audit
				// .fieldMap("packetDTO.auditDTOs",
				// "audit").converter("auditListConverter").aToB().add()

				// 8.CheckSum
				// After custom object mapping activity completed, retrieved packet info then
				// assign checkSum

				.register();

	}

}
