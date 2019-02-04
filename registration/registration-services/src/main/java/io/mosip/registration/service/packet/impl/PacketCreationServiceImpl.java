package io.mosip.registration.service.packet.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
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
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dao.AuditLogControlDAO;
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
	private static Random random = new Random(5000);
	@Autowired
	private AuditFactory auditFactory;
	@Autowired
	private AuditLogControlDAO auditLogControlDAO;
	@Autowired
	private AuditDAO auditDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PacketCreationService#create(io.mosip.
	 * registration.dto.RegistrationDTO)
	 */
	@Override
	public byte[] create(final RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been called");
		try {
			String rid = registrationDTO.getRegistrationId();
			String loggerMessageForCBEFF = "Byte array of %s file generated successfully";

			// Map object to store the UUID's generated for BIR in CBEFF
			Map<String, String> birUUIDs = new HashMap<>();
			SessionContext.getSessionContext().getMapObject().put(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME, birUUIDs);

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

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
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

					LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
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

					LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
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

					LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
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

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.DEMOGRPAHIC_JSON_NAME));
			auditFactory.audit(AuditEvent.PACKET_DEMO_JSON_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.DEMOGRPAHIC_JSON_NAME), REGISTRATION_ID,
					rid);

			// Generating Audit JSON as byte array
			// Fetch unsync'ed audit logs from DB
			List<Audit> audits = auditDAO.getAudits(auditLogControlDAO.getLatestRegistrationAuditDates());

			registrationDTO.setAuditLogStartTime(Timestamp.valueOf(audits.get(0).getCreatedAt()));
			registrationDTO.setAuditLogEndTime(Timestamp.valueOf(audits.get(audits.size() - 1).getCreatedAt()));
			filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(audits).getBytes());

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.AUDIT_JSON_FILE));
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.AUDIT_JSON_FILE), REGISTRATION_ID, rid);

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(new BiometricSequence(new LinkedList<>(), new LinkedList<>()),
					new DemographicSequence(new LinkedList<>()), new LinkedList<>());
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME,
					HMACGeneration.generatePacketDTOHash(registrationDTO, filesGeneratedForPacket, hashSequence));

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_DATA_HASH_FILE_NAME));
			auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_DATA_HASH_FILE_NAME),
					REGISTRATION_ID, rid);

			// Generating packet_osi_hash text file as byte array
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME,
					HMACGeneration.generatePacketOSIHash(filesGeneratedForPacket, hashSequence.getOsiDataHashSequence()));

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_OSI_HASH_FILE_NAME));
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_OSI_HASH_FILE_NAME),
					REGISTRATION_ID, rid);

			// Generating Packet Meta-Info JSON as byte array
			PacketMetaInfo packetInfo = MAPPER_FACADE.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");

			// Add HashSequence
			packetInfo.getIdentity().setHashSequence(buildHashSequence(hashSequence));

			// Add HashSequence for packet_osi_data
			packetInfo.getIdentity().setHashSequence2(hashSequence.getOsiDataHashSequence());

			filesGeneratedForPacket.put(RegistrationConstants.PACKET_META_JSON_NAME,
					javaObjectToJsonString(packetInfo).getBytes());

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_META_JSON_NAME));
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_META_JSON_NAME), REGISTRATION_ID,
					rid);

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been ended");
			auditFactory.audit(AuditEvent.PACKET_INTERNAL_ZIP, Components.PACKET_CREATOR,
					"Packet Internal Zip File created successfully", REGISTRATION_ID, rid);

			return packetZipBytes;
		} catch (JsonProcessingException mosipJsonProcessingException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_JSON_PROCESSING_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_JSON_PROCESSING_EXCEPTION.getErrorMessage());
		} catch (JsonValidationProcessingException | JsonIOException | JsonSchemaIOException
				| FileIOException jsonValidationException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorMessage(),
					jsonValidationException);
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
			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Creating CBEFF file as bytes");

			List<BIR> birs = new ArrayList<>();

			boolean onlyUniqueRequiredInCBEFF = RegistrationConstants.GLOBAL_CONFIG_TRUE_VALUE
					.equalsIgnoreCase(String.valueOf(ApplicationContext.getInstance().getApplicationMap()
							.get(RegistrationConstants.CBEFF_ONLY_UNIQUE_TAGS)));

			if (biometricInfoDTO.getFingerprintDetailsDTO() != null
					&& !biometricInfoDTO.getFingerprintDetailsDTO().isEmpty()) {
				createFingerprintsBIR(onlyUniqueRequiredInCBEFF, personType,
						biometricInfoDTO.getFingerprintDetailsDTO(), birs, birUUIDs);
			}

			if (biometricInfoDTO.getIrisDetailsDTO() != null && !biometricInfoDTO.getIrisDetailsDTO().isEmpty()) {
				for (IrisDetailsDTO iris : biometricInfoDTO.getIrisDetailsDTO()) {

					BIR bir = buildBIR(onlyUniqueRequiredInCBEFF, iris.getIris(), CbeffConstant.ISO_FORMAT_OWNER,
							CbeffConstant.FORMAT_TYPE_IRIS, (int) Math.round(iris.getQualityScore()),
							Arrays.asList(SingleType.IRIS),
							Arrays.asList(
									iris.getIrisType().equalsIgnoreCase("lefteye") ? SingleAnySubtypeType.LEFT.value()
											: SingleAnySubtypeType.RIGHT.value()));

					birs.add(bir);
					birUUIDs.put(personType.concat(iris.getIrisType()).toLowerCase(), bir.getBdbInfo().getIndex());
				}
			}

			byte[] cbeffXMLInBytes = null;

			if (!birs.isEmpty()) {
				cbeffXMLInBytes = cbeffI.createXML(birs);
			}

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Creating CBEFF file as bytes has been completed");

			return cbeffXMLInBytes;
		} catch (Exception exception) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_PACKET_BIO_CBEFF_GENERATION_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_BIO_CBEFF_GENERATION_ERROR_CODE.getErrorMessage(),
					exception);
		}
	}

	private void createFingerprintsBIR(boolean onlyUniqueRequiredInCBEFF, String personType,
			List<FingerprintDetailsDTO> fingerprints, List<BIR> birs, Map<String, String> birUUIDs) {
		for (FingerprintDetailsDTO fingerprint : fingerprints) {
			if (personType.equals(RegistrationConstants.INDIVIDUAL) && fingerprint.getSegmentedFingerprints() != null
					&& !fingerprint.getSegmentedFingerprints().isEmpty()) {
				for (FingerprintDetailsDTO segmentedFingerprint : fingerprint.getSegmentedFingerprints()) {
					BIR bir = buildFingerprintBIR(onlyUniqueRequiredInCBEFF, segmentedFingerprint,
							segmentedFingerprint.getFingerPrint());
					birs.add(bir);
					birUUIDs.put(personType.concat(segmentedFingerprint.getFingerType()).toLowerCase(),
							bir.getBdbInfo().getIndex());
				}
			} else {
				BIR bir = buildFingerprintBIR(onlyUniqueRequiredInCBEFF, fingerprint, fingerprint.getFingerPrint());
				birs.add(bir);
				birUUIDs.put(personType.concat(fingerprint.getFingerType()).toLowerCase(), bir.getBdbInfo().getIndex());
			}
		}
	}

	private BIR buildFingerprintBIR(boolean onlyUniqueRequiredInCBEFF, FingerprintDetailsDTO fingerprint,
			byte[] fingerprintImageInBytes) {
		return buildBIR(onlyUniqueRequiredInCBEFF, fingerprintImageInBytes, CbeffConstant.ISO_FORMAT_OWNER,
				CbeffConstant.FORMAT_TYPE_FINGER, (int) Math.round(fingerprint.getQualityScore()),
				Arrays.asList(SingleType.FINGER), getFingerSubType(fingerprint.getFingerType()));
	}

	private BIR buildBIR(boolean onlyUniqueRequiredInCBEFF, byte[] bdb, long isoFormatOwner, long formatType,
			int qualityScore, List<SingleType> type, List<String> subType) {
		TestBiometricType testBiometricType = new TestBiometricType();
		testBiometricType.setXmlns("testschema");
		if (onlyUniqueRequiredInCBEFF) {
			testBiometricType.setTestBiometric(TestBiometric.UNIQUE);
		} else {
			testBiometricType
					.setTestBiometric((random.nextInt() % 2 == 0) ? TestBiometric.DUPLICATE : TestBiometric.UNIQUE);
		}

		return new BIR.BIRBuilder().withBdb(bdb).withTestFingerPrint(testBiometricType)
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(isoFormatOwner).withFormatType(formatType)
						.withQuality(qualityScore).withType(type).withSubtype(subType).withPurpose(PurposeType.ENROLL)
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
