package io.mosip.registration.service.packet.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.cbeff.BDBInfo;
import io.mosip.registration.dto.cbeff.BIR;
import io.mosip.registration.dto.cbeff.BIRInfo;
import io.mosip.registration.dto.cbeff.jaxbclasses.ProcessedLevelType;
import io.mosip.registration.dto.cbeff.jaxbclasses.PurposeType;
import io.mosip.registration.dto.cbeff.jaxbclasses.SingleAnySubtypeType;
import io.mosip.registration.dto.cbeff.jaxbclasses.SingleType;
import io.mosip.registration.dto.cbeff.jaxbclasses.TestBiometric;
import io.mosip.registration.dto.cbeff.jaxbclasses.TestBiometricType;
import io.mosip.registration.dto.demographic.CBEFFFilePropertiesDTO;
import io.mosip.registration.dto.json.metadata.Audit;
import io.mosip.registration.dto.json.metadata.BiometricSequence;
import io.mosip.registration.dto.json.metadata.DemographicSequence;
import io.mosip.registration.dto.json.metadata.FieldValueArray;
import io.mosip.registration.dto.json.metadata.HashSequence;
import io.mosip.registration.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.external.ZipCreationService;
import io.mosip.registration.service.packet.PacketCreationService;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.util.kernal.cbeff.constant.CbeffConstant;
import io.mosip.registration.util.kernal.cbeff.service.CbeffI;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_CREATION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.constants.RegistrationConstants.REGISTRATION_ID;
import static io.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;

