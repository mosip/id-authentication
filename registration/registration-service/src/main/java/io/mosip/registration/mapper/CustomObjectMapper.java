package io.mosip.registration.mapper;

import java.time.OffsetDateTime;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.json.demo.Address;
import io.mosip.registration.dto.json.demo.Demographic;
import io.mosip.registration.dto.json.demo.DemographicInfo;
import io.mosip.registration.dto.json.metadata.PacketInfo;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;

/**
 * Class for Orika Object Mapper
 * 
 * @author YASWANTH S
 * @author Balaji Sridharan
 * @since 1.0.0
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
		converterFactory.registerConverter("osiDataConverter", new OSIDataConverter());

		mapperFactory.classMap(DemographicInfoDTO.class, DemographicInfo.class).byDefault().register();

		mapperFactory.classMap(AddressDTO.class, Address.class).byDefault().register();

		mapperFactory.classMap(DemographicDTO.class, Demographic.class).exclude("applicantDocumentDTO")
				.exclude("introducerRID").exclude("introducerUIN").byDefault().register();

		// packetInfo
		mapperFactory.classMap(RegistrationDTO.class, PacketInfo.class)

				// 1photograph
				.field("demographicDTO.applicantDocumentDTO.photographName", "photograph.photographName")

				.field("demographicDTO.applicantDocumentDTO.hasExceptionPhoto", "photograph.hasExceptionPhoto")

				.field("demographicDTO.applicantDocumentDTO.exceptionPhotoName", "photograph.exceptionPhotoName")
				.field("demographicDTO.applicantDocumentDTO.qualityScore", "photograph.qualityScore")
				.field("demographicDTO.applicantDocumentDTO.numRetry", "photograph.numRetry")

				// 2.BiometricData

				// i)FingerPrints
				.fieldMap("biometricDTO.applicantBiometricDTO.fingerprintDetailsDTO",
						"biometericData.fingerprintData.fingerprints")
				.aToB().add()
				.fieldMap("biometricDTO.applicantBiometricDTO.fingerPrintBiometricExceptionDTO",
						"biometericData.fingerprintData.exceptionFingerprints")
				.aToB().add()

				// ii) Iris
				.fieldMap("biometricDTO.applicantBiometricDTO.irisDetailsDTO", "biometericData.irisData.iris").aToB()
				.add().field("biometricDTO.applicantBiometricDTO.numOfIrisRetry", "biometericData.irisData.numRetry")
				.fieldMap("biometricDTO.applicantBiometricDTO.irisBiometricExceptionDTO",
						"biometericData.irisData.exceptionIris")
				.aToB().add()

				// 3.Document
				.fieldMap("demographicDTO.applicantDocumentDTO.documentDetailsDTO", "document.documentDetails").add()
				.field("demographicDTO.applicantDocumentDTO.acknowledgeReceiptName", "document.registrationAckCopy")

				// 4.MetaData
				.field("registrationMetaDataDTO.geoLatitudeLoc", "metaData.geoLocation.latitude")
				.field("registrationMetaDataDTO.geoLongitudeLoc", "metaData.geoLocation.longitude")
				.field("preRegistrationId", "metaData.preRegistrationId")
				.field("registrationId", "metaData.registrationId")
				.field("registrationMetaDataDTO.applicationType", "metaData.applicationType")
				.field("registrationMetaDataDTO.registrationCategory", "metaData.registrationCategory").register();

	}

}
