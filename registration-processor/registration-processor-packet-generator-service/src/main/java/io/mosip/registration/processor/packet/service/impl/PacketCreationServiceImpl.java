package io.mosip.registration.processor.packet.service.impl;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.processor.packet.service.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.processor.packet.service.mapper.CustomObjectMapper.MAPPER_FACADE;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.packet.service.PacketCreationService;
import io.mosip.registration.processor.packet.service.builder.AuditRequestBuilder;
import io.mosip.registration.processor.packet.service.builder.Builder;
import io.mosip.registration.processor.packet.service.constants.RegistrationConstants;
import io.mosip.registration.processor.packet.service.dto.AuditDTO;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
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

	@Autowired
	private Environment environment;

	private static Random random = new Random(5000);

	private String creationTime = null;

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

			// Map object to store the UUID's generated for BIR in CBEFF
			Map<String, String> birUUIDs = new HashMap<>();

			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> filesGeneratedForPacket = new HashMap<>();

			// Generating Demographic JSON as byte array
			String idJsonAsString = javaObjectToJsonString(registrationDTO.getDemographicDTO().getDemographicInfoDTO());
			jsonValidator.validateJson(idJsonAsString, "mosip-identity-json-schema.json");
			filesGeneratedForPacket.put(DEMOGRPAHIC_JSON_NAME, idJsonAsString.getBytes());

			AuditRequestBuilder auditRequestBuilder = new AuditRequestBuilder();
			// Getting Host IP Address and Name
			String hostIP = null;
			String hostName = null;
			try {
				hostIP = InetAddress.getLocalHost().getHostAddress();
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException unknownHostException) {

				hostIP = environment.getProperty(RegistrationConstants.HOST_IP);
				hostName = environment.getProperty(RegistrationConstants.HOST_NAME);
			}
			auditRequestBuilder.setActionTimeStamp(LocalDateTime.now(ZoneOffset.UTC))
					.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC)).setUuid("")
					.setApplicationId(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_ID))
					.setApplicationName(environment.getProperty(RegistrationConstants.AUDIT_APPLICATION_NAME))
					.setCreatedBy("Packet_Generator").setDescription("Packet uploaded successfully")
					.setEventId("RPR_405").setEventName("packet uploaded").setEventType("USER").setHostIp(hostIP)
					.setHostName(hostName).setId(rid).setIdType("REGISTRATION_ID").setModuleId("REG - MOD - 119")
					.setModuleName("Packet Generator").setSessionUserId("mosip").setSessionUserName("Registration");
			AuditDTO auditDto = auditRequestBuilder.build();

			filesGeneratedForPacket.put(RegistrationConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(auditDto).getBytes());

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(new DemographicSequence(new LinkedList<>()),
					new LinkedList<>());
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME,
					HMACGeneration.generatePacketDTOHash(registrationDTO, filesGeneratedForPacket, hashSequence));

			// Generating packet_osi_hash text file as byte array
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME, HMACGeneration
					.generatePacketOSIHash(filesGeneratedForPacket, hashSequence.getOsiDataHashSequence()));

			// Generating Packet Meta-Info JSON as byte array
			PacketMetaInfo packetInfo = MAPPER_FACADE.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");
			List<FieldValue> metadata = packetInfo.getIdentity().getMetaData();
			for (FieldValue field : metadata) {
				if (field.getLabel().equalsIgnoreCase(JsonConstant.CREATIONDATE)) {
					creationTime = field.getValue();
				}

			}
			// Add HashSequence
			packetInfo.getIdentity().setHashSequence(buildHashSequence(hashSequence));
			List<String> hashsequence2List = new ArrayList<String>();
			hashsequence2List.add("audit");
			// Add HashSequence for packet_osi_data
			packetInfo.getIdentity()
					.setHashSequence2(
							(List<FieldValueArray>) Builder.build(ArrayList.class)
									.with(values -> values.add(Builder.build(FieldValueArray.class)
											.with(field -> field.setLabel("otherFiles"))
											.with(field -> field.setValue(hashsequence2List)).get()))
									.get());
			filesGeneratedForPacket.put(RegistrationConstants.PACKET_META_JSON_NAME,
					javaObjectToJsonString(packetInfo).getBytes());

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = zipCreationService.createPacket(registrationDTO, filesGeneratedForPacket);

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

		// Add Sequence of Applicant Demographic
		fieldValueArray = new FieldValueArray();
		fieldValueArray.setLabel("applicantDemographicSequence");
		fieldValueArray.setValue(hashSequence.getDemographicSequence().getApplicant());
		hashSequenceList.add(fieldValueArray);

		return hashSequenceList;
	}

	@Override
	public String getCreationTime() {

		return creationTime;
	}

}