/**
 * Class for creating the Resident Registration
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class PacketCreationServiceImpl implements PacketCreationService {

	@Autowired
	private ZipCreationService zipCreationService;
	private static final Logger LOGGER = AppConfig.getLogger(PacketCreationServiceImpl.class);
	@Autowired
	private CbeffI cbeffI;
	@Autowired
	private JsonValidator jsonValidator;
	
	private Random random = new Random(5000);
	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PacketCreationService#create(io.mosip.
	 * registration.dto.RegistrationDTO)
	 */
	@Override
	public byte[] create(final RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been called");
		try {
			String rid = registrationDTO.getRegistrationId();
			String loggerMessageForCBEFF = "Byte array of %s file generated successfully";

			// Fetch unsync'ed audit logs from DB
			// TODO: Commented below line intentionally. Will be updated
			// registrationDTO.setAuditDTOs(MAPPER_FACADE.mapAsList(auditDAO.getAllUnsyncAudits(),
			// AuditDTO.class));

			// Map object to store the UUID's generated for BIR in CBEFF
			Map<String, String> birUUIDs = new HashMap<>();
			SessionContext.getInstance().getMapObject().put(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME, birUUIDs);

			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();

			byte[] cbeffInBytes = createCBEFFXML(registrationDTO.getBiometricDTO().getApplicantBiometricDTO(),
					RegistrationConstants.INDIVIDUAL, birUUIDs);
			if (cbeffInBytes != null) {
				filesGeneratedForPacket.put(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME, cbeffInBytes);

				registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity()
						.setIndividualBiometrics(Builder.build(CBEFFFilePropertiesDTO.class)
								.with(cbeffProperty -> cbeffProperty.setFormat(RegistrationConstants.CBEFF_FILE_FORMAT))
								.with(cbeffProperty -> cbeffProperty
										.setValue(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME.replace(
												RegistrationConstants.XML_FILE_FORMAT, RegistrationConstants.EMPTY)))
								.with(cbeffProperty -> cbeffProperty.setVersion(1.0)).get());
			}

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME));
			auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME),
					REGISTRATION_ID, rid);

			if (registrationDTO.getBiometricDTO().getIntroducerBiometricDTO() != null) {
				cbeffInBytes = createCBEFFXML(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO(),
						RegistrationConstants.INTRODUCER, birUUIDs);

				if (cbeffInBytes != null) {
					filesGeneratedForPacket.put(RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME, cbeffInBytes);

					registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity()
							.setParentOrGuardianBiometrics(Builder.build(CBEFFFilePropertiesDTO.class)
									.with(cbeffProperty -> cbeffProperty
											.setFormat(RegistrationConstants.CBEFF_FILE_FORMAT))
									.with(cbeffProperty -> cbeffProperty
											.setValue(RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME.replace(
													RegistrationConstants.XML_FILE_FORMAT,
													RegistrationConstants.EMPTY)))
									.with(cbeffProperty -> cbeffProperty.setVersion(1.0)).get());

					LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
							String.format(loggerMessageForCBEFF, RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME));
					auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR,
							String.format(loggerMessageForCBEFF, RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME),
							REGISTRATION_ID, rid);
				}
			}

			if (registrationDTO.getBiometricDTO().getOperatorBiometricDTO() != null) {
				cbeffInBytes = createCBEFFXML(registrationDTO.getBiometricDTO().getOperatorBiometricDTO(),
						RegistrationConstants.OFFICER, birUUIDs);

				if (cbeffInBytes != null) {
					filesGeneratedForPacket.put(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME, cbeffInBytes);

					LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
							String.format(loggerMessageForCBEFF, RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME));
					auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR,
							String.format(loggerMessageForCBEFF, RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME),
							REGISTRATION_ID, rid);
				}
			}

			if (registrationDTO.getBiometricDTO().getSupervisorBiometricDTO() != null) {
				cbeffInBytes = createCBEFFXML(registrationDTO.getBiometricDTO().getSupervisorBiometricDTO(),
						RegistrationConstants.SUPERVISOR, birUUIDs);

				if (cbeffInBytes != null) {
					filesGeneratedForPacket.put(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME, cbeffInBytes);

					LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
							String.format(loggerMessageForCBEFF, RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME));
					auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR,
							String.format(loggerMessageForCBEFF, RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME),
							REGISTRATION_ID, rid);
				}
			}

			// Generating Demographic JSON as byte array
			String idJsonAsString = javaObjectToJsonString(registrationDTO.getDemographicDTO().getDemographicInfoDTO());
			jsonValidator.validateJson(idJsonAsString, "mosip-identity-json-schema.json");
			filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, idJsonAsString.getBytes());

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.DEMOGRPAHIC_JSON_NAME));
			auditFactory.audit(AuditEvent.PACKET_DEMO_JSON_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.DEMOGRPAHIC_JSON_NAME), REGISTRATION_ID,
					rid);

			// Generating Audit JSON as byte array
			filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(MAPPER_FACADE.mapAsList(registrationDTO.getAuditDTOs(), Audit.class))
							.getBytes());

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.AUDIT_JSON_FILE));
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.AUDIT_JSON_FILE), REGISTRATION_ID, rid);

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(
					new BiometricSequence(new LinkedList<String>(), new LinkedList<String>()),
					new DemographicSequence(new LinkedList<String>()));
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME,
					HMACGeneration.generatePacketDTOHash(registrationDTO, filesGeneratedForPacket, hashSequence));

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_DATA_HASH_FILE_NAME));
			auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_DATA_HASH_FILE_NAME),
					REGISTRATION_ID, rid);

			// Generating packet_osi_hash text file as byte array
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME,
					HMACGeneration.generatePacketOSIHash(filesGeneratedForPacket));

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_OSI_HASH_FILE_NAME));
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_OSI_HASH_FILE_NAME),
					REGISTRATION_ID, rid);

			// Generating Packet Meta-Info JSON as byte array
			PacketMetaInfo packetInfo = MAPPER_FACADE.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");

			// Add HashSequence
			packetInfo.getIdentity().setHashSequence(buildHashSequence(hashSequence));

			filesGeneratedForPacket.put(RegistrationConstants.PACKET_META_JSON_NAME,
					javaObjectToJsonString(packetInfo).getBytes());

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_META_JSON_NAME));
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_META_JSON_NAME), REGISTRATION_ID,
					rid);

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been ended");
			auditFactory.audit(AuditEvent.PACKET_INTERNAL_ZIP, Components.PACKET_CREATOR,
					"Packet Internal Zip File created successfully", REGISTRATION_ID, rid);

			return packetZipBytes;
		} catch (JsonProcessingException mosipJsonProcessingException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_JSON_PROCESSING_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_JSON_PROCESSING_EXCEPTION.getErrorMessage());
		} catch (BaseCheckedException baseCheckedException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorMessage(),
					baseCheckedException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_CREATION_EXCEPTION,
					runtimeException.toString());
		}
	}

	
	private List<FieldValueArray> buildHashSequence(final HashSequence hashSequence) {
		List<FieldValueArray> hashSequenceList = new LinkedList<>();
		// Add Sequence of Applicant Biometric
		FieldValueArray fieldValueArray = new FieldValueArray();
		fieldValueArray.setLabel("applicantBiometricSequence");
		fieldValueArray.setValue(hashSequence.getBiometricSequence().getApplicant());
		hashSequenceList.add(fieldValueArray);
		
		// Add Sequence of Introducer Biometric
		fieldValueArray = new FieldValueArray();
		fieldValueArray.setLabel("introducerBiometricSequence");
		fieldValueArray.setValue(hashSequence.getBiometricSequence().getIntroducer());
		hashSequenceList.add(fieldValueArray);
		
		// Add Sequence of Applicant Demographic
		fieldValueArray = new FieldValueArray();
		fieldValueArray.setLabel("applicantDemographicSequence");
		fieldValueArray.setValue(hashSequence.getDemographicSequence().getApplicant());
		hashSequenceList.add(fieldValueArray);
		
		return hashSequenceList;
	}

	private byte[] createCBEFFXML(final BiometricInfoDTO biometricInfoDTO, String personType,
			Map<String, String> birUUIDs) throws RegBaseCheckedException {
		try {
			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Creating CBEFF file as bytes");

			List<BIR> birs = new ArrayList<>();

			if (biometricInfoDTO.getFingerprintDetailsDTO() != null
					&& !biometricInfoDTO.getFingerprintDetailsDTO().isEmpty()) {
				createFingerprintsBIR(personType, biometricInfoDTO.getFingerprintDetailsDTO(), birs, birUUIDs);
			}

			if (biometricInfoDTO.getIrisDetailsDTO() != null && !biometricInfoDTO.getIrisDetailsDTO().isEmpty()) {
				for (IrisDetailsDTO iris : biometricInfoDTO.getIrisDetailsDTO()) {
					TestBiometricType testBiometricType = new TestBiometricType();
					testBiometricType.setXmlns("testschema");
					testBiometricType.setTestBiometric((random.nextInt()%2 == 0) ? TestBiometric.DUPLICATE : TestBiometric.UNIQUE);
					BIR bir = new BIR.BIRBuilder().withBdb(iris.getIris())
							.withTestIris(testBiometricType)
							.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
							.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(CbeffConstant.ISO_FORMAT_OWNER)
									.withFormatType(CbeffConstant.FORMAT_TYPE_IRIS)
									.withQuality((int) Math.round(iris.getQualityScore()))
									.withType(Arrays.asList(SingleType.IRIS))
									.withSubtype(Arrays.asList(iris.getIrisType().equalsIgnoreCase("lefteye")
											? SingleAnySubtypeType.LEFT.value()
											: SingleAnySubtypeType.RIGHT.value()))
									.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.INTERMEDIATE)
									.withCreationDate(new Date()).withIndex(UUID.randomUUID().toString()).build())
							.build();
					birs.add(bir);
					birUUIDs.put(personType.concat(iris.getIrisType()).toLowerCase(), bir.getBdbInfo().getIndex());
				}
			}

			byte[] cbeffXMLInBytes = null;

			if (!birs.isEmpty()) {
				cbeffXMLInBytes = cbeffI.createXML(birs);
			}

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Creating CBEFF file as bytes has been completed");

			return cbeffXMLInBytes;
		} catch (Exception exception) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_PACKET_BIO_CBEFF_GENERATION_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_BIO_CBEFF_GENERATION_ERROR_CODE.getErrorMessage(),
					exception);
		}
	}

	private void createFingerprintsBIR(String personType, List<FingerprintDetailsDTO> fingerprints,
			List<BIR> birs, Map<String, String> birUUIDs) {
		for (FingerprintDetailsDTO fingerprint : fingerprints) {
			if (personType.equals(RegistrationConstants.INDIVIDUAL) && fingerprint.getSegmentedFingerprints() != null
					&& !fingerprint.getSegmentedFingerprints().isEmpty()) {
				for (FingerprintDetailsDTO segmentedFingerprint : fingerprint.getSegmentedFingerprints()) {
					BIR bir = buildFingerprintBIR(segmentedFingerprint, segmentedFingerprint.getFingerPrint());
					birs.add(bir);
					birUUIDs.put(personType.concat(segmentedFingerprint.getFingerType()).toLowerCase(),
							bir.getBdbInfo().getIndex());
				}
			} else {
				BIR bir = buildFingerprintBIR(fingerprint, fingerprint.getFingerPrint());
				birs.add(bir);
				birUUIDs.put(personType.concat(fingerprint.getFingerType()).toLowerCase(), bir.getBdbInfo().getIndex());
			}
		}
	}

	private BIR buildFingerprintBIR(FingerprintDetailsDTO fingerprint, byte[] fingerprintImageInBytes) {
		TestBiometricType testBiometricType = new TestBiometricType();
		testBiometricType.setXmlns("testschema");
		testBiometricType.setTestBiometric((random.nextInt()%2 == 0) ? TestBiometric.DUPLICATE : TestBiometric.UNIQUE);
		return new BIR.BIRBuilder().withBdb(fingerprintImageInBytes)
				.withTestFingerPrint(testBiometricType)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(CbeffConstant.ISO_FORMAT_OWNER)
						.withFormatType(CbeffConstant.FORMAT_TYPE_FINGER)
						.withQuality((int) Math.round(fingerprint.getQualityScore()))
						.withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(getFingerSubType(fingerprint.getFingerType())).withPurpose(PurposeType.ENROLL)
						.withLevel(ProcessedLevelType.INTERMEDIATE).withCreationDate(new Date())
						.withIndex(UUID.randomUUID().toString()).build())
				.build();
	}

	private List<String> getFingerSubType(String fingerType) {
		List<String> fingerSubTypes = new ArrayList<>();

		if (fingerType.startsWith(RegistrationConstants.LEFT.toLowerCase())) {
			fingerSubTypes.add(SingleAnySubtypeType.LEFT.value());
			fingerType = fingerType.replace(RegistrationConstants.LEFT.toLowerCase(), RegistrationConstants.EMPTY);
		} else if (fingerType.startsWith(RegistrationConstants.RIGHT.toLowerCase())) {
			fingerSubTypes.add(SingleAnySubtypeType.RIGHT.value());
			fingerType = fingerType.replace(RegistrationConstants.RIGHT.toLowerCase(), RegistrationConstants.EMPTY);
		}

		if (fingerType.equalsIgnoreCase(RegistrationConstants.THUMB.toLowerCase())) {
			fingerSubTypes.add(SingleAnySubtypeType.THUMB.value());
		} else {
			fingerSubTypes.add(SingleAnySubtypeType
					.fromValue(StringUtils.capitalizeFirstLetter(fingerType).concat("Finger")).value());
		}

		return fingerSubTypes;
	}

}
