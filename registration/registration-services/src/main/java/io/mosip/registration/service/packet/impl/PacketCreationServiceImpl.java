package io.mosip.registration.service.packet.impl;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_CREATION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;

import java.io.InputStream;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIRVersion;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.QualityType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dao.DocumentTypeDAO;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.BaseDTO;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.dto.json.metadata.BiometricSequence;
import io.mosip.registration.dto.json.metadata.DemographicSequence;
import io.mosip.registration.dto.json.metadata.FieldValue;
import io.mosip.registration.dto.json.metadata.FieldValueArray;
import io.mosip.registration.dto.json.metadata.HashSequence;
import io.mosip.registration.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.entity.DocumentType;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.external.ZipCreationService;
import io.mosip.registration.service.packet.PacketCreationService;
import io.mosip.registration.util.advice.AuthenticationAdvice;
import io.mosip.registration.util.advice.PreAuthorizeUserId;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.validator.RegIdObjectValidator;

/**
 * Implementation class of {@link PacketCreationService} for creating the
 * Resident Registration as zip file
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class PacketCreationServiceImpl extends BaseService implements PacketCreationService {

	@Autowired
	private ZipCreationService zipCreationService;
	private static final Logger LOGGER = AppConfig.getLogger(PacketCreationServiceImpl.class);
	@Autowired
	private CbeffImpl cbeffI;
	@Autowired
	private RegIdObjectValidator idObjectValidator;
	private static SecureRandom random = new SecureRandom(String.valueOf(5000).getBytes());
	@Autowired
	private AuditManagerService auditFactory;
	@Autowired
	private AuditLogControlDAO auditLogControlDAO;
	@Autowired
	private AuditDAO auditDAO;
	@Autowired
	private MachineMappingDAO machineMappingDAO;
	@Autowired
	private DocumentTypeDAO documentTypeDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PacketCreationService#create(io.mosip.
	 * registration.dto.RegistrationDTO)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@PreAuthorizeUserId(roles= {AuthenticationAdvice.OFFICER_ROLE,AuthenticationAdvice.SUPERVISOR_ROLE, AuthenticationAdvice.ADMIN_ROLE})
	public byte[] create(final RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been called");
		try {

			validateContexts();
			validateRegistrationDTO(registrationDTO);
			
			String rid = registrationDTO.getRegistrationId();
			String loggerMessageForCBEFF = "Byte array of %s file generated successfully";

			// validate the input against the schema, mandatory, pattern and master data. if
			// any error then stop the rest of the process
			// and display error message to the user.
			idObjectValidator.validateIdObject(registrationDTO.getDemographicDTO().getDemographicInfoDTO(),
					registrationDTO.getRegistrationMetaDataDTO().getRegistrationCategory());
			
			// Map object to store the UUID's generated for BIR in CBEFF
			Map<String, String> birUUIDs = new HashMap<>();
			SessionContext.map().put(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME, birUUIDs);

			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();

			byte[] cbeffInBytes = createCBEFFXML(registrationDTO, RegistrationConstants.INDIVIDUAL, birUUIDs);
			if (cbeffInBytes != null) {
				filesGeneratedForPacket.put(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME, cbeffInBytes);

				LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
						String.format(loggerMessageForCBEFF, RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME));
				auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR, rid,
						AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());
			}

			cbeffInBytes = registrationDTO.getBiometricDTO().getApplicantBiometricDTO().getExceptionFace().getFace();
			if (cbeffInBytes != null) {
				if (registrationDTO.isUpdateUINChild()) {
					filesGeneratedForPacket.put(RegistrationConstants.PARENT
							.concat(RegistrationConstants.PACKET_INTRODUCER_EXCEP_PHOTO_NAME), cbeffInBytes);
				} else {
					filesGeneratedForPacket.put(RegistrationConstants.INDIVIDUAL
							.concat(RegistrationConstants.PACKET_INTRODUCER_EXCEP_PHOTO_NAME), cbeffInBytes);
				}
			}

			introducerCbeff(registrationDTO, rid, loggerMessageForCBEFF, birUUIDs, filesGeneratedForPacket);

			if (registrationDTO.getBiometricDTO().getOperatorBiometricDTO() != null) {
				cbeffInBytes = createCBEFFXML(registrationDTO, RegistrationConstants.OFFICER, birUUIDs);

				if (cbeffInBytes != null) {
					filesGeneratedForPacket.put(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME, cbeffInBytes);

					LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
							String.format(loggerMessageForCBEFF, RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME));
					auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR, rid,
							AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());
				}
			}

			if (registrationDTO.getBiometricDTO().getSupervisorBiometricDTO() != null) {
				cbeffInBytes = createCBEFFXML(registrationDTO, RegistrationConstants.SUPERVISOR, birUUIDs);

				if (cbeffInBytes != null) {
					filesGeneratedForPacket.put(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME, cbeffInBytes);

					LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
							String.format(loggerMessageForCBEFF, RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME));
					auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR, rid,
							AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());
				}
			}

			//Convert the Document Name to Codes
			setDocumentCodes(registrationDTO);
			
			// Generating Demographic JSON as byte array
			filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME,
					javaObjectToJsonString(registrationDTO.getDemographicDTO().getDemographicInfoDTO()).getBytes());

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.DEMOGRPAHIC_JSON_NAME));
			auditFactory.audit(AuditEvent.PACKET_DEMO_JSON_CREATED, Components.PACKET_CREATOR, rid,
					AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Generating Audit JSON as byte array
			// Fetch unsync'ed audit logs from DB
			List<Audit> audits = auditDAO.getAudits(auditLogControlDAO.getLatestRegistrationAuditDates());

			registrationDTO.setAuditLogStartTime(Timestamp.valueOf(audits.get(0).getCreatedAt()));
			registrationDTO.setAuditLogEndTime(Timestamp.valueOf(audits.get(audits.size() - 1).getCreatedAt()));
			filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(audits).getBytes());

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.AUDIT_JSON_FILE));
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR, rid,
					AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(new BiometricSequence(new LinkedList<>(), new LinkedList<>()),
					new DemographicSequence(new LinkedList<>()), new LinkedList<>());
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME,
					HMACGeneration.generatePacketDTOHash(registrationDTO, filesGeneratedForPacket, hashSequence));

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_DATA_HASH_FILE_NAME));
			auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR, rid,
					AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Generating packet_osi_hash text file as byte array
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME, HMACGeneration
					.generatePacketOSIHash(filesGeneratedForPacket, hashSequence.getOsiDataHashSequence()));

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_OSI_HASH_FILE_NAME));
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR, rid,
					AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Generating Packet Meta-Info JSON as byte array
			PacketMetaInfo packetInfo = MAPPER_FACADE.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");

			// Set Registered Device
			packetInfo.getIdentity().setCapturedRegisteredDevices(getRegisteredDevices());

			// Set Registered Device
			packetInfo.getIdentity().setCapturedNonRegisteredDevices(null);

			// Add HashSequence
			packetInfo.getIdentity().setHashSequence1(buildHashSequence(hashSequence));

			// Add HashSequence for packet_osi_data
			packetInfo.getIdentity()
					.setHashSequence2((List<FieldValueArray>) Builder.build(ArrayList.class)
							.with(values -> values.add(Builder.build(FieldValueArray.class)
									.with(field -> field.setLabel("otherFiles"))
									.with(field -> field.setValue(hashSequence.getOsiDataHashSequence())).get()))
							.get());

			filesGeneratedForPacket.put(RegistrationConstants.PACKET_META_JSON_NAME,
					javaObjectToJsonString(packetInfo).getBytes());

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format(loggerMessageForCBEFF, RegistrationConstants.PACKET_META_JSON_NAME));
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR, rid,
					AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);

			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been ended");
			auditFactory.audit(AuditEvent.PACKET_INTERNAL_ZIP, Components.PACKET_CREATOR, rid,
					AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			return packetZipBytes;
		} catch (JsonProcessingException mosipJsonProcessingException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_JSON_PROCESSING_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_JSON_PROCESSING_EXCEPTION.getErrorMessage());
		} catch (BaseCheckedException baseCheckedException) {
			throw new RegBaseCheckedException(baseCheckedException.getErrorCode(), baseCheckedException.getErrorText(),
					baseCheckedException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.REG_PACKET_CREATION_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_CREATION_EXCEPTION.getErrorMessage(), runtimeException);
		} finally {
			SessionContext.map().remove(RegistrationConstants.CBEFF_BIR_UUIDS_MAP_NAME);
		}
	}

	private void introducerCbeff(final RegistrationDTO registrationDTO, String rid, String loggerMessageForCBEFF,
			Map<String, String> birUUIDs, Map<String, byte[]> filesGeneratedForPacket) throws RegBaseCheckedException {
		byte[] cbeffInBytes;
		if (registrationDTO.getBiometricDTO().getIntroducerBiometricDTO() != null) {
			cbeffInBytes = createCBEFFXML(registrationDTO, RegistrationConstants.INTRODUCER, birUUIDs);

			if (cbeffInBytes != null) {

				filesGeneratedForPacket.put(RegistrationConstants.AUTHENTICATION_BIO_CBEFF_FILE_NAME, cbeffInBytes);

				LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, String.format(loggerMessageForCBEFF,
						RegistrationConstants.AUTHENTICATION_BIO_CBEFF_FILE_NAME));
				auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR, rid,
						AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());
			}

			cbeffInBytes = registrationDTO.getBiometricDTO().getIntroducerBiometricDTO().getExceptionFace()
					.getFace();
			if (cbeffInBytes != null) {
				if (registrationDTO.isUpdateUINNonBiometric()
						&& !registrationDTO.isUpdateUINChild()) {
					filesGeneratedForPacket.put(RegistrationConstants.INDIVIDUAL
							.concat(RegistrationConstants.PACKET_INTRODUCER_EXCEP_PHOTO_NAME), cbeffInBytes);
				} else {
					filesGeneratedForPacket.put(RegistrationConstants.PARENT
							.concat(RegistrationConstants.PACKET_INTRODUCER_EXCEP_PHOTO_NAME), cbeffInBytes);
				}
			}
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

	private byte[] createCBEFFXML(final RegistrationDTO registrationDTO, String personType,
			Map<String, String> birUUIDs) throws RegBaseCheckedException {
		try {
			LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Creating CBEFF file as bytes");

			List<BIR> birs = new ArrayList<>();

			BiometricInfoDTO biometricInfoDTO = getBiometricDataByActor(registrationDTO.getBiometricDTO(), personType);

			if (biometricInfoDTO != null) {
				// Add Fingerprint
				createFingerprintsBIR(personType, biometricInfoDTO.getFingerprintDetailsDTO(), birs, birUUIDs);

				// Add Iris
				if (isListNotEmpty(biometricInfoDTO.getIrisDetailsDTO())) {
					for (IrisDetailsDTO iris : biometricInfoDTO.getIrisDetailsDTO()) {
						BIR bir = buildBIR(iris.getIrisIso(), CbeffConstant.FORMAT_TYPE_IRIS,
								(int) Math.round(iris.getQualityScore()), Arrays.asList(SingleType.IRIS),
								Arrays.asList(iris.getIrisType().toLowerCase().contains("left")
										? SingleAnySubtypeType.LEFT.value()
										: SingleAnySubtypeType.RIGHT.value()));

						birs.add(bir);
						birUUIDs.put(personType.concat(iris.getIrisType()).toLowerCase(), bir.getBdbInfo().getIndex());
					}
				}

				// Add Face
				byte[] faceBytes = biometricInfoDTO.getFace().getFaceISO() != null
						? biometricInfoDTO.getFace().getFaceISO()
						: biometricInfoDTO.getFace().getFace();
				createFaceBIR(personType, birUUIDs, birs, faceBytes,
						(int) Math.round(biometricInfoDTO.getFace().getQualityScore()),
						RegistrationConstants.VALIDATION_TYPE_FACE);

			}

			byte[] cbeffXMLInBytes = null;

			if (!birs.isEmpty()) {
				InputStream file = this.getClass().getResourceAsStream(RegistrationConstants.CBEFF_SCHEMA_FILE_PATH);
				byte[] bytesArray = new byte[(int) file.available()];
				file.read(bytesArray);
				file.close();
				cbeffXMLInBytes = cbeffI.createXML(birs, bytesArray);
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

	private void createFaceBIR(String personType, Map<String, String> birUUIDs, List<BIR> birs, byte[] image,
			int qualityScore, String imageType) {
		if (image != null) {
			BIR bir = buildBIR(image, CbeffConstant.FORMAT_TYPE_FACE, qualityScore,
					Arrays.asList(SingleType.FACE), Arrays.asList());

			birs.add(bir);
			birUUIDs.put(personType.concat(imageType).toLowerCase(), bir.getBdbInfo().getIndex());
		}
	}

	private BiometricInfoDTO getBiometricDataByActor(BiometricDTO biometricDTO, String actorType) {
		BiometricInfoDTO biometricInfoDTO;

		switch (actorType) {
		case RegistrationConstants.INDIVIDUAL:
			biometricInfoDTO = biometricDTO.getApplicantBiometricDTO();
			break;
		case RegistrationConstants.INTRODUCER:
			biometricInfoDTO = biometricDTO.getIntroducerBiometricDTO();
			break;
		case RegistrationConstants.OFFICER:
			biometricInfoDTO = biometricDTO.getOperatorBiometricDTO();
			break;
		case RegistrationConstants.SUPERVISOR:
			biometricInfoDTO = biometricDTO.getSupervisorBiometricDTO();
			break;
		default:
			biometricInfoDTO = null;
		}

		return biometricInfoDTO;
	}

	private void createFingerprintsBIR(String personType, List<FingerprintDetailsDTO> fingerprints, List<BIR> birs,
			Map<String, String> birUUIDs) {
		if (isListNotEmpty(fingerprints)) {
			for (FingerprintDetailsDTO fingerprint : fingerprints) {
				if ((personType.equals(RegistrationConstants.INDIVIDUAL)
						|| personType.equals(RegistrationConstants.INTRODUCER))
						&& isListNotEmpty(fingerprint.getSegmentedFingerprints())) {
					for (FingerprintDetailsDTO segmentedFingerprint : fingerprint.getSegmentedFingerprints()) {
						BIR bir = buildFingerprintBIR(segmentedFingerprint, segmentedFingerprint.getFingerPrintISOImage());
						birs.add(bir);
						birUUIDs.put(personType.concat(segmentedFingerprint.getFingerType()).toLowerCase(),
								bir.getBdbInfo().getIndex());
					}
				} else {
					BIR bir = buildFingerprintBIR(fingerprint, fingerprint.getFingerPrintISOImage());
					birs.add(bir);
					birUUIDs.put(personType.concat(fingerprint.getFingerType()).toLowerCase(),
							bir.getBdbInfo().getIndex());
				}
			}
		}
	}

	private BIR buildFingerprintBIR(FingerprintDetailsDTO fingerprint, byte[] fingerprintImageInBytes) {
		return buildBIR(fingerprintImageInBytes, CbeffConstant.FORMAT_TYPE_FINGER,
				(int) Math.round(fingerprint.getQualityScore()), Arrays.asList(SingleType.FINGER),
				getFingerSubType(fingerprint.getFingerType()));
	}

	private BIR buildBIR(byte[] bdb, long formatType, int qualityScore, List<SingleType> type, List<String> subType) {

		// Format
		RegistryIDType birFormat = new RegistryIDType();
		birFormat.setOrganization(getValueFromApplicationContext(RegistrationConstants.CBEFF_FORMAT_ORG,
				RegistrationConstants.CBEFF_DEFAULT_FORMAT_ORG));
		birFormat.setType(String.valueOf(formatType));

		// Algorithm
		RegistryIDType birAlgorithm = new RegistryIDType();
		birAlgorithm.setOrganization(getValueFromApplicationContext(RegistrationConstants.CBEFF_ALG_ORG,
				RegistrationConstants.CBEFF_DEFAULT_ALG_ORG));
		birAlgorithm.setType(getValueFromApplicationContext(RegistrationConstants.CBEFF_ALG_TYPE,
				RegistrationConstants.CBEFF_DEFAULT_ALG_TYPE));

		// Quality Type
		QualityType qualityType = new QualityType();
		qualityType.setAlgorithm(birAlgorithm);
		qualityType.setScore((long) qualityScore);

		return new BIR.BIRBuilder().withBdb(bdb).withElement(Arrays.asList(getCBEFFTestTag(type.get(0))))
				.withVersion(new BIRVersion.BIRVersionBuilder().withMajor(1).withMinor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMajor(1).withMinor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(birFormat).withQuality(qualityType).withType(type)
						.withSubtype(subType).withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now(ZoneOffset.UTC)).withIndex(UUID.randomUUID().toString())
						.build())
				.build();
	}

	private String getValueFromApplicationContext(Object key, String defaultValue) {
		Object valueFromAppContext = ApplicationContext.map().get(key);
		return valueFromAppContext == null ? defaultValue : String.valueOf(valueFromAppContext);
	}

	private JAXBElement<String> getCBEFFTestTag(SingleType biometricType) {
		String testTagType = null;
		String testTagElementName = null;

		if (RegistrationConstants.GLOBAL_CONFIG_TRUE_VALUE
				.equalsIgnoreCase(String.valueOf(ApplicationContext.map().get(RegistrationConstants.CBEFF_UNQ_TAG)))) {
			testTagType = "Unique";
		} else {
			testTagType = random.nextInt() % 2 == 0 ? "Duplicate" : "Unique";
		}

		if (biometricType.equals(SingleType.FINGER)) {
			testTagElementName = "TestFinger";
		} else if (biometricType.equals(SingleType.IRIS)) {
			testTagElementName = "TestIris";
		} else if (biometricType.equals(SingleType.FACE)) {
			testTagElementName = "TestFace";
		}

		return new JAXBElement<>(new QName("testschema", testTagElementName), String.class, testTagType);
	}

	private List<String> getFingerSubType(String fingerType) {
		List<String> fingerSubTypes = new ArrayList<>();
		fingerType=fingerType.toLowerCase();
		if (fingerType.startsWith(RegistrationConstants.LEFT.toLowerCase())) {
			fingerSubTypes.add(SingleAnySubtypeType.LEFT.value());
			fingerType = fingerType.replace(RegistrationConstants.LEFT.toLowerCase(), RegistrationConstants.EMPTY);
		} else if (fingerType.startsWith(RegistrationConstants.RIGHT.toLowerCase())) {
			fingerSubTypes.add(SingleAnySubtypeType.RIGHT.value());
			fingerType = fingerType.replace(RegistrationConstants.RIGHT.toLowerCase(), RegistrationConstants.EMPTY);
		}
		fingerType=fingerType.trim();
		if (fingerType.equalsIgnoreCase(RegistrationConstants.THUMB.toLowerCase())) {
			fingerSubTypes.add(SingleAnySubtypeType.THUMB.value());
		} else {
			fingerSubTypes.add(SingleAnySubtypeType
					.fromValue(StringUtils.capitalizeFirstLetter(fingerType).concat("Finger")).value());
		}

		return fingerSubTypes;
	}

	private List<FieldValue> getRegisteredDevices() {
		List<RegDeviceMaster> registeredDevices = machineMappingDAO
				.getDevicesMappedToRegCenter(ApplicationContext.applicationLanguage());

		List<FieldValue> capturedRegisteredDevices = new ArrayList<>();
		FieldValue capturedRegisteredDevice;

		if (registeredDevices != null) {
			for (RegDeviceMaster registeredDevice : registeredDevices) {
				capturedRegisteredDevice = new FieldValue();
				capturedRegisteredDevice.setLabel(registeredDevice.getRegDeviceSpec().getRegDeviceType().getName());
				capturedRegisteredDevice.setValue(registeredDevice.getRegMachineSpecId().getId());
				capturedRegisteredDevices.add(capturedRegisteredDevice);
			}
		}

		return capturedRegisteredDevices;
	}

	private boolean isListNotEmpty(List<? extends BaseDTO> listToValidate) {
		return !(listToValidate == null || listToValidate.isEmpty());
	}

	private void validateRegistrationDTO(RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		if (registrationDTO == null) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_NULL_PACKET_EXCEPTION);
		} else {

			if (isStringEmpty(registrationDTO.getRegistrationId())) {
				throwRegBaseCheckedException(RegistrationExceptionConstants.REG_INVALID_RID_EXCEPTION);
			}

			validateRegistrationMetaData(registrationDTO.getRegistrationMetaDataDTO());

			boolean isFingerprintOrIrisCaptureEnabled = isDeviceEnabled(RegistrationConstants.FINGERPRINT_DISABLE_FLAG)
					|| isDeviceEnabled(RegistrationConstants.IRIS_DISABLE_FLAG);

			boolean isFaceCaptureEnabled = isDeviceEnabled(RegistrationConstants.FACE_DISABLE_FLAG);
			Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();

			if (isFingerprintOrIrisCaptureEnabled || isFaceCaptureEnabled) {
				biometricsCapturedFlags = validateBiometrics(registrationDTO, isFingerprintOrIrisCaptureEnabled,
						isFaceCaptureEnabled);
			}

			validateOSIData(registrationDTO, biometricsCapturedFlags);
		}
	}

	private void validateRegistrationMetaData(RegistrationMetaDataDTO metaData) throws RegBaseCheckedException {
		if (metaData == null) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_METADATA_NULL_EXCEPTION);
		} else {
			if (isStringEmpty(metaData.getRegistrationCategory())) {
				throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_INVALID_REG_CATEGORY_EXCEPTION);
			}

			if (isStringEmpty(metaData.getCenterId())) {
				throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_INVALID_CENTER_ID_EXCEPTION);
			}

			if (isStringEmpty(metaData.getMachineId())) {
				throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_INVALID_MACHINE_ID_EXCEPTION);
			}

			if (isStringEmpty(metaData.getConsentOfApplicant())) {
				throwRegBaseCheckedException(
						RegistrationExceptionConstants.REG_PKT_INVALID_APPLICANT_CONSENT_EXCEPTION);
			}
		}
	}

	private void validateOSIData(RegistrationDTO registrationDTO, Map<String, Boolean> biometricsCapturedFlags)
			throws RegBaseCheckedException {
		OSIDataDTO osiData = registrationDTO.getOsiDataDTO();

		if (osiData == null) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_OSIDATA_NULL_EXCEPTION);
		} else {
			if (isStringEmpty(osiData.getOperatorID())) {
				throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_OFFICER_ID_NULL_EXCEPTION);
			}

			if (isOperatorAuthNotProvided(biometricsCapturedFlags, osiData)) {
				throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_OFFICER_AUTH_INVALID_EXCEPTION);
			}

			if (biometricsCapturedFlags.getOrDefault(RegistrationConstants.IS_SUPERVISOR_AUTH_REQUIRED, false)) {
				if (isStringEmpty(osiData.getSupervisorID())) {
					throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_SUPERVISOR_ID_NULL_EXCEPTION);
				}

				if (!(osiData.isSuperviorAuthenticatedByPassword() || osiData.isSuperviorAuthenticatedByPIN()
						|| biometricsCapturedFlags.getOrDefault(RegistrationConstants.IS_SUPERVISOR_BIOMETRICS_CAPTURED,
								false))) {
					throwRegBaseCheckedException(
							RegistrationExceptionConstants.REG_PKT_SUPERVISOR_AUTH_INVALID_EXCEPTION);
				}
			}
		}
	}

	private boolean isOperatorAuthNotProvided(Map<String, Boolean> biometricsCapturedFlags, OSIDataDTO osiData) {
		return !(osiData.isOperatorAuthenticatedByPassword() || osiData.isOperatorAuthenticatedByPIN()
				|| biometricsCapturedFlags.getOrDefault(RegistrationConstants.IS_OFFICER_BIOMETRICS_CAPTURED,
						false));
	}

	private Map<String, Boolean> validateBiometrics(RegistrationDTO registration,
			boolean isFingerprintOrIrisCaptureEnabled, boolean isFaceCaptureEnabled) throws RegBaseCheckedException {

		Map<String, Boolean> biometricsCapturedFlags = new WeakHashMap<>();

		if (registration.getBiometricDTO() == null) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_BIOMETRICS_NULL_EXCEPTION);
		}

		BiometricInfoDTO applicantBiometrics = registration.getBiometricDTO().getApplicantBiometricDTO();
		BiometricInfoDTO authenticationBiometrics = registration.getBiometricDTO().getIntroducerBiometricDTO();

		boolean hasApplicantBiometricException = false;
		boolean hasAuthenticationBiometricException = false;
		if (isFingerprintOrIrisCaptureEnabled) {
			hasApplicantBiometricException = applicantBiometrics != null
					&& isListNotEmpty(applicantBiometrics.getBiometricExceptionDTO());

			if (authenticationBiometrics != null) {
				hasAuthenticationBiometricException = isListNotEmpty(
						authenticationBiometrics.getBiometricExceptionDTO());
			}
		}

		validateFace(isFaceCaptureEnabled, applicantBiometrics, authenticationBiometrics,
				hasApplicantBiometricException, hasAuthenticationBiometricException);

		biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_AUTH_REQUIRED,
				hasApplicantBiometricException || hasAuthenticationBiometricException);
		biometricsCapturedFlags.put(RegistrationConstants.IS_OFFICER_BIOMETRICS_CAPTURED,
				isBiometricCaptured(registration.getBiometricDTO().getOperatorBiometricDTO()));
		biometricsCapturedFlags.put(RegistrationConstants.IS_SUPERVISOR_BIOMETRICS_CAPTURED,
				isBiometricCaptured(registration.getBiometricDTO().getSupervisorBiometricDTO()));

		return biometricsCapturedFlags;
	}

	private void validateFace(boolean isFaceCaptureEnabled, BiometricInfoDTO applicantBiometrics,
			BiometricInfoDTO authenticationBiometrics, boolean hasApplicantBiometricException,
			boolean hasAuthenticationBiometricException) throws RegBaseCheckedException {
		if (isFaceCaptureEnabled) {
			if (validateFace(applicantBiometrics.getFace()) && authenticationBiometrics != null
					&& validateFace(authenticationBiometrics.getFace())) {
				throwRegBaseCheckedException(
						RegistrationExceptionConstants.REG_PKT_APPLICANT_BIO_INVALID_FACE_EXCEPTION);
			}

			if (hasApplicantBiometricException && validateFace(applicantBiometrics.getExceptionFace())) {
				throwRegBaseCheckedException(
						RegistrationExceptionConstants.REG_PKT_APPLICANT_BIO_INVALID_EXCEPTION_FACE_EXCEPTION);
			}

			if (hasAuthenticationBiometricException && authenticationBiometrics != null
					&& validateFace(authenticationBiometrics.getExceptionFace())) {
				throwRegBaseCheckedException(
						RegistrationExceptionConstants.REG_PKT_AUTHENTICATION_BIO_INVALID_EXCEPTION_FACE_EXCEPTION);
			}
		}
	}

	private boolean validateFace(FaceDetailsDTO face) {
		return face == null || face.getFace() == null;
	}

	private boolean isBiometricCaptured(BiometricInfoDTO biometrics) {
		boolean isBiometricCaptured = true;

		if (biometrics == null) {
			isBiometricCaptured = false;
		} else {
			isBiometricCaptured = !(isListEmpty(biometrics.getFingerprintDetailsDTO())
					&& isListEmpty(biometrics.getIrisDetailsDTO()) && validateFace(biometrics.getFace()));
		}

		return isBiometricCaptured;
	}

	private boolean isDeviceEnabled(String key) {
		return String.valueOf(ApplicationContext.map().get(key)).equalsIgnoreCase(RegistrationConstants.ENABLE);
	}

	private void validateContexts() throws RegBaseCheckedException {
		if (SessionContext.map() == null) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_CREATE_NO_SESSION_CONTEXT_MAP);
		}

		if (ApplicationContext.map() == null) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_CREATE_NO_APP_CONTEXT_MAP);
		}

		if (isStringEmpty(ApplicationContext.applicationLanguage())) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PKT_CREATE_NO_APP_LANG);
		}

		if (!ApplicationContext.map().containsKey(RegistrationConstants.CBEFF_UNQ_TAG)) {
			LOGGER.warn(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					String.format("%s --> %s",
							RegistrationExceptionConstants.REG_PKT_CREATE_NO_CBEFF_TAG_FLAG.getErrorCode(),
							RegistrationExceptionConstants.REG_PKT_CREATE_NO_CBEFF_TAG_FLAG.getErrorMessage()));
		}
	}
	
	private void setDocumentCodes(RegistrationDTO registrationDTO) {
		IndividualIdentity individualIdentity = (IndividualIdentity)registrationDTO.getDemographicDTO().getDemographicInfoDTO().getIdentity();
		
		setDocumentTypeCode(individualIdentity.getProofOfAddress());
		setDocumentTypeCode(individualIdentity.getProofOfDateOfBirth());
		setDocumentTypeCode(individualIdentity.getProofOfException());
		setDocumentTypeCode(individualIdentity.getProofOfIdentity());
		setDocumentTypeCode(individualIdentity.getProofOfRelationship());
	}

	private void setDocumentTypeCode(DocumentDetailsDTO documentDetailsDTO) {
		if(documentDetailsDTO != null) {
			List<DocumentType> documentTypes = documentTypeDAO.getDocTypeByName(documentDetailsDTO.getType());
			
			if(!documentTypes.isEmpty()) documentDetailsDTO.setType(documentTypes.get(0).getCode());
		}
	}
	
}
