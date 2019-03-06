package io.mosip.registration.processor.packet.service.impl;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.processor.packet.service.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.processor.packet.service.mapper.CustomObjectMapper.MAPPER_FACADE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.processor.packet.service.PacketCreationService;
import io.mosip.registration.processor.packet.service.builder.Builder;
import io.mosip.registration.processor.packet.service.constants.RegistrationConstants;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.dto.json.metadata.BiometricSequence;
import io.mosip.registration.processor.packet.service.dto.json.metadata.DemographicSequence;
import io.mosip.registration.processor.packet.service.dto.json.metadata.FieldValue;
import io.mosip.registration.processor.packet.service.dto.json.metadata.FieldValueArray;
import io.mosip.registration.processor.packet.service.dto.json.metadata.HashSequence;
import io.mosip.registration.processor.packet.service.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.packet.service.exception.RegBaseUncheckedException;
import io.mosip.registration.processor.packet.service.exception.RegistrationExceptionConstants;
import io.mosip.registration.processor.packet.service.external.ZipCreationService;
import io.mosip.registration.processor.packet.service.util.hmac.HMACGeneration;

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

	@Autowired
	private JsonValidator jsonValidator;

	private static Random random = new Random(5000);
	// @Autowired
	// private AuditFactory auditFactory;
	// @Autowired
	// private AuditLogControlDAO auditLogControlDAO;
	// @Autowired
	// private AuditDAO auditDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.PacketCreationService#create(io.mosip.
	 * registration.dto.RegistrationDTO)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public byte[] create(final RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		// LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration
		// Creation had been called");
		try {
			String rid = registrationDTO.getRegistrationId();
			String loggerMessageForCBEFF = "Byte array of %s file generated successfully";

			// Map object to store the UUID's generated for BIR in CBEFF
			Map<String, String> birUUIDs = new HashMap<>();

			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();

			// LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
			// String.format(loggerMessageForCBEFF,
			// RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME));
			// auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED,
			// Components.PACKET_CREATOR, rid,
			// AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Generating Demographic JSON as byte array
			String idJsonAsString = javaObjectToJsonString(registrationDTO.getDemographicDTO().getDemographicInfoDTO());
			jsonValidator.validateJson(idJsonAsString, "mosip-identity-json-schema.json");
			filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, idJsonAsString.getBytes());

			// LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
			// String.format(loggerMessageForCBEFF,
			// RegistrationConstants.DEMOGRPAHIC_JSON_NAME));
			// auditFactory.audit(AuditEvent.PACKET_DEMO_JSON_CREATED,
			// Components.PACKET_CREATOR, rid,
			// AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Generating Audit JSON as byte array
			// Fetch unsync'ed audit logs from DB
			// List<Audit> audits =
			// auditDAO.getAudits(auditLogControlDAO.getLatestRegistrationAuditDates());

			// registrationDTO.setAuditLogStartTime(Timestamp.valueOf(audits.get(0).getCreatedAt()));
			// registrationDTO.setAuditLogEndTime(Timestamp.valueOf(audits.get(audits.size()
			// - 1).getCreatedAt()));
			// filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE,
			// javaObjectToJsonString(audits).getBytes());

			// LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
			// String.format(loggerMessageForCBEFF, RegistrationConstants.AUDIT_JSON_FILE));
			// auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED,
			// Components.PACKET_CREATOR, rid,
			// AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(new BiometricSequence(new LinkedList<>(), new LinkedList<>()),
					new DemographicSequence(new LinkedList<>()), new LinkedList<>());
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME,
					HMACGeneration.generatePacketDTOHash(registrationDTO, filesGeneratedForPacket, hashSequence));

			// LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
			// String.format(loggerMessageForCBEFF,
			// RegistrationConstants.PACKET_DATA_HASH_FILE_NAME));
			// auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED,
			// Components.PACKET_CREATOR, rid,
			// AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Generating packet_osi_hash text file as byte array
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME, HMACGeneration
					.generatePacketOSIHash(filesGeneratedForPacket, hashSequence.getOsiDataHashSequence()));

			// LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
			// String.format(loggerMessageForCBEFF,
			// RegistrationConstants.PACKET_OSI_HASH_FILE_NAME));
			// auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED,
			// Components.PACKET_CREATOR, rid,
			// AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Generating Packet Meta-Info JSON as byte array
			PacketMetaInfo packetInfo = MAPPER_FACADE.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");

			// Set Registered Device
			packetInfo.getIdentity().setCapturedRegisteredDevices(getRegisteredDevices());

			// Set Registered Device
			packetInfo.getIdentity().setCapturedNonRegisteredDevices(null);

			// Add HashSequence
			packetInfo.getIdentity().setHashSequence(buildHashSequence(hashSequence));

			// Add HashSequence for packet_osi_data
			packetInfo.getIdentity()
					.setHashSequence2((List<FieldValueArray>) Builder.build(ArrayList.class)
							.with(values -> values.add(Builder.build(FieldValueArray.class)
									.with(field -> field.setLabel("otherFiles"))
									.with(field -> field.setValue(hashSequence.getOsiDataHashSequence())).get()))
							.get());
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_META_JSON_NAME,
					javaObjectToJsonString(packetInfo).getBytes());

			// LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
			// String.format(loggerMessageForCBEFF,
			// RegistrationConstants.PACKET_META_JSON_NAME));
			// auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED,
			// Components.PACKET_CREATOR, rid,
			// AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);

			// LOGGER.info(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration
			// Creation had been ended");
			// auditFactory.audit(AuditEvent.PACKET_INTERNAL_ZIP, Components.PACKET_CREATOR,
			// rid,
			// AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());

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

	private List<FieldValue> getRegisteredDevices() {
		// List<RegDeviceMaster> registeredDevices = machineMappingDAO
		// .getDevicesMappedToRegCenter(ApplicationContext.applicationLanguage());

		List<FieldValue> capturedRegisteredDevices = new ArrayList<>();
		FieldValue capturedRegisteredDevice;

		/*
		 * if (registeredDevices != null) { for (RegDeviceMaster registeredDevice :
		 * registeredDevices) { capturedRegisteredDevice = new FieldValue();
		 * capturedRegisteredDevice.setLabel(registeredDevice.getRegDeviceSpec().
		 * getRegDeviceType().getName());
		 * capturedRegisteredDevice.setValue(registeredDevice.getRegMachineSpecId().
		 * getId()); capturedRegisteredDevices.add(capturedRegisteredDevice); } }
		 */

		return capturedRegisteredDevices;
	}

}
