package io.mosip.registration.service.packet.impl;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.constants.RegistrationExceptions;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dto.AuditDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.json.demo.Demographic;
import io.mosip.registration.dto.json.metadata.Audit;
import io.mosip.registration.dto.json.metadata.BiometricSequence;
import io.mosip.registration.dto.json.metadata.DemographicSequence;
import io.mosip.registration.dto.json.metadata.FieldValueArray;
import io.mosip.registration.dto.json.metadata.HashSequence;
import io.mosip.registration.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.hmac.HMACGeneration;
import io.mosip.registration.util.json.JSONConverter;
import io.mosip.registration.service.external.ZipCreationService;
import io.mosip.registration.service.packet.PacketCreationService;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.DEMOGRPAHIC_JSON_NAME;
import static io.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;
import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_CREATION;
import static io.mosip.registration.constants.RegistrationConstants.REGISTRATION_ID;

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
	private AuditDAO auditDAO;
	@Autowired
	private ZipCreationService zipCreationService;
	private static final Logger LOGGER = AppConfig.getLogger(PacketCreationServiceImpl.class);

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
	public byte[] create(RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been called");
		
		try {
			String rid = registrationDTO.getRegistrationId();
			// Fetch unsync'ed audit logs from DB
			// TODO: Commented below line intentionally. Will be updated
			//registrationDTO.setAuditDTOs(MAPPER_FACADE.mapAsList(auditDAO.getAllUnsyncAudits(), AuditDTO.class));

			// Map object to store the byte array of JSON objects namely Demographic, HMAC,
			// Packet Meta-Data and Audit
			Map<String, byte[]> jsonsMap = new HashMap<>();

			// Generating Demographic JSON as byte array
			jsonsMap.put(DEMOGRPAHIC_JSON_NAME,
					javaObjectToJsonString(JSONConverter.jsonConvertor(registrationDTO.getDemographicDTO()))
							.getBytes());

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Demographic Json created successfully");
			auditFactory.audit(AuditEvent.PACKET_DEMO_JSON_CREATED, Components.PACKET_CREATOR,
					"Demographic JSON created successfully", REGISTRATION_ID, rid);

			// Generating HMAC File as byte array
			HashSequence hashSequence = new HashSequence(
					new BiometricSequence(new LinkedList<String>(), new LinkedList<String>()),
					new DemographicSequence(new LinkedList<String>()));
			jsonsMap.put(RegistrationConstants.HASHING_JSON_NAME, HMACGeneration.generatePacketDTOHash(registrationDTO,
					jsonsMap.get(DEMOGRPAHIC_JSON_NAME), hashSequence));

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "HMAC File generated successfully");
			auditFactory.audit(AuditEvent.PACKET_HMAC_FILE_CREATED, Components.PACKET_CREATOR,
					"Packet HMAC File created successfully", REGISTRATION_ID, rid);

			// Generating Packet Meta-Info JSON as byte array
			PacketMetaInfo packetInfo = MAPPER_FACADE.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");
			
			// Add HashSequence
			packetInfo.getIdentity().setHashSequence(buildHashSequence(hashSequence));
			
			jsonsMap.put(RegistrationConstants.PACKET_META_JSON_NAME, javaObjectToJsonString(packetInfo).getBytes());

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", REGISTRATION_ID, rid);

			// Generating Audit JSON as byte array
			jsonsMap.put(RegistrationConstants.AUDIT_JSON_FILE,
					javaObjectToJsonString(MAPPER_FACADE.mapAsList(registrationDTO.getAuditDTOs(), Audit.class))
							.getBytes());

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID,
					"Registration Packet Meta-Info JSON generated successfully");
			auditFactory.audit(AuditEvent.PACKET_META_JSON_CREATED, Components.PACKET_CREATOR,
					"Packet Meta-Data JSON created successfully", REGISTRATION_ID, rid);

			// Creating in-memory zip file for Packet Encryption
			byte[] packetZipBytes = zipCreationService.createPacket(registrationDTO, jsonsMap);

			LOGGER.debug(LOG_PKT_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Creation had been ended");
			auditFactory.audit(AuditEvent.PACKET_INTERNAL_ZIP, Components.PACKET_CREATOR,
					"Packet Internal Zip File created successfully", REGISTRATION_ID, rid);

			return packetZipBytes;
		} catch (JsonProcessingException mosipJsonProcessingException) {
			throw new RegBaseCheckedException(RegistrationExceptions.REG_JSON_PROCESSING_EXCEPTION.getErrorCode(),
					RegistrationExceptions.REG_JSON_PROCESSING_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_CREATION_EXCEPTION,
					runtimeException.toString());
		}
	}

	
	private List<FieldValueArray> buildHashSequence(HashSequence hashSequence) {
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
}
